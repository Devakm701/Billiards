package com.billiards;

import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;


/**
 * This class is the Backbone of the entire application. It is a hybrid GUI and manager class. 
 * It instantiates the necessary amount of all the other classes. It holds the loop that renders
 * the game and updates the physics world. It opens a new screen when necessary and can play specific sounds.
 * Serves as a bridge for all of the other classes. Extends the application adapter class for the library.
 * @author Devak M
 * @author Collaborators: Roy Y, Jeffrey L
 * @version 2022 May 23
 * 
 */
public class Billiards extends Game {
    public static final int WIDTH = 900;
    public static final int HEIGHT = 600;
    public static final float PHYSICS_DT = 1/144f;
    public static final Vector2 TABLE_CENTER = new Vector2(450,250);
    public static boolean altControl = true;
    public static boolean stickFlip = true;
    private float volume = 1f;
    private int FPS = 144;
    private long lastTime;
    public static boolean turnUpdated = false;
    public boolean noBallsMoving = true;
    private static ShapeRenderer drawShape;
    private SpriteBatch batch;
    private LaunchMenu launchMenu;
    private SettingsMenu settingsMenu;
    private Texture table, background, tableMask, person, arrow, mover;
    private PoolStick stick;
    private LinkedList<Ball> balls; 
    private Ball cueBall;
    private World world; 
    private Sound cushionSound;
    private Sound ballSound;
    private Sound cueSound;
    private Sound buttonClickSound;
    private Sound pocketSound;
    private Music akashProject; 
    private Screen lastScreen = launchMenu;
    private Stage stage;
    private Button settingsButton;
    private LinkedList<Ball> ballsOut;
    private Environment environment;
    private OrthographicCamera camera;
    private ModelBatch modelBatch;
    private GameProcessor game;
    private BitmapFont font;
    private boolean isOver = false;
    private  boolean debugLines = false;
    private final boolean drawPrediction = true;
    private LinkedList<Ball> ballsPotted;
    private HashMap<String, Texture> ballIcons;
    private Label winLabel;
    private Vector2[] tableEdges;


    /**
     * Circle array that represents the holes/pots that the balls can go into
     */
    public static Circle[] holes = {
        new Circle(150, 404, 17),
        new Circle(749, 404, 17),
        new Circle(150, 96, 17),
        new Circle(749, 96, 17),
        new Circle(450, 410, 15),
        new Circle(450, 90, 15)
    };

    
    
    /**
     * Creates and initializes every aspect of the game. Initializes the Launch Menu and the Settings Menus, Textures, Sprites, etc.
     */
    @Override
    public void create () {
        // Menu Initialization
        background = new Texture("dimmerBackground.png");
        launchMenu = new LaunchMenu(this, background);
        settingsMenu = new SettingsMenu(this, background);
        this.setScreen(launchMenu); // enable launch menu 
        this.getScreen().show();
        // System.out.println(circleLineIntersect(new Vector2(1,1), new Vector2(-4,1), new Circle(-2,-2,1)));

        // Game and Texture initializtion
        batch = new SpriteBatch();
        table = new Texture("stolenTableCropped.png");
        stick = new PoolStick(new Texture("pool stick.png"), 450f , 300f, this);
        tableMask = new Texture("TableMask.png");
        mover = new Texture("mover.png");
        game = new GameProcessor(this);
        font = new BitmapFont();
        arrow = new Texture("turnArrow.png");
        person = new Texture("default.jpg");
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf")); 
        FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = 40;
        fontParam.borderWidth = 1;
        fontParam.borderColor = Color.BLACK;
        fontParam.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParam);
        fontGenerator.dispose();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        camera = new OrthographicCamera();
        modelBatch = new ModelBatch();
        ballIcons = new HashMap<>();
        for (int i = 1; i <= 15; i++) {
            ballIcons.put(i + "", new Texture("ball_" + i + ".png"));
        }
        LabelStyle labelStyle = new LabelStyle(); 
        labelStyle.background = Billiards.getDrawable("blue.png");
        labelStyle.font = font;
        winLabel = new Label("Game Over", labelStyle);
        winLabel.setSize(400, 300);
        winLabel.setPosition(WIDTH / 2 - winLabel.getWidth()/2, HEIGHT / 2 - winLabel.getHeight()/2);
        
