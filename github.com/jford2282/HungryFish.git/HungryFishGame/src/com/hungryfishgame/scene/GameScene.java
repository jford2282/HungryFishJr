package com.hungryfishgame.scene;

import java.util.Iterator;
import java.util.LinkedList;
 

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
 

import com.hungryfishgame.GameActivity;
import com.hungryfishgame.manager.SceneManager;
import com.hungryfishgame.manager.SceneManager.SceneType;
import com.hungryfishgame.CoralPillar;
import com.hungryfishgame.CoralPillarFactory;
 
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameScene extends BaseScene implements IOnSceneTouchListener, ContactListener {
	
	private static final int TIME_TO_RESSURECTION = 200;
    private HUD gameHUD;
    private Text scoreText;
    private Text tapToPlayText;
    private Text highScoreText;
    private Text yourScoreText;
    private PhysicsWorld physicWorld;
    LinkedList<CoralPillar> pillars = new LinkedList<CoralPillar>();
    private TiledSprite fish;
    protected Body fishBody;
    private boolean scored;
    private int score;
   
    enum STATE {
            NEW, PAUSED, PLAY, DEAD, AFTERLIFE;
    }
   
    long timestamp = 0;
    private STATE state = STATE.NEW;
    private STATE lastState;
    protected boolean musicIsOn = true;
   
   
    @Override
    public void createScene() {
            //ResourceManager.getInstance().loadGameResources();
            resourceManager.gameMusic.setVolume(0.2f);
            resourceManager.gameMusic.setLooping(true);
            resourceManager.gameMusic.play();
           
            createPhysics();
            CoralPillarFactory.getInstance().create(physicWorld);
            createBackground();    
            createHUD();
            createBounds();
            createActor();
            resourceManager.camera.setChaseEntity(fish);         
            setOnSceneTouchListener(this);
            try {
                    activity.getHighScore();
            } catch (Exception e) {
                    activity.setHighScore(0);
            }
    }

    private void createPhysics() {
            physicWorld = new PhysicsWorld(new Vector2(0, 0), true);
            physicWorld.setContactListener(this);
            //registerUpdateHandler(physicWorld);                  
    }
   
    private void createBounds() {
            float bigNumber = 999999; // i dunno, just a big number
            resourceManager.parallaxFrontLayerRegion.setTextureWidth(bigNumber);
            Sprite ground = new Sprite(0, -150, resourceManager.parallaxFrontLayerRegion, vbom);
            ground.setAnchorCenter(0, 0);
            ground.setZIndex(10);
            attachChild(ground);
           
            Body groundBody = PhysicsFactory.createBoxBody(
                            physicWorld, ground, BodyType.StaticBody, CoralPillar.WALL_FIXTURE);
            groundBody.setUserData(CoralPillar.BODY_WALL);
           
            // just to limit the movement at the top
            @SuppressWarnings("unused")
            Body ceillingBody = PhysicsFactory.createBoxBody(
                            physicWorld, bigNumber / 2, 820, bigNumber, 20, BodyType.StaticBody, CoralPillar.CEILLING_FIXTURE);
    }
   
    private void createActor() {
            //player = new Player(200, 400, resourceManager.playerRegion, vbom, camera, physicWorld) {
            fish = new TiledSprite(200, 400, 90, 90 , resourceManager.playerRegion, vbom);
            fish.setZIndex(999);
            fish.registerUpdateHandler(new IUpdateHandler() {

                    @Override
                    public void onUpdate(float pSecondsElapsed) {
                            if (fishBody.getLinearVelocity().y > -0.01) {
                                    fish.setCurrentTileIndex(1);
                            } else {
                                    fish.setCurrentTileIndex(0);
                            }
                    }

                    @Override
                    public void reset() { }
            });
            fishBody = PhysicsFactory.createCircleBody(
                            physicWorld, fish, BodyType.DynamicBody, CoralPillar.FISH_FIXTURE);
            fishBody.setFixedRotation(true);
            fishBody.setUserData(CoralPillar.BODY_ACTOR);
            physicWorld.registerPhysicsConnector(new PhysicsConnector(fish, fishBody));
            attachChild(fish);
    }
   
    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
            super.onManagedUpdate(pSecondsElapsed);
//          pb.setParallaxValue(res.camera.getCenterX());
            if (scored) {
                    addPillar();
                    sortChildren();
                    scored = false;
                    score++;
                    scoreText.setText("Score: " + String.valueOf(score));
            }
           
            // if first pillar is out of the screen, delete it
            if (!pillars.isEmpty()) {
                    CoralPillar fp = pillars.getFirst();
                    if (fp.getX() + fp.getWidth() < resourceManager.camera.getXMin()) {
                            CoralPillarFactory.getInstance().recycle(fp);
                            pillars.remove();
                    }
            }
           
            if (state == STATE.DEAD && timestamp + TIME_TO_RESSURECTION < System.currentTimeMillis()) {
                    state = STATE.AFTERLIFE;
            }
    }

    private void createHUD() {             
      
           
            gameHUD = new HUD();
            scoreText = new Text(20, 30, resourceManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
            scoreText.setAnchorCenter(0, 0);
            scoreText.setText("Score: 0");
           
            tapToPlayText = new Text(GameActivity.CAMERA_WIDTH/2 - 150, GameActivity.CAMERA_HEIGHT/2, resourceManager.font, "TAP TO PLAY", new TextOptions(HorizontalAlign.LEFT), vbom);
            tapToPlayText.setAnchorCenter(0, 0);
            tapToPlayText.setText("TAP TO PLAY");
           
            yourScoreText = new Text(GameActivity.CAMERA_WIDTH/2 - 150, GameActivity.CAMERA_HEIGHT/2 + 50, resourceManager.font, "Your Score: ", new TextOptions(HorizontalAlign.LEFT), vbom);
            yourScoreText.setAnchorCenter(0, 0);
            yourScoreText.setText("Your Score: 0");
           
            highScoreText = new Text(GameActivity.CAMERA_WIDTH/2 - 150, GameActivity.CAMERA_HEIGHT/2 + 0, resourceManager.font, "High Score: ", new TextOptions(HorizontalAlign.LEFT), vbom);
            highScoreText.setAnchorCenter(0, 0);
            highScoreText.setText("High Score: 0");        
           
            gameHUD.attachChild(tapToPlayText);
            gameHUD.attachChild(scoreText);
            gameHUD.attachChild(highScoreText);
            gameHUD.attachChild(yourScoreText);
            camera.setHUD(gameHUD);
            highScoreText.setVisible(false);
            yourScoreText.setVisible(false);
    }

    private void createBackground() {
            final ParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0f,0f,5f,60);
            this.setBackground(autoParallaxBackground);
           
            final Sprite parallaxBackLayerSprite = new Sprite(0, 0, resourceManager.parallaxBackLayerRegion, vbom);
            parallaxBackLayerSprite.setOffsetCenter(0, 0);
            autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, parallaxBackLayerSprite));
