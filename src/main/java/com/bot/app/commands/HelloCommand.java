package com.bot.app.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelloCommand implements Command {

    private final String name = "hello";
    private final String commandName = Prefix.prefix + name;
    private final MessageReceivedEvent event;

    public HelloCommand(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public void execute() {
        event.getChannel().sendMessage("Hello").queue();
    }
}
