/**
 *
 */
package net.letskit.redbook.fourth;
import java.awt.Dimension;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.sun.opengl.util.BufferUtil;

/**
 * This program demonstrates multiple vertex arrays, specifically the OpenGL
 * routine glMultiDrawElements(), but it's a bitch to setup--so I use
 * DrawElements in a loop instead.
 *
 * @author Kiet Le (JOGL port)
 */
public class mvarray //
        extends glskeleton//
        implements GLEventListener //
        , KeyListener {
    private GLU glu;
    
    private int vertices[] = { 25, 25,//
    75, 75,//
    100, 125,//
    150, 75,//
    200, 175,//
    250, 150,//
    300, 125,//
    100, 200,//
    150, 250,//
    200, 225,//
    250, 300,//
    300, 250 };
    
    private IntBuffer vertexBuf = //
            BufferUtil.newIntBuffer(vertices.length);
    
    private byte oneIndices[] = { 0, 1, 2, 3, 4, 5, 6 };
    
    private byte twoIndices[] = { 1, 7, 8, 9, 10, 11 };
    
    private int count[] = { 7, 6 };
    
    private ByteBuffer indices[] = {//
        BufferUtil.newByteBuffer(oneIndices.length),
        BufferUtil.newByteBuffer(twoIndices.length) };
    
    // static GLvoid * indices[2] = {oneIndices, twoIndices};
    {
        vertexBuf.put(vertices);
        indices[0].put(oneIndices);
        indices[1].put(twoIndices);
        
        vertexBuf.rewind();
        indices[0].rewind();
        indices[1].rewind();
    }
        
        private boolean mde_bug;
        
        /**
         *
         */
        public mvarray() {
        }
        
        private void setupPointer(GL gl) {
            gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
            gl.glVertexPointer(2, GL.GL_INT, 0, vertexBuf);
        }
        
    /*
     * (non-Javadoc)
     *
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
        public void display(GLAutoDrawable drawable) {
            final GL gl = drawable.getGL();
            //
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            
            if (mde_bug)
                gl.glMultiDrawElements(GL.GL_LINE_STRIP, count, 0,//
                        GL.GL_UNSIGNED_BYTE, indices, 2);
            else {
                // workaround for glMultiDrawElem bug before July
                for (int i = 0; i < indices.length; i++)
                    gl.glDrawElements(GL.GL_LINE_STRIP, count[i], //
                            GL.GL_UNSIGNED_BYTE, indices[i]);
            }
            gl.glFlush();
        }
        
    /*
     * (non-Javadoc)
     *
     * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable,
     *      boolean, boolean)
     */
        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
                boolean deviceChanged) {
        }
        
    /*
     * (non-Javadoc)
     *
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
        public void init(GLAutoDrawable drawable) {
            final GL gl = drawable.getGL();
            glu = new GLU();
            //
            
            mde_bug = !gl.isFunctionAvailable("glMultiDrawElements");
            System.out.println("glMultiDrawElements bug: " + mde_bug);
            
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glShadeModel(GL.GL_SMOOTH);
            setupPointer(gl);
        }
        
    /*
     * (non-Javadoc)
     *
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable,
     *      int, int, int, int)
     */
        public void reshape(GLAutoDrawable drawable, int x, int y, //
                int width, int height) {
            final GL gl = drawable.getGL();
            //
            gl.glViewport(0, 0, width, height);
            
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            
            glu.gluOrtho2D(0.0, (double) width, 0.0, (double) height);
            
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
        }
        
        /**
         * @param args
         */
        public static void main(String[] args) {
            GLCapabilities caps = new GLCapabilities();
            caps.setSampleBuffers(true);
            GLJPanel canvas = new GLJPanel(caps);
            canvas.setPreferredSize(new Dimension(350,350));
            
            mvarray demo = new mvarray();
            canvas.addGLEventListener(demo);
            demo.setCanvas(canvas);
            demo.setDefaultListeners(demo);
            
//            JFrame.setDefaultLookAndFeelDecorated(true);
            JFrame frame = new JFrame("mvarray");
            frame.setSize(350, 350);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.getContentPane().add(canvas);
            frame.pack();
            frame.setVisible(true);
            frame.setFocusable(false);
            canvas.requestFocusInWindow();
        }
        
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    super.runExit();
                    break;
                    
                default:
                    break;
            }
        }
        
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub
        }
        
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub
        }
        
}
