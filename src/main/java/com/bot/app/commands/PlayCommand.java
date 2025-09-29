package com.bot.app.commands;

import com.bot.app.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements Command {

    // TODO: add automatic searching for songs when message content is not url

    private final String name = "play";
    private final String commandName = Prefix.prefix + name;
    private final MessageReceivedEvent event;
    private final String url;

    public PlayCommand(MessageReceivedEvent event, String url) {
        this.event = event;
        this.url = url;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        connectToVc(event);
        play(event, url);
    }

    private void play(MessageReceivedEvent event, String url) {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.play(event, url);
    }

    //TODO: add exceptions and handling
    //TODO: add logging
    private void connectToVc(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (!memberVoiceState.inAudioChannel()) {
            informUserNotInVc(event.getChannel(), member.getUser());
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            VoiceChannel vc = (VoiceChannel) memberVoiceState.getChannel();   //don't use .asVoiceChannel(), creates null pointer exception
            AudioManager manager = guild.getAudioManager();
            manager.openAudioConnection(vc);
        } else {
            if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
                informUserNotInSameVc(event.getChannel(), member.getUser());
            }
        }
    }

    private void informUserNotInVc(MessageChannel channel, User user) {
        String id = user.getId();
        String message = String.format("User <@%s> is currently not in a voice channel.", id);
        channel.sendMessage(message).queue();
    }

    private void informUserNotInSameVc(MessageChannel channel, User user) {
        String id = user.getId();
        String message = String.format("User <@%s> is not in the same voice channel as bot.", id);
        channel.sendMessage(message).queue();
    }
}
