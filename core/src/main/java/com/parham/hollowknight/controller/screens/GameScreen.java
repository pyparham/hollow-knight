package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.AssetLoader;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.*;
import com.parham.hollowknight.model.BreakableWall;
import com.parham.hollowknight.model.entities.*;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyType;
import com.parham.hollowknight.model.enums.KnightState;
import com.parham.hollowknight.model.enums.WallState;
import com.parham.hollowknight.view.*;

import java.util.*;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class GameScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private final List<Rectangle> platforms = new ArrayList<>();
    private final List<Spike> spikes = new ArrayList<>();
    private final List<DoorTrigger> doors = new ArrayList<>();
    private final Map<String, Vector2> spawnPoints = new HashMap<>();
    private final List<BreakableWall> breakableWalls = new ArrayList<>();
    private final List<Rectangle> bossDoors = new ArrayList<>();


    private Knight knight;
    private PlayerInputProcessor inputProcessor;
    private KnightAnimationManager animManager;
    private EnemyAnimationManager crawlidAnimManager;
    private EnemyAnimationManager huskAnimManager;
    private EnemyAnimationManager mossflyAnimManager;
    private BossAnimationManager bossAnimationManager;
    private ShockWaveAnimationManager shockWaveAnimManager;


    private final List<Enemy> enemies = new ArrayList<>();
    private final CombatManager combat = new CombatManager();
    private SpellManager spellManager = new SpellManager();
    private SpellAnimationManager spellAnimManager;
    private DeathController deathHandler;

    private HudRenderer hud;

    private Vector2 startSpawn = new Vector2();
    BackGroundDust menuDust = new BackGroundDust();
    private PauseMenu pauseMenu;

    private boolean isInitialized = false;

    private float mapWidthWorld;
    private float mapHeightWorld;
    private float shakeTimer = 0f;
    private float shakeIntensity = 0f;

    private float timeAccumulator = 0f;

    private float fadeAlpha = 0f;
    private int fadeState = 0;
    private final float FADE_SPEED = 3f;

    private String pendingMap = "";
    private String pendingSpawn = "";

    private String checkpointMap = "crossroads";
    private String checkpointSpawn = "startGame_spawn";

    private float bgR = 0.04f;
    private float bgG = 0.07f;
    private float bgB = 0.15f;

    private Zote zote;
    private ZoteDialogueManager zoteDialogueManager = new ZoteDialogueManager();
    private ZoteVoiceManager zoteVoiceManager;
    private ZoteAnimationManager zoteAnimManager;
    private DialogueBox dialogueBox;


    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public Stage getStage() {
        return stage;
    }


    @Override
    public void show() {
        if (!isInitialized) {
            AudioManager.getInstance().switchMusicWithFade(game.assets.crossMusic);

            camera = new OrthographicCamera();
            camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

            viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
            stage = new Stage(viewport, game.batch);

            initialGameSetup();
            pauseMenu = new PauseMenu(game, this);

            hud = new HudRenderer(
                game.assets.shellGrow,
                game.assets.soulOrbAtlas,
                game.assets.orbEye,
                game.assets.orbCircleMask,
                game.assets.filledHealth,
                game.assets.emptyHealth,
                game.assets.shine,
                game.assets.refillHealth,
                game.assets.breakLife
            );
            hud.playIntro();

        }
        restoreGameInput();


        if (pauseMenu.isVisible())
            Gdx.input.setInputProcessor(pauseMenu.getStage());

        isInitialized = true;

    }


    private void initialGameSetup() {

        if (game.currentGameData.currentRoomId != null) {
            checkpointMap = game.currentGameData.currentRoomId;
            checkpointSpawn = "startGame_spawn";
        }
        tiledMap = getMapByName(checkpointMap);
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        parseMapData(tiledMap);
        applyMapTheme(checkpointMap);

        Vector2 spawn = spawnPoints.get(checkpointSpawn);
        if (spawn == null) spawn = spawnPoints.get("startGame_spawn");
        if (spawn != null) startSpawn.set(spawn);

        knight = new Knight(this, startSpawn.x, startSpawn.y);
        knight.lastSafePosition.set(startSpawn);

        inputProcessor = new PlayerInputProcessor(knight);
        deathHandler = new DeathController(game, this, knight);

        animManager = new KnightAnimationManager(
            game.assets.knightIdle, 9,
            game.assets.knightRun, 13,
            game.assets.knightJump, 9,
            game.assets.knightDoubleJump, 8,
            game.assets.knightFall, 3,
            game.assets.knightLanding, 4,
            game.assets.knightDash, 12,
            game.assets.knightAttack, 5,
            game.assets.focusStart, 3,
            game.assets.focusLoop, 4,
            game.assets.focusGet, 6,
            game.assets.focusEnd, 3,
            game.assets.knightHit, 7,
            game.assets.knightDeath, 18,
            game.assets.sideSlash, 6,
            game.assets.UpSlash, 6,
            game.assets.DownSlash, 6,
            game.assets.lookUp, 6,
            game.assets.lookDown, 6,
            game.assets.dashEffect, 8,
            game.assets.wallSlide, 4,
            game.assets.wallJump, 9
        );

        crawlidAnimManager = new EnemyAnimationManager(
            game.assets.crawlidWalk, 4, 0.1f,
            null, 0, 0f,
            game.assets.crawlidTurn, 2, 0.075f,
            null, 0, 0f,
            null, 0, 0f,
            null, 0, 0f,
            game.assets.crawlidDeadAir, 3, 0.1f,
            game.assets.crawlidDeadLand, 2, 0.07f
        );

        huskAnimManager = new EnemyAnimationManager(
            game.assets.huskWalk, 7, 0.08f,
            game.assets.huskIdle, 6, 0.08f,
            game.assets.huskTurn, 2, 0.075f,
            game.assets.huskAttackLunge, 12, 0.1f,
            game.assets.huskAttackAnticipate, 5, 0.1f,
            null, 0, 0f,
            game.assets.huskDeathAir, 1, 0.1f,
            game.assets.huskDeathLand, 8, 0.1f
        );

        mossflyAnimManager = new EnemyAnimationManager(
            game.assets.mossflyFly, 4, 0.08f,
            game.assets.mossflyShake, 3, 0.08f,
            game.assets.mossflyTurnToFly, 3, 0.075f,
            game.assets.mossflyFly, 4, 0.08f,
            game.assets.mossflyAppear, 6, 0.08f,
            game.assets.mossflyFly, 4, 0.08f,
            game.assets.mossflyDeathAir, 4, 0.1f,
            game.assets.mossflyDeathLand, 2, 0.1f
        );

        spellAnimManager = new SpellAnimationManager(game.assets.soulBall, 4, game.assets.blast, 6);

        bossAnimationManager = new BossAnimationManager(
            game.assets.falsenightIdle, 5,
            game.assets.falsenightRunAntic, 2,
            game.assets.falsenightRun, 5,
            game.assets.falsenightAttackAntic, 6,
            game.assets.falsenightAttack, 3,
            game.assets.falsenightAttackRecover, 5,
            game.assets.falsenightJumpAntic, 3,
            game.assets.falsenightJump, 4,
            game.assets.falsenightJumpAttack, 8,
            game.assets.falsenightLand, 5,
            game.assets.falsenightBody, 5,
            game.assets.falsenightStunRecover, 6,
            game.assets.falsenightDeathFall, 3,
            game.assets.falsenightDeathHit, 3,
            game.assets.falsenightDeathLand, 11
        );

        zoteAnimManager = new ZoteAnimationManager(
            game.assets.zoteIdle, 5,
            game.assets.zoteTalk, 5,
            game.assets.zoteAttack, 4,
            game.assets.zoteTurn, 2
        );
        dialogueBox = new DialogueBox(game.assets.listFont);

        zoteVoiceManager = new ZoteVoiceManager(Arrays.asList(
            game.assets.zote1, game.assets.zote2,
            game.assets.zote3, game.assets.zote4,
            game.assets.zote6
        ));

        shockWaveAnimManager = new ShockWaveAnimationManager(game.assets.shockwave, 7);

    }


    private void parseMapData(TiledMap map) {
        platforms.clear();
        spikes.clear();
        doors.clear();
        enemies.clear();
        spawnPoints.clear();
        bossDoors.clear();

        zote = null;

        int mapHeightInTiles = map.getProperties().get("height", Integer.class);
        int mapWidthInTiles = map.getProperties().get("width", Integer.class);
        int tileHeightPx = map.getProperties().get("tileheight", Integer.class);
        int tileWidthPx = map.getProperties().get("tilewidth", Integer.class);

        mapHeightWorld = mapHeightInTiles * tileHeightPx;
        mapWidthWorld = mapWidthInTiles * tileWidthPx;

        MapLayer platformLayer = map.getLayers().get("platform");
        if (platformLayer != null) {
            for (MapObject obj : platformLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                platforms.add(new Rectangle(r.x, r.y, r.width, r.height));
            }
        }

        MapLayer spikesLayer = map.getLayers().get("spikes");
        if (spikesLayer != null) {
            for (MapObject obj : spikesLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                boolean isOnGround = obj.getProperties().get("isOnGround", false, Boolean.class);
                spikes.add(new Spike(rect, isOnGround));
            }
        }

        MapLayer doorLayer = map.getLayers().get("doors");
        if (doorLayer != null) {
            for (MapObject obj : doorLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();

                String targetMap = obj.getProperties().get("targetMap", String.class);
                String targetSpawn = obj.getProperties().get("targetSpawn", String.class);

                if (targetMap != null && targetSpawn != null) {
                    doors.add(new DoorTrigger(rect, targetMap, targetSpawn));
                }
            }
        }

        MapLayer spawnLayer = map.getLayers().get("spawn");
        if (spawnLayer != null) {
            for (MapObject obj : spawnLayer.getObjects()) {
                String name = obj.getName() != null ? obj.getName() : "";
                float worldX = 0f;
                float worldY = 0f;
                Vector2 pointer = ((PointMapObject) obj).getPoint();
                worldX = pointer.x;
                worldY = pointer.y;

                spawnPoints.put(name, new Vector2(worldX, worldY));

                if (name.equals("startGame_spawn")) startSpawn.set(worldX, worldY);

            }
        }

        MapLayer bossDoorLayer = map.getLayers().get("bossDoor");
        if (bossDoorLayer != null && game.currentGameData != null && !game.currentGameData.isFalseKnightDefeated) {
            for (MapObject obj : bossDoorLayer.getObjects()) {
                if (!(obj instanceof RectangleMapObject)) continue;
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                bossDoors.add(rect);
                platforms.add(rect);
            }
        }

        MapLayer enemyLayer = map.getLayers().get("enemySpawn");
        if (enemyLayer != null) {
            for (MapObject obj : enemyLayer.getObjects()) {
                float worldX = 0f;
                float worldY = 0f;

                Vector2 pointer = ((PointMapObject) obj).getPoint();
                worldX = pointer.x;
                worldY = pointer.y;

                String enemyType = obj.getProperties().get("type", String.class);

                if (enemyType != null) {
                    if (enemyType.equalsIgnoreCase("crawlid")) {
                        float distLeft = getFloat(obj, "leftRange", 150f);
                        float distRight = getFloat(obj, "rightRange", 150f);
                        float wLeft = worldX - distLeft;
                        float wRight = worldX + distRight;
                        enemies.add(new Crawlid(this, worldX, worldY, wLeft, wRight));
                    } else if (enemyType.equalsIgnoreCase("huskhorn")) {
                        float distLeft = getFloat(obj, "leftRange", 150f);
                        float distRight = getFloat(obj, "rightRange", 150f);
                        float wLeft = worldX - distLeft;
                        float wRight = worldX + distRight;
                        enemies.add(new HuskHornhead(this, worldX, worldY, wLeft, wRight));
                    } else if (enemyType.equalsIgnoreCase("mossfly")) {
                        float distLeft = getFloat(obj, "leftRange", 150f);
                        float distRight = getFloat(obj, "rightRange", 150f);
                        float wLeft = worldX - distLeft;
                        float wRight = worldX + distRight;
                        enemies.add(new Mossfly(this, worldX, worldY, wLeft, wRight));
                    } else if (enemyType.equalsIgnoreCase("boss")) {
                        float distLeft = getFloat(obj, "leftRange", 150f);
                        float distRight = getFloat(obj, "rightRange", 150f);
                        float wLeft = worldX - distLeft;
                        float wRight = worldX + distRight;
                        FalseKnight falseKnight = new FalseKnight(this, worldX, worldY, wLeft, wRight);
                        if (game.currentGameData != null && game.currentGameData.isFalseKnightDefeated)
                            falseKnight.setCorpse();
                        enemies.add(falseKnight);
                    }
                }
            }
        }

        MapLayer zoteLayer = map.getLayers().get("zote");
        if (zoteLayer != null) {
            for (MapObject obj : zoteLayer.getObjects()) {
                if (!(obj instanceof PointMapObject)) continue;

                Vector2 pointer = ((PointMapObject) obj).getPoint();
                zote = new Zote(this, pointer.x, pointer.y);
                enemies.add(zote);
            }
        }

        MapLayer wallLayer = map.getLayers().get("breakable");
        if (wallLayer != null) {
            boolean wallDestroyed = game.currentGameData.isSecretWallDestroyed;

            for (MapObject obj : wallLayer.getObjects()) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                Rectangle rect = new Rectangle(r.x, r.y, r.width, r.height);
                String targetLayer = obj.getProperties().get("targetLayer", String.class);

                if (wallDestroyed) {
                    if (targetLayer != null) {
                        MapLayer secretLayer = map.getLayers().get(targetLayer);
                        if (secretLayer != null) secretLayer.setVisible(false);
                    }
                } else {
                    int hp = getInt(obj, "hp", 3);
                    breakableWalls.add(new BreakableWall(rect, hp, targetLayer, new TextureRegion(game.assets.wallIdleFrame),
                        BreakableWall.buildAnim(game.assets.wallShakeAnim, 2, 0.1f, Animation.PlayMode.LOOP)));
                    platforms.add(rect);
                }
            }
        }
    }


    @Override
    public void render(float delta) {
        if (delta > 0.05f) delta = 0.03f;

        checkPause();

        Gdx.gl.glClearColor(bgR, bgG, bgB, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!pauseMenu.isVisible() && !deathHandler.isActive()) {
            knight.update(delta, platforms, spikes);
            for (Enemy enemy : enemies)
                enemy.update(delta, platforms, knight.position, spikes);
            combat.update(knight, enemies);

            if (knight.currentState != KnightState.DEAD) {
                for (Enemy enemy : enemies) {
                    if (enemy.getType() == EnemyType.FALSE_KNIGHT) {
                        FalseKnight fk = (FalseKnight) enemy;
                        for (ShockWave sw : fk.activeShockWaves) {
                            if (knight.hitbox.overlaps(sw.hitbox) && !knight.isInvincible()) {
                                boolean hitFromRight = sw.position.x > knight.position.x;
                                knight.takeHit(1, hitFromRight);
                                knight.needsRespawn = true;
                                knight.vY = 800f;
                                sw.isDestroyed = true;
                            }
                        }
                    }
                }
            }

            if (game.currentGameData != null && game.currentGameData.isFalseKnightDefeated && !bossDoors.isEmpty()) {
                platforms.removeAll(bossDoors);
                bossDoors.clear();
                triggerScreenShake(0.5f, 5f);
            }

            if (knight.wantsCast) {
                if (spellManager.tryCastVengefulSpirit(knight))
                    triggerScreenShake(0.1f, 4f);
                knight.wantsCast = false;
            }
            spellManager.update(delta, platforms, enemies);

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                if (zote != null && !dialogueBox.isVisible()) {
                    if (zote.canInteract(knight.position, 250f)) {
                        zoteDialogueManager.beginInteraction(zote);
                        dialogueBox.show(zoteDialogueManager.getCurrentLine());
                        zoteVoiceManager.playRandomVoice();
                    }
                }
            }
            float dist = 0f;
            try {
                dist = Math.abs(knight.position.x - zote.position.x);
            } catch (Exception e) {
            }
            if ((Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || dist > 250f) && dialogueBox.isVisible()) {
                if (dialogueBox.handleAdvanceInput()) {
                    boolean hasMore = zoteDialogueManager.advance(zote);
                    if (hasMore) {
                        dialogueBox.show(zoteDialogueManager.getCurrentLine());
                        zoteVoiceManager.playRandomVoice();
                    } else {
                        dialogueBox.hide();
                    }
                }
            }

            for (DoorTrigger door : doors) {
                if (knight.hitbox.overlaps(door.rect)) {
                    changeMap(door.targetMapName, door.targetSpawnName);
                    break;
                }
            }
            if (game.currentGameData != null) {
                timeAccumulator += delta;
                if (timeAccumulator >= 1f) {
                    game.currentGameData.playTimeSeconds += (long) timeAccumulator;
                    timeAccumulator -= (long) timeAccumulator;
                }
            }
        }

        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);
        game.batch.end();

        deathHandler.update(delta);
        animManager.update(delta, knight);
        if (zote != null) zoteAnimManager.update(delta);


        float targetX = knight.position.x + Knight.WIDTH / 2f;
        float targetY = knight.position.y + 150;

        camera.position.x = MathUtils.lerp(camera.position.x, targetX, 4f * delta);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, 4f * delta);

        float minX = camera.viewportWidth / 2f;
        float maxX = mapWidthWorld - (camera.viewportWidth / 2f);
        float minY = camera.viewportHeight / 2f;
        float maxY = mapHeightWorld - (camera.viewportHeight / 2f);

        if (maxX < minX) maxX = minX;
        if (maxY < minY) maxY = minY;

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);

        if (shakeTimer > 0) {
            shakeTimer -= delta;
            float offsetX = MathUtils.random(-shakeIntensity, shakeIntensity);
            float offsetY = MathUtils.random(-shakeIntensity, shakeIntensity);
            camera.position.add(offsetX, offsetY, 0);

            if (shakeTimer <= 0) shakeTimer = 0f;
        }

        camera.update();


        if (game.batch.isDrawing()) game.batch.end();
        mapRenderer.setView(camera);
        mapRenderer.render(new int[]{0});
        game.globalShapeRenderer.setProjectionMatrix(camera.combined);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);

        for (Enemy enemy : enemies) {
            if (enemy.getType() == EnemyType.CRAWLID)
                crawlidAnimManager.draw(game.batch, enemy);

            if (enemy.getType() == EnemyType.HUSK_HORNHEAD)
                huskAnimManager.draw(game.batch, enemy);

            if (enemy.getType() == EnemyType.MOSSFLY)
                mossflyAnimManager.draw(game.batch, enemy);

            if (enemy.getType() == EnemyType.FALSE_KNIGHT) {
                bossAnimationManager.update(delta, (FalseKnight) enemy);
                bossAnimationManager.draw(game.batch, (FalseKnight) enemy);

                for (ShockWave sw : ((FalseKnight) enemy).activeShockWaves)
                    shockWaveAnimManager.draw(game.batch, sw);

            }

            if (enemy.getType() == EnemyType.ZOTE)
                zoteAnimManager.draw(game.batch, (Zote) enemy);
        }


        for (BreakableWall wall : breakableWalls)
            wall.draw(game.batch);
        for (Rectangle doorRect : bossDoors)
            game.batch.draw(game.assets.bossDoor, doorRect.x - 30, doorRect.y - 160, doorRect.width + 60, doorRect.height + 200);


        for (VengefulSpirit spell : spellManager.getActiveSpells())
            spellAnimManager.draw(game.batch, spell);
        animManager.draw(game.batch, knight);


        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // box

