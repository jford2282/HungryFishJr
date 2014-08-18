package com.hungryfishgame.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.hungryfishgame.manager.SceneManager;
import com.hungryfishgame.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{

	private static final int MENU_PLAY = 0;
	private static final Account account = null;
    private MenuScene menuChild;
    private Sprite menubackGround;

    @Override
    public void createScene() {
            createBackGround();
            createMenuChildScene();
           
    }

    private void createMenuChildScene() {
            menuChild = new MenuScene(camera);
            menuChild.setPosition(0, 0);
            final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourceManager.playbutton_region, vbom), 1.2f, 1);
            menuChild.addMenuItem(playMenuItem);
            menuChild.buildAnimations();
            menuChild.setBackgroundEnabled(true);
            SpriteBackground sBg = new SpriteBackground(menubackGround);
            menuChild.setBackground(sBg);
            playMenuItem.setPosition(playMenuItem.getX() + 0, playMenuItem.getY() + 0);
            menuChild.setOnMenuItemClickListener(this);
            setChildScene(menuChild);
           
    }

    private void createBackGround() {
            //this.setBackground(new Background(Color.BLACK));
            menubackGround = new Sprite(250, 400, 500, 900, resourceManager.menuBackgroundRegion, vbom);
    }

    @Override
    public void onBackKeyPressed() {
            System.exit(0);
           
    }

    @Override
    public SceneType getSceneType() {
            return SceneManager.SceneType.SCENE_MENU;
    }

    @Override
    public void disposeScene() {
            // TODO Auto-generated method stub
           
    }

    @Override
    public boolean onMenuItemClicked(
                    org.andengine.entity.scene.menu.MenuScene pMenuScene,
                    IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
            switch (pMenuItem.getID()) {
            case MENU_PLAY:
                    SceneManager.getInstance().loadGameScene(engine);
                    return true;
            default:
                    return false;
            }
    }

    @Override
    public void resume() {
            // TODO Auto-generated method stub
           
    }

    @Override
    public void pause() {
            // TODO Auto-generated method stub
           
    }

    public static String[] getAccounts(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        String[] names = new String[accounts.length];

        for (int i = 0; i < accounts.length; ++i) names[i] = accounts[i].name;

        return names;
    }
    
    public static String getToken(Activity activity, String serviceName) {
        try {
            Bundle result = AccountManager.get(activity).getAuthToken(account, serviceName, null, activity, null, null).getResult();

            return result.getString(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException e) {
            Log.d("Test", "Operation Canceled");
        } catch (AuthenticatorException e) {
            Log.d("Test", "Authenticator Exception");
        } catch (IOException e) {
            Log.d("Test", "Auth IOException");
        }

        return null;
}


}
