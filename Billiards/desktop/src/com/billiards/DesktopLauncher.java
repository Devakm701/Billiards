package com.billiards;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.billiards.Billiards;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        //config.setForegroundFPS(144);
		config.useVsync(true);
        config.setWindowedMode(900, 600);
        config.setResizable(false);
        config.setTitle("8 Ball Pool");
        config.setBackBufferConfig(8,8,8,8,16,0,32); //or 2 or 8 or 16
        config.setWindowIcon("8-ball.png");
        new Lwjgl3Application(new Billiards(), config);
    }
}
