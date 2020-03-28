package Shiyi;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyInput implements KeyListener {
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
