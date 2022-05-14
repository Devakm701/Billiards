package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SettingsMenu implements Screen {
    private Preferences settings = Gdx.app.getPreferences("8 Ball Pool");
    private Billiards billiardsGame;
    private Stage stage;
    private Image background;
    private BitmapFont font = new BitmapFont();
    private TextButton hiGraphicsButton;
    private TextButton loGraphicsButton;
    private Button exitButton;

    public SettingsMenu(Billiards game, Texture bg) { 
        billiardsGame = game;
        
        background = new Image(bg);
    }

    @Override
    public void show() {
        stage = new Stage();
        stage.addActor(background);
        background.setPosition(0f, 0f);
        Gdx.input.setInputProcessor(stage);
        
        // Initialize Font
        font = new BitmapFont();
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Comic_Sans_MS.ttf")); // Oh no 
        FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 16;
        fontParam.borderWidth = 1;
        fontParam.borderColor = Color.BLACK;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose(); 

        // Table 
        Table table = new Table();

        // Button Styler
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture("blue.png"))); // up is when button released
        style.checked = new TextureRegionDrawable(new TextureRegion(new Texture("bluePress.png"))); // down is pressed
        style.over = new TextureRegionDrawable(new TextureRegion(new Texture("blueHover.png"))); 
        
        // High Graphics
        hiGraphicsButton = new TextButton("High", style);
        hiGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loGraphicsButton.setChecked(false);
                Gdx.graphics.setForegroundFPS(144);
            }
        });
        stage.addActor(hiGraphicsButton);
        hiGraphicsButton.setBounds(Billiards.WIDTH * 0.2f, Billiards.HEIGHT - 70, 150, 50);

        // Low Graphics
        loGraphicsButton = new TextButton("Low", style);
        loGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hiGraphicsButton.setChecked(false);
                Gdx.graphics.setForegroundFPS(30);
            }
        });
        loGraphicsButton.setBounds(Billiards.WIDTH * 0.4f, Billiards.HEIGHT - 70, 150, 50);
        stage.addActor(loGraphicsButton);

        // Close button
        TextButtonStyle exitStyle = new TextButtonStyle();
        exitStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarker.png")));
        exitStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarkerer.png")));
        exitStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarkesterest.png")));
        exitButton = new Button(exitStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.openLaunchMenu();
            }
        });
        exitButton.setBounds(10, Billiards.HEIGHT - 30, 20, 20);
        stage.addActor(exitButton);

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
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
    
}