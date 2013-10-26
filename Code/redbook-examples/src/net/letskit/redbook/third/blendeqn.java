package net.letskit.redbook.third;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

/**
 * Demonstrate the different blending functions available with the OpenGL
 * imaging subset. This program demonstrates use of the glBlendEquation() call.
 * The following keys change the selected blend equation function: <br>
 * <ul>
 * <li>'a' -> GL_FUNC_ADD
 * <li>'s' -> GL_FUNC_SUBTRACT
 * <li>'r' -> GL_FUNC_REVERSE_SUBTRACT
 * <li>'m' -> GL_MIN 'x' -> GL_MAX
 * </ul>
 *
 * @author Kiet Le (java port)
 */


public class blendeqn//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private KeyEvent key;
    
    public blendeqn() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        
        blendeqn demo = new blendeqn();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//      JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("blendeqn");
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
        
        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
        gl.glEnable(GL.GL_BLEND);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        if (key != null) {
            switch (key.getKeyCode()) {
                case KeyEvent.VK_A:
          /*
           * Colors are added as: (1, 1, 0) + (0, 0, 1) = (1, 1, 1) which will
           * produce a white square on a yellow background.
           */
                    gl.glBlendEquation(GL.GL_FUNC_ADD);
                    break;
                    
                case KeyEvent.VK_S:
          /*
           * Colors are subtracted as: (0, 0, 1) - (1, 1, 0) = (-1, -1, 1) which
           * is clamped to (0, 0, 1), producing a blue square on a yellow
           * background
           */
                    gl.glBlendEquation(GL.GL_FUNC_SUBTRACT);
                    break;
                    
                case KeyEvent.VK_R:
          /*
           * Colors are subtracted as: (1, 1, 0) - (0, 0, 1) = (1, 1, -1) which
           * is clamed to (1, 1, 0). This produces yellow for both the square
           * and the background.
           */
                    gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
                    break;
                    
                case KeyEvent.VK_M:
                    
          /*
           * The minimum of each component is computed, as [min(1, 0), min(1,
           * 0), min(0, 1)] which equates to (0, 0, 0). This will produce a
           * black square on the yellow background.
           */
                    gl.glBlendEquation(GL.GL_MIN);
                    break;
                    
                case KeyEvent.VK_X:
          /*
           * The minimum of each component is computed, as [max(1, 0), max(1,
           * 0), max(0, 1)] which equates to (1, 1, 1) This will produce a white
           * square on the yellow background.
           */
                    gl.glBlendEquation(GL.GL_MAX);
                    break;
            }
            key = null;
        }
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glRectf(-0.5f, -0.5f, 0.5f, 0.5f);
        
        gl.glFlush();
        
    }
    
    public void reshape(GLAutoDrawable drawable, //
            int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        double aspect = (double) w / (double) h;
        
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (aspect < 1.0) {
            aspect = 1.0 / aspect;
            gl.glOrtho(-aspect, aspect, -1.0, 1.0, -1.0, 1.0);
        } else gl.glOrtho(-1.0, 1.0, -aspect, aspect, -1.0, 1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        
    }
    
    public void displayChanged(GLAutoDrawable drawable, //
            boolean deviceChanged, boolean modeChanged) {
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
        this.key = e;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                break;
        }
        super.refresh();
    }
    
    public void keyReleased(KeyEvent e) {
    }
}
