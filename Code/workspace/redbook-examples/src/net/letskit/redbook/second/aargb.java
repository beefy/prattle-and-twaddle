package net.letskit.redbook.second;
import java.awt.Dimension;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

public class aargb//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private float rotAngle = 0f;
    private boolean rotate = false;
    
    public aargb() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(caps.getNumSamples() * 2);
        
        GLJPanel canvas = new GLJPanel(caps);
        canvas.setPreferredSize(new Dimension(512,512));
        aargb demo = new aargb();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("aargb");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        float values[] = new float[2];
        gl.glGetFloatv(GL.GL_LINE_WIDTH_GRANULARITY, values, 0);
        System.out
                .println("GL.GL_LINE_WIDTH_GRANULARITY value is " + values[0]);
        gl.glGetFloatv(GL.GL_LINE_WIDTH_RANGE, values, 0);
        System.out.println("GL.GL_LINE_WIDTH_RANGE values are " + values[0]
                + ", " + values[1]);
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
        gl.glLineWidth(1.5f);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glPushMatrix();
        gl.glRotatef(-rotAngle, 0.0f, 0.0f, 0.1f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(-0.5f, 0.5f);
        gl.glVertex2f(0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glPushMatrix();
        gl.glRotatef(rotAngle, 0.0f, 0.0f, 0.1f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0.5f, 0.5f);
        gl.glVertex2f(-0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glFlush();
        if (rotate)
            rotAngle += 1f;
        if (rotAngle >= 360f)
            rotAngle = 0f;
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h) //
            glu.gluOrtho2D(-1.0, 1.0, -1.0 * (float) h / (float) w, //
                    1.0 * (float) h / (float) w);
        else
            glu.gluOrtho2D(-1.0 * (float) w / (float) h, //
                    1.0 * (float) w / (float) h, -1.0, 1.0);
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
                super.runExit();
            case KeyEvent.VK_R:
                rotate = !rotate;
                super.refresh();
            default:
                break;
        }
    }
    
    public void keyReleased(KeyEvent key) {
    }
}
