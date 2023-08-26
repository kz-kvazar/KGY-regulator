package com.add.vpn.model;

import android.content.Context;
import android.media.MediaPlayer;
import com.add.vpn.R;

public class AlarmSound {
    private final MediaPlayer mediaPlayer;

    public AlarmSound(Context context) {
            this.mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
    }

    public void alarmPlay() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    }

    public void alarmStop() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
}
