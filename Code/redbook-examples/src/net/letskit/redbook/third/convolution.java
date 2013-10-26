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
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.opengl.util.BufferUtil;

 
/**
 * Use various 2D convolutions filters to find edges in an image.
 * 
 * @author Kiet Le (Java port)
 */
public class convolution//
        extends glskeleton//
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
    private float horizontal[][] = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };

    private float vertical[][] = { { 0, 0, 0 }, { -1, 1, 0 }, { 0, 0, 0 } };

    private float laplacian[][] = { { -0.125f, -0.125f, -0.125f },
            { -0.125f, 1.0f, -0.125f }, { -0.125f, -0.125f, -0.125f } };

    private FloatBuffer horizontalBuf = BufferUtil
            .newFloatBuffer(horizontal.length * horizontal[0].length);
    private FloatBuffer verticalBuf = BufferUtil.newFloatBuffer(vertical.length
            * vertical[0].length);
    private FloatBuffer laplacianBuf = BufferUtil
            .newFloatBuffer(laplacian.length * laplacian[0].length);
    {
        for (int i = 0; i < 3; i++) {
            horizontalBuf.put(horizontal[i]);
            verticalBuf.put(vertical[i]);
            laplacianBuf.put(laplacian[i]);
        }
        horizontalBuf.rewind();
        verticalBuf.rewind();
        laplacianBuf.rewind();
    }

    public convolution() {
    }

    protected void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);

        convolution demo = new convolution();
        demo.setCanvas(canvas);
        canvas.addGLEventListener(demo);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("convolution");
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

        if (gl.isExtensionAvailable("GL_ARB_imaging")) {
            if (gl.isFunctionAvailable("glConvolutionFilter2D")) {
                System.out.println("Using the horizontal filter");
                gl.glConvolutionFilter2D(GL.GL_CONVOLUTION_2D, GL.GL_LUMINANCE, //
                        3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, horizontalBuf);
                gl.glEnable(GL.GL_CONVOLUTION_2D);
            }
        } else {
            frame.setTitle("convolution: NO ARB Imaging Subset");
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

        if (gl.isFunctionAvailable("glConvolutionFilter2D"))
            if (key != null)
                switch (key.getKeyChar()) {
                case 'h':
                    System.out.println("Using a horizontal filter");
                    gl.glConvolutionFilter2D(GL.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, horizontalBuf);
                    break;

                case 'v':
                    System.out.println("Using the vertical filter\n");
                    gl.glConvolutionFilter2D(GL.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, verticalBuf);
                    break;

                case 'l':
                    System.out.println("Using the laplacian filter\n");
                    gl.glConvolutionFilter2D(GL.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, laplacianBuf);
                    break;

                }// key sw

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
        this.key = key;
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
