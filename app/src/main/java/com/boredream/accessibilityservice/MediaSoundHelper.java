package com.boredream.accessibilityservice;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaSoundHelper {

    private final MediaPlayer mPlayer;

    public MediaSoundHelper(Context context) {
        mPlayer = MediaPlayer.create(context, R.raw.success);
    }

    public void playRingSound() {
        mPlayer.start();
    }

    public void stopPlay() {
       mPlayer.stop();
    }
}
