package com.parham.hollowknight.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.parham.hollowknight.Main;

public class AudioManager {
    private static AudioManager audioManager;
    private Sound clickSound;
    private Music currentMusic;
    private Music nextMusic;
    private static float masterVolume = 1.0f;
    private static float musicVolume = 1.0f;
    private static float soundVolume = 1.0f;
    private static float fadeSpeed = 0.5f;
    private static boolean isFading = false;
    private static float lastMusicVolume = 1.0f;
    private Sound soundToPlay;
    private static boolean isMuted = false;


    private AudioManager() {
    }


    public static void init(Main game) {
        if (audioManager == null)
            audioManager = new AudioManager();
    }

    public static AudioManager getInstance() {
        return audioManager;
    }


    public void playMenuMusic(Main game) {
        if (this.currentMusic == null) {
            this.currentMusic = game.assets.backgroundMusic;
            this.clickSound = game.assets.clickSound;
            if (this.currentMusic != null) {
                this.currentMusic.setLooping(true);
            }
        }

        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.setVolume(masterVolume * musicVolume);
            currentMusic.play();
        }
    }

    public void playClickSound() {
        if (!isMuted) clickSound.play(masterVolume * soundVolume);
    }

    public void updateMusicVolume(float master, float music, float sound) {
        masterVolume = master / 10f;
        musicVolume = music / 10f;
        soundVolume = sound / 10f;
        lastMusicVolume = musicVolume;
        if (currentMusic != null)
            currentMusic.setVolume(isMuted ? 0f : masterVolume * musicVolume);

    }

    public void dispose() {
        if (clickSound != null) clickSound.dispose();
        if (currentMusic != null) currentMusic.dispose();
        if (nextMusic != null) nextMusic.dispose();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void switchMusicWithFade(Music music) {
        nextMusic = music;
        nextMusic.setVolume(0f);
        nextMusic.setLooping(true);
        nextMusic.play();

        isFading = true;
    }

    public void update(float delta) {
        if (!isFading) return;

        if (currentMusic != null && currentMusic.isPlaying()) {
            float curVol = currentMusic.getVolume();
            curVol -= fadeSpeed * delta;
            if (curVol <= 0f) {
                currentMusic.stop();
                currentMusic.setVolume(lastMusicVolume);
                currentMusic = null;
            } else {
                currentMusic.setVolume(curVol);
            }
        }

        if (nextMusic != null) {
            float nextVol = nextMusic.getVolume();
            nextVol += fadeSpeed * delta;
            if (nextVol >= lastMusicVolume) {
                nextMusic.setVolume(lastMusicVolume);
                currentMusic = nextMusic;
                nextMusic = null;
                isFading = false;
            } else {
                nextMusic.setVolume(nextVol);
            }
        }
    }


    public Music getCurrentMusic() {
        return currentMusic;
    }

    public void playSound(Sound sound) {
        if (!isMuted) sound.play(soundVolume * masterVolume);
    }



    public void stopSound(Sound sound) {
        if (sound != null) sound.stop();
    }

    public Sound getCurrentSound() {
        return soundToPlay;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
        if (currentMusic != null) {
            currentMusic.setVolume(isMuted ? 0f : masterVolume * musicVolume);
        }
    }

}