//         
//          final Sprite parallaxMidLayerSprite = new Sprite(100,400,resourceManager.parallaxMidLayerRegion, vbom);
//          parallaxMidLayerSprite.setOffsetCenter(0, 0);
//          autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5f, parallaxMidLayerSprite));
//         
            this.setBackground(autoParallaxBackground);
            this.setBackgroundEnabled(true);               
    }
   
    @Override
    public void reset() {
            super.reset();
            physicWorld.setGravity(new Vector2(0, 0));
           
            Iterator<CoralPillar> pi = pillars.iterator();
            while (pi.hasNext()) {
               CoralPillar p = pi.next();
               CoralPillarFactory.getInstance().recycle(p);
               pi.remove();
            }
                           
            CoralPillarFactory.getInstance().reset();
           
            fishBody.setTransform(200 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                            400 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
           
            addPillar();
            addPillar();
            addPillar();
           
            score = 0;
           
            tapToPlayText.setText("TAP TO PLAY");
            tapToPlayText.setVisible(true);
           
            //resourceManager.activity.setHighScore(0);
            yourScoreText.setText("Your Score: 0");
            yourScoreText.setVisible(false);
            highScoreText.setText("High Score: " + activity.getHighScore());
            highScoreText.setVisible(false);
           
            sortChildren();

            unregisterUpdateHandler(physicWorld);
            physicWorld.onUpdate(0);
            if (musicIsOn == true)
            {
                    resourceManager.gameMusic.play();
            }
            state = STATE.NEW;
    }
   
    private void addPillar() {
            CoralPillar p = CoralPillarFactory.getInstance().next();
            pillars.add(p);
            attachIfNotAttached(p);
    }
   
    private void attachIfNotAttached(CoralPillar p) {
            if (!p.hasParent()) {
                    attachChild(p);
            }
    }

    @Override
    public void onBackKeyPressed() {
            gameHUD.setVisible(false);
            resourceManager.gameMusic.pause();
            camera.clearUpdateHandlers();
            SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType() {
            return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene() {
            // TODO Auto-generated method stub
           
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
            if (pSceneTouchEvent.isActionDown()) {
                    if (state == STATE.PAUSED) {
                            if (lastState != STATE.NEW) {
                                    registerUpdateHandler(physicWorld);
                            }
                            state = lastState;
                            Debug.d("->" + state);
                    } else if (state == STATE.NEW) {
                            reset();
                            registerUpdateHandler(physicWorld);
                            state = STATE.PLAY;
                            Debug.d("->PLAY");
                            physicWorld.setGravity(new Vector2(0, CoralPillar.GRAVITY));
                            fishBody.setLinearVelocity(new Vector2(CoralPillar.SPEED_X, 0));
                            scoreText.setText("Score: 0");
                            tapToPlayText.setVisible(false);
                            yourScoreText.setVisible(false);
                            highScoreText.setVisible(false);
                           
                    } else if (state == STATE.DEAD) {
//                          // don't touch the dead!
                    } else if (state == STATE.AFTERLIFE) {
                            reset();
                            state = STATE.NEW;
                            Debug.d("->NEW");                              
                    }
                    else {
                            resourceManager.jumpSound.play();
                            Vector2 v = fishBody.getLinearVelocity();
                            v.x = CoralPillar.SPEED_X;
                            v.y = CoralPillar.SPEED_Y;
                            fishBody.setLinearVelocity(v);
                    }
            }
            return false;
    }

    @Override
    public void beginContact(Contact contact) {
            if (CoralPillar.BODY_WALL.equals(contact.getFixtureA().getBody().getUserData()) ||
                            CoralPillar.BODY_WALL.equals(contact.getFixtureB().getBody().getUserData())) {
                    state = STATE.DEAD;
                    if (score > activity.getHighScore()) {
                            activity.setHighScore(score);
                    }
                    timestamp = System.currentTimeMillis();
                    // play sound die
                    resourceManager.dieSound.play();
                    fishBody.setLinearVelocity(0, 0);
                    for (CoralPillar p : pillars) {
                            p.getPillarUpBody().setActive(false);
                            p.getPillarDownBody().setActive(false);
                    }
                   
                    yourScoreText.setText("Your Score: " + score);
                    yourScoreText.setColor(Color.GREEN);
                    yourScoreText.setVisible(true);
                   
                    highScoreText.setText("High Score: " + activity.getHighScore());
                    highScoreText.setColor(Color.RED);
                    highScoreText.setVisible(true);
                    resourceManager.gameMusic.pause();
            }
           
    }

    @Override
    public void endContact(Contact contact) {
            if (CoralPillar.BODY_SENSOR.equals(contact.getFixtureA().getBody().getUserData()) ||
                            CoralPillar.BODY_SENSOR.equals(contact.getFixtureB().getBody().getUserData())) {
                    resourceManager.scoreSound.play();
                    scored = true;
            }
           
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
            // TODO Auto-generated method stub
           
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
            // TODO Auto-generated method stub
           
    }
   
    public void resume() {
            if (musicIsOn == true)
            {
                    resourceManager.gameMusic.play();
            }
    }

    public void pause() {
            resourceManager.gameMusic.pause();           
    }      

}
