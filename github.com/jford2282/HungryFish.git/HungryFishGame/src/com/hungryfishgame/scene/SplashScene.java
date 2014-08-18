package com.hungryfishgame.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;
 


import com.hungryfishgame.GameActivity;
import com.hungryfishgame.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {
	
	Sprite splash;
    

    @Override
    public void createScene() {
            
           
            this.getBackground().setColor(Color.BLACK);
           
            splash = new Sprite(0, 0, resourceManager.splash_region, vbom)
    {
            @Override
        protected void preDraw(GLState pGLState, Camera pCamera)
            {
            super.preDraw(pGLState, pCamera);
            pGLState.enableDither();
        }
    };
    splash.setScale(1.5f);
    splash.setPosition(GameActivity.CAMERA_WIDTH/2, GameActivity.CAMERA_HEIGHT/2);
    attachChild(splash);
   
            
    }

    @Override
    public void onBackKeyPressed() {
            // TODO Auto-generated method stub
           
    }

    @Override
    public SceneType getSceneType() {
            return SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene() {
            splash.detachSelf();
            splash.dispose();
            this.detachSelf();
            this.dispose();
           
    }

    @Override
    public void resume() {
            // TODO Auto-generated method stub
           
    }

    @Override
    public void pause() {
            // TODO Auto-generated method stub
           
    }

}
