package com.parham.hollowknight.controller;

import com.badlogic.gdx.Gdx;
import com.parham.hollowknight.model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveManager {

    private static SaveManager instance;
    private static final int MAX_SLOTS = 4;

    private final GameData[] cachedSlots = new GameData[MAX_SLOTS];

    private SaveManager() {
        initDatabase();
        loadAllSlots();
    }

    public static SaveManager getInstance() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }

    private String getDbUrl() {
        String dbPath = Gdx.files.local("saves.db").file().getAbsolutePath();
        return "jdbc:sqlite:" + dbPath;
    }

    private void initDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS saves (
                slotIndex INTEGER PRIMARY KEY,
                areaName TEXT,
                playTimeSeconds INTEGER,
                lastSavedTimestamp INTEGER,
                maxHealth INTEGER,
                currentHealth INTEGER,
                maskShards INTEGER,
                soulVessels INTEGER,
                soul REAL,
                currentRoomId TEXT,
                playerX REAL,
                playerY REAL,
                lastSpawnName TEXT,
                isSecretWallDestroyed INTEGER,
                isFalseKnightDefeated INTEGER,
                charmsUnlocked TEXT,
                abilitiesUnlocked TEXT,
                defeatedBosses TEXT,
                visitedRooms TEXT,
                deathCount INTEGER,
                enemiesKilled INTEGER
            );
            """;

        try (Connection conn = DriverManager.getConnection(getDbUrl());
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            Gdx.app.error("SaveManager", "Failed to initialize database", e);
        }
    }

    public void save(GameData data) {
        data.lastSavedTimestamp = System.currentTimeMillis();

        String insertSQL = """
            INSERT OR REPLACE INTO saves (
                slotIndex, areaName, playTimeSeconds, lastSavedTimestamp,
                maxHealth, currentHealth, maskShards, soulVessels, soul,
                currentRoomId, playerX, playerY, lastSpawnName,
                isSecretWallDestroyed, isFalseKnightDefeated,
                charmsUnlocked, abilitiesUnlocked, defeatedBosses, visitedRooms,
                deathCount, enemiesKilled
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setInt(1, data.slotIndex);
            pstmt.setString(2, data.areaName);
            pstmt.setLong(3, data.playTimeSeconds);
            pstmt.setLong(4, data.lastSavedTimestamp);
            pstmt.setInt(5, data.maxHealth);
            pstmt.setInt(6, data.currentHealth);
            pstmt.setInt(7, data.maskShards);
            pstmt.setInt(8, data.soulVessels);
            pstmt.setFloat(9, data.soul);
            pstmt.setString(10, data.currentRoomId);
            pstmt.setFloat(11, data.playerX);
            pstmt.setFloat(12, data.playerY);
            pstmt.setString(13, data.lastSpawnName);

            pstmt.setInt(14, data.isSecretWallDestroyed ? 1 : 0);
            pstmt.setInt(15, data.isFalseKnightDefeated ? 1 : 0);

            pstmt.setString(16, booleanArrayToString(data.charmsUnlocked));
            pstmt.setString(17, booleanArrayToString(data.abilitiesUnlocked));
            pstmt.setString(18, listToString(data.defeatedBosses));
            pstmt.setString(19, listToString(data.visitedRooms));

            pstmt.setInt(20, data.deathCount);
            pstmt.setInt(21, data.enemiesKilled);

            pstmt.executeUpdate();

            cachedSlots[data.slotIndex] = data;

        } catch (SQLException e) {
            Gdx.app.error("SaveManager", "Failed to save slot " + data.slotIndex, e);
        }
    }

    private void loadAllSlots() {
        for (int i = 0; i < MAX_SLOTS; i++) {
            cachedSlots[i] = load(i);
        }
    }

    public GameData load(int slotIndex) {
        String selectSQL = "SELECT * FROM saves WHERE slotIndex = ?";

        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setInt(1, slotIndex);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                GameData data = new GameData();
                data.slotIndex = rs.getInt("slotIndex");
                data.areaName = rs.getString("areaName");
                data.playTimeSeconds = rs.getLong("playTimeSeconds");
                data.lastSavedTimestamp = rs.getLong("lastSavedTimestamp");
                data.maxHealth = rs.getInt("maxHealth");
                data.currentHealth = rs.getInt("currentHealth");
                data.maskShards = rs.getInt("maskShards");
                data.soulVessels = rs.getInt("soulVessels");
                data.soul = rs.getFloat("soul");
                data.currentRoomId = rs.getString("currentRoomId");
                data.playerX = rs.getFloat("playerX");
                data.playerY = rs.getFloat("playerY");
                data.lastSpawnName = rs.getString("lastSpawnName");

                data.isSecretWallDestroyed = rs.getInt("isSecretWallDestroyed") == 1;
                data.isFalseKnightDefeated = rs.getInt("isFalseKnightDefeated") == 1;

                data.charmsUnlocked = stringToBooleanArray(rs.getString("charmsUnlocked"), 8);
                data.abilitiesUnlocked = stringToBooleanArray(rs.getString("abilitiesUnlocked"), 10);
                data.defeatedBosses = stringToList(rs.getString("defeatedBosses"));
                data.visitedRooms = stringToList(rs.getString("visitedRooms"));

                data.deathCount = rs.getInt("deathCount");
                data.enemiesKilled = rs.getInt("enemiesKilled");

                return data;
            }
        } catch (SQLException e) {
            Gdx.app.error("SaveManager", "Failed to load slot " + slotIndex, e);
        }
        return null;
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
        String deleteSQL = "DELETE FROM saves WHERE slotIndex = ?";
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setInt(1, slotIndex);
            pstmt.executeUpdate();
            cachedSlots[slotIndex] = null;

        } catch (SQLException e) {
            Gdx.app.error("SaveManager", "Failed to clear slot " + slotIndex, e);
        }
    }

    private String booleanArrayToString(boolean[] arr) {
        if (arr == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i] ? "1" : "0");
            if (i < arr.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    private boolean[] stringToBooleanArray(String str, int size) {
        boolean[] arr = new boolean[size];
        if (str == null || str.isEmpty()) return arr;
        String[] parts = str.split(",");
        for (int i = 0; i < Math.min(size, parts.length); i++) {
            arr[i] = parts[i].equals("1");
        }
        return arr;
    }

    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }

    private List<String> stringToList(String str) {
        List<String> list = new ArrayList<>();
        if (str != null && !str.trim().isEmpty()) {
            list.addAll(Arrays.asList(str.split(",")));
        }
        return list;
    }
}
