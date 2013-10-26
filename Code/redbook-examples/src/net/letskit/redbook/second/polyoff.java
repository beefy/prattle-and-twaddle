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

import com.sun.opengl.util.GLUT;


/**
 * This program demonstrates polygon offset to draw a shaded polygon and its
 * wireframe counterpart without ugly visual artifacts ("stitching").
 *
 * @author Kiet Le (java port)
 */
public class polyoff//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
        , MouseListener//
{
    private GLU glu;
    private GLUT glut;
    
    private int list;
    private int spinx = 0;
    private int spiny = 0;
    private float tdist = 0.0f;
    private float polyfactor = 1.0f;
    private float polyunits = 1.0f;
    
    //
    public polyoff() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        polyoff demo = new polyoff();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("polyoff");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    /*
     * specify initial properties create display list with sphere initialize
     * lighting and depth buffer
     */
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();
        //
        
        float light_ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        
        float global_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        list = gl.glGenLists(1);
        gl.glNewList(list, GL.GL_COMPILE);
        glut.glutSolidSphere(1.0, 20, 12);
        gl.glEndList();
        
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);
    }
    
    /*
     * display() draws two spheres, one with a gray, diffuse material, the other
     * sphere with a magenta material with a specular highlight.
     */
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        /* clear all pixels */
        // double mat_ambient[] = { 0.8, 0.8, 0.8, 1.0 };
        // double mat_diffuse[] = { 1.0, 0.0, 0.5, 1.0 };
        // double mat_specular[] = { 1.0, 1.0, 1.0, 1.0 };
        float gray[] = { 0.8f, 0.8f, 0.8f, 1.0f };
        float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glTranslated(0.0, 0.0, tdist);
        gl.glRotated((double) spinx, 1.0, 0.0, 0.0);
        gl.glRotated((double) spiny, 0.0, 1.0, 0.0);
        
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, gray, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, black, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.0f);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(polyfactor, polyunits);
        gl.glCallList(list);
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
        
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_LIGHT0);
        gl.glColor3d(1.0, 1.0, 1.0);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        gl.glCallList(list);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        
        gl.glPopMatrix();
        gl.glFlush();
        
    }
    
    /* call when window is resized */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (double) width / (double) height, 1.0, 10.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
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
                
            case 't':
                if (tdist < 4.0)
                    tdist = (tdist + 0.5f);
                break;
            case 'T':
                if (tdist > -5.0)
                    tdist = (tdist - 0.5f);
                break;
            case 'F':
                polyfactor = polyfactor + 0.1f;
                System.out.println("polyfactor is " + polyfactor);
                break;
            case 'f':
                polyfactor = polyfactor - 0.1f;
                System.out.println("polyfactor is " + polyfactor);
                break;
            case 'U':
                polyunits = polyunits + 1.0f;
                System.out.println("polyunits is " + polyunits);
                break;
            case 'u':
                polyunits = polyunits - 1.0f;
                System.out.println("polyunits is " + polyunits);
                break;
            default:
                break;
        }
        super.refresh();
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
    public void mouseClicked(MouseEvent mouse) {
    }
    
    public void mousePressed(MouseEvent mouse) {
        switch (mouse.getButton()) {
            case MouseEvent.BUTTON1:
                spinx = (spinx + 5) % 360;
                break;
            case MouseEvent.BUTTON2:
                spiny = (spiny + 5) % 360;
                break;
                
            default:
                break;
        }
        super.refresh();
    }
    
    public void mouseReleased(MouseEvent mouse) {
    }
    
    public void mouseEntered(MouseEvent mouse) {
    }
    
    public void mouseExited(MouseEvent mouse) {
    }
    
}
