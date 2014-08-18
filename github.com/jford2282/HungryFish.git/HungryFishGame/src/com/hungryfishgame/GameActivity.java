package com.hungryfishgame;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
 

import com.hungryfishgame.FollowCamera;


import com.hungryfishgame.manager.ResourceManager;
import com.hungryfishgame.manager.SceneManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

public class GameActivity extends BaseGameActivity {
	 
    public static final float CAMERA_WIDTH = 480;
    public static final float CAMERA_HEIGHT = 800;
    private FollowCamera camera;
   
    SharedPreferences prefs;
   
    public void setHighScore(int score) {
    SharedPreferences.Editor settingsEditor = prefs.edit();
    settingsEditor.putInt(Constants.KEY_HIGHSCORE, score);
    settingsEditor.commit();
    }
   
    public int getHighScore() {
            return prefs.getInt(Constants.KEY_HIGHSCORE, 0);
    }      

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions)
    {
            return new LimitedFPSEngine(pEngineOptions, 60);
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
        }
        return false;
    }
   
    @Override
    public EngineOptions onCreateEngineOptions() {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            camera = new FollowCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
            EngineOptions engineOption = new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(), camera);
            engineOption.getAudioOptions().setNeedsMusic(true);
            engineOption.getAudioOptions().setNeedsSound(true);
            engineOption.getRenderOptions().getConfigChooserOptions().setRequestedMultiSampling(true);
            engineOption.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
            return engineOption;
    }

    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
            ResourceManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
            pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
                    throws IOException {
            SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene,
                    OnPopulateSceneCallback pOnPopulateSceneCallback)
                    throws IOException {
            mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
                   
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                            mEngine.unregisterUpdateHandler(pTimerHandler);
                            SceneManager.getInstance().createMenuScene();
                    }
            }));
            pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
   
    @Override
    public synchronized void onResumeGame() {
            super.onResumeGame();
            if (SceneManager.getInstance().getCurrentScene() == SceneManager.getInstance().gameScene)
            {
                    SceneManager.getInstance().gameScene.resume();
            }
    }

    @Override
    public synchronized void onPauseGame() {
            super.onPauseGame();
            SceneManager.getInstance().gameScene.pause();
    }
//  @Override
//  protected void onDestroy()
//  {
//          super.onDestroy();
//          System.exit(0);
//  }

}
