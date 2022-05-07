package com.billiards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class LaunchMenu implements Screen {

    Game billiardsGame;

    // https://www.youtube.com/watch?v=67ZCQt8QpNA useful tutorial to familiarize yourself with screen interface
    // https://stackoverflow.com/questions/21488311/how-to-create-a-button-in-libgdx good idea on how to implement the buttons

    public LaunchMenu(Game game) {
        super();
        billiardsGame = game;
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    /**
     * method to exit out of the menu
     */
    private void closeMenu() {
        billiardsGame.setScreen(null);
    }
    
}
