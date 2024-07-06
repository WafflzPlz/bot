package com.bot.app;

import com.bot.app.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class CommandListener extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // We don't want to respond to other bot accounts, including ourself
        if (event.getAuthor().isBot()) return;

        // Make sure we only respond to events that occur in a guild
        if (!event.isFromGuild()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        String[] command = content.split(" ", 2);

        switch (command[0]) {
            case "!ping":
                MessageChannel channel = event.getChannel();
                channel.sendMessage("Pong!").queue();
                break;
            case "!play":
                if (command.length == 1) {
                    //TODO: exception
                    return;
                }
                connectToVc(event);
                play(event.getGuild(), command[1]);
                break;
            case "!dc":
                disconnect(event);
                break;
            case "!skip":
                skip(event);
                break;
            case "!pause":
                pause(event);
                break;
            case "!resume":
                resume(event);
                break;
        }
    }

    private void resume(MessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.resume(event.getGuild());
    }

    private void pause(MessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.pause(event.getGuild());
    }

    private void skip(MessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.skip(event.getGuild());
    }

    private void disconnect(MessageReceivedEvent event) {

        PlayerManager playerManager = PlayerManager.get();
        playerManager.destroy(event.getGuild());

        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        manager.closeAudioConnection();

        MessageChannel channel = event.getChannel();
        channel.sendMessage("Bye!").queue();
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

    private void play(Guild guild, String url) {
        PlayerManager playerManager = PlayerManager.get();
        playerManager.play(guild, url);
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
