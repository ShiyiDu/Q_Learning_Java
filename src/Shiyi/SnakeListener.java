package Shiyi;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

public class SnakeListener extends AppleFinderListener {
    private LinkedBlockingDeque<Point2D> body;

    public SnakeListener(LinkedBlockingDeque<Point2D> body) {
        super();
        this.body = body;
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        super.display(glAutoDrawable);
        GL2 gl = glAutoDrawable.getGL().getGL2();

//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glBegin(gl.GL_QUADS);

        for (Point2D point : body) {
            drawSquare(gl, point, 0, 1, 0);
        }
        gl.glEnd();
    }
}
