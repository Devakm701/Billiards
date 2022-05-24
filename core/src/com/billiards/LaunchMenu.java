package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Represents the launch menu of the game. Prompts the user with play game, settings menu, and exit game options
 * @author Roy Y
 * @version 2022 May 23
 */
public class LaunchMenu implements Screen {

    private Stage stage;
    private Billiards billiardsGame;
    private TextButton settingsButton;
    private TextButton exitButton;
    private TextButton launchButton;
    private BitmapFont font;
    private Image background;
    private Image title;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParam;

    /**
     * Constructor that adds settings to the game and background
     * @param game main game for accessing methods
     * @param bg the background image
     */
    public LaunchMenu(Billiards game, Texture bg) {
        super();
        billiardsGame = game;
        background = new Image(bg);
    }

    /**
     * Generates all of the styles, fonts, buttons which is added to a table.
     * It has ChangeListeners to add functionality to buttons when pressed
     */
    @Override
    public void show() {
        // Create stage to draw and create background 
        stage = new Stage();
        title = new Image(new Texture("PoolLogo.png"));
        stage.addActor(background);
        background.setPosition(0f, 0f);
        stage.addActor(title);
        title.setPosition(Billiards.WIDTH / 2 - title.getWidth() / 2, Billiards.HEIGHT * 0.45f);
        Gdx.input.setInputProcessor(stage);
        
        // Change font
        font = new BitmapFont();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Comic_Sans_MS.ttf")); // Oh no 
        fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 16;
        fontParam.borderWidth = 1;
        fontParam.borderColor = Color.BLACK;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose(); // idk if this makes difference but someone said theirs worked when they commented out 
        
        // Button Styler
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture("blue.png"))); // up is when button released
        style.down = new TextureRegionDrawable(new TextureRegion(new Texture("blueHover.png"))); // down is pressed
        style.over = new TextureRegionDrawable(new TextureRegion(new Texture("bluePress.png"))); 
        
        // Launch Button
        launchButton = new TextButton("Launch Game", style);
        launchButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.closeMenu();
                billiardsGame.playButtonClick();
            }
        });
        
        // Settings Button
        settingsButton = new TextButton("Settings", style);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.openSettings();
                billiardsGame.playButtonClick();
            }
        });
        

        // Exit Button
        exitButton = new TextButton("Exit Game", style);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
                billiardsGame.playButtonClick();
            } 
        });

        // Table 
        Table table = new Table();
        table.add(launchButton).size(250, 50).pad(10);
        table.row();
        table.add(settingsButton).size(250, 50).pad(10);
        table.row();
        table.add(exitButton).size(250, 50).pad(10);
        stage.addActor(table); 
        table.setPosition(Billiards.WIDTH / 2 - table.getWidth(), Billiards.HEIGHT * 0.25f); // 
        // table.setDebug(true); // uncomment to enable debug lines
    }

    /**
     * Called when the screen should render itself
     * @param delta seconds since last render
     */
    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Unused method
     * @param width width to resize
     * @param height height to resize
     */
    @Override
    public void resize(int width, int height) throws UnsupportedOperationException {
        // Unused method
    }

    /**
     * Unused method
     */
    @Override
    public void pause() throws UnsupportedOperationException {
        // Unused method
    }
    
    /**
     * Unused method
     */
    @Override
    public void resume() throws UnsupportedOperationException {
        // Unused method
    }

    /**
     * Unused method
     */
    @Override
    public void hide() throws UnsupportedOperationException {
        // Unused method
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

}
