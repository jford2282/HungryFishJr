package com.hungryfishgame.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
 

import com.hungryfishgame.scene.BaseScene;
import com.hungryfishgame.scene.GameScene;
import com.hungryfishgame.scene.LoadingScene;
import com.hungryfishgame.scene.MainMenuScene;
import com.hungryfishgame.scene.SplashScene;

public class SceneManager {

	public BaseScene splashScene;
    public BaseScene menuScene;
    public BaseScene loadingScene;
    public BaseScene gameScene;
   
    private static final SceneManager INSTANCE = new SceneManager();
   
    private BaseScene currentScene;
    private Engine engine = ResourceManager.getInstance().engine;
   
   
    public enum SceneType{
            SCENE_SPLASH,
            SCENE_MENU,
            SCENE_LOADING,
            SCENE_GAME
    }
   
    public static SceneManager getInstance() {
            return INSTANCE;
    }
   
    public void setScene(BaseScene baseScene) {
            engine.setScene(baseScene);
            currentScene = baseScene;
            baseScene.getSceneType();
    }
   
    public void setScene(SceneType sceneType) {
            switch (sceneType) {
            case SCENE_SPLASH:
                    setScene(splashScene);
                    break;
            case SCENE_MENU:
                    setScene (menuScene);
            case SCENE_LOADING:
                    setScene(loadingScene);
            case SCENE_GAME:
                    setScene(gameScene);
            default:
                    break;
            }
    }
   
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
            ResourceManager.getInstance().loadSplashResources();
            splashScene = new SplashScene();
            currentScene = splashScene;
            pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
   
    public void disposeSplashScene() {
            ResourceManager.getInstance().unloadSplashScreen();
            splashScene.disposeScene();
            splashScene = null;
    }

    public void createMenuScene() {
            ResourceManager.getInstance().loadMenuResources();
            menuScene = new MainMenuScene();
            loadingScene = new LoadingScene();
            SceneManager.getInstance().setScene(menuScene);
            disposeSplashScene();
    }
   
    public BaseScene getCurrentScene() {
            return currentScene;
    }

    public void loadGameScene(final Engine mEngine) {
            setScene(loadingScene);
            ResourceManager.getInstance().unloadMenuTexture();
            mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
                   
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                            mEngine.unregisterUpdateHandler(pTimerHandler);
                            ResourceManager.getInstance().loadGameResources();
                            gameScene = new GameScene();
                            setScene(gameScene);
                    }
            }));
           
    }

    public void loadMenuScene(final Engine mEngine) {
            setScene(loadingScene);
            gameScene.disposeScene();
            ResourceManager.getInstance().unloadGameTextures();
            mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
            {
        public void onTimePassed(final TimerHandler pTimerHandler)
        {
            mEngine.unregisterUpdateHandler(pTimerHandler);
            ResourceManager.getInstance().loadMenuTextures();
                    setScene(menuScene);
        }
            }));
           
    }
}
