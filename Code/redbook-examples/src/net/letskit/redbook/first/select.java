package net.letskit.redbook.first;
import net.letskit.redbook.glskeleton;

import javax.swing.*;

import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import java.nio.*;

/**
 * This is an illustration of the selection mode and name stack, which detects
 * whether objects which collide with a viewing volume. First, four triangles
 * and a rectangular box representing a viewing volume are drawn (drawScene
 * routine). The green triangle and yellow triangles appear to lie within the
 * viewing volume, but the red triangle appears to lie outside it. Then the
 * selection mode is entered (selectObjects routine). Drawing to the screen
 * ceases. To see if any collisions occur, the four triangles are called. In
 * this example, the green triangle causes one hit with the name 1, and the
 * yellow triangles cause one hit with the name 3.
 *
 * @author Kiet Le (Java port)
 */

public class select//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLU glu;
    private static final int BUFSIZE = 512;
    
    public select() {
    }
    
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        select demo = new select();
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);
        //
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("select");
        frame.setSize(200, 200);
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
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_FLAT);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        drawScene(gl);
        selectObjects(gl);
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable,//
            int x, int y, int width, int height) {
//        GL gl = drawable.getGL();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
  /*
   * draw a triangle with vertices at (x1, y1), (x2, y2) and (x3, y3) at z units
   * away from the origin.
   */
    private void drawTriangle(GL gl, float x1, float y1, float x2, float y2,
            float x3, float y3, float z) {
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex3f(x1, y1, z);
        gl.glVertex3f(x2, y2, z);
        gl.glVertex3f(x3, y3, z);
        gl.glEnd();
    }
    
    /* draw a rectangular box with these outer x, y, and z values */
    private void drawViewVolume(GL gl, float x1, float x2, float y1, float y2,
            float z1, float z2) {
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(x1, y1, -z1);
        gl.glVertex3f(x2, y1, -z1);
        gl.glVertex3f(x2, y2, -z1);
        gl.glVertex3f(x1, y2, -z1);
        gl.glEnd();
        
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(x1, y1, -z2);
        gl.glVertex3f(x2, y1, -z2);
        gl.glVertex3f(x2, y2, -z2);
        gl.glVertex3f(x1, y2, -z2);
        gl.glEnd();
        
        gl.glBegin(GL.GL_LINES); /* 4 lines */
        gl.glVertex3f(x1, y1, -z1);
        gl.glVertex3f(x1, y1, -z2);
        gl.glVertex3f(x1, y2, -z1);
        gl.glVertex3f(x1, y2, -z2);
        gl.glVertex3f(x2, y1, -z1);
        gl.glVertex3f(x2, y1, -z2);
        gl.glVertex3f(x2, y2, -z1);
        gl.glVertex3f(x2, y2, -z2);
        gl.glEnd();
    }
    
  /*
   * drawScene draws 4 triangles and a wire frame which represents the viewing
   * volume.
   */
    void drawScene(GL gl) {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(40.0, 4.0 / 3.0, 1.0, 100.0);
        
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(7.5, 7.5, 12.5, 2.5, 2.5, -5.0, 0.0, 1.0, 0.0);
        gl.glColor3f(0.0f, 1.0f, 0.0f); /* green triangle */
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
        gl.glColor3f(1.0f, 0.0f, 0.0f); /* red triangle */
        drawTriangle(gl, 2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
        gl.glColor3f(1.0f, 1.0f, 0.0f); /* yellow triangles */
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
        drawViewVolume(gl, 0.0f, 5.0f, 0.0f, 5.0f, 0.0f, 10.0f);
    }
    
  /*
   * processHits prints out the contents of the selection array
   */
    private void processHits(int hits, int buffer[]) {
        int names;
        int ptr;
        
        System.out.println("hits = " + hits);
        // ptr = buffer;
        ptr = 0;
        for (int i = 0; i < hits; i++) { /* for each hit */
            names = buffer[i];
            System.out.println(" number of names for hit =  " + names);
            ptr++;
            System.out.print("  z1 is " + (float) buffer[ptr] / 0x7fffffff);
            ptr++;
            System.out.println(" z2 is " + (float) buffer[ptr] / 0x7fffffff);
            ptr++;
            System.out.print("\tthe name is ");
            for (int j = 0; j < buffer.length /* names */; j++) { /* for each name */
                System.out.print(" " + buffer[--ptr]);
                ptr++;
            }
            System.out.println();
        }
    }
    
  /*
   * selectObjects "draws" the triangles in selection mode, assigning names for
   * the triangles. Note that the third and fourth triangles share one name, so
   * that if either or both triangles intersects the viewing/clipping volume,
   * only one hit will be registered.
   */
    private void selectObjects(GL gl) {
        int selectBuf[] = new int[BUFSIZE];
        IntBuffer selectBuffer = BufferUtil.newIntBuffer(BUFSIZE);
        int hits;
        
        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL.GL_SELECT);
        
        gl.glInitNames();
        gl.glPushName(0);
        
        gl.glPushMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, 5.0, 0.0, 5.0, 0.0, 10.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glLoadName(1);
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
        gl.glLoadName(2);
        drawTriangle(gl, 2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
        gl.glLoadName(3);
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
        drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
        gl.glPopMatrix();
        gl.glFlush();
        
        hits = gl.glRenderMode(GL.GL_RENDER);
        selectBuffer.get(selectBuf);
        processHits(hits, selectBuf);
    }
    
    public void keyTyped(KeyEvent key) {
    }
    
    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
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
