package com.bot.app;

import com.bot.app.commands.Command;
import com.bot.app.commands.CommandGenerator;
import com.bot.app.exceptions.CommandNotFoundException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter
{

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // We don't want to respond to other bot accounts, including ourselves
        if (event.getAuthor().isBot()) return;

        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (!content.startsWith("!")) {
            return;
        }
        try {
            Command command = CommandGenerator.generate(event);
            command.execute();
        } catch (CommandNotFoundException e) {
            event.getChannel().sendMessage(e.getMessage()).queue();
        }

    }
}
