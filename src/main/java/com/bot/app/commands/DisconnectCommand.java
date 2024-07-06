package com.bot.app.commands;

import com.bot.app.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class DisconnectCommand implements Command {

    private final String name = "dc";
    private final String commandName = Prefix.prefix + name;
    private final MessageReceivedEvent event;

    public DisconnectCommand(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.destroy(event.getGuild());

        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        manager.closeAudioConnection();

        MessageChannel channel = event.getChannel();
        channel.sendMessage("Bye!").queue();
    }
}
