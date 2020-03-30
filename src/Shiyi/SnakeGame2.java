package Shiyi;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class SnakeGame2 extends AppleFinderGame {

    private LinkedBlockingDeque<Point2D> body;
    private HashSet<Point2D> bodySet; //use bodyset for faster lookups

    private static Point2D left = new Point2D.Float(-1, 0);
    private static Point2D right = new Point2D.Float(1, 0);
    private static Point2D up = new Point2D.Float(0, 1);
    private static Point2D down = new Point2D.Float(0, -1);

    private final Point2D[] checkPoints = { //we have 2^16 * 4 states!.
            new Point2D.Float(-2f, 2f),
            new Point2D.Float(0f, 2f),
            new Point2D.Float(2f, 2f),
            new Point2D.Float(-1f, 1f),
            new Point2D.Float(0f, 1f),
            new Point2D.Float(1f, 1f),
            new Point2D.Float(-2f, 0f),
            new Point2D.Float(-1f, 0f),
            new Point2D.Float(1f, 0f),
            new Point2D.Float(2f, 0f),
            new Point2D.Float(-1f, -1f),
            new Point2D.Float(0f, -1f),
            new Point2D.Float(1f, -1f),
//            new Point2D.Float(-2f, -2f),
            new Point2D.Float(0f, -2f),
//            new Point2D.Float(2f, -2f)
    };

    public SnakeGame2(int width, int height) {
        super(width, height);
        restartGame();
    }

    public LinkedBlockingDeque<Point2D> getBody() {
        return body;
    }

    HashSet<Point2D> lastSearched;
    int lastResult;

    private int searchSpace(Point2D point2D) {
        //do a bfs and get how many position is accessable from the current point
        if (lastSearched != null) {
            if (lastSearched.contains(point2D)) return lastResult;
        }

        HashSet<Point2D> searched = new HashSet<>();
        LinkedList<Point2D> toBeSearched = new LinkedList<>();
        int result = 0;
        toBeSearched.add(point2D);
        while (!toBeSearched.isEmpty()) {
            Point2D next = toBeSearched.pollLast();
            if (outsideGame(next) || bodySet.contains(next) || searched.contains(next) || next.equals(finderPosition)) {
                continue;
            } else {
                result++;
                Point2D left = addPoint(next, SnakeGame2.left);
                Point2D up = addPoint(next, SnakeGame2.up);
                Point2D right = addPoint(next, SnakeGame2.right);
                Point2D down = addPoint(next, SnakeGame2.down);

                toBeSearched.addFirst(down);
                toBeSearched.addFirst(left);
                toBeSearched.addFirst(up);
                toBeSearched.addFirst(right);

                searched.add(next);
            }
        }
        lastSearched = searched;
        lastResult = result;
//        System.out.println("search result:" + result);
        return result;
    }

    @Override
    public int getState() {
        //where do we have more space
        lastSearched = null;
        int left = searchSpace(addPoint(finderPosition, SnakeGame2.left));
        int up = searchSpace(addPoint(finderPosition, SnakeGame2.up));
        int right = searchSpace(addPoint(finderPosition, SnakeGame2.right));
        int down = searchSpace(addPoint(finderPosition, SnakeGame2.down));

        int remaining = gameWidth * gameHeight - body.size() - 1;
        int minority = (int) (0.2f * remaining);
        int spaceState = 0;


        if (left < minority) {
            spaceState += 1;
//            System.out.println("low left:" + left);
//            System.out.println("minority:" + minority);
        }
        spaceState <<= 1;
        if (up < minority) {
            spaceState += 1;
//            System.out.println("low up:" + up);
        }
        spaceState <<= 1;
        if (right < minority) {
            spaceState += 1;
//            System.out.println("low right:" + right);
        }
        spaceState <<= 1;
        if (down < minority) {
            spaceState += 1;
//            System.out.println("low down:" + down);
        }

        //relative apple position from 0 ~ 3
        int result;
        int appleState = (int) Math.floor(getAngle(applePosition) / 90f);
        result = appleState;
        for (Point2D point : checkPoints) {
            result <<= 1;
            Point2D checkPoint = addPoint(finderPosition, point);
            if (outsideGame(checkPoint) || bodySet.contains(checkPoint)) {
                result += 1;
            }
        }

        result <<= 4;
        result += spaceState;
        return result;
    }

    public void moveLeft() {
        int oldScore = this.score;
        float oldX = (float) finderPosition.getX();
        float oldY = (float) finderPosition.getY();
        super.moveLeft();
        moveTailToHead(oldX, oldY, oldScore < this.score);
    }

    public void moveUp() {
        int oldScore = this.score;
        float oldX = (float) finderPosition.getX();
        float oldY = (float) finderPosition.getY();
        super.moveUp();
        moveTailToHead(oldX, oldY, oldScore < this.score);
    }

    public void moveRight() {
        int oldScore = this.score;
        float oldX = (float) finderPosition.getX();
        float oldY = (float) finderPosition.getY();
        super.moveRight();
        moveTailToHead(oldX, oldY, oldScore < this.score);
    }

    public void moveDown() {
        int oldScore = this.score;
        float oldX = (float) finderPosition.getX();
        float oldY = (float) finderPosition.getY();
        super.moveDown();
        moveTailToHead(oldX, oldY, oldScore < this.score);
    }

    private void moveTailToHead(float oldX, float oldY, boolean addNew) {
        Point2D tail = body.pollLast();
        bodySet.remove(tail);

        if (addNew) {
            Point2D newTail = (Point2D) tail.clone();
            bodySet.add(newTail);
            body.addLast(newTail);
        }

        tail.setLocation(oldX, oldY);
        body.addFirst(tail);
        bodySet.add(tail);
    }

    private Point2D.Float addPoint(Point2D a, Point2D b) {
        return new Point2D.Float((float) (a.getX() + b.getX()), (float) (a.getY() + b.getY()));
    }

    private boolean outsideGame(Point2D point) {
        //return true if the point is outside of the game
        if (point.getX() >= gameWidth || point.getX() < 0) {
            return true;
        } else return point.getY() >= gameHeight || point.getY() < 0;
    }

    @Override
    protected boolean updateScore() {
        //check for wall collision
        if (outsideGame(finderPosition)) {
            restartGame();
            return false;
        }
        //check for body collision
        if (bodySet.contains(finderPosition)) {
            restartGame();
            return false;
        }

        //check for apple position
        if (finderPosition.equals(applePosition)) {
            //regenerate apple
            do {
                setRandomApple();
            } while (this.bodySet.contains(applePosition));
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
        if (bodySet == null) bodySet = new HashSet<>();
        body.clear();
        bodySet.clear();

        Point2D defaultBody = new Point2D.Float((float) finderPosition.getX(), (float) finderPosition.getY() - 1);
        body.add(defaultBody);
        bodySet.add(defaultBody);
    }
}
