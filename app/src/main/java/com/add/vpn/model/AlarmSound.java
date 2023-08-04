package com.add.vpn.model;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.add.vpn.holders.ContextHolder;

public class AlarmSound {
    private Ringtone ringtone;

    public AlarmSound() {
        Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSoundUri != null) {
            this.ringtone = RingtoneManager.getRingtone(ContextHolder.getActivity(), alarmSoundUri);
        }
    }

    public void alarmPlay() {
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }

    }

    public void alarmStop() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}
