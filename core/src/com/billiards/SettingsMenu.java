package com.billiards;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Represents a settings menu, allows you to change various settings 
 * from fps, to control settings, music and can play Akashs music 
 * genesis project by clicking "Surround Sound"
 * @author Roy Y
 * @version 2022 May 23
 */
public class SettingsMenu implements Screen {
    private Billiards billiardsGame;
    private Stage stage;
    private Image background;
    private BitmapFont font = new BitmapFont();
    private TextButton hiGraphicsButton;
    private TextButton loGraphicsButton;
    private Button exitButton;
    private TextField fpsField;
    private TextButton soundOffButton;
    private TextButton soundOnButton;
    private boolean initialized = false;
    private TextButton mouseButton;
    private TextButton hoverButton;
    private TextButton flipOn;
    private TextButton flipOff;

    /**
     * Constructor that adds settings to the game and background
     * @param game main game for accessing methods
     * @param bg the background image
     */
    public SettingsMenu(Billiards game, Texture bg) { 
        billiardsGame = game;
        
        background = new Image(bg);
    }

    /**
     * Generates all of the styles, fonts, buttons which is added to a table.
     * It has ChangeListeners to add functionality to buttons when pressed
     */
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


        // Close button
        TextButtonStyle exitStyle = new TextButtonStyle();
        exitStyle.up = Billiards.getDrawable("arrowDarker.png");
        exitStyle.over = Billiards.getDrawable("arrowDarkerer.png");
        exitStyle.down = Billiards.getDrawable("arrowDarkesterest.png");
        exitButton = new Button(exitStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                billiardsGame.openLaunchMenu();
                initialized = false;
                buttonChange();
            }
        });
        exitButton.setBounds(10, Billiards.HEIGHT - 30, 20, 20);
        stage.addActor(exitButton);

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
        
        SelectBoxStyle selectStyle = new SelectBoxStyle();
        selectStyle.font = font;
        selectStyle.background = Billiards.getDrawable("blue.png");
        selectStyle.backgroundDisabled = Billiards.getDrawable("blueHover.png");
        selectStyle.backgroundOpen = Billiards.getDrawable("bluePress.png");

        // Graphics Preset
        Label presetLabel = new Label("  Graphics Preset", labelStyle);
        hiGraphicsButton = new TextButton("High", style);
        hiGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (hiGraphicsButton.isChecked() && initialized) {
                    loGraphicsButton.setChecked(false);
                    billiardsGame.setFPS(144);
                    fpsField.setText(billiardsGame.getFPS() + "");
                    buttonChange();
                }
            }
        });
        
        loGraphicsButton = new TextButton("Low", style);
        loGraphicsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (loGraphicsButton.isChecked() && initialized) {
                    hiGraphicsButton.setChecked(false);
                    billiardsGame.setFPS(30);
                    fpsField.setText(billiardsGame.getFPS() + "");
                    buttonChange();
                }
            }
        });
        Table presetTable = createTable2(loGraphicsButton, hiGraphicsButton);

        // Controls
        Label controlLabel = new Label("  Cue Control Style", labelStyle);
        mouseButton = new TextButton("Mouse Buttons Only", style);
        mouseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mouseButton.isChecked() && initialized) {
                    hoverButton.setChecked(false);
                    Billiards.altControl = false;
                    buttonChange();                    
                }
            }
        });

        hoverButton = new TextButton("Hover", style);
        hoverButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (hoverButton.isChecked() && initialized) {
                    mouseButton.setChecked(false);
                    Billiards.altControl = true;
                    buttonChange();
                }
            }
        });
        Table controlTable = createTable2(mouseButton, hoverButton);

        // Flip Pool Stick Direction 
        Label flipLabel = new Label("  Flip Cue Direction", labelStyle);
        flipOn = new TextButton("On", style);
        flipOn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flipOn.isChecked() && initialized) {
                    flipOff.setChecked(false);
                    Billiards.stickFlip = true;
                    buttonChange();
                }
            }
        });

        flipOff = new TextButton("Off", style);
        flipOff.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flipOff.isChecked() && initialized) {
                    flipOn.setChecked(false);
                    Billiards.stickFlip = false;
                    buttonChange();
                } 
            }
        });
        Table flipTable = createTable2(flipOff, flipOn);

        // Fps
        Label fpsLabel = new Label("  Frame Rate", labelStyle);
        fpsField = new TextField("", textStyle);
        fpsField.setAlignment(Align.center);
        fpsField.setTextFieldFilter(new TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }            
        });
        fpsField.setText(billiardsGame.getFPS() + "");

        fpsField.addListener(new FocusListener() {
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                super.keyboardFocusChanged(event, actor, focused);
                if (!focused) {
                    String txt = fpsField.getText();
                    if (!txt.isEmpty()) {
                        billiardsGame.setFPS(Integer.valueOf(txt));
                    }
                }
            }
          });
        
        // Music Sound
        Label musicLabel = new Label("  Sound", labelStyle);
        soundOffButton = new TextButton("Off", style);
        soundOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (soundOffButton.isChecked() && initialized) {
                    billiardsGame.playAkashSound();
                    soundOnButton.setChecked(false);
                    billiardsGame.getAkash().setPan(1f, 1f);
                    buttonChange();
                }
            }
        });

        soundOnButton = new TextButton("On", style);
        soundOnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (soundOnButton.isChecked() && initialized) {
                    billiardsGame.stopAkashSound();
                    soundOffButton.setChecked(false);
                    billiardsGame.getAkash().setPan(1f, 0.3f);
                    buttonChange();
                }
            }
        });
        Table musicTable = createTable2(soundOffButton, soundOnButton);
        
        Table table = new Table();
        table.add(presetLabel).size(400, 50);
        table.add(presetTable);
        table.row(); 
        table.add(musicLabel).size(400, 50);
        table.add(musicTable).size(400, 50).pad(2);
        table.setPosition(Billiards.WIDTH / 2 , Billiards.HEIGHT / 2); 
        table.row();
        table.add(fpsLabel).size(400, 50);
        table.add(fpsField).size(400, 50);
        table.row();
        table.add(controlLabel).size(400, 50);
        table.add(controlTable).size(400, 50).pad(2);
        table.row();
        table.add(flipLabel).size(400, 50);
        table.add(flipTable).size(400, 50);
        stage.addActor(table);

        // Initialize Default Settings
        hiGraphicsButton.setChecked(true);
        soundOnButton.setChecked(true);
        hoverButton.setChecked(Billiards.altControl);
        mouseButton.setChecked(!Billiards.altControl);
        flipOn.setChecked(Billiards.stickFlip);
        flipOff.setChecked(!Billiards.stickFlip);
        initialized = true;
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
     * Plays sound when button is clicked
     */
    public void buttonChange() {
        billiardsGame.playButtonClick();
        stage.unfocus(fpsField);
    }

    /**
     * Helper method for tables
     * @param left left side of button
     * @param right right side of button
     * @return output of the new table
     */
    private Table createTable2(TextButton left, TextButton right) {
        Table output = new Table();
        output.add(left).size(199, 50);
        output.add(right).size(199, 50).pad(0, 2, 0, 0);
        return output;
    }

    /**
     * Unused method
     * @param width width to resize
     * @param height height to resize
     */
    @Override
    public void resize(int width, int height) throws UnsupportedOperationException {}

    /**
     * Unused method
     */
    @Override
    public void pause() throws UnsupportedOperationException {}

    /**
     * Unused method
     */
    @Override
    public void resume() throws UnsupportedOperationException {}

    /**
     * Unused method
     */
    @Override
    public void hide() throws UnsupportedOperationException {}

    /**
     * Unused method
     */
    @Override
    public void dispose() throws UnsupportedOperationException {}
}