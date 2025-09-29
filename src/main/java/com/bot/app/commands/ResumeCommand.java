package com.bot.app.commands;

import com.bot.app.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResumeCommand implements Command {

    private final String name = "resume";
    private final String commandName = Prefix.prefix + name;
    private final MessageReceivedEvent event;

    public ResumeCommand(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.resume(event);
    }
}