        // Initialize camera for 3d models
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        camera.position.set(0, 0, 1);
        camera.lookAt(0, 0, -10);
        camera.near = 0f;
        camera.far = 1f;
        camera.update();



        // Create Box2D world
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        
        // Shape Shenanigans
        drawShape = new ShapeRenderer();

        // Ball Creation
        Sprite ballShadow = new Sprite(new Texture("shadowResized.png"));
        balls = new LinkedList<>();
        BodyDef ballDef = new BodyDef();
        FixtureDef fixDef = new FixtureDef();
        ballDef.type = BodyDef.BodyType.DynamicBody;
        ballDef.bullet = true;
        CircleShape ballCircle = new CircleShape();
        ballCircle.setRadius(Ball.RADIUS_M);
        fixDef.shape = ballCircle;
        fixDef.restitution = 0.5f; // restitution is how much of the speed remains after a collision
        //fixDef.friction = 0.1f;
        fixDef.friction = 0.1f;
        fixDef.density = 1f;
        Body ball = world.createBody(ballDef);
        ball.createFixture(fixDef);
        G3dModelLoader modelLoader = new G3dModelLoader(new JsonReader());

        
        int num = 1;
        int h = 1;
        int downShift = 0;
        for (int i = 0; i <= h && h <= 5; i++) {
            for (int j = 0; j <= i; j++) {
                Body tmpBall = world.createBody(ballDef);
                tmpBall.createFixture(fixDef);
                String file = "shine2.png";
                Model ballModel = modelLoader.loadModel(Gdx.files.internal(num + "ball.g3dj")); // assets/BallModels/0ball.g3dj
                balls.add(new Ball(600 + i * 18, 250 + j * 20 - downShift, this, file, tmpBall, num, ballShadow, new ModelInstance(ballModel)));
                num++;
            }
            downShift += 10;
            h++;
        }
        Model cueModel = modelLoader.loadModel(Gdx.files.internal("0ball.g3dj"));
        cueBall = new Ball(300, 250, this, "shine2.png", ball, 0, ballShadow, new ModelInstance(cueModel));
        stick.setCueBall(cueBall);
        cueBall.setMoveable(true);

        // Initialize outline
        ChainShape border = new ChainShape();
        // Vector2[] windowOutline = {
        //     new Vector2(0, 0),
        //     new Vector2(WIDTH * Ball.SCALE_INV, 0),
        //     new Vector2(WIDTH* Ball.SCALE_INV, HEIGHT* Ball.SCALE_INV),
        //     new Vector2(0, HEIGHT* Ball.SCALE_INV)
        // };
        Vector2[] tableOutline = {
            new Vector2(183, 396),
            new Vector2(430, 396),
            new Vector2(435, 410),
            new Vector2(435, 426),
            new Vector2(463, 426),
            new Vector2(463, 410),
            new Vector2(468, 396),
            new Vector2(715, 396),
            new Vector2(748, 429),
            new Vector2(773, 404),
            new Vector2(741, 372),
            new Vector2(741, 128),
            new Vector2(773, 97),
            new Vector2(748, 70),
            new Vector2(715, 104),
            new Vector2(468, 104),
            new Vector2(463, 90),
            new Vector2(463,74),
            new Vector2(435, 74),
            new Vector2(435, 90),
            new Vector2(430, 104),
            new Vector2(183, 104),
            new Vector2(150, 71),
            new Vector2(126, 98),
            new Vector2(158, 128),
            new Vector2(158, 372),
            new Vector2(126, 404),
            new Vector2(150, 429),
        };
        for (int i = 0; i < tableOutline.length; i++) {
            tableOutline[i] = tableOutline[i].scl(Ball.SCALE_INV);
        }
        tableEdges = tableOutline;
        border.createLoop(tableOutline);
        BodyDef bd = new BodyDef();
        bd.type = BodyType.StaticBody;
        FixtureDef fd = new FixtureDef();
        fd.shape = border;
        fd.restitution = 1f;
        fd.density = 1f;
        fd.friction = 0f;
        Body outlineBody = world.createBody(bd);
        outlineBody.createFixture(fd);
        
