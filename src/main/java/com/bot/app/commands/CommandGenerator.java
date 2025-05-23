package com.bot.app.commands;

import com.bot.app.exceptions.CommandNotFoundException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandGenerator {

    public static Command generate(MessageReceivedEvent event) throws CommandNotFoundException {

        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] splice = content.split(" ", 2);

        return switch (splice[0]) {
            case "!play" -> new PlayCommand(event, splice[1]);
            case "!dc" -> new DisconnectCommand(event);
            case "!skip" -> new SkipCommand(event);
            case "!pause" -> new PauseCommand(event);
            case "!resume" -> new ResumeCommand(event);
            case "!hello" -> new HelloCommand(event);
            case "!clear" -> new ClearQueueCommand(event);
            default -> throw new CommandNotFoundException(splice[0]);
        };

    }
}
