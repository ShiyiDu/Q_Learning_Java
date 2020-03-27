package Shiyi;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import java.util.concurrent.TimeUnit;

public class Main {

    private static int width;
    private static GLWindow window = null;

    public static void init() throws InterruptedException {
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);

        AppleFinderGame gameModel = new AppleFinderGame(20, 20);

        GameListener gameListener = new GameListener();

        AppleFinderLearner learner = new AppleFinderLearner(gameModel);

        gameListener.setFinderPosition((int) gameModel.getFinderPosition().getX(), (int) gameModel.getFinderPosition().getY());
        gameListener.setApplePosition((int) gameModel.getApplePosition().getX(), (int) gameModel.getApplePosition().getY());


        window = GLWindow.create(caps);
        window.setSize(800, 800);
        window.setResizable(false);
        window.addGLEventListener(gameListener);

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();

        window.setVisible(true);

        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            learner.learn();
            gameListener.setFinderPosition((int) gameModel.getFinderPosition().getX(), (int) gameModel.getFinderPosition().getY());
            gameListener.setApplePosition((int) gameModel.getApplePosition().getX(), (int) gameModel.getApplePosition().getY());
        }
    }

    public static void main(String[] args) {
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
