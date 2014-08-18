package com.hungryfishgame;

import java.util.Random;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.util.adt.pool.GenericPool;
 

import com.hungryfishgame.manager.ResourceManager;

public class CoralPillarFactory {
	
	private static final CoralPillarFactory INSTANCE = new CoralPillarFactory();
    
    private CoralPillarFactory() {  
    }
   
    GenericPool<CoralPillar> pool;

    int nextX;
    int nextY;
    int dy;
   
    int dx = 400;
   
    final int maxY = 550;
    final int minY = 350;

    private Random randomVal;
   

    public static final CoralPillarFactory getInstance() {
            return INSTANCE;
    }

    public void create(final PhysicsWorld physics) {
            reset();
            pool = new GenericPool<CoralPillar>(4) {
                           
                            @Override
                            protected CoralPillar onAllocatePoolItem() {
                                    CoralPillar p = new CoralPillar(0, 0,
                                                    ResourceManager.getInstance().pillarRegion,
                                                    ResourceManager.getInstance().pillarRegionUp,
                                                    ResourceManager.getInstance().vbom,
                                                    physics);
                                    return p;
                            }
                    };
           
    }
   
    public CoralPillar next() {
            CoralPillar p = pool.obtainPoolItem();
            p.setPosition(nextX, nextY);
           
            p.getScoreSensor().setTransform(nextX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                            nextY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
            
           
            p.getPillarUpBody().setTransform(nextX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                            (nextY + p.getPillarShift()) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
           
            p.getPillarDownBody().setTransform(nextX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                            (nextY - p.getPillarShift()) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);      
           
            p.getScoreSensor().setActive(true);
            p.getPillarUpBody().setActive(true);
            p.getPillarDownBody().setActive(true);
           
            randomVal=new Random();
           
            dx= randomVal.nextInt(465 - 345) + 345;                        
            nextX += dx;
           
            dy= randomVal.nextInt(440 - 280) + 280;
            if (dy > maxY || dy < minY)
            {
                    dy = maxY;
            }
            nextY = dy;
           
            return p;
    }
   
    public void recycle(CoralPillar p) {
            p.detachSelf();
            p.getScoreSensor().setActive(true);
            p.getPillarUpBody().setActive(false);
            p.getPillarDownBody().setActive(false);        
            p.getScoreSensor().setTransform(-1000, -1000, 0);
            p.getPillarUpBody().setTransform(-1000, -1000, 0);
            p.getPillarDownBody().setTransform(-1000, -1000, 0);
            pool.recyclePoolItem(p);
    }
   
    public void reset() {
            nextX = 1500;
            nextY = 350;
            dy = 50;
    }

}
