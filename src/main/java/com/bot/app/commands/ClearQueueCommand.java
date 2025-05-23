package com.bot.app.commands;

import com.bot.app.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearQueueCommand implements Command {

    private String name = "clear";
    private String commandName = Prefix.prefix + name;
    private MessageReceivedEvent event;

    public ClearQueueCommand(MessageReceivedEvent event) { this.event = event; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        Guild guild = event.getGuild();
        PlayerManager playerManager = PlayerManager.get();
        playerManager.emptyQueue(guild);
    }
}
