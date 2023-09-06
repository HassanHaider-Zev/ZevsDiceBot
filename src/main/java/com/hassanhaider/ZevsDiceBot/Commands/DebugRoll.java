package com.hassanhaider.ZevsDiceBot.Commands;

import com.hassanhaider.ZevsDiceBot.DiceEngine.Dice;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.DecimalFormat;
import java.util.Random;

public class DebugRoll {
    public DebugRoll (MessageReceivedEvent event, Random random) {
        //String diceExpression = sampleSize + "d20";
        Dice dice = new Dice("10000d20", random);

        int[] diceValueCounter = new int[20];

        for (int a : dice.getDiceRolls()) {
            for (int b = 0; b < 20; b++) {
                if (a == (b + 1)) {
                    diceValueCounter[b] = diceValueCounter[b] + 1;
                }
            }
        }

        StringBuilder discordMessage = new StringBuilder();

        discordMessage.append("<@").append(event.getMessage().getAuthor().getIdLong()).append("> Here are your roll statistics:\n");

        for (int b = 0; b < 20; b++) {
            double percent = (double) diceValueCounter[b] /10000;
            double percent2 = percent * 100;

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            discordMessage.append(b + 1).append(" = ").append(df.format(percent2)).append("%\n");
        }


        event.getMessage().delete().queue();
        event.getMessage().getGuildChannel().asStandardGuildMessageChannel().sendMessage(discordMessage).queue();
    }
}
