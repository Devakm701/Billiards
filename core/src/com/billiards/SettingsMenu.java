package com.billiards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
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
    private Slider fxVolume;
    private SelectBox<String> antiAliasing;
    private TextField fpsField;
    private TextButton soundOffButton;
    private TextButton soundOnButton;

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

        // Label Styler
        LabelStyle labelStyle = new LabelStyle(); 
        labelStyle.background = Billiards.getDrawable("blue.png");
        labelStyle.font = font;

        // Text Field Styler
        TextFieldStyle textStyle = new TextFieldStyle();
        textStyle.background = Billiards.getDrawable("blue.png");
        textStyle.font = font;
        textStyle.fontColor = Color.WHITE;
        textStyle.focusedBackground = Billiards.getDrawable("bluePress.png");

        // Button Styler
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture("blue.png"))); // up is when button released
        style.checked = new TextureRegionDrawable(new TextureRegion(new Texture("bluePress.png"))); // down is pressed
        style.over = new TextureRegionDrawable(new TextureRegion(new Texture("blueHover.png"))); 
        
        // Graphics Preset
        Label presetLabel = new Label("  Graphics Preset", labelStyle);
        hiGraphicsButton = new TextButton("High", style);
        hiGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // loGraphicsButton.setChecked(false);
                billiardsGame.playButtonClick();
                billiardsGame.setFPS(1000);
                billiardsGame.lastScreen();
            }
        });
        stage.addActor(hiGraphicsButton);
        // hiGraphicsButton.setBounds(Billiards.WIDTH * 0.2f, Billiards.HEIGHT - 70, 150, 50);
        loGraphicsButton = new TextButton("Low", style);
        loGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // hiGraphicsButton.setChecked(false);
                billiardsGame.playButtonClick();
                billiardsGame.setFPS(30);
                billiardsGame.openLaunchMenu();
            }
        });
        // loGraphicsButton.setBounds(Billiards.WIDTH * 0.4f, Billiards.HEIGHT - 70, 150, 50);
        stage.addActor(loGraphicsButton);
        Table presetTable = new Table();
        presetTable.add(hiGraphicsButton).size(199, 50); 
        presetTable.add(loGraphicsButton).size(199, 50).pad(0, 2, 0, 0);
        
        // Close button
        TextButtonStyle exitStyle = new TextButtonStyle();
        exitStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarker.png")));
        exitStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarkerer.png")));
        exitStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture("arrowDarkesterest.png")));
        exitButton = new Button(exitStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.playButtonClick();
                billiardsGame.openLaunchMenu();
            }
        });
        exitButton.setBounds(10, Billiards.HEIGHT - 30, 20, 20);
        stage.addActor(exitButton);

        // Volume Slider
        SliderStyle volumeStyle = new SliderStyle();
        volumeStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture("VolumeBar.png")));
        fxVolume = new Slider(0, 1.0f, 0.01f, false, new Skin(Gdx.files.internal("assets/ui-skins/holo/skin/dark-hdpi/Holo-dark-hdpi.json")));//volumeStyle);
        fxVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.setVolume(fxVolume.getVisualValue());
            }
        });
        fxVolume.setPosition(200, 200);
        fxVolume.setWidth(400);
        stage.addActor(fxVolume);

        // Anti Aliasing Drop Down
        // do stage.add or table.add
        // SelectBoxStyle aAStyle = new SelectBoxStyle();
        // aAStyle.font = font;
        // aAStyle.background = Billiards.getDrawable("blue.png");
        // aAStyle.background = Billiards.getDrawable("blue.png");
        antiAliasing = new SelectBox<String>(new Skin(Gdx.files.internal("assets/ui-skins/holo/skin/dark-hdpi/Holo-dark-hdpi.json")));  
        antiAliasing.setItems("2x", "8x", "16x"); 
        antiAliasing.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //Gdx.graphics.setBackBufferConfig(8,8,8,8,16,0,16); well fix this later
                System.out.println(antiAliasing.getSelection());
                billiardsGame.playButtonClick();
            }       
        });
        stage.addActor(antiAliasing); // need to figure that out // will do that later
        antiAliasing.setPosition(200, 400);

        // Controls
        Label controlLabel = new Label("Control Buttons", labelStyle);

        // Fps
        Label fpsLabel = new Label("Fps", labelStyle);
        fpsField = new TextField("", textStyle);
        fpsField.setText("144");
        fpsField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.setFPS(Integer.valueOf(fpsField.getText()));
            }
        });
        
        // Music Sound
        Label musicLabel = new Label("  Surround Sound", labelStyle);
        soundOffButton = new TextButton("Off", style);
        soundOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.stopAkashSound();
                soundOffButton.setChecked(false);
                billiardsGame.getAkash().setPan(1f, 1f); //yeah
                billiardsGame.playButtonClick();
            }
        });

        soundOnButton = new TextButton("On", style);
        soundOnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.playAkashSound();
                soundOnButton.setChecked(false);
                billiardsGame.getAkash().setPan(1f, 0.1f); //yeah
                billiardsGame.playButtonClick();
            }
        });
        Table musicTable = new Table();
        musicTable.add(soundOnButton).size(199, 50); 
        musicTable.add(soundOffButton).size(199, 50).pad(0, 2, 0, 0);

        // UI Clicking
        // just make sound for every button noe true
        // Table  no what other settings can we add // Anti Aliasing, 
                 //can change between 2x, 8x, and 16x use drop down menu for this, I made alternate control 
                 // scheme on pool stick so we can modify that, maybe make a separate slider for background music
                 // LOL
        
        Table table = new Table();
        table.add(presetLabel).size(400, 50);
        table.add(presetTable);
        table.row(); // oh no
        //table.add(fpsLabel).size(400, 50);
        //table.row();
        table.add(musicLabel).size(400, 50);
        table.add(musicTable).size(400, 50).pad(2);
        table.setPosition(Billiards.WIDTH / 2 , Billiards.HEIGHT / 2); 
        table.row();
        table.add(fpsLabel).size(400, 50);
        table.add(fpsField).size(400, 50);
        stage.addActor(table);
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