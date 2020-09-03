package FX;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class Main extends Application {
    /**
     * Disclaimer
     *
     *  I am not the creator of this code this is a port for Java, this is
     *  inspired by javidx9's video "Code-It-Yourself!
     *  First Person Shooter (Quick and Simple C++)."
     *
     *  credit
     *
     *  original project in C++ (Github): https://github.com/OneLoneCoder/CommandLineFPS
     *  video: https://youtu.be/xW8skO7MFYw
     *
     *
     *
     *
     */

    public static void main(String[] args) {
        launch(args);
    }


    float screenWidth = 120;
    float screenHeight = 40;

    float playerX = 8.0f;
    float playerY = 8.0f;
    float playerAngle = 0.0f;

    int mapHeight = 16;
    int mapWidth = 16;

    float fov = 3.14159f / 4.0f;
    float depth = 16.0f;
    float speed = 5.0f;

    String map = "";

    char[] screen = new char[(int) (screenHeight * screenWidth)];

    Label root = new Label();


    @Override
    public void start(Stage primaryStage) {

        root.setOnKeyTyped(this::keyReg);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(this::keyReg);
        root.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, this::keyReg);

        primaryStage.setResizable(false);
        primaryStage.setMaxHeight(850);
        primaryStage.setMinHeight(850);
        primaryStage.setMinWidth(600);


        //  it only renders half
        map += "################";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "#..............#";
        map += "################";

         AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    update();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();


    }

    double elapsedTime = 0.016; //AnimationTimer is capped at 60 fps
    private void keyReg(KeyEvent event){

        switch (event.getCode()) {
            case A:
                playerAngle -= (speed * 0.75f) * elapsedTime;
                break;
            case D:
                playerAngle += (speed * 0.75f) * elapsedTime;
                break;
            case W:
                playerX += Math.sin(playerAngle) * 5.0f * elapsedTime;
                playerY += Math.cos(playerAngle) * 5.0f * elapsedTime;
                break;
            case S:
                playerX -= Math.sin(playerAngle) * 5.0f * elapsedTime;
                playerY -= Math.cos(playerAngle) * 5.0f * elapsedTime;
                break;
        }
    }

    private void update() throws InterruptedException { //AnimationTimer Loop
        for (int x = 0; x < screenWidth; x++) {


            float rayAngle = (playerAngle - fov / 2.0f) + ((float) x / screenWidth) * fov;

            float distanceToWall = 0.0f;
            float stepSize = 0.1f;
            boolean hitWall = false;
            boolean boundary = false;

            float eyeX = (float) Math.sin(rayAngle);
            float eyeY = (float) Math.cos(rayAngle);

            while (!hitWall && distanceToWall < depth) {
                distanceToWall += stepSize;

                int testX = (int) (playerX + eyeX * distanceToWall);
                int testY = (int) (playerY + eyeY * distanceToWall);

                if (testX < 0 || testX >= mapWidth || testY < 0 || testY >= mapHeight) {
                    hitWall = true;
                    distanceToWall = depth;
                } else {
                    if (map.charAt(testY * mapWidth + testX) == '#') {
                        hitWall = true;
                    }
                }
            }

            int celling = (int) ((screenHeight / 2f) - screenHeight / (distanceToWall));
            int floor = (int) (screenHeight - celling);

            short shade = ' ';

            if (distanceToWall <= depth / 4.0f)     shade = 0x2588;
            else if (distanceToWall < depth / 3.0f) shade = 0x2593;
            else if (distanceToWall < depth / 2.0f) shade = 0x2592;
            else if (distanceToWall < depth)        shade = 0x2591;
            else                                    shade = ' ';

            if (boundary) {
                shade = ' ';
            }

            for (int y = 0; y < screenHeight; y++) {
                if (y <= celling) {
                    screen[(int) (y * screenWidth + x)] = ' ';
                } else if (y > celling && y <= floor) {
                    screen[(int) (y * screenWidth + x)] = (char) shade;
                } else {
                    float b = 1.0f - (((float)y -screenHeight/2.0f) / (screenHeight / 2.0f));
                    if (b < 0.25)		shade = '#';
                    else if (b < 0.5)	shade = 'x';
                    else if (b < 0.75)	shade = '.';
                    else if (b < 0.9)	shade = '-';
                    else				shade = ' ';
                    screen[(int) (y*screenWidth + x)] = (char) shade;
                }
            }

        }

        String s = lineBreaker(String.valueOf(screen));

        root.setText(s);
//        System.out.println(s); //debug

    }

    private String lineBreaker(String input) { //to display the screen in the right Ratio

        String result = "";
        for (int i = 0; i < input.length(); i+= 120) {
            result += input.substring(i, i + 120) + "\n";
        }
        return result;
    }
}
