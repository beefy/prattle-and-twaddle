package net.letskit.redbook.third;
import net.letskit.redbook.glskeleton;
import net.letskit.redbook.*;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

import com.sun.opengl.util.BufferUtil;



/**
 * This program uses the color matrix to exchange the color channels of an
 * image. <br>
 * <br>
 * Red -> Green <br>
 * Green -> Blue <br>
 * Blue -> Red
 *
 * @author Kiet Le (Java port)
 */
public class colormatrix//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    
    //
    private ByteBuffer pixels;
    // private int width; not reference as params...
    // private int height;...as are all Java primitives
    private Dimension dim = new Dimension(0, 0);
    
    public colormatrix() {
    }
    
    
    public static void main(String[] args) {
        
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        canvas.setPreferredSize(new Dimension(640, 480));
        
        colormatrix demo = new colormatrix();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("colormatrix");
//        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.getContentPane().add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        float m[] = { 0.0f, 1.0f, 0.0f, 0.0f,//
        0.0f, 0.0f, 1.0f, 0.0f,//
        1.0f, 0.0f, 0.0f, 0.0f,//
        0.0f, 0.0f, 0.0f, 1.0f };
        
        if (pixels == null) {
            pixels = readImage("net/letskit/redbook/data/leeds.bin", dim);
            System.out.println(pixels.toString());
        }
        
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        gl.glMatrixMode(GL.GL_COLOR);
        gl.glLoadMatrixf(m, 0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        gl.glRasterPos2i(1, 1);
        gl.glDrawPixels(dim.width, dim.height, //
                GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
        
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, w, 0, h, -1.0, 1.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
    /**
     * @author Mike "Top Coder" Butler : fucking signed byte is gay!
     * @author Kiet "Abysmal Coder" Le : major. gay. primitive.
     */
    private ByteBuffer readImage(String filename, Dimension dim) {
        if (dim == null)
            dim = new Dimension(0, 0);
        ByteBuffer bytes = null;
        try {
            // InputStream is = getClass().getClassLoader()
            // .getResourceAsStream(filename);
            
            // FileInputStream fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(getClass()
            .getClassLoader().getResourceAsStream(filename));
            // DataInputStream dis = new DataInputStream(fis);
            
            // int width = 0, height = 0;
            dim.width = dis.readInt();
            dim.height = dis.readInt();
            System.out.println("Creating buffer, width: " + dim.width
                    + " height: " + dim.height);
            // byte[] buf = new byte[3 * dim.height * dim.width];
            bytes = BufferUtil.newByteBuffer(3 * dim.width * dim.height);
            for (int i = 0; i < bytes.capacity(); i++) {
                bytes.put(dis.readByte());
                // int b = dis.readByte();// dis.read();
                // System.out.print(b + " ");
                // if (i % 3 == 0) System.out.println();
                // bytes.put((byte) b);
                // System.out.print(bytes.get(i) + " . ");
                // if (i %3 ==0) System.out.println();
            }
            // fis.close();
            dis.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        bytes.rewind();
        return bytes;
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
