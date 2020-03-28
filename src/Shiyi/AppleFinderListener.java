package Shiyi;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

public class AppleFinderListener implements GLEventListener {

    protected Point2D applePosition;
    protected Point2D finderPosition;

    public void setApplePosition(int x, int y) {
        applePosition.setLocation(x, y);
    }

    public void setFinderPosition(int x, int y) {
        finderPosition.setLocation(x, y);
    }

    public AppleFinderListener() {
        super();
        applePosition = new Point2D.Float();
        finderPosition = new Point2D.Float();
    }

//    public void setGameSize(int width, int height) {
//        this.gameWidth = width;
//        this.gameHeight = height;
//    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClearColor(0, 0, 0, 0);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glBegin(gl.GL_QUADS);
        drawSquare(gl, applePosition, 1, 0, 0);
        drawSquare(gl, finderPosition, 1, 1, 1);

        gl.glEnd();
    }

    protected void drawSquare(GL2 gl2, Point2D position, float r, float g, float b) {
        gl2.glColor3f(r, g, b);
        gl2.glVertex2f((float) position.getX(), (float) position.getY());
        gl2.glVertex2f((float) position.getX(), (float) position.getY() + 1);
        gl2.glVertex2f((float) position.getX() + 1, (float) position.getY() + 1);
        gl2.glVertex2f((float) position.getX() + 1, (float) position.getY());
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        //called when resizing
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrtho(0, AppleFinderGame.gameWidth, 0, AppleFinderGame.gameHeight, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
