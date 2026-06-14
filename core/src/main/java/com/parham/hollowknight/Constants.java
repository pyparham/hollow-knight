package com.parham.hollowknight;

public class Constants {

    //for screen
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;

    //for intro
    public static final float INTRO_DURATION = 1f;
    public static final float INTRO_FADE_TIME = 1f;
    public static final float MIN_DISPLAY_TIME = 0f;


    //assets path
    public static final String INTRO_LOGO = "images/intro/intro.png";
    public static final String MENU_BG = "images/menus/mainMenu/Voidheart_menu_BG.png";
    public static final String LOADING_BG = "images/intro/controller_prompt_bg.png";
    public static final String MENU_POINTER = "images/menus/menuPointer.png";


    //audio
    public static final String OPTION_CLICK = "audio/effects/click.wav";
    public static final String BREAKABLE_HIT = "audio/effects/breakable_wall_hit_1.wav";
    public static final String BREAKABLE_DEATH = "audio/effects/breakable_wall_death.wav";

    // knight sound effect
    public static final String KNIGHT_DASH = "audio/effects/knight/hero_dash.wav";
    public static final String KNIGHT_JUMP = "audio/effects/knight/hero_jump.wav";
    public static final String KNIGHT_LANDING = "audio/effects/knight/hero_land_soft.wav";
    public static final String KNIGHT_WALK = "audio/effects/knight/hero_walk_footsteps_stone.wav";
    public static final String KNIGHT_ATTACK = "audio/effects/knight/sword_1.wav";
    public static final String KNIGHT_WALL_JUMP = "audio/effects/knight/hero_wall_jump.wav";


    // enemy sound effect


    // music
    public static final String MENU_MUSIC = "audio/music/menu.mp3";
    public static final String CROSS_ROADS = "audio/music/cross.mp3";
    public static final String GREEN_PATH = "audio/music/green.mp3";

    //loading bar
    public static final float LOADING_BAR_WIDTH = 400f;
    public static final float LOADING_BAR_HEIGHT = 10f;
    public static final float LOADING_BAR_Y_OFFSET = 100f;

    //Main Menu
    public static final String MENU_LOGO = "images/menus/mainMenu/title.png";

    // Fonts
    public static final String FONT_MENU = "ui/TrajanPro-Bold.otf";
    public static final String FONT_LIST = "ui/primary_font.ttf";

    // Settings assets
    public static final String SETTINGS_DIVIDER = "images/menus/setting/titleBottom.png";
    public static final String LINE_BAR = "images/menus/setting/line.png";
    public static final String SLIDER_ARROW = "images/menus/setting/slider_arrow.png";

    public static final String CURSOR = "images/cursor.png";

    //knight animations
    public static final String FOCUS_START = "maps/knight/move/FocusStart.png";
    public static final String FOCUS_CYCLE = "maps/knight/move/Focus.png";
    public static final String FOCUS_GET = "maps/knight/move/FocusGet.png";
    public static final String FOCUS_END = "maps/knight/move/FocusEnd.png";

    public static final String IDLE = "maps/knight/move/Idle.png";
    public static final String RUN = "maps/knight/move/Run.png";

    public static final String LOOK_UP = "maps/knight/move/LookUp.png";
    public static final String LOOK_DOWN = "maps/knight/move/LookDown.png";

    public static final String JUMP = "maps/knight/move/jump.png";
    public static final String DOUBLE_JUMP = "maps/knight/move/DoubleJump.png";
    public static final String FALL = "maps/knight/move/fall.png";
    public static final String LANDING = "maps/knight/move/Landing.png";

    public static final String SIDE_SLASH = "images/holloweffect/kick/SlashEffectAlt.png";
    public static final String UP_SLASH = "images/holloweffect/kick/UpSlashEffect.png";
    public static final String DOWN_SLASH = "images/holloweffect/kick/DownSlashEffect.png";

    public static final String DASH_EFFECT = "images/holloweffect/move/DashEffect.png";

    public static final String DASH = "maps/knight/move/Dash.png";
    public static final String DEATH = "maps/knight/move/Death.png";
    public static final String STANDUP = "maps/knight/move/StandUp.png";

    public static final String WALL_SLIDE = "maps/knight/move/WallSlide.png";
    public static final String WALL_JUMP = "maps/knight/move/Walljump.png";

    public static final String ATTACK = "maps/knight/move/SlashAlt.png";
    public static final String HIT = "maps/knight/move/Scream.png";

    // maps
    public static final String CROSS_ROADS_MAP = "maps/crossroad.tmx";
    public static final String BOSS_ROOM_MAP = "maps/bossfightroom.tmx";
    public static final String ZOTE_ROOM_MAP = "maps/zoteroom.tmx";
    public static final String GREEN_PATH_MAP = "maps/greenpath/greenpath.tmx";
    public static final String BREAK_WALL = "maps/crossparts/wall/breakablePath_001.png";
    public static final String SHAKE_WALL = "maps/crossparts/wall/brakeshake.png";


