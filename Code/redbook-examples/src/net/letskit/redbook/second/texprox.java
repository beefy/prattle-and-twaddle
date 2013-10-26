package net.letskit.redbook.second;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

/**
 * The brief program illustrates use of texture proxies. This program only
 * prints out some messages about whether certain size textures are supported
 * and then exits.
 * 
 * @author Kiet Le (Java port)
 */
public class texprox//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{

    //

    public texprox() {
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        GLJPanel canvas = new GLJPanel(caps);
        texprox demo = new texprox();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);

//        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texprox");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();

        System.out.println("This program demonstrates a feature "
                + "which is not in OpenGL Version 1.0.");
        System.out.println("If your implementation of OpenGL "
                + "Version 1.0 has the right extensions,");
        System.out.println("you may be able to modify this "
                + "program to make it run.");
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        int proxyComponents[] = new int[1];

        gl.glTexImage2D(GL.GL_PROXY_TEXTURE_2D, 0, GL.GL_RGBA8, 64, 64, 0,
                GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
        gl.glGetTexLevelParameteriv(GL.GL_PROXY_TEXTURE_2D, 0,
                GL.GL_TEXTURE_COMPONENTS, proxyComponents, 0);
        System.out.println("proxyComponents are " + proxyComponents[0]);
        if (proxyComponents[0] == GL.GL_RGBA8) //
            System.out.println("proxy allocation succeeded");
        else
            System.out.println("proxy allocation failed");
        //
        gl.glTexImage2D(GL.GL_PROXY_TEXTURE_2D, 0, GL.GL_RGBA16, 2048, 2048, 0,
                GL.GL_RGBA, GL.GL_UNSIGNED_SHORT, null);
        gl.glGetTexLevelParameteriv(GL.GL_PROXY_TEXTURE_2D, 0,
                GL.GL_TEXTURE_COMPONENTS, proxyComponents, 0);
        System.out.println("proxyComponents are " + proxyComponents[0]);
        if (proxyComponents[0] == GL.GL_RGBA16) //
            System.out.println("proxy allocation succeeded");
        else
            System.out.println("proxy allocation failed");
    }

    public void display(GLAutoDrawable drawable) {
        drawable.getGL().glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }
}
