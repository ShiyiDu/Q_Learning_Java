package Shiyi;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.Random;

//a game representing a simple food finder game
public class AppleFinderGame {
    private int score;
    private Point2D finderPosition;
    private Point2D applePosition;
    public static int gameWidth;
    public static int gameHeight;

    public AppleFinderGame(int width, int height) {
        gameHeight = height;
        gameWidth = width;
        this.score = 0;
        this.finderPosition = new Point2D.Float(gameWidth / 2, gameHeight / 2);
        this.applePosition = new Point2D.Float();
        setRandomApple();
    }

    public void moveLeft() {
        int newX = (int) Math.max(finderPosition.getX() - 1, 0);
        this.finderPosition.setLocation(newX, finderPosition.getY());
        updateScore();
    }

    public void moveUp() {
        int newY = (int) Math.min(finderPosition.getY() + 1, gameHeight - 1);
        finderPosition.setLocation(finderPosition.getX(), newY);
        updateScore();
    }

    public void moveRight() {
        int newX = (int) Math.min(finderPosition.getX() + 1, gameWidth - 1);
        finderPosition.setLocation(newX, finderPosition.getY());
        updateScore();
    }

    public void moveDown() {
        int newY = (int) Math.max(finderPosition.getY() - 1, 0);
        finderPosition.setLocation(finderPosition.getX(), newY);
    }

    public Point2D getApplePosition() {
        return applePosition;
    }

    public Point2D getFinderPosition() {
        return finderPosition;
    }

    public int getScore() {
        return score;
    }

    public float distanceToApple() {
        return (float) this.applePosition.distance(finderPosition.getX(), finderPosition.getY());
    }

    public int getState() {
        //return the relative apple position from 0 ~ 7
        return (int) Math.floor(getAngle(applePosition) / 45f);
    }

    private float getAngle(Point2D apple) {
        float y = (float) this.finderPosition.getY();
        float x = (float) this.finderPosition.getX();
        float angle = (float) Math.toDegrees(Math.atan2(apple.getY() - y, apple.getX() - x));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private void updateScore() {
        //determine if the apple is eat
        if (finderPosition.equals(applePosition)) {
            this.score++;
            setRandomApple();
        }
    }

    private void setRandomApple() {
        int x, y;
        Random random = new Random();
        do {
            x = random.nextInt(gameWidth);
            y = random.nextInt(gameHeight);
        } while (x == finderPosition.getX() && y == finderPosition.getY());
        //make sure the apple doesn't appear in the player position

        this.applePosition.setLocation(x, y);
    }


}
