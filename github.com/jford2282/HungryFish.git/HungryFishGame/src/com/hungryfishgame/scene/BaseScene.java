package com.hungryfishgame.scene;

import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
 


import com.hungryfishgame.GameActivity;
import com.hungryfishgame.FollowCamera;
import com.hungryfishgame.manager.ResourceManager;
import com.hungryfishgame.manager.SceneManager.SceneType;

public abstract class BaseScene extends Scene{
	
	// Variables
    protected Engine engine;
    protected GameActivity activity;
    protected FollowCamera camera;
    protected VertexBufferObjectManager vbom;
    protected ResourceManager resourceManager;
   
    // Constructors
    public BaseScene() {
            this.resourceManager = ResourceManager.getInstance();
            this.activity = resourceManager.activity;
            this.camera = resourceManager.camera;
            this.vbom= resourceManager.vbom;
            this.engine = resourceManager.engine;
            createScene();
    }
   
    // Abstraction
    public abstract void createScene();
    public abstract void onBackKeyPressed();
    public abstract SceneType getSceneType();
    public abstract void disposeScene();
    public abstract void resume();
    public abstract void pause();

}
