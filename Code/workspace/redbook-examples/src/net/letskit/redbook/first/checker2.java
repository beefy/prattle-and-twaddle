package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import java.awt.event.*;
import javax.swing.*;

import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import net.letskit.redbook.glskeleton;

/**
 * This program texture maps a checkerboard image onto two rectangles. This
 * program clamps the texture, if the texture coordinates fall outside 0.0 and
 * 1.0.
 *
 * @author Kiet Le
 */
public class checker2//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;
    /* Create checkerboard texture */
    private static final int checkImageWidth = 64;
    private static final int checkImageHeight = 64;
    private static final int color = 3;
    private byte checkImage[][][] = new byte[checkImageWidth][checkImageHeight][color];
    private ByteBuffer checkImageBuf = //
            BufferUtil.newByteBuffer(checkImageHeight * checkImageWidth * color);
    
    //
    public checker2() {
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        // caps.setSampleBuffers(true );
        GLJPanel canvas = new GLJPanel(caps);
        checker2 demo = new checker2();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("checker2");
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
        glu = new GLU();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        
        makeCheckImage();
        
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, color, checkImageWidth,
                checkImageHeight, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
                checkImageBuf);// checkImage[0][0][0]);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, //
                GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, //
                GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, //
                GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameterf(GL.GL_TEXTURE_2D, //
                GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-2.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-2.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(0.0f, -1.0f, 0.0f);
        
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(2.41421f, 1.0f, -1.41421f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(2.41421f, -1.0f, -1.41421f);
        gl.glEnd();
        
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, 1.0 * (float) w / (float) h, 1.0, 30.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -3.6f);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    /*
     * 3D array won't be used. I left it here for you to see.
     */
    private void makeCheckImage() {
        byte c = (byte) 0xFF;
        
        for (int i = 0; i < checkImageWidth; i++) {
            for (int j = 0; j < checkImageHeight; j++) {
                // c = ((((i & 0x8) == 0) ^ ((j & 0x8)) == 0)) * 255;
                c = (byte) ((((byte) ((i & 0x8) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x8) == 0 ? 0x00 : 0xff))));
                // if (i % 2 == 0) if (j % 2 == 1) c = (byte) 0xff;
                // else c = (byte) 0x00;
                // else if (j % 2 == 1) c = (byte) 0x00;
                // else c = (byte) 0xff;
                checkImage[i][j][0] = (byte) c;
                checkImage[i][j][1] = (byte) c;
                checkImage[i][j][2] = (byte) c;
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
            }
        }
        checkImageBuf.rewind();
    }//
    
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
