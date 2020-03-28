package Shiyi;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void initSnakeGame() throws InterruptedException {

        SnakeGame gameModel = new SnakeGame(20, 20);
        SnakeLearner learner = new SnakeLearner(gameModel);

        SnakeListener gameListener = new SnakeListener(gameModel.getBody());

        initGL(gameListener);

        while (true) {
            learner.learn();
            gameListener.setFinderPosition((int) gameModel.getFinderPosition().getX(), (int) gameModel.getFinderPosition().getY());
            gameListener.setApplePosition((int) gameModel.getApplePosition().getX(), (int) gameModel.getApplePosition().getY());
            if (learner.currentGeneration() > 300) TimeUnit.MILLISECONDS.sleep(100);
//            else TimeUnit.MILLISECONDS.sleep(2);
        }
    }

    public static void initAppleFinderGame() throws InterruptedException {

        AppleFinderGame gameModel = new AppleFinderGame(20, 20);
        AppleFinderLearner learner = new AppleFinderLearner(gameModel);

        AppleFinderListener gameListener = new AppleFinderListener();

        initGL(gameListener);

        while (true) {
            learner.learn();
            gameListener.setFinderPosition((int) gameModel.getFinderPosition().getX(), (int) gameModel.getFinderPosition().getY());
            gameListener.setApplePosition((int) gameModel.getApplePosition().getX(), (int) gameModel.getApplePosition().getY());
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    private static void initGL(AppleFinderListener listener) {
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);

        GLWindow window = GLWindow.create(caps);
        window.setSize(800, 800);
        window.setResizable(false);
        window.addGLEventListener(listener);
        window.addKeyListener(new KeyInput());

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();

        window.setVisible(true);
    }

    public static void main(String[] args) {
        try {
//            initAppleFinderGame();
            initSnakeGame();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
