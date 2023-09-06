package com.hassanhaider.ZevsDiceBot.DiceEngine;

import com.hassanhaider.ZevsDiceBot.Commands.Roll;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventListener extends ListenerAdapter {
    SecureRandom random = new SecureRandom();
    //Random random = new Random();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //search for commands
        final Pattern inputFilter = Pattern.compile("![A-Za-z]+");
        final Matcher inputMatcher = inputFilter.matcher(event.getMessage().getContentRaw());

        //if command is found run commands
        if (inputMatcher.find()) {
            //string for user input
            String userCommand = inputMatcher.group(0);

            //commands
            switch (userCommand) {
                case "!roll":
                    try {
                        Roll roll = new Roll(event, random);
                        roll.sendDiscordMessage();
                        event.getMessage().delete().queue();
                    } catch (Exception e) {
                        event.getMessage().getGuildChannel().asStandardGuildMessageChannel().sendMessage("That was not a valid roll").queue();
                        e.printStackTrace();
                    }

                    break;

                case "!helloworld":
                    System.out.println("Hello, World");
                    break;

//                case "!debugroll":
//                    DebugRoll debugRoll = new DebugRoll(event, random);
//                    break;

                default: //message user if command is invalid
                    event.getMessage().getGuildChannel().asStandardGuildMessageChannel().sendMessage("That is not a valid command").queue();
            }

        }
    }
}
