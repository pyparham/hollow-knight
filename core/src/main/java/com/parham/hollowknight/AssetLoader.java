package com.parham.hollowknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import static com.parham.hollowknight.Constants.*;

public class AssetLoader {

    private final AssetManager manager = new AssetManager();

    public BitmapFont menuFont;
    public BitmapFont listFont;
    public Texture menuPointer;
    public Texture menuBackground;
    public Texture menuLogo;
    public Cursor customCursor;
    public Texture settingsDivider;
    public Texture lineBar;
    public Texture sliderArrow;

    public Music backgroundMusic;
    public Music crossMusic;
    public Music greenPathMusic;

    public Sound clickSound;
    public Sound breakableHitSound;
    public Sound breakableDeathSound;

    public Texture focusStart;
    public Texture focusLoop;
    public Texture focusGet;
    public Texture focusEnd;
    public Texture knightIdle;
    public Texture knightHit;
    public Texture knightRun;
    public Texture knightJump;
    public Texture knightDoubleJump;
    public Texture knightDash;
    public Texture knightDeath;
    public Texture knightWallSlide;
    public Texture knightFall;
    public Texture knightAttack;
    public Texture knightLanding;
    public Texture sideSlash;
    public Texture UpSlash;
    public Texture DownSlash;
    public Texture lookUp;
    public Texture lookDown;
    public Texture dashEffect;
    public Texture wallJump;
    public Texture wallSlide;

    public TextureAtlas soulOrbAtlas;
    public Texture shellGrow;
    public Texture orbEye;
    public Texture filledHealth;
    public Texture breakLife;
    public Texture shine;
    public Texture emptyHealth;
    public Texture refillHealth;
    public Texture orbCircleMask;

    public Texture crawlidWalk;
    public Texture crawlidDeadAir;
    public Texture crawlidDeadLand;
    public Texture crawlidTurn;

    public Texture huskAttackAnticipate;
    public Texture huskAttackLunge;
    public Texture huskDeathLand;
    public Texture huskDeathAir;
    public Texture huskIdle;
    public Texture huskWalk;
    public Texture huskTurn;

    public Texture mossflyAppear;
    public Texture mossflyDeathAir;
    public Texture mossflyDeathLand;
    public Texture mossflyFly;
    public Texture mossflyShake;
    public Texture mossflyTurnToFly;

    public Texture crystallizedDeathAir;
    public Texture crystallizedDeathLand;
    public Texture crystallizedEvade;
    public Texture crystallizedIdle;
    public Texture crystallizedRun;
    public Texture crystallizedShoot;
    public Texture crystallizedTurn;

    public Texture falsenightAttack;
    public Texture falsenightAttackAntic;
    public Texture falsenightAttackRecover;
    public Texture falsenightBody;
    public Texture falsenightDeathFall;
    public Texture falsenightDeathHit;
    public Texture falsenightDeathLand;
    public Texture falsenightIdle;
    public Texture falsenightJump;
    public Texture falsenightJumpAntic;
    public Texture falsenightJumpAttack;
    public Texture falsenightLand;
    public Texture falsenightRun;
    public Texture falsenightStunRecover;

    public Texture zoteAttack;
    public Texture zoteFall;
    public Texture zoteGetUp;
    public Texture zoteIdle;
    public Texture zoteRoll;
    public Texture zoteTalk;
    public Texture zoteTurn;

    public TiledMap crossroadsMap;
    public TiledMap bossfightRoom;
    public TiledMap zootRoom;
    public TiledMap greenPath;

    public Texture wallIdleFrame;
    public Texture wallShakeAnim;

    public void queueSettings() {
        manager.load(Constants.SETTINGS_DIVIDER, Texture.class);
        manager.load(Constants.LINE_BAR, Texture.class);
        manager.load(Constants.SLIDER_ARROW, Texture.class);
    }

