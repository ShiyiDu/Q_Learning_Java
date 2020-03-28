package Shiyi;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

public class SnakeGame extends AppleFinderGame {

    private LinkedBlockingDeque<Point2D> body;

    private static Point2D left = new Point2D.Float(-1, 0);
    private static Point2D right = new Point2D.Float(1, 0);
    private static Point2D up = new Point2D.Float(0, 1);
    private static Point2D down = new Point2D.Float(0, -1);

    private int direction; //0 left, 1 up, 2 right, 3 down

    public SnakeGame(int width, int height) {
        super(width, height);
        restartGame();
    }

    public LinkedBlockingDeque<Point2D> getBody() {
        return body;
    }

    public void doNothing() {
        //move to the current direction by 1
        float oldX = (float) finderPosition.getX();
        float oldY = (float) finderPosition.getY();
        Point2D tail = body.pollLast();
        //then move the tail to the front
        switch (direction) {
            case 0:
                finderPosition.setLocation(finderPosition.getX() - 1, finderPosition.getY());
                break;
            case 1:
                finderPosition.setLocation(finderPosition.getX(), finderPosition.getY() + 1);
                break;
            case 2:
                finderPosition.setLocation(finderPosition.getX() + 1, finderPosition.getY());
                break;
            case 3:
                finderPosition.setLocation(finderPosition.getX(), finderPosition.getY() - 1);
                break;
        }

        if (updateScore()) {
            Point2D newTail = (Point2D) tail.clone();
            body.add(newTail);
        }
        tail.setLocation(oldX, oldY);
        body.addFirst(tail);
    }

    public void turnLeft() {
        direction += 3;
        direction %= 4;
        doNothing();
    }

    public void turnRight() {
        direction += 1;
        direction %= 4;
        doNothing();
    }

    @Override
    public void moveLeft() {
    }

    @Override
    public void moveUp() {
    }

    @Override
    public void moveRight() {
    }

    @Override
    public void moveDown() {
    }

    @Override
    public int getState() {
        //6 bit  00 0000, first 2 is apple position, the last 4 is the nearby body position it can see.
        //relative apple position from 0 ~ 3
        int appleState = (int) Math.floor(getAngle(applePosition) / 90f);
        appleState += direction;
        appleState %= 4;
        appleState = appleState << 3;

        int bodyState = 0;
        for (Point2D bodyPoint : body) {
            if (bodyPoint.getX() == finderPosition.getX() - 1 && bodyPoint.getY() == finderPosition.getY()) {
                bodyState |= direction == 1 ? 1 : direction == 2 ? 0 : direction == 3 ? 4 : 2;
            }
            if (bodyPoint.getX() == finderPosition.getX() && bodyPoint.getY() == finderPosition.getY() + 1) {
                bodyState |= direction == 1 ? 2 : direction == 2 ? 1 : direction == 3 ? 0 : 4;
            }
            if (bodyPoint.getX() == finderPosition.getX() + 1 && bodyPoint.getY() == finderPosition.getY()) {
                bodyState |= direction == 1 ? 4 : direction == 2 ? 2 : direction == 3 ? 1 : 0;
            }
            if (bodyPoint.getX() == finderPosition.getX() && bodyPoint.getY() == finderPosition.getY() - 1) {
                bodyState |= direction == 1 ? 0 : direction == 2 ? 4 : direction == 3 ? 2 : 1;
            }
        }

        if (finderPosition.getX() == 0)
            bodyState |= direction == 1 ? 1 : direction == 2 ? 0 : direction == 3 ? 4 : 2;
        if (finderPosition.getY() == gameHeight - 1)
            bodyState |= direction == 1 ? 2 : direction == 2 ? 1 : direction == 3 ? 0 : 4;
        if (finderPosition.getX() == gameWidth - 1)
            bodyState |= direction == 1 ? 4 : direction == 2 ? 2 : direction == 3 ? 1 : 0;
        if (finderPosition.getY() == 0)
            bodyState |= direction == 1 ? 0 : direction == 2 ? 4 : direction == 3 ? 2 : 1;

        if ((appleState | bodyState) >= 64) {
            System.out.println("appleState:" + appleState);
            System.out.println("bodyState:" + bodyState);
        }

        return appleState | bodyState;
    }

    @Override
    protected boolean updateScore() {
        //check for wall collision
        if (finderPosition.getX() >= gameWidth || finderPosition.getX() < 0) {
            restartGame();
            return false;
        } else if (finderPosition.getY() >= gameHeight || finderPosition.getY() < 0) {
            restartGame();
            return false;
        }

        //check for body collision
        if (body.contains(finderPosition)) {
            restartGame();
            return false;
        }

        //check for apple position
        if (finderPosition.equals(applePosition)) {
            //regenerate apple
            do {
                setRandomApple();
                //TODO: this takes O(n) time, it can be optimized with a hashset.
            } while (this.body.contains(applePosition));
            this.score++;
            return true;
        }

        return false;
    }

    private void restartGame() {
        this.score = 0;
        this.finderPosition = new Point2D.Float(gameWidth / 2, gameHeight / 2);
        this.applePosition = new Point2D.Float();
        setRandomApple();

        if (body == null) body = new LinkedBlockingDeque<>();
        body.clear();
        body.add(new Point2D.Float((float) finderPosition.getX(), (float) finderPosition.getY() - 1));
        direction = 1;
    }
}
