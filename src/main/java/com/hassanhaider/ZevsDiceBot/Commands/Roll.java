package com.hassanhaider.ZevsDiceBot.Commands;

import com.hassanhaider.ZevsDiceBot.DiceEngine.Dice;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll {
    //arraylist to hold die modifiers of each addend by user
    private ArrayList<String> rollMathConstructor = new ArrayList<>();

    //arraylist to hold added rolls by user (eg. 1d20, +10, 1d4)
    private ArrayList<String> rollAddend = new ArrayList<>();

    //arralist to hold roll addend total
    private ArrayList<Integer> rollAddendTotal = new ArrayList<>();

    //arralist to hold roll addend total
    private ArrayList<Boolean> rollAddendIsDice = new ArrayList<>();

    //arraylist to hold individual rolls of each dice rolled
    private ArrayList<ArrayList<Integer>> rollDiceRolls = new ArrayList<>();

    //sum of the roll expression
    private int rollTotal = 0;

    //get for event
    MessageReceivedEvent event;
    private MessageReceivedEvent getEvent() {
        return event;
    }

    public Roll (MessageReceivedEvent event, Random random) {
        //set event
        this.event = event;
        String userMessage = getEvent().getMessage().getContentRaw();

        //replace - with <subtract>
        String userInputSubtractionCleaned = userMessage.replace("-", "<subtract>");

        //replace + with <add>
        String userInputAdditionCleaned = userInputSubtractionCleaned.replace("+", "<add>");

        //split string into independent rolls or modifiers
        String [] arrayUserInput = userInputAdditionCleaned.split("(?=<add>)|(?=<subtract>)|!roll");

        //arraylist to hold split message
        ArrayList<String> arrayListUserInput = new ArrayList<>();

        //convert user input array into arraylist
        for (String a : arrayUserInput) {
            String userInput = a.replace(" ", "");
            arrayListUserInput.add(userInput);
        }

        //remove first index of arraylist because it is not needed
        if (arrayListUserInput.isEmpty()) { //in user rolls !roll alone
            AddRoll("+", "1d20", random);
        }
        else { //remove first array and set first value in standard format
            arrayListUserInput.remove(0); //if user rolls custom !roll
            arrayListUserInput.set(0, "<add>" + arrayListUserInput.get(0));
        }

        //set to true to test final array message
        boolean testFinalArrayList = false;
        if (testFinalArrayList) {
            for (String a : arrayListUserInput) {
                System.out.println(a);
            }
        }

        //parse add modifer and roll to array
        for (String a : arrayListUserInput) {
            if (a.contains("<add>")) {
                AddRoll("+", a, random);
            }
            else if (a.contains("<subtract>")) {
                AddRoll("-", a, random);
            }
        }
    }
    private void AddRoll (String MathConstructor, String addend, Random random) { //constructor(+,-) addend(modifier,dice expression)
        //pattern to find roll
        Pattern addendPattern = Pattern.compile("[0-9]+d[0-9]+|[0-9]+");

        //add modifer to arraylist
        rollMathConstructor.add(MathConstructor);

        //extract roll and set add to arraylist
        Matcher addendMatcher = addendPattern.matcher(addend);
        addendMatcher.find();
        rollAddend.add(addendMatcher.group());

        //add dies roll to total and addend total
        //pattern to match dice rolld format (#d##)
        Pattern addendIsDice = Pattern.compile("[0-9]+d[0-9]+");
        Matcher rollMatcher = addendIsDice.matcher(addend);

        //pattern to match number addend
        Pattern addendIsNumber = Pattern.compile("[0-9]+");
        Matcher addendNumberMatcher = addendIsNumber.matcher(addend);

        if (rollMatcher.find()) {
            //create dice object
            Dice dice = new Dice(rollMatcher.group(),random);
            rollDiceRolls.add(dice.getDiceRolls());

            //set if modifier is dice or not
            rollAddendIsDice.add(true);

            //add total
            if (MathConstructor.equals("+")) {
                rollTotal = rollTotal + dice.getTotal();
            }
            else if (MathConstructor.equals("-")) {
                rollTotal = rollTotal - dice.getTotal();
            }

            //add roll total to arraylist
            rollAddendTotal.add(dice.getTotal());
        }
        else if (addendNumberMatcher.find()){
            rollAddendIsDice.add(false);

            //add total
            if (MathConstructor.equals("+")) {
                rollTotal = rollTotal + Integer.parseInt(addendNumberMatcher.group());
            }
            else if (MathConstructor.equals("-")) {
                rollTotal = rollTotal - Integer.parseInt(addendNumberMatcher.group());
            }

            rollAddendTotal.add(Integer.parseInt(addendNumberMatcher.group()));
        }
    }

    //prints message for discord
    public void sendDiscordMessage() {
        StringBuilder outputMessageDiscord = new StringBuilder();

        //roll output
        outputMessageDiscord.append("<@").append(getEvent().getMessage().getAuthor().getIdLong()).append(">\n").append("**Rolled:** ");
        for (int i = 0; i < rollAddend.size(); i++) {
            //print roll
            outputMessageDiscord.append(rollAddend.get(i));

            //create string for individual dice rolls
            StringBuilder diceRolls = new StringBuilder();
            for (int a = 0; a < rollDiceRolls.get(i).size(); a++) {
                if (rollDiceRolls.get(i).get(a) == 1 || rollDiceRolls.get(i).get(a) == 20) {
                    diceRolls.append("**").append(rollDiceRolls.get(i).get(a)).append("**");
                }
                else {
                    diceRolls.append(rollDiceRolls.get(i).get(a));
                }
                if (a < rollDiceRolls.get(i).size() - 1) {
                    diceRolls.append(", ");
                }
            }


            if (rollAddendIsDice.get(i)) {
                outputMessageDiscord.append("(").append(diceRolls).append(")");
            }
            if (i < rollAddend.size() - 1) {
                outputMessageDiscord.append(" ").append(rollMathConstructor.get(i+1)).append(" ");
            }
        }

        //total output
        outputMessageDiscord.append("\n**Total:** ").append(rollTotal);

        //send message
        getEvent().getMessage().getGuildChannel().asStandardGuildMessageChannel().sendMessage(outputMessageDiscord).queue();
    }

    public void SendDebugRollMessage () {
        int[] diceValueCounter = new int[20];

        for (int a : rollDiceRolls.get(0)) {
            for (int b = 0; b < 20; b++) {
                if (a == (b + 1)) {
                    diceValueCounter[b] = diceValueCounter[b] + 1;
                }
            }
        }

        StringBuilder discordMessage = new StringBuilder();

        discordMessage.append("<@").append(event.getMessage().getAuthor().getIdLong()).append("> Here are your roll statistics:\n");

        for (int b = 0; b < 20; b++) {
            double percent = (double) diceValueCounter[b] /50;
            double percent2 = percent * 100;

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            discordMessage.append(b + 1).append(" = ").append(df.format(percent2)).append("%\n");
        }


        event.getMessage().delete().queue();
        event.getMessage().getGuildChannel().asStandardGuildMessageChannel().sendMessage(discordMessage).queue();
    }
}