        // Sounds
        cushionSound = Gdx.audio.newSound(Gdx.files.internal("cushionHit.wav"));
        ballSound = Gdx.audio.newSound(Gdx.files.internal("ballHit2.wav"));
        cueSound = Gdx.audio.newSound(Gdx.files.internal("cueHit.wav"));
        akashProject = Gdx.audio.newMusic(Gdx.files.internal("Akash Music Genesis Project.wav"));
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("DoubleClick.wav"));
        pocketSound = Gdx.audio.newSound(Gdx.files.internal("pocketHit.wav"));

        // Settings Button
        stage = new Stage();
        TextButtonStyle exitStyle = new TextButtonStyle();
        exitStyle.up = Billiards.getDrawable("menuIcon.png");
        exitStyle.over = Billiards.getDrawable("menuHover.png");
        exitStyle.down = Billiards.getDrawable("menuPress.png");
        settingsButton = new Button(exitStyle);
        settingsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
                lastScreen = null;
                openSettings();
                playButtonClick();
			}
        });
        
        settingsButton.setBounds(10, Billiards.HEIGHT - 30, 20, 20);
        stage.addActor(settingsButton);
        
        
        // Miscellaneous
        ballCircle.dispose();
        lastTime = System.currentTimeMillis();
        setFPS(FPS);
        ballsOut = new LinkedList<>();
        ballsPotted = new LinkedList<>();

        balls.add(cueBall);
    }


    /**
     * Renders every frame of the game, updates the game info and logic at the frame rate.
     */
    @Override
    public void render () {
        if (this.getScreen() != null) {
            super.render();
            return;
        }
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        world.step(PHYSICS_DT, 40, 40);
        // world.step(Math.min(Gdx.graphics.getDeltaTime(), 0.15f), 6, 2);
        batch.draw(background, 0, 0);
        font.draw(batch, "Player 1:", 100, HEIGHT - 10);
        font.draw(batch, "Player 2:", 600, HEIGHT - 10);

        if (!isOver) {
            batch.draw(arrow, game.getTurn() == game.getPlayer1() ? 275 : 775, HEIGHT-45, 40, 40);
        }
        Player p1 = game.getPlayer1();
        Player p2 = game.getPlayer2();
        int num = 0;
        for (int i = 1; i < 15; i++) {
            
        }
        for (Ball ball : p1.getBalls()) {
            batch.draw(ballIcons.get(ball.getNum() + ""), 100 + num * 38, HEIGHT - 80, 32, 32);
            num++;
        }
        int num2 = 0;
        for (Ball ball : p2.getBalls()) {
            batch.draw(ballIcons.get(ball.getNum() + ""), 600 + num2 * 38, HEIGHT - 80, 32, 32);
            num2++;
        }

        batch.draw(table, 450 - table.getWidth() / 2, 0);
        // if (getTurnNum() == 1) {
            batch.end();
            drawShape.begin(ShapeType.Line);
            drawShape.setColor(128, 128, 128, 0.1f);
            drawShape.line(300, 104, 300, 396);
            drawShape.end();
            batch.begin();
        // }
        // cueBall.update();
        int ballsMoving = 0;
        for (Ball ball : balls) {
            if (ball.update()) {
                removeBall(ball, ballsPotted);
            }
            if (ball.isVisible()){
                ball.drawShadow(batch);                
            }
            if (ball.isMoving()) {
                ballsMoving++;
            }
        }
        noBallsMoving = ballsMoving == 0;
        if (noBallsMoving) {
            // System.out.println(turnUpdated);
            if (!turnUpdated) {
                game.updateTurn(ballsPotted);
                ballsPotted = new LinkedList<>();
                // Player solids = game.getSolidsPlayer(); // Debug code
                // if (solids != null) {
                //     System.out.println("Solids" + solids.getName() + solids.getBalls().toString());
                // }
                // Player stripes = game.getStripesPlayer();
                // if (stripes != null) {
                //     System.out.println("stripes" + stripes.getName() + stripes.getBalls().toString());
                // }
                turnUpdated = true;
            }
            
            // stick.setVisible(true);
        }
        else {
            turnUpdated = false;
        }
        if (cueBall.isMoveable()) {
            batch.draw(mover, cueBall.getCenter().x - 20, cueBall.getCenter().y - 20, 40, 40);
        }
        batch.end();
        modelBatch.begin(camera);
        for (Ball ball : balls) {
            if (ball.isVisible()) {
                modelBatch.render(ball.getModel());
            }
        }
        modelBatch.end(); 
        batch.begin();
        for (Ball ball : balls) {
            if (ball.isVisible()) {
                ball.getSprite().draw(batch);
            }
        }
        batch.draw(tableMask, 450 - table.getWidth() / 2, 0);
        stick.draw(batch);
        batch.end();
        drawShape.begin(ShapeType.Line);
        drawShape.setColor(new Color(1f,0,0,0.01f));
        if (Gdx.input.isButtonPressed(Buttons.RIGHT) && debugLines) { // creating debug lines for holes and walls of pool table
            // Draw Hole Debug Circle
            // drawShape.circle(150, 404, 17); // top left
            // drawShape.circle(749, 404, 17); // top right
            // drawShape.circle(150, 96, 17); // bottom left
            // drawShape.circle(749, 96, 17); // bottom right
            // drawShape.circle(450, 410, 15); // top middle
            // drawShape.circle(450, 90, 15); // bottom middle
            
            
            
            // Draw Table Debug Borders
            drawShape.line(183, 396, 430, 396); // top left
            drawShape.line(468, 396, 715, 396); // top right
            drawShape.line(183, 104, 430, 104); // bottom left
            drawShape.line(468, 104, 715, 104); // bottom right
            drawShape.line(158, 128, 158, 372); // mid left
            drawShape.line(741, 128, 741, 372); // mid right
            
            // top right corner
            drawShape.line(715, 396, 748, 429); 
            drawShape.line(741, 372, 773, 404);
            drawShape.line(748, 429,773, 404);
            
            // top middle corner
            drawShape.line(430, 396, 435, 410); // to wooden part on the left side
            drawShape.line(435, 410, 435, 426); // vertical line on the left side
            drawShape.line(435, 426, 463, 426); // top vertical line
            drawShape.line(468, 396, 463, 410); // to wooden part on the right side
            drawShape.line(463, 410, 463, 426); // vertical line on the right side
            
            // top left corner (same order as the previous ones)
            drawShape.line(183, 396, 150, 429); 
            drawShape.line(158, 372, 126, 404);
            drawShape.line(126, 404, 150, 429);
            
            // bottom left corner
            drawShape.line(183, 104, 150, 71); 
            drawShape.line(158, 128, 126, 98); 
            drawShape.line(126, 98, 150, 71);
            
            // bottom right corner
            drawShape.line(715, 104, 748, 70); // left verti
            drawShape.line(741, 128, 773, 97);
            drawShape.line(748, 70, 773, 97);
            
            // bottom middle 
            drawShape.line(430, 104, 435, 90); // to wooden part on the left side
            drawShape.line(435, 90, 435, 74); // vertical line on the left side
            drawShape.line(435, 74, 463, 74); // bottom vertical line
            drawShape.line(468, 104, 463, 90); // to wooden part on the right side
            drawShape.line(463, 90, 463, 74); // vertical line on the right side




            
            // draw starting ball positions
            int h = 1;
            int downShift = 0;
            for (int i = 0; i <= h && h <= 5; i++) {
                for (int j = 0; j <= i; j++) {
                    drawShape.circle(600 + i * 18, 250 + j * 20 - downShift, 10);
                }
                downShift += 10;
                h++;
            }
        }
        // System.out.println(Gdx.graphics.getFramesPerSecond());
        // // Prediction code that doesnt work
        // Circle vBall = new Circle(cueBall.getCircle());
        // Vector2 finalPoint;
        // double precision = 1;
        // int k = 0;
        // while(drawPrediction && k < 4000) {
        //     boolean broken = false;
        //     for (Ball ball : balls) {
        //         Circle c = ball.getCircle();
        //         if (ball != cueBall && vBall.overlaps(c)) {
        //             double ang = Math.atan2(c.y - vBall.y, c.x - vBall.x);
        //             double dist = (20 - ball.getCenter().dst(vBall.x, vBall.y)) * 0.5;
        //             vBall.x -= (float) (dist*Math.cos(ang)); 
        //             vBall.y -= (float) (dist*Math.sin(ang));
        //             broken = true;
        //             break;
        //         }
        //     }
        //     if (broken) {
        //         break;
        //     }
        //     for (int i = 0; i < tableEdges.length - 1; i++) {
        //         if (circleLineIntersect(tableEdges[i], tableEdges[i+1], vBall)) {
        //             break;
        //         }
        //     }
        //     if(broken) {
        //         break;
        //     }
        //     double rad = Math.toRadians(stick.getRotation());
        //     vBall.x += (float) (precision*Math.cos(rad)); 
        //     vBall.y += (float) (precision*Math.sin(rad));
        // }
        // drawShape.circle(vBall.x, vBall.y, vBall.radius);
        drawShape.end();
        drawShape.begin(ShapeType.Filled);
        drawShape.end();
        long currentTime = System.currentTimeMillis();
        stage.act(PHYSICS_DT);
        stage.draw();
        lastTime = currentTime;
    }

    // unused method for calculating collisions
    // public static boolean circleLineIntersect(Vector2 p1, Vector2 p2 ,Circle circle)
    // {
    //     float r = circle.radius;
    //     p1.x -= circle.x;
    //     p1.y -= circle.y;
    //     p2.x -= circle.x;
    //     p2.y -= circle.y;
    //     float a = (p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y);
    //     float b = 2*(p1.x*(p2.x - p1.x) + p1.y*(p2.y - p1.y));
    //     float c = p1.x*p1.x + p1.y*p1.y - r*r;
    //     float disc = b*b - 4*a*c;
    //     if(disc <= 0) return false;
    //     double sqrtdisc = Math.sqrt(disc);
    //     double t1 = (-b + sqrtdisc)/(2*a);
    //     double t2 = (-b - sqrtdisc)/(2*a);
    //     return (0 < t1 && t1 < 1) || (0 < t2 && t2 < 1);
    // }
    
    /**
     * Sets the volume of the game
     * @param vol the volume to set to
     * @return the set volume
     */
    public float setVolume(float vol) {
        volume = vol;
        return vol;
    }
    
    /**
     * Getter method for the Sprite Batch field. The sprite batch is used to draw to the screen
     * @return
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Opens the Settings UI
     */
    public void openSettings() {
        lastScreen = this.getScreen();
        this.setScreen(settingsMenu);
    }

    /**
     * Opens the Launch Menu
     */
    public void openLaunchMenu() {
        lastScreen = this.getScreen();
        this.setScreen(launchMenu);
    }

    /**
     * Static helper method for GUI classes
     * @param fileName filename of Texture
     * @return Texture as a drawable
     */
    public static Drawable getDrawable(String fileName) {
        return (Drawable)(new TextureRegionDrawable(new TextureRegion(new Texture(fileName))));
    }

    /**
     * Goes back to the previous screen
     */
    public void lastScreen() {
        Screen tmp = this.getScreen();
        this.setScreen(lastScreen);
        lastScreen = tmp;
    }

    /**
     * Update the max frame rate of the game
     * @param fps max frame
     */
    public void setFPS(int fps) {
        FPS = fps;
        Gdx.graphics.setForegroundFPS(fps);
        // physicsDelta = 1f / FPS;
    }

    /**
     * Getter for max frame rate
     * @return max frame of the game
     */
    public int getFPS() {
        return FPS;
    }

    /**
     * removes the given ball from the game
     * @param ball ball to remove
     * @param ballsPotted list to add the ball to
     */
    public void removeBall(Ball ball, LinkedList<Ball> ballsPotted) {
        ballsPotted.add(ball);
        if (ball.getNum() == 0) {
            cueBall.setVisible(false);
            cueBall.move(-40, -40);
            cueBall.setVelocity(0, 0);
            return;
        }
        ball.setMoving(false);
        world.destroyBody(ball.getBody());
        ball.setVisible(false);
        if (ball != cueBall) {
            ballsOut.add(ball);
        }
        
    }

    /**
     * Clears all screens and opens the game
     */
    public void closeMenu() {
        lastScreen = this.getScreen();
        this.setScreen(null);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Plays the sound of the cue hitting the ball
     */
    public void playCueSound() {
        cueSound.play(volume);
    } // is there sound

    /**
     * Starts playing Akashs Music Genesis project
     */
    public void playAkashSound() {
        akashProject.setLooping(true);
        akashProject.play();
    }

    /**
     * Stops playing Akashs Music Genesis Project
     */
    public void stopAkashSound() {
        akashProject.stop();
    }

    /**
     * getter for Akashs Music Genesis project
     * @return Akashs Music Genesis project as a music object
     */
    public Music getAkash() {
        return akashProject;
    }

    /**
     * Plays the button click sound
     */
    public void playButtonClick() {
        buttonClickSound.play(volume);
    }

    /**
     * Getter for the cue ball
     * @return the cue ball
     */
    public Ball getCueBall() {
        return cueBall;
    }

    /**
     * getter for all of the balls in play
     * @return a LinkedList of all of the balls in play
     */
    public LinkedList<Ball> getBallList() {
        return balls;
    }

    /**
     * Getter for the Pool stick/cue's texture
     * @return
     */
    public Texture getStickTexture() {
        return stick.getTexture();
    }

    /**
     * updates the position of the pool cue
     * @param v
     */
    public void movePoolStick(Vector2 v) {
        if (stick == null) {
            return;
        }
        stick.move(v);
    }

    /**
     * Private class for playing sounds upon collision
     */
    private class CollisionListener implements ContactListener {
        /**
         * Plays sounds upon collision
         */
        @Override
        public void beginContact(Contact contact) {
            float v = volume/ 100f *contact.getFixtureA().getBody().getLinearVelocity().add(contact.getFixtureB().getBody().getLinearVelocity()).dst(0,0);
            if (v < 0.05) {
                return;
            }
            if ((contact.getFixtureA().getShape() instanceof ChainShape) || (contact.getFixtureB().getShape() instanceof ChainShape)) {
                cushionSound.play(v * 0.75f);
            } else {//if (contact.getFixtureA().getBody().getLinearVelocity() < V_MIN){
                ballSound.play(v);
            }
        }

        /**
         * Unused method required for interface
         */
        @Override
        public void endContact(Contact contact) {}

        /**
         * Unused method required for interface
         */
        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {}

        /**
         * Unused method required for interface
         */
        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {}

    }

    /**
     * Called when the game ends, handles winning
     * @param player player that won
     */
    public void win(Player player) {
        // System.out.println(player.getName() + " wins");
        isOver = true;
        stage.addActor(winLabel);
        winLabel.setAlignment(Align.center);
        winLabel.setText(player.getName() + " wins");
    }

    /**
     * Method to check if the game is still going
     * @return a boolean which represents whether or not the game is over
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * Plays the sound for when a ball goes into a pocket
     */
    public void playPocketSound() {
        pocketSound.play(volume * 0.6f);
    }


    /**
     * removes a Box2D body from the game world
     * @param body
     */
    public void destroyBody(Body body) {
        world.destroyBody(body);
    }

    /**
     * Accessor method for the camera used to view the 3d models
     * @return Orthographic camera used for viewing the 3d models
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Resets the cue ball to the center of the board
     */
    public void resetCueBall() {
        // System.out.println("cue ball reset");
        cueBall.setMoveable(true);
        cueBall.move(450, 250);
        cueBall.setVelocity(0, 0);
        cueBall.move(450, 250);
        cueBall.getBody().setLinearVelocity(0, 0);
    }

    /**
     * getter for the pool cue object
     * @return The pool cue object used by the billiards class 
     */
    public PoolStick getStick() {
        return stick;
    }

    /**
     * getter for the turn number from the game class
     * @return turn number 
     */
    public int getTurnNum() {
        return game.getTurnNum();
    }
    /**
     * Sets the visibility of the Pool Cue
     * @param visibility true for visible and false for hidden
     */
    public void setStickVisible(boolean visibility) {
        stick.setVisible(visibility);
    }
}