    public void finishSettings() {
        settingsDivider = manager.get(Constants.SETTINGS_DIVIDER, Texture.class);
        lineBar = manager.get(Constants.LINE_BAR, Texture.class);
        sliderArrow = manager.get(Constants.SLIDER_ARROW, Texture.class);
        applyLinearFilterToAllTextures();
    }

    public void queueMusic() {
        manager.load(MENU_MUSIC, Music.class);
        manager.load(OPTION_CLICK, Sound.class);
        manager.load(BREAKABLE_HIT,  Sound.class);
        manager.load(BREAKABLE_DEATH,  Sound.class);
        manager.load(CROSS_ROADS, Music.class);
        manager.load(GREEN_PATH, Music.class);
    }

    public void finishMusic() {
        backgroundMusic = manager.get(MENU_MUSIC, Music.class);
        clickSound = manager.get(Constants.OPTION_CLICK, Sound.class);
        crossMusic = manager.get(CROSS_ROADS, Music.class);
        greenPathMusic = manager.get(GREEN_PATH, Music.class);
        breakableHitSound = manager.get(BREAKABLE_HIT,  Sound.class);
        breakableDeathSound = manager.get(BREAKABLE_DEATH,  Sound.class);

    }


    public float update() {
        manager.update();
        return manager.getProgress();
    }

    public boolean isFinished() {
        return manager.isFinished();
    }

    public void queueMainMenu() {
        manager.load(MENU_BG, Texture.class);
        manager.load(MENU_LOGO, Texture.class);
        manager.load(MENU_POINTER, Texture.class);
        manager.load(CURSOR, Pixmap.class);
        queueSettings();
        queueKnight();
        queueSoulOrb();
        queueEnemies();
    }

