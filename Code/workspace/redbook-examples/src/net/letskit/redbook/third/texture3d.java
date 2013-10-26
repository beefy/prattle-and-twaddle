package net.letskit.redbook.third;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.BufferUtil;
  

/**
 * This program demonstrates using a three-dimensional texture. It creates a 3D
 * texture and then renders two rectangles with different texture coordinates to
 * obtain different "slices" of the 3D texture.
 * 
 * @author Kiet Le (Java port)
 */
public class texture3d //
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private GLU glu;
    private static final int iWidth = 16;
    private static final int iHeight = 16;
    private static final int iDepth = 16;
    private static final int iRgb = 3;
    private ByteBuffer image //
    = BufferUtil.newByteBuffer(iRgb * iWidth * iHeight * iDepth);
    private int texName[] = new int[1];

    //
    public texture3d() {
    }

    public void run() {
    }

    public static void main(String[] args) {

        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        
        texture3d demo = new texture3d();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texture3d");
        frame.setSize(250, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        glu = new GLU();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        makeImage();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(1, texName, 0);
        gl.glBindTexture(GL.GL_TEXTURE_3D, texName[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexImage3D(GL.GL_TEXTURE_3D, 0, GL.GL_RGB,//
                iWidth, iHeight, iDepth, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
                image);
        gl.glEnable(GL.GL_TEXTURE_3D);
    }//

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(-2.25f, -1.0f, 0.0f);
        gl.glTexCoord3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-2.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-0.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 0.0f, 1.0f);
        gl.glVertex3f(-0.25f, -1.0f, 0.0f);

        gl.glTexCoord3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.25f, -1.0f, 0.0f);
        gl.glTexCoord3f(0.0f, 1.0f, 1.0f);
        gl.glVertex3f(0.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(2.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(2.25f, -1.0f, 0.0f);
        gl.glEnd();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (float) w / (float) h, 1.0, 30.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -4.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * Create a 16x16x16x3 array with different color values in each array
     * element [r, g, b]. Values range from 0 to 255.
     */

    private void makeImage() {
        int ss = 0, tt = 0, rr = 0;
        for (int s = 0; s < 16; s++)
            for (int t = 0; t < 16; t++)
                for (int r = 0; r < 16; r++) {
                    // image[r][t][s][0] = (GLubyte) (s * 17);
                    // image[r][t][s][1] = (GLubyte) (t * 17);
                    // image[r][t][s][2] = (GLubyte) (r * 17);
                    ss = s * 17;
                    tt = t * 17;
                    rr = r * 17;
                    // System.out.println("s" + ss + "." + ss//
                    // + "t" + tt + "." + tt//
                    // + "r" + rr + "." + rr);
                    image.put((byte) (ss * 17));
                    image.put((byte) tt);
                    image.put((byte) rr);
                }
        image.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            super.runExit();
            break;

        }
    }

    public void keyReleased(KeyEvent key) {
    }
}
