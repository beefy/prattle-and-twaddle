package net.letskit.redbook.third;
import net.letskit.redbook.glskeleton;

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
import javax.swing.SwingUtilities;

import com.sun.opengl.util.BufferUtil;

/**
 * Determine the minimum and maximum values of a group of pixels. This
 * demonstrates use of the glMinmax() call.
 *
 * @author Kiet Le (Java port)
 */
public class minmax//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener //
{
    private JFrame frame;
    
    private ByteBuffer pixels;
    // private int width; not reference as params...
    // private int height;...as are all Java primitives
    private Dimension dim = new Dimension(0, 0);
    
    // private static final int HISTOGRAM_SIZE = 256;
    
    public minmax() {
    }
    
    protected void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        
        minmax demo = new minmax();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);
        
//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("minmax");
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        demo.setFrame(frame);
        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }
    
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        if (gl.isExtensionAvailable("GL_ARB_imaging") //
        && gl.isFunctionAvailable("glMinmax")) {
            gl.glMinmax(GL.GL_MINMAX, GL.GL_RGB, false);
            gl.glEnable(GL.GL_MINMAX);
        } else {
            frame.setTitle("minmax: NO (optional) ARB Imaging Subset");
            SwingUtilities.updateComponentTreeUI(frame);
        }
        
        pixels = readImage("net/letskit/redbook/data/leeds.bin", dim);
        System.out.println(pixels.toString());
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        // byte [] values=new byte[6];
        ByteBuffer values = BufferUtil.newByteBuffer(6);
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glRasterPos2i(1, 1);
        gl.glDrawPixels(dim.width, dim.height, //
                GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
        gl.glFlush();
        
        if (gl.isExtensionAvailable("GL_ARB_imaging") //
        && gl.isFunctionAvailable("glGetMinmax")) {
            gl.glGetMinmax(GL.GL_MINMAX, true, //
                    GL.GL_RGB, GL.GL_UNSIGNED_BYTE, values);
            System.out.println(" Red   : min = %d " //
                    + values.get(0) + ", max = " + values.get(3));
            System.out.println(" Green : min = %d " //
                    + values.get(1) + ", max = " + values.get(4));
            System.out.println(" Blue  : min = %d " //
                    + values.get(2) + " max = " + values.get(5));
        }
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
            DataInputStream dis = new DataInputStream(getClass()
            .getClassLoader().getResourceAsStream(filename));
            dim.width = dis.readInt();
            dim.height = dis.readInt();
            System.out.println("Creating buffer, width: " + dim.width
                    + " height: " + dim.height);
            
            bytes = BufferUtil.newByteBuffer(3 * dim.width * dim.height);
            for (int i = 0; i < bytes.capacity(); i++) {
                bytes.put(dis.readByte());
                
            }
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
        super.refresh();
    }
    
    public void keyReleased(KeyEvent key) {
    }
    
}