    public void finishMainMenu() {
        menuBackground = manager.get(Constants.MENU_BG, Texture.class);
        menuLogo = manager.get(Constants.MENU_LOGO, Texture.class);
        menuPointer = manager.get(MENU_POINTER, Texture.class);
        finishSettings();
        finishKnight();
        finishSoulOrb();
        finishEnemies();

        applyLinearFilterToAllTextures();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_MENU));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        menuFont = generator.generateFont(parameter);

        FreeTypeFontGenerator listGenerator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_LIST));
        parameter.size = 30;
        listFont = listGenerator.generateFont(parameter);

        generator.dispose();
        listGenerator.dispose();

        menuFont.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        listFont.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void dispose() {
        manager.dispose();
        if (menuFont != null) menuFont.dispose();
        if (listFont != null) listFont.dispose();
        if (customCursor != null) customCursor.dispose();
    }

    private void applyLinearFilterToAllTextures() {
        Array<Texture> loadedTextures = new Array<>();
        manager.getAll(Texture.class, loadedTextures);

        for (Texture texture : loadedTextures)
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void queueKnight() {
        manager.load(Constants.FOCUS_START, Texture.class);
        manager.load(Constants.FOCUS_CYCLE, Texture.class);
        manager.load(Constants.FOCUS_GET, Texture.class);
        manager.load(Constants.FOCUS_END, Texture.class);

        manager.load(Constants.IDLE, Texture.class);
        manager.load(Constants.RUN, Texture.class);

        manager.load(Constants.JUMP, Texture.class);
        manager.load(Constants.DOUBLE_JUMP, Texture.class);
        manager.load(Constants.FALL, Texture.class);
        manager.load(Constants.LANDING, Texture.class);
        manager.load(Constants.SIDE_SLASH, Texture.class);
        manager.load(Constants.UP_SLASH, Texture.class);
        manager.load(Constants.DOWN_SLASH, Texture.class);
        manager.load(Constants.DASH_EFFECT, Texture.class);

        manager.load(Constants.LOOK_UP, Texture.class);
        manager.load(Constants.LOOK_DOWN, Texture.class);

        manager.load(Constants.DASH, Texture.class);
        manager.load(Constants.DEATH, Texture.class);
        manager.load(Constants.WALL_SLIDE, Texture.class);
        manager.load(Constants.ATTACK, Texture.class);
        manager.load(Constants.HIT, Texture.class);

        manager.setLoader(TiledMap.class, new TmxMapLoader());
        manager.load(CROSS_ROADS_MAP, TiledMap.class);
        manager.load(BOSS_ROOM_MAP, TiledMap.class);
        manager.load(ZOTE_ROOM_MAP, TiledMap.class);
        manager.load(GREEN_PATH_MAP, TiledMap.class);
        manager.load(BREAK_WALL, Texture.class);
        manager.load(SHAKE_WALL, Texture.class);

        manager.load(Constants.WALL_SLIDE, Texture.class);
        manager.load(Constants.WALL_JUMP, Texture.class);
    }

    public void finishKnight() {
        focusStart = manager.get(Constants.FOCUS_START, Texture.class);
        focusLoop = manager.get(Constants.FOCUS_CYCLE, Texture.class);
        focusGet = manager.get(Constants.FOCUS_GET, Texture.class);
        focusEnd = manager.get(Constants.FOCUS_END, Texture.class);

        knightIdle = manager.get(Constants.IDLE, Texture.class);
        knightRun = manager.get(Constants.RUN, Texture.class);

        knightJump = manager.get(Constants.JUMP, Texture.class);
        knightDoubleJump = manager.get(Constants.DOUBLE_JUMP, Texture.class);
        knightFall = manager.get(Constants.FALL, Texture.class);
        knightLanding = manager.get(Constants.LANDING, Texture.class);

        sideSlash = manager.get(Constants.SIDE_SLASH, Texture.class);
        UpSlash = manager.get(Constants.UP_SLASH, Texture.class);
        DownSlash = manager.get(Constants.DOWN_SLASH, Texture.class);
        dashEffect = manager.get(Constants.DASH_EFFECT, Texture.class);

        lookUp = manager.get(Constants.LOOK_UP, Texture.class);
        lookDown = manager.get(Constants.LOOK_DOWN, Texture.class);

        knightDash = manager.get(Constants.DASH, Texture.class);
        knightDeath = manager.get(Constants.DEATH, Texture.class);
        knightWallSlide = manager.get(Constants.WALL_SLIDE, Texture.class);
        knightAttack = manager.get(Constants.ATTACK, Texture.class);
        knightHit = manager.get(Constants.HIT, Texture.class);

        crossroadsMap = manager.get(CROSS_ROADS_MAP, TiledMap.class);
        bossfightRoom = manager.get(BOSS_ROOM_MAP, TiledMap.class);
        zootRoom = manager.get(ZOTE_ROOM_MAP, TiledMap.class);
        greenPath = manager.get(GREEN_PATH_MAP, TiledMap.class);

        wallIdleFrame = manager.get(BREAK_WALL, Texture.class);
        wallShakeAnim = manager.get(SHAKE_WALL, Texture.class);

        wallSlide = manager.get(Constants.WALL_SLIDE, Texture.class);
        wallJump = manager.get(Constants.WALL_JUMP, Texture.class);

        applyLinearFilterToAllTextures();
    }


    public void queueSoulOrb() {
        manager.load(Constants.SOUL_ATLAS, TextureAtlas.class);
        manager.load(Constants.SHELL_GROW, Texture.class);
        manager.load(Constants.ORB_EYE, Texture.class);
        manager.load(Constants.FILLED_HEALTH, Texture.class);
        manager.load(Constants.BREAK_LIFE, Texture.class);
        manager.load(Constants.SHINE, Texture.class);
        manager.load(Constants.EMPTY_HEALTH, Texture.class);
        manager.load(Constants.REFILL_HEALTH, Texture.class);
        manager.load(Constants.ORB_CIRCLE_MASK, Texture.class);
    }

    public void finishSoulOrb() {
        soulOrbAtlas = manager.get(Constants.SOUL_ATLAS, TextureAtlas.class);
        shellGrow = manager.get(Constants.SHELL_GROW, Texture.class);
        orbEye = manager.get(Constants.ORB_EYE, Texture.class);
        filledHealth = manager.get(Constants.FILLED_HEALTH, Texture.class);
        breakLife = manager.get(Constants.BREAK_LIFE, Texture.class);
        shine = manager.get(Constants.SHINE, Texture.class);
        emptyHealth = manager.get(Constants.EMPTY_HEALTH, Texture.class);
        refillHealth = manager.get(Constants.REFILL_HEALTH, Texture.class);
        orbCircleMask = manager.get(Constants.ORB_CIRCLE_MASK, Texture.class);
    }

    public void queueEnemies() {
        manager.load(CRAWLID_WALK, Texture.class);
        manager.load(CRAWLID_DEATH_AIR, Texture.class);
        manager.load(CRAWLID_DEATH_LAND, Texture.class);
        manager.load(CRAWLID_TURN, Texture.class);

        manager.load(HUSK_ATTACK_ANTICIPATE, Texture.class);
        manager.load(HUSK_ATTACK_LUNGE, Texture.class);
        manager.load(HUSK_DEATH_LAND, Texture.class);
        manager.load(HUSK_DEATH_AIR, Texture.class);
        manager.load(HUSK_IDLE, Texture.class);
        manager.load(HUSK_WALK, Texture.class);
        manager.load(HUSK_TURN, Texture.class);

        manager.load(MOSSFLY_APPEAR, Texture.class);
        manager.load(MOSSFLY_DEATH_AIR, Texture.class);
        manager.load(MOSSFLY_DEATH_LAND, Texture.class);
        manager.load(MOSSFLY_FLY, Texture.class);
        manager.load(MOSSFLY_SHAKE, Texture.class);
        manager.load(MOSSFLY_TURN_TO_FLY, Texture.class);

        manager.load(CRYSTALLIZED_DEATH_AIR, Texture.class);
        manager.load(CRYSTALLIZED_DEATH_LAND, Texture.class);
        manager.load(CRYSTALLIZED_EVADE, Texture.class);
        manager.load(CRYSTALLIZED_IDLE, Texture.class);
        manager.load(CRYSTALLIZED_RUN, Texture.class);
        manager.load(CRYSTALLIZED_SHOOT, Texture.class);
        manager.load(CRYSTALLIZED_TURN, Texture.class);

        manager.load(FALSENIGHT_ATTACK, Texture.class);
        manager.load(FALSENIGHT_ATTACK_ANTIC, Texture.class);
        manager.load(FALSENIGHT_ATTACK_RECOVER, Texture.class);
        manager.load(FALSENIGHT_BODY, Texture.class);
        manager.load(FALSENIGHT_DEATH_FALL, Texture.class);
        manager.load(FALSENIGHT_DEATH_HIT, Texture.class);
        manager.load(FALSENIGHT_DEATH_LAND, Texture.class);
        manager.load(FALSENIGHT_IDLE, Texture.class);
        manager.load(FALSENIGHT_JUMP, Texture.class);
        manager.load(FALSENIGHT_JUMP_ANTIC, Texture.class);
        manager.load(FALSENIGHT_JUMP_ATTACK, Texture.class);
        manager.load(FALSENIGHT_LAND, Texture.class);
        manager.load(FALSENIGHT_RUN, Texture.class);
        manager.load(FALSENIGHT_STUN_RECOVER, Texture.class);

        manager.load(ZOTE_ATTACK, Texture.class);
        manager.load(ZOTE_FALL, Texture.class);
        manager.load(ZOTE_GET_UP, Texture.class);
        manager.load(ZOTE_IDLE, Texture.class);
        manager.load(ZOTE_ROLL, Texture.class);
        manager.load(ZOTE_TALK, Texture.class);
        manager.load(ZOTE_TURN, Texture.class);

    }

    public void finishEnemies() {
        crawlidWalk = manager.get(CRAWLID_WALK, Texture.class);
        crawlidDeadAir = manager.get(CRAWLID_DEATH_AIR, Texture.class);
        crawlidDeadLand = manager.get(CRAWLID_DEATH_LAND, Texture.class);
        crawlidTurn = manager.get(CRAWLID_TURN, Texture.class);

        huskAttackAnticipate = manager.get(HUSK_ATTACK_ANTICIPATE, Texture.class);
        huskAttackLunge = manager.get(HUSK_ATTACK_LUNGE, Texture.class);
        huskDeathLand = manager.get(HUSK_DEATH_LAND, Texture.class);
        huskDeathAir = manager.get(HUSK_DEATH_AIR, Texture.class);
        huskIdle = manager.get(HUSK_IDLE, Texture.class);
        huskWalk = manager.get(HUSK_WALK, Texture.class);
        huskTurn = manager.get(HUSK_TURN, Texture.class);

        mossflyAppear = manager.get(MOSSFLY_APPEAR, Texture.class);
        mossflyDeathAir = manager.get(MOSSFLY_DEATH_AIR, Texture.class);
        mossflyDeathLand = manager.get(MOSSFLY_DEATH_LAND, Texture.class);
        mossflyFly = manager.get(MOSSFLY_FLY, Texture.class);
        mossflyShake = manager.get(MOSSFLY_SHAKE, Texture.class);
        mossflyTurnToFly = manager.get(MOSSFLY_TURN_TO_FLY, Texture.class);

        crystallizedDeathAir = manager.get(CRYSTALLIZED_DEATH_AIR, Texture.class);
        crystallizedDeathLand = manager.get(CRYSTALLIZED_DEATH_LAND, Texture.class);
        crystallizedEvade = manager.get(CRYSTALLIZED_EVADE, Texture.class);
        crystallizedIdle = manager.get(CRYSTALLIZED_IDLE, Texture.class);
        crystallizedRun = manager.get(CRYSTALLIZED_RUN, Texture.class);
        crystallizedShoot = manager.get(CRYSTALLIZED_SHOOT, Texture.class);
        crystallizedTurn = manager.get(CRYSTALLIZED_TURN, Texture.class);

        falsenightAttack = manager.get(FALSENIGHT_ATTACK, Texture.class);
        falsenightAttackAntic = manager.get(FALSENIGHT_ATTACK_ANTIC, Texture.class);
        falsenightAttackRecover = manager.get(FALSENIGHT_ATTACK_RECOVER, Texture.class);
        falsenightBody = manager.get(FALSENIGHT_BODY, Texture.class);
        falsenightDeathFall = manager.get(FALSENIGHT_DEATH_FALL, Texture.class);
        falsenightDeathHit = manager.get(FALSENIGHT_DEATH_HIT, Texture.class);
        falsenightDeathLand = manager.get(FALSENIGHT_DEATH_LAND, Texture.class);
        falsenightIdle = manager.get(FALSENIGHT_IDLE, Texture.class);
        falsenightJump = manager.get(FALSENIGHT_JUMP, Texture.class);
        falsenightJumpAntic = manager.get(FALSENIGHT_JUMP_ANTIC, Texture.class);
        falsenightJumpAttack = manager.get(FALSENIGHT_JUMP_ATTACK, Texture.class);
        falsenightLand = manager.get(FALSENIGHT_LAND, Texture.class);
        falsenightRun = manager.get(FALSENIGHT_RUN, Texture.class);
        falsenightStunRecover = manager.get(FALSENIGHT_STUN_RECOVER, Texture.class);

        zoteAttack = manager.get(ZOTE_ATTACK, Texture.class);
        zoteFall = manager.get(ZOTE_FALL, Texture.class);
        zoteGetUp = manager.get(ZOTE_GET_UP, Texture.class);
        zoteIdle = manager.get(ZOTE_IDLE, Texture.class);
        zoteRoll = manager.get(ZOTE_ROLL, Texture.class);
        zoteTalk = manager.get(ZOTE_TALK, Texture.class);
        zoteTurn = manager.get(ZOTE_TURN, Texture.class);
    }
}