//        game.globalShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//
//        game.globalShapeRenderer.setColor(1, 0, 0, 1);
//        game.globalShapeRenderer.rect(
//            knight.hitbox.x, knight.hitbox.y,
//            knight.hitbox.width, knight.hitbox.height);
//
//        game.globalShapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        int totalLayers = tiledMap.getLayers().getCount();

        int[] allLayersIndices = new int[totalLayers];
        for (int i = 0; i < totalLayers; i++) allLayersIndices[i] = i;

        mapRenderer.render(allLayersIndices);

//        game.globalShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        game.globalShapeRenderer.setColor(0, 1, 0, 1);
//        for (Rectangle p : platforms)
//            game.globalShapeRenderer.rect(p.x, p.y, p.width, p.height);
//        game.globalShapeRenderer.setColor(0, 0, 1, 1);
//        for (Spike spike : spikes)
//            game.globalShapeRenderer.rect(spike.hitbox.x, spike.hitbox.y, spike.hitbox.width, spike.hitbox.height);
//
//        game.globalShapeRenderer.setColor(1, 1, 0, 1);
//        for (DoorTrigger door : doors)
//            game.globalShapeRenderer.rect(door.rect.x, door.rect.y, door.rect.width, door.rect.height);
//
//        game.globalShapeRenderer.setColor(0, 0, 1, 1);
//
//        for (Enemy enemy : enemies) {
//            if (enemy.getBodyHitbox() != null) {
//                game.globalShapeRenderer.rect(
//                    enemy.getBodyHitbox().x,
//                    enemy.getBodyHitbox().y,
//                    enemy.getBodyHitbox().width,
//                    enemy.getBodyHitbox().height
//                );
//            }
//            if (enemy.getAttackHitbox() != null) {
//                game.globalShapeRenderer.setColor(1, 0, 0, 1);
//                game.globalShapeRenderer.rect(
//                    enemy.getAttackHitbox().x,
//                    enemy.getAttackHitbox().y,
//                    enemy.getAttackHitbox().width,
//                    enemy.getAttackHitbox().height
//                );
//            }
//            if (enemy instanceof FalseKnight) {
//                FalseKnight fk = (FalseKnight) enemy;
//                for (ShockWave shockWave : fk.activeShockWaves) {
//                    game.globalShapeRenderer.setColor(1, 0, 1, 1);
//                    game.globalShapeRenderer.rect(
//                        shockWave.hitbox.x,
//                        shockWave.hitbox.y,
//                        shockWave.hitbox.width,
//                        shockWave.hitbox.height
//                    );
//                }
//            }
//        }
//
//        game.globalShapeRenderer.setColor(1, 0, 1, 1);
//
//        if (knight.getNailHitbox() != null) {
//            game.globalShapeRenderer.rect(
//                knight.getNailHitbox().x,
//                knight.getNailHitbox().y,
//                knight.getNailHitbox().width,
//                knight.getNailHitbox().height
//            );
//        }
//
//
//        game.globalShapeRenderer.end();

        stage.act(delta);
        stage.draw();

        pauseMenu.update(delta);
        pauseMenu.render(game.globalShapeRenderer);

        hud.notifySoulChanged(knight.soul);
        hud.notifyHealthChanged(knight.currentHealth, knight.maxHealth);
        hud.render(delta);

        if (dialogueBox.isVisible()) {
            dialogueBox.update(delta);
            game.batch.setProjectionMatrix(stage.getCamera().combined);
            game.batch.begin();

            float boxWidth = 800f;
            float boxHeight = 200f;
            float boxX = (viewport.getWorldWidth() - boxWidth) / 2f;
            float boxY = 80f;

            dialogueBox.draw(game.batch, boxX, boxY, boxWidth, boxHeight);
            game.batch.end();
        }
        if (fadeState == 1) {
            fadeAlpha += delta * FADE_SPEED;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                performMapChange(pendingMap, pendingSpawn);
                fadeState = 2;
            }

        } else if (fadeState == 2) {
            fadeAlpha -= delta * FADE_SPEED;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                fadeState = 0;
            }

            knight.currentState = KnightState.IDLE;
            knight.vX = 0;
            knight.vY = 0;

        }

        Iterator<BreakableWall> breakableWalls = this.breakableWalls.iterator();
        while (breakableWalls.hasNext()) {
            BreakableWall wall = breakableWalls.next();
            wall.update(delta);

            if (knight.isCurrentlyAttacking() && knight.getNailHitbox() != null) {
                if (wall.hitbox.overlaps(knight.getNailHitbox())) {
                    if (wall.takeDamage()) {
                        knight.applyNailRecoil();
                        triggerScreenShake(0.15f, 4f);
                        AudioManager.getInstance().playSound(game.assets.breakableHitSound);
                    }
                }
            }

            MapLayer secretLayer = tiledMap.getLayers().get(wall.targetLayerName);
            if (wall.state == WallState.BREAKING && !wall.physicsRemoved) {
                platforms.remove(wall.hitbox);

                if (secretLayer != null) secretLayer.setVisible(false);

                wall.physicsRemoved = true;
                AudioManager.getInstance().playSound(game.assets.breakableDeathSound);
                game.currentGameData.isSecretWallDestroyed = true;

            } else if (secretLayer != null && !isWallBreaked()) secretLayer.setVisible(true);
        }

        if (fadeAlpha > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            game.globalShapeRenderer.setProjectionMatrix(camera.combined);
            game.globalShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.globalShapeRenderer.setColor(0, 0, 0, fadeAlpha);

            game.globalShapeRenderer.rect(
                camera.position.x - camera.viewportWidth / 2f,
                camera.position.y - camera.viewportHeight / 2f,
                camera.viewportWidth, camera.viewportHeight
            );

            game.globalShapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

    }

    private float getFloat(MapObject object, String key, float def) {
        Object v = object.getProperties().get(key);
        return v instanceof Float ? (Float) v
            : v instanceof Integer ? ((Integer) v).floatValue()
            : def;
    }

    private int getInt(MapObject object, String key, int def) {
        Object v = object.getProperties().get(key);
        return v instanceof Integer ? (Integer) v : def;
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        if (pauseMenu != null) pauseMenu.resize(w, h);
        hud.resize(w, h);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    public PlayerInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public void showPauseMenu() {
        pauseMenu.show();
    }

    private void checkPause() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            setPauseMenuVisible(!pauseMenu.isVisible());
    }

    public void setPauseMenuVisible(boolean visible) {
        if (this.pauseMenu == null) return;

        if (visible) {
            this.pauseMenu.show();
            Gdx.input.setInputProcessor(pauseMenu.getStage());
        } else {
            this.pauseMenu.hide();
            restoreGameInput();
        }
    }

    public void restoreGameInput() {
        InputMultiplexer mux = new InputMultiplexer();
        mux.addProcessor(stage);
        mux.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(mux);
    }

    public void changeMap(String targetMapName, String targetSpawnName) {
        if (fadeState != 0) return;
        this.pendingMap = targetMapName;
        this.pendingSpawn = targetSpawnName;
        this.fadeState = 1;
    }

    private void performMapChange(String targetMapName, String targetSpawnName) {
        SaveManager.getInstance().save(game.currentGameData);
        TiledMap nextMap = getMapByName(targetMapName);
        applyMapTheme(targetMapName);

        boolean isSafeRoom = targetMapName.equals("crossroads") || targetMapName.equals("greenPath");
        if (game.currentGameData != null && isSafeRoom) {
            game.currentGameData.currentRoomId = targetMapName;
            game.currentGameData.lastSpawnName = targetSpawnName;
            game.currentGameData.areaName = targetMapName;
        } else if (knight.currentState == KnightState.DEAD) {
            if (!targetMapName.equals("greenPath")) targetMapName = "crossroads";
        }

        this.checkpointMap = targetMapName;
        this.checkpointSpawn = targetSpawnName;

        tiledMap = nextMap;
        mapRenderer.setMap(tiledMap);
        parseMapData(tiledMap);

        Vector2 newSpawn = spawnPoints.get(targetSpawnName);
        if (newSpawn != null) {
            knight.position.set(newSpawn.x, newSpawn.y);
            knight.lastSafePosition.set(newSpawn.x, newSpawn.y);
        }


        knight.currentState = KnightState.IDLE;
        knight.vX = 0;
        knight.vY = 0;

    }

    private TiledMap getMapByName(String mapName) {
        if (mapName.equals("bossRoom")) return game.assets.bossfightRoom;
        if (mapName.equals("zoteRoom")) return game.assets.zootRoom;
        if (mapName.equals("greenPath")) return game.assets.greenPath;
        return game.assets.crossroadsMap;
    }

    private void applyMapTheme(String mapName) {
        if (mapName.equals("greenPath")) {
            bgR = 0.02f;
            bgG = 0.06f;
            bgB = 0.02f;
            menuDust.setDustColor(new Color(0.8f, 0.9f, 0.4f, 1f));
            if (game.assets.greenPathMusic != AudioManager.getInstance().getCurrentMusic())
                AudioManager.getInstance().switchMusicWithFade(game.assets.greenPathMusic);
        } else {
            bgR = 0.04f;
            bgG = 0.07f;
            bgB = 0.15f;
            menuDust.setDustColor(Color.WHITE);
            if (mapName.equals("bossRoom")) {
                if (game.assets.bossfightMusic != AudioManager.getInstance().getCurrentMusic())
                    AudioManager.getInstance().switchMusicWithFade(game.assets.bossfightMusic);
            } else if (game.assets.crossMusic != AudioManager.getInstance().getCurrentMusic())
                AudioManager.getInstance().switchMusicWithFade(game.assets.crossMusic);
        }
    }

    public void respawnAtCheckpoint() {
        checkpointSpawn = "startGame_spawn";
        if (knight.currentState == KnightState.DEAD)
            checkpointMap = checkpointMap.equals("greenPath") ? "greenPath" : "crossroads";
        performMapChange(checkpointMap, checkpointSpawn);
        knight.currentHealth = knight.maxHealth;
        knight.soul = 0;
    }

    public void triggerScreenShake(float duration, float intensity) {
        this.shakeTimer = duration;
        this.shakeIntensity = intensity;
    }


    private static class DoorTrigger {
        Rectangle rect;
        String targetMapName;
        String targetSpawnName;

        public DoorTrigger(Rectangle rect, String targetMapName, String targetSpawnName) {
            this.rect = rect;
            this.targetMapName = targetMapName;
            this.targetSpawnName = targetSpawnName;
        }
    }

    public Knight getKnight() {
        return knight;
    }

    public boolean isWallBreaked() {
        if (game.currentGameData != null)
            return game.currentGameData.isSecretWallDestroyed;
        return false;
    }


    public AssetLoader getGameAssets() {
        return game.assets;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Main getGame() {
        return game;
    }

}
