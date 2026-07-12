package com.parham.hollowknight.controller;

import com.badlogic.gdx.audio.Sound;

import java.util.List;

public class ZoteVoiceManager {

    private final List<Sound> voiceLines;

    public ZoteVoiceManager(List<Sound> voiceLines) {
        this.voiceLines = voiceLines;
    }

    public void playRandomVoice() {
        if (voiceLines.isEmpty()) return;
        int idx = (int) (Math.random() * voiceLines.size());
        while (AudioManager.getInstance().getCurrentSound() == voiceLines.get(idx))
            idx = (int) (Math.random() * voiceLines.size());
        for (Sound line : voiceLines) {
            try {
                AudioManager.getInstance().stopSound(line);
            } catch (Exception ex) {
                continue;
            }
        }
        AudioManager.getInstance().playSound(voiceLines.get(idx));
    }
}
