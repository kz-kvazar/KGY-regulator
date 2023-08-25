package com.add.vpn.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmSound {
    private MediaPlayer mediaPlayer;

    public AlarmSound(Context context) {
        Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSoundUri != null) {
            this.mediaPlayer = MediaPlayer.create(context,alarmSoundUri);
        }
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
            //mediaPlayer.release();
            //mediaPlayer = null;
        }
    }
}
