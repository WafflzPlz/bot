package com.bot.app.lavaplayer;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> guildMusicManagerMap = new HashMap<>();
    private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SPOTIFY_CLIENT_ID = dotenv.get("SPOTIFY_CLIENT_ID");
    private static final String SPOTIFY_CLIENT_SECRET = dotenv.get("SPOTIFY_CLIENT_SECRET");

    public PlayerManager() {
        SpotifySourceManager spotifySourceManager = new SpotifySourceManager(null, SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET, "AUT", audioPlayerManager);
        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();

        audioPlayerManager.registerSourceManager(spotifySourceManager);
        audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public static PlayerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagerMap.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());
            return musicManager;
        });
    }

    public void play(MessageReceivedEvent event, String trackURL) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(event.getGuild());
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                guildMusicManager.getTrackScheduler().queue(audioTrack);
                postSongInfo(event, audioTrack.getInfo());
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                postPlaylistInfo(event, audioPlaylist);
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    guildMusicManager.getTrackScheduler().queue(track);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void destroy(Guild guild) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(guild);
        guildMusicManager.getTrackScheduler().destroy();
    }

    public void skip(MessageReceivedEvent event) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(event.getGuild());
        var audioTrack = guildMusicManager.getTrackScheduler().getQueue().peek();
        guildMusicManager.getTrackScheduler().skip();
        if (audioTrack == null) {
            postMessage(event.getChannel(), "Skipping song");
        } else {
            var info = audioTrack.getInfo();
            postMessage(event.getChannel(), String.format("Skipping song, now playing %s from %s", info.title, info.author));
        }
    }

    public void pause(MessageReceivedEvent event) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(event.getGuild());
        guildMusicManager.getTrackScheduler().pause();
        postMessage(event.getChannel(), "Pause playing");
    }

    public void resume(MessageReceivedEvent event) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(event.getGuild());
        guildMusicManager.getTrackScheduler().resume();
        postMessage(event.getChannel(), "Resume playing");
    }

    public void emptyQueue(MessageReceivedEvent event) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(event.getGuild());
        guildMusicManager.getTrackScheduler().emptyQueue();
        postMessage(event.getChannel(), "Queue is emptied");
    }

    private void postMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    private void postSongInfo(MessageReceivedEvent event, AudioTrackInfo info) {
        var guildMusicManager = getGuildMusicManager(event.getGuild());
        var message = String.format("Added %s from %s to queue", info.title, info.author);
        if (guildMusicManager.getTrackScheduler().getQueue().isEmpty()) {
            message = String.format("Now playing %s from %s", info.title, info.author);
        }

        event.getChannel().sendMessage(message).queue();
    }

    private void postPlaylistInfo(MessageReceivedEvent event, AudioPlaylist playlist) {
        var guildMusicManager = getGuildMusicManager(event.getGuild());
        var message = "Adding playlist to queue";

        var firstTrackInfo = playlist.getTracks().get(0).getInfo();
        if (guildMusicManager.getTrackScheduler().getQueue().isEmpty()) {
            message += String.format("\nNow playing %s from %s", firstTrackInfo.title, firstTrackInfo.author);
        }

        event.getChannel().sendMessage(message).queue();
    }
}
