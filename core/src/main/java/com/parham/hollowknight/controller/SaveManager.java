package com.parham.hollowknight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.parham.hollowknight.model.GameData;

public class SaveManager {

    private static SaveManager instance;
    private static final int MAX_SLOTS = 4;
    private static final String SAVE_DIR = "saves/";

    private final Json json;
    private final GameData[] cachedSlots = new GameData[MAX_SLOTS];

    private SaveManager() {
        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        loadAllSlots();
    }

    public static SaveManager getInstance() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }

    private FileHandle getSlotFile(int slotIndex) {
        return Gdx.files.local(SAVE_DIR + "slot_" + slotIndex + ".json");
    }

    public void save(GameData data) {
        data.lastSavedTimestamp = System.currentTimeMillis();
        FileHandle file = getSlotFile(data.slotIndex);
        file.writeString(json.prettyPrint(data), false);
        cachedSlots[data.slotIndex] = data;
    }


    private void loadAllSlots() {
        for (int i = 0; i < MAX_SLOTS; i++) {
            cachedSlots[i] = load(i);
        }
    }

    public GameData load(int slotIndex) {
        FileHandle file = getSlotFile(slotIndex);
        if (!file.exists()) return null;
        try {
            GameData data = json.fromJson(GameData.class, file.readString());
            cachedSlots[slotIndex] = data;
            return data;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load slot " + slotIndex, e);
            return null;
        }
    }


    public GameData getSlot(int slotIndex) {
        return cachedSlots[slotIndex];
    }

    public boolean isSlotEmpty(int slotIndex) {
        return cachedSlots[slotIndex] == null;
    }

    public int getMaxSlots() {
        return MAX_SLOTS;
    }

    public GameData createNewGame(int slotIndex) {
        GameData data = GameData.createNew(slotIndex);
        save(data);
        return data;
    }

    public void clearSlot(int slotIndex) {
        FileHandle file = getSlotFile(slotIndex);
        if (file.exists()) file.delete();
        cachedSlots[slotIndex] = null;
    }
}
