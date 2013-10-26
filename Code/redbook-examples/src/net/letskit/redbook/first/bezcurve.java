package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

/**
 * This program uses evaluators to draw a Bezier curve.
 *
 * @author Kiet Le (Java conversion)
 */
public class bezcurve//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private float ctrlpoints[][] = new float[][] { { -4.0f, -4.0f, 0.0f },
    { -2.0f, 4.0f, 0.0f }, { 2.0f, -4.0f, 0.0f }, { 4.0f, 4.0f, 0.0f } };
    private FloatBuffer ctrlpointBuf;
    
    // = BufferUtil.newFloatBuffer(ctrlpoints[0].length * ctrlpoints.length);
    
    //
    public bezcurve() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        
        bezcurve demo = new bezcurve();
        GLJPanel canvas = new GLJPanel(caps);
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("bezcurve");
        frame.getContentPane().add(canvas);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        // need to convert 2d array to buffer type
        ctrlpointBuf = BufferUtil.newFloatBuffer(ctrlpoints[0].length
                * ctrlpoints.length);
        for (int i = 0; i < ctrlpoints.length; i++) {
            ctrlpointBuf.put(ctrlpoints[i]);
        }
        ctrlpointBuf.rewind();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
        gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpointBuf);
        gl.glEnable(GL.GL_MAP1_VERTEX_3);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int i = 0; i <= 30; i++) {
            gl.glEvalCoord1f((float) i / (float) 30.0);
        }
        gl.glEnd();
        /* The following code displays the control points as dots. */
        gl.glPointSize(5.0f);
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glBegin(GL.GL_POINTS);
        for (int i = 0; i < 4; i++) {
            gl.glVertex3fv(ctrlpointBuf);
            ctrlpointBuf.position(i * 3);
        }
        gl.glEnd();
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h) //
            gl.glOrtho(-5.0, 5.0, -5.0 * (float) h / (float) w, //
                    5.0 * (float) h / (float) w, -5.0, 5.0);
        else
            gl.glOrtho(-5.0 * (float) w / (float) h, //
                    5.0 * (float) w / (float) h,//
                    -5.0, 5.0, -5.0, 5.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    public void keyTyped(KeyEvent key) {
    }
    
    public void keyPressed(KeyEvent key) {
        switch (key.getKeyChar()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                break;
                
            default:
                break;
        }
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
