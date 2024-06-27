package com.bot.app;

import net.dv8tion.jda.api.entities.Guild;
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
                play(event, command[1]);
                break;
            case "!dc":
                disconnect(event);
                break;
        }
    }

    private void disconnect(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        AudioManager manager = guild.getAudioManager();
        manager.closeAudioConnection();

        MessageChannel channel = event.getChannel();
        channel.sendMessage("Bye!").queue();
    }

    //TODO: add exceptions and handling
    //TODO: add logging
    private void play(MessageReceivedEvent event, String url) {
        Guild guild = event.getGuild();
        Member member = event.getMessage().getMember();
        if (member == null) {
            return;
        }
        if (member.getVoiceState() == null || member.getVoiceState().getChannel() == null) {
            informUserNotInVc(event.getChannel(), member.getUser());
        }
        VoiceChannel vc = (VoiceChannel) member.getVoiceState().getChannel();   //don't use .asVoiceChannel(), creates null pointer exception
        AudioManager manager = guild.getAudioManager();
        //manager.setSendingHandler(new MyHandler)
        manager.openAudioConnection(vc);
    }

    private void informUserNotInVc(MessageChannel channel, User user) {
        String id = user.getId();
        String message = String.format("User <@%s> is currently not in a voice channel.", id);
        channel.sendMessage(message).queue();
    }


}
