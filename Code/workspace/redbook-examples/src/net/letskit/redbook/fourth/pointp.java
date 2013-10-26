package net.letskit.redbook.fourth;
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


/**
 * This program demonstrates point parameters and their effect on point
 * primitives. 250 points are randomly generated within a 10 by 10 by 40 region,
 * centered at the origin. In some modes (including the default), points that
 * are closer to the viewer will appear larger. Pressing the 'l', 'q', and 'c'
 * keys switch the point parameters attenuation mode to linear, quadratic, or
 * constant, respectively. Pressing the 'f' and 'b' keys move the viewer forward
 * and backwards. In either linear or quadratic attenuation mode, the distance
 * from the viewer to the point will change the size of the point primitive.
 * Pressing the '+' and '-' keys will change the current point size. In this
 * program, the point size is bounded, so it will not get less than 2.0, nor
 * greater than GL_POINT_SIZE_MAX.
 *
 * @author Kiet Le (Java port)
 */
public class pointp//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
// , MouseListener //
// , MouseMotionListener //
// , MouseWheelListener //
{
    
    private GLU glu;
    
    //
    private static float psize = 7.0f;
    private static float pmax[] = new float[1];
    private static float constant[] = { 1.0f, 0.0f, 0.0f };
    private static float linear[] = { 0.0f, 0.12f, 0.0f };
    private static float quadratic[] = { 0.0f, 0.0f, 0.01f };
    private KeyEvent key;
    
    //
    private pointp() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        // GLCanvas canvas = new GLCanvas(caps);
        GLJPanel canvas = new GLJPanel(caps);
        canvas.setPreferredSize(new Dimension(500,500));
        
        pointp demo = new pointp();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
        /* metal/ocean LAF can't handle heavy-weight canvas on resize */
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("pointp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = new GLU();
        //
        gl.glNewList(1, GL.GL_COMPILE);
        gl.glBegin(GL.GL_POINTS);
        
        float min = -5;
        float max = 5;
        for (int i = 0; i < 250; i++) {
            gl.glColor3f(1.0f, (float) Math.random(), (float) Math.random());
            /*
             * randomly generated vertices: -5 < x < 5; -5 < y < 5; -5 < z < -45
             */
            gl.glVertex3f((float) ((Math.random() * (max - min)) - max),//
                    (float) ((Math.random() * (max - min)) - max),//
                    (float) (((Math.random() * (max - min)) - max)));
        }
        gl.glEnd();
        gl.glEndList();
        
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_POINT_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPointSize(psize);
        gl.glGetFloatv(GL.GL_POINT_SIZE_MAX_EXT, pmax, 0);
        
        gl.glPointParameterfvEXT(GL.GL_DISTANCE_ATTENUATION_EXT, linear, 0);
        gl.glPointParameterfEXT(GL.GL_POINT_FADE_THRESHOLD_SIZE_EXT, 2.0f);
        
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        if (key != null)
            switch (key.getKeyChar()) {
                case 'b':
                    gl.glMatrixMode(GL.GL_MODELVIEW);
                    gl.glTranslatef(0.0f, 0.0f, -0.5f);
                    
                    break;
                case 'c':
                    gl.glPointParameterfvEXT(GL.GL_DISTANCE_ATTENUATION_EXT,
                            constant, 0);
                    
                    break;
                case 'f':
                    gl.glMatrixMode(GL.GL_MODELVIEW);
                    gl.glTranslatef(0.0f, 0.0f, 0.5f);
                    
                    break;
                case 'l':
                    gl.glPointParameterfvEXT(GL.GL_DISTANCE_ATTENUATION_EXT,
                            linear, 0);
                    
                    break;
                case 'q':
                    gl.glPointParameterfvEXT(GL.GL_DISTANCE_ATTENUATION_EXT,
                            quadratic, 0);
                    
                    break;
                case '+':
                    if (psize < (pmax[0] + 1.0f))
                        psize = psize + 1.0f;
                    gl.glPointSize(psize);
                    
                    break;
                case '-':
                    if (psize >= 2.0f)
                        psize = psize - 1.0f;
                    gl.glPointSize(psize);
                    
                    break;
                    
            }
            
            gl.glCallList(1);
            gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(35, (float) w / (float) h, 0.25, 200);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        gl.glTranslatef(0.0f, 0.0f, -10.0f);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    public void keyTyped(KeyEvent key) {
    }
    
    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                break;
                
            default:
                break;
        }
        
        super.refresh();
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
