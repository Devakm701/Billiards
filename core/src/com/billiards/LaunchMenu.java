package com.billiards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LaunchMenu implements Screen {

    private Stage stage;
    private Game billiardsGame;
    private TextButton settingsButton;
    private TextButton exitButton;
    private TextButton launchButton;
    private BitmapFont font;
    private Image background;
    private Image title;
    private TextureAtlas buttonAtlas;

    // private Texture playButtonOn;
    // private Texture playButtonOff;
    // private Texture exitButtonOn;
    // private Texture exitButtonOff;

    // https://www.youtube.com/watch?v=67ZCQt8QpNA useful tutorial to familiarize
    // yourself with screen interface
    // https://stackoverflow.com/questions/21488311/how-to-create-a-button-in-libgdx
    // good idea on how to implement the buttons
    // its not necessary to implement all of these because we wont be using all of
    // these methods.
    // https://i.pinimg.com/originals/88/bf/2a/88bf2afe538d6109aba8410fda6fa2e7.jpg
    // sex with baby penguins ??????????????????
    // https://external-preview.redd.it/v1xGvjMe8Ssq92nPj_Az48S7_FZMPcxKMWjVfQY6A1w.gif?format=png8&s=daa9cd4989638e74f24803fc0ca3f7caba5955eb
    // good reference links xD LOL
    // send me a file and ping if you want to upload texture
    // idk if you can add using live share
    public LaunchMenu(Game game, Texture bg) {
        super();
        billiardsGame = game;
        background = new Image(bg);
    }

    @Override
    public void show() {
        TextButtonStyle style = new TextButtonStyle();
        stage = new Stage();
        stage.addActor(background);
        background.setPosition(0f, 0f);
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont(); // stage is not drawing for some reaason
        
        // Button Styler
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture("blue.png"))); // up is when button released
        style.down = new TextureRegionDrawable(new TextureRegion(new Texture("blueHover.png"))); // down is pressed
        style.over = new TextureRegionDrawable(new TextureRegion(new Texture("bluePress.png"))); 
        
        // Launch Button
        launchButton = new TextButton("Launch Game", style);
        launchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                closeMenu();
            }
        });
        
        // Settings Button
        settingsButton = new TextButton("Settings", style);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("hello"); // change when settings UI is implemented
            }
        });
        

        // Exit Button
        exitButton = new TextButton("Exit Game", style);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            } 
        });

        // Game Title
        // title // should we make a logo with the title included
        // Table 
        Table table = new Table();
        table.add(launchButton).size(250, 50).pad(10);
        table.row();
        table.add(settingsButton).size(250, 50).pad(10);
        table.row();
        table.add(exitButton).size(250, 50).pad(10);
        stage.addActor(table); 
        table.setPosition(Billiards.WIDTH / 2 - table.getWidth(), Billiards.HEIGHT / 4); // 
        // table.setDebug(true); // debug lines
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Unused method
    }

    @Override
    public void pause() {
        // Unused method
    }

    @Override
    public void resume() {
        // Unused method
    }

    @Override
    public void hide() {
        // Unused method
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * method to exit out of the menu
     */
    private void closeMenu() {
        billiardsGame.setScreen(null);
    }
}
