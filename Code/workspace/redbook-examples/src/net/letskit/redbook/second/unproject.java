package net.letskit.redbook.second;
import net.letskit.redbook.glskeleton;
 
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

  

/**
 * When the left mouse button is pressed, this program reads the mouse position
 * and determines two 3D points from which it was transformed. Very little is
 * displayed.
 * 
 * @author Kiet Le (Java port)
 */
public class unproject//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
        , MouseListener//
{
    private GLU glu;  
    private MouseEvent mouse;

    //
    public unproject() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        unproject demo = new unproject();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("unproject");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
//        GL gl = drawable.getGL();
        glu = new GLU(); 
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        int viewport[] = new int[4];
        double mvmatrix[] = new double[16];
        double projmatrix[] = new double[16];
        int realy = 0;// GL y coord pos
        double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
        if (mouse != null) {
            int x = mouse.getX(), y = mouse.getY();
            switch (mouse.getButton()) {
            case MouseEvent.BUTTON1:
                gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix, 0);
                gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix, 0);
                /* note viewport[3] is height of window in pixels */
                realy = viewport[3] - (int) y - 1;
                System.out.println("Coordinates at cursor are (" + x + ", "
                        + realy);
                glu.gluUnProject((double) x, (double) realy, 0.0, //
                        mvmatrix, 0,//
                        projmatrix, 0, //
                        viewport, 0, //
                        wcoord, 0);
                System.out
                        .println("World coords at z=0.0 are ( " //
                                + wcoord[0] + ", " + wcoord[1] + ", "
                                + wcoord[2] + ")");
                glu.gluUnProject((double) x, (double) realy, 1.0, //
                        mvmatrix, 0,//
                        projmatrix, 0,//
                        viewport, 0, //
                        wcoord, 0);
                System.out
                        .println("World coords at z=1.0 are (" //
                                + wcoord[0] + ", " + wcoord[1] + ", "
                                + wcoord[2] + ")");
                break;
            case MouseEvent.BUTTON2:
                break;
            default:
                break;
            }
        }

        gl.glFlush();
    }

    /* Change these values for a different transformation */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) w / (float) h, 1.0, 100.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {

        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }

    }

    public void keyReleased(KeyEvent key) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouse = e;
        super.refresh();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
