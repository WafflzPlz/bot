package com.bot.app.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private BlockingDeque<AudioTrack> queue = new LinkedBlockingDeque<>();

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            player.startTrack(queue.poll(), false);
        }
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void emptyQueue() {
        this.queue = new LinkedBlockingDeque<>();
    }

    public void destroy() {
        player.destroy();
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);
    }

    public void skip() {
        player.startTrack(queue.poll(), false);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingDeque<AudioTrack> getQueue() {
        return queue;
    }
}
