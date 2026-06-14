package com.parham.hollowknight.model;

import java.util.ArrayList;
import java.util.List;

public class GameData {

    public int slotIndex;
    public String areaName;
    public long playTimeSeconds;
    public long lastSavedTimestamp;

    public int maxHealth;
    public int currentHealth;
    public int maskShards;
    public int soulVessels;
    public float soul;

    public String currentRoomId;
    public float playerX;
    public float playerY;
    public String lastSpawnName;
    public boolean isSecretWallDestroyed;
    public boolean[] charmsUnlocked;
    public boolean[] abilitiesUnlocked;
    public List<String> defeatedBosses = new ArrayList<>();
    public List<String> visitedRooms = new ArrayList<>();

    public int deathCount;
    public int enemiesKilled;

    //static factory method
    public GameData() {
    }

    public static GameData createNew(int slotIndex) {
        GameData data = new GameData();
        data.slotIndex = slotIndex;
        data.areaName = "Crossroad";
        data.playTimeSeconds = 0;
        data.lastSavedTimestamp = System.currentTimeMillis();
        data.maxHealth = 5;
        data.currentHealth = 5;
        data.maskShards = 0;
        data.soulVessels = 0;
        data.soul = 0;
        data.isSecretWallDestroyed = false;
        data.currentRoomId = "Crossroad";
        data.lastSpawnName = "startGame_spawn";
        data.playerX = 0;
        data.playerY = 0;
        data.charmsUnlocked = new boolean[8];
        data.abilitiesUnlocked = new boolean[10];
        data.deathCount = 0;
        data.enemiesKilled = 0;
        return data;
    }


    public String getFormattedPlayTime() {
        long hours = playTimeSeconds / 3600;
        long minutes = (playTimeSeconds % 3600) / 60;
        if (hours > 0) return hours + "H " + minutes + "M";
        else if (minutes > 0) return minutes + "M";
        else return "< 1M";
    }
}