    // soul
    public static final String SOUL_ATLAS = "images/holloweffect/soul/Soulorb.atlas";
    public static final String SHELL_GROW = "images/holloweffect/soul/HealthBar.png";
    public static final String ORB_EYE = "images/holloweffect/soul/SoulOrb_Eye.png";
    public static final String FILLED_HEALTH = "images/holloweffect/soul/FilledHealth.png";
    public static final String BREAK_LIFE = "images/holloweffect/soul/BreakHealth.png";
    public static final String SHINE = "images/holloweffect/soul/FilledHealthShine.png";
    public static final String EMPTY_HEALTH = "images/holloweffect/soul/EmptyHealth.png";
    public static final String REFILL_HEALTH = "images/holloweffect/soul/HealthRefill.png";
    public static final String ORB_CIRCLE_MASK = "images/holloweffect/soul/SoulOrb_Empty.png";


    // enemy animations
    public static final String CRAWLID_WALK = "animations/crawlid/Walk.png";
    public static final String CRAWLID_TURN = "animations/crawlid/Turn.png";
    public static final String CRAWLID_DEATH_LAND = "animations/crawlid/DeathLand.png";
    public static final String CRAWLID_DEATH_AIR = "animations/crawlid/DeathAir.png";

    public static final String HUSK_ATTACK_ANTICIPATE = "animations/huskhornhead/AttackAnticipate.png";
    public static final String HUSK_ATTACK_LUNGE = "animations/huskhornhead/AttackLunge.png";
    public static final String HUSK_DEATH_LAND = "animations/huskhornhead/DeathLand.png";
    public static final String HUSK_DEATH_AIR = "animations/huskhornhead/DeathAir.png";
    public static final String HUSK_IDLE = "animations/huskhornhead/Idle.png";
    public static final String HUSK_WALK = "animations/huskhornhead/Walk.png";
    public static final String HUSK_TURN = "animations/huskhornhead/Turn.png";

    public static final String MOSSFLY_APPEAR = "animations/mossfly/Appear.png";
    public static final String MOSSFLY_DEATH_AIR = "animations/mossfly/DeathAir.png";
    public static final String MOSSFLY_DEATH_LAND = "animations/mossfly/DeathLand.png";
    public static final String MOSSFLY_FLY = "animations/mossfly/Fly.png";
    public static final String MOSSFLY_SHAKE = "animations/mossfly/Shake.png";
    public static final String MOSSFLY_TURN_TO_FLY = "animations/mossfly/TurnToFly.png";

    public static final String CRYSTALLIZED_DEATH_AIR = "animations/crystallized/DeathAir.png";
    public static final String CRYSTALLIZED_DEATH_LAND = "animations/crystallized/DeathLand.png";
    public static final String CRYSTALLIZED_EVADE = "animations/crystallized/Evade.png";
    public static final String CRYSTALLIZED_IDLE = "animations/crystallized/Idle.png";
    public static final String CRYSTALLIZED_RUN = "animations/crystallized/Run.png";
    public static final String CRYSTALLIZED_SHOOT = "animations/crystallized/shoot.png";
    public static final String CRYSTALLIZED_TURN = "animations/crystallized/Turn.png";

    public static final String FALSENIGHT_ATTACK = "animations/falsenight/Attack.png";
    public static final String FALSENIGHT_ATTACK_ANTIC = "animations/falsenight/AttackAntic.png";
    public static final String FALSENIGHT_ATTACK_RECOVER = "animations/falsenight/AttackRecover.png";
    public static final String FALSENIGHT_BODY = "animations/falsenight/Body.png";
    public static final String FALSENIGHT_DEATH_FALL = "animations/falsenight/DeathFall.png";
    public static final String FALSENIGHT_DEATH_HIT = "animations/falsenight/DeathHit.png";
    public static final String FALSENIGHT_DEATH_LAND = "animations/falsenight/DeathLand.png";
    public static final String FALSENIGHT_IDLE = "animations/falsenight/Idle.png";
    public static final String FALSENIGHT_JUMP = "animations/falsenight/Jump.png";
    public static final String FALSENIGHT_JUMP_ANTIC = "animations/falsenight/JumpAntic.png";
    public static final String FALSENIGHT_JUMP_ATTACK = "animations/falsenight/JumpAttack.png";
    public static final String FALSENIGHT_LAND = "animations/falsenight/Land.png";
    public static final String FALSENIGHT_RUN = "animations/falsenight/Run.png";
    public static final String FALSENIGHT_STUN_RECOVER = "animations/falsenight/StunRecover.png";

    public static final String ZOTE_ATTACK = "animations/zote/Attack.png";
    public static final String ZOTE_FALL = "animations/zote/Fall.png";
    public static final String ZOTE_GET_UP = "animations/zote/GetUp.png";
    public static final String ZOTE_IDLE = "animations/zote/Idle.png";
    public static final String ZOTE_ROLL = "animations/zote/Roll.png";
    public static final String ZOTE_TALK = "animations/zote/Talk.png";
    public static final String ZOTE_TURN = "animations/zote/Turn.png";


}
