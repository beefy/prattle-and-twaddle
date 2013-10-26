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
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.opengl.util.BufferUtil;
 
/**
 * Compute the histogram of the image. This program illustrates the use of the
 * glHistogram() function.
 * 
 * @author Kiet Le (Java port)
 */
public class histogram //
        extends glskeleton //
        implements GLEventListener//
        , KeyListener //
// , MouseListener //
// , MouseMotionListener //
// , MouseWheelListener //
{
    private JFrame frame;
    private KeyEvent key;
    //
    private ByteBuffer pixels;
    // private int width; not reference as params...
    // private int height;...as are all Java primitives
    private Dimension dim = new Dimension(0, 0);
    private static final int HISTOGRAM_SIZE = 256;

    public histogram() {
    }

    protected void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);

        histogram demo = new histogram();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("histogram");
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
                && gl.isFunctionAvailable("glHistogram")) {
            gl.glHistogram(GL.GL_HISTOGRAM, HISTOGRAM_SIZE, GL.GL_RGB, false);
            gl.glEnable(GL.GL_HISTOGRAM);
        } else {
            frame.setTitle("histogram: NO (optional) ARB Imaging Subset");

            SwingUtilities.updateComponentTreeUI(frame);
        }

        if (pixels == null) {
            pixels = readImage("net/letskit/redbook/data/leeds.bin", dim);
            System.out.println(pixels.toString());
        }

    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        boolean sink = false;
        if (key != null && gl.isFunctionAvailable("glHistogram"))
            switch (key.getKeyChar()) {
            case 's':
                if (gl.isExtensionAvailable("GL_ARB_imaging") && //
                        gl.isFunctionAvailable("glHistogram")) {
                    sink = !sink;
                    gl.glHistogram(GL.GL_HISTOGRAM, HISTOGRAM_SIZE, GL.GL_RGB,
                            sink);
                }
                break;

            }// key sw

        // short values[][] = new short[HISTOGRAM_SIZE][3];
        ShortBuffer values = BufferUtil.newShortBuffer(HISTOGRAM_SIZE * 3);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glRasterPos2i(1, 1);
        gl.glDrawPixels(dim.width, dim.height, //
                GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);

        if (gl.isExtensionAvailable("GL_ARB_imaging")//
                && gl.isFunctionAvailable("glGetHistogram")) {
            gl.glGetHistogram(GL.GL_HISTOGRAM, true, GL.GL_RGB,//
                    GL.GL_UNSIGNED_SHORT, values);
        }

        /* Plot histogram */
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2));
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2 + 1));// [i][1]);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2 + 2));// [i][2]);
        gl.glEnd();

        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 256, 0, 10000, -1.0, 1.0);
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
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

}
