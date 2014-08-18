package com.hungryfishgame;

import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
 
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CoralPillar extends Entity {
	
	private Sprite pillarUp;
    private Sprite pillarDown;
    private Sprite fishfood;
   
    private Body pillarUpBody;
    private Body pillarDownBody;
    private Body scoreSensor;
    
   
    public static final int CW = 480;
    public static final int CH = 800;
    public static final int FPS_LIMIT = 60;
   
   
    public final static String BODY_WALL = "wall";
    public final static String BODY_ACTOR = "actor";
    public final static String BODY_SENSOR = "sensor";
   
    public static final FixtureDef FISH_FIXTURE = PhysicsFactory.createFixtureDef(1f, 0f, 1f, false);
    public static final FixtureDef WALL_FIXTURE = PhysicsFactory.createFixtureDef(1f, 0f, 1f, false);
    public static final FixtureDef CEILLING_FIXTURE = PhysicsFactory.createFixtureDef(1f, 0f, 0f, false);
    public static final FixtureDef SENSOR_FIXTURE = PhysicsFactory.createFixtureDef(1f, 0f, 1f, true);
   
    public static final float SPEED_X  = 11;
    public static final float GRAVITY = -23;
    public static final float SPEED_Y = 10;
   
    public static final String KEY_HIGHSCORE = "High Score";
    static float shift = 88;
   
    public CoralPillar(float x, float y, TextureRegion reg, TextureRegion regUp, VertexBufferObjectManager vbom, PhysicsWorld physics) {
            super(x, y);
            //float shift = 80;
            Random ar=new Random();
            shift= ar.nextInt(110 - 80) + 80;
            pillarUp = new Sprite(0, shift, 110, 600, regUp, vbom);
           
            //pillarUp.setFlippedVertical(true);
            pillarUp.setAnchorCenterY(0);
            attachChild(pillarUp);
           
            pillarUpBody = PhysicsFactory.createBoxBody(physics, pillarUp, BodyType.StaticBody, WALL_FIXTURE);
            pillarUpBody.setUserData("wall");
           
            pillarDown = new Sprite(0, -shift, 110, 600, reg, vbom);
            pillarDown.setAnchorCenterY(1);
            attachChild(pillarDown);       

            pillarDownBody = PhysicsFactory.createBoxBody(physics, pillarDown, BodyType.StaticBody, WALL_FIXTURE);
            pillarDownBody.setUserData("wall");
                   

            pillarDownBody = PhysicsFactory.createBoxBody(physics, pillarDown, BodyType.StaticBody, WALL_FIXTURE);
            pillarDownBody.setUserData("wall");
            
            Rectangle r = new Rectangle(0, 0, 1, 9999, vbom); // just to make sure it's big
            r.setColor(Color.GREEN);
            r.setAlpha(0f);
            attachChild(r);
            
            
            scoreSensor = PhysicsFactory.createBoxBody(physics, r, BodyType.StaticBody, SENSOR_FIXTURE);
            scoreSensor.setUserData("sensor");
            
            

    }

    @Override
    public boolean collidesWith(IEntity pOtherEntity) {
            return pillarUp.collidesWith(pOtherEntity) || pillarDown.collidesWith(pOtherEntity);
    }

    public Body getPillarUpBody() {
            return pillarUpBody;
    }

    public Body getPillarDownBody() {
            return pillarDownBody;
    }

    public Body getScoreSensor() {
            return scoreSensor;
    }
    
   
    public float getPillarShift() {
            return pillarUp.getHeight() / 2 + shift;
    }

    @Override
    public float getWidth() {
            return pillarDown.getWidth();
    }
	

}
