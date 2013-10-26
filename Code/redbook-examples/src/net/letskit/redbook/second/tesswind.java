package net.letskit.redbook.second;
import net.letskit.redbook.glskeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.swing.JFrame;


/**
 * This program demonstrates the winding rule polygon tessellation property.
 * Four tessellated objects are drawn, each with very different contours. When
 * the w key is pressed, the objects are drawn with a different winding rule.
 *
 * @author Kiet Le (Java conversion) 
 */
public class tesswind//
        extends glskeleton//
        implements GLEventListener//
        , KeyListener//
{
    private GLCanvas canvas;
    private GLU glu;
    private GLUtessellator tobj;
    private int list;
//  private int currentShape = 0;
    private double currentWinding = GLU.GLU_TESS_WINDING_ODD;
    private KeyEvent key;
    
    //
    public tesswind() {
    }
    
    
    
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(caps.getNumSamples() * 2);
        
        GLJPanel canvas = new GLJPanel(caps);
        tesswind demo = new tesswind();
        canvas.addGLEventListener(demo);
        demo.setCanvas(canvas);
        demo.setDefaultListeners(demo);
        
//      JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("tesswind");
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
    /*
     * jogl specific addition for tessellation
     */
        tessell tessCallback = new tessell(gl, glu);
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
        
        tobj = glu.gluNewTess();
        
        glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);
        glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);
        
        list = gl.glGenLists(4);
        makeNewLists(gl, glu);
    }
    
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        //
        if (key != null) {
            makeNewLists(gl, glu);
            key = null;// keyPressed re-reference again
        }
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPushMatrix();
        gl.glCallList(list);
        gl.glTranslatef(0.0f, 500.0f, 0.0f);
        gl.glCallList(list + 1);
        gl.glTranslatef(500.0f, -500.0f, 0.0f);
        gl.glCallList(list + 2);
        gl.glTranslatef(0.0f, 500.0f, 0.0f);
        gl.glCallList(list + 3);
        gl.glPopMatrix();
        gl.glFlush();
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h) glu.gluOrtho2D(0.0, 1000.0, //
                0.0, 1000.0 * (double) h / (double) w);
        else glu.gluOrtho2D(0.0, 1000.0 * (double) w / (double) h,//
                0.0, 1000.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
    
  /*
   * Make four display lists, each with a different tessellated object.
   */
    private void makeNewLists(GL gl, GLU glu) {
        int i;
        final/* static */double rects[][] = new double[][]
        { // [12][3]
            { 50.0, 50.0, 0.0 },
            { 300.0, 50.0, 0.0 },
            { 300.0, 300.0, 0.0 },
            { 50.0, 300.0, 0.0 },
            { 100.0, 100.0, 0.0 },
            { 250.0, 100.0, 0.0 },
            { 250.0, 250.0, 0.0 },
            { 100.0, 250.0, 0.0 },
            { 150.0, 150.0, 0.0 },
            { 200.0, 150.0, 0.0 },
            { 200.0, 200.0, 0.0 },
            { 150.0, 200.0, 0.0 } };
        final/* static */double spiral[][] = new double[][]
        {// [16][3] =
            { 400.0, 250.0, 0.0 },
            { 400.0, 50.0, 0.0 },
            { 50.0, 50.0, 0.0 },
            { 50.0, 400.0, 0.0 },
            { 350.0, 400.0, 0.0 },
            { 350.0, 100.0, 0.0 },
            { 100.0, 100.0, 0.0 },
            { 100.0, 350.0, 0.0 },
            { 300.0, 350.0, 0.0 },
            { 300.0, 150.0, 0.0 },
            { 150.0, 150.0, 0.0 },
            { 150.0, 300.0, 0.0 },
            { 250.0, 300.0, 0.0 },
            { 250.0, 200.0, 0.0 },
            { 200.0, 200.0, 0.0 },
            { 200.0, 250.0, 0.0 } };
        final/* static */double quad1[][] = new double[][]
        {// [4][3] =
            { 50.0, 150.0, 0.0 },
            { 350.0, 150.0, 0.0 },
            { 350.0, 200.0, 0.0 },
            { 50.0, 200.0, 0.0 } };
        final/* static */double quad2[][] = new double[][]
        { // [4][3] =
            { 100.0, 100.0, 0.0 },
            { 300.0, 100.0, 0.0 },
            { 300.0, 350.0, 0.0 },
            { 100.0, 350.0, 0.0 } };
        final/* static */double tri[][] = new double[][]
        {// [3][3] =
            { 200.0, 50.0, 0.0 },
            { 250.0, 300.0, 0.0 },
            { 150.0, 300.0, 0.0 } };
        
        glu.gluTessProperty(tobj, //
                GLU.GLU_TESS_WINDING_RULE, currentWinding);
        
        gl.glNewList(list, GL.GL_COMPILE);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 4; i++)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 4; i < 8; i++)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 8; i < 12; i++)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();
        
        gl.glNewList(list + 1, GL.GL_COMPILE);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 4; i++)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 7; i >= 4; i--)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 11; i >= 8; i--)
            glu.gluTessVertex(tobj, rects[i], 0, rects[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();
        
        gl.glNewList(list + 2, GL.GL_COMPILE);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 16; i++)
            glu.gluTessVertex(tobj, spiral[i], 0, spiral[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();
        
        gl.glNewList(list + 3, GL.GL_COMPILE);
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 4; i++)
            glu.gluTessVertex(tobj, quad1[i], 0, quad1[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 4; i++)
            glu.gluTessVertex(tobj, quad2[i], 0, quad2[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessBeginContour(tobj);
        for (i = 0; i < 3; i++)
            glu.gluTessVertex(tobj, tri[i], 0, tri[i]);
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
        gl.glEndList();
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                super.runExit();
                
            case KeyEvent.VK_W:
                if (currentWinding == GLU.GLU_TESS_WINDING_ODD) currentWinding = GLU.GLU_TESS_WINDING_NONZERO;
                else if (currentWinding == GLU.GLU_TESS_WINDING_NONZERO) currentWinding = GLU.GLU_TESS_WINDING_POSITIVE;
                else if (currentWinding == GLU.GLU_TESS_WINDING_POSITIVE) currentWinding = GLU.GLU_TESS_WINDING_NEGATIVE;
                else if (currentWinding == GLU.GLU_TESS_WINDING_NEGATIVE) currentWinding = GLU.GLU_TESS_WINDING_ABS_GEQ_TWO;
                else if (currentWinding == GLU.GLU_TESS_WINDING_ABS_GEQ_TWO) currentWinding = GLU.GLU_TESS_WINDING_ODD;
                canvas.display();
                break;
            default:
                break;
        }
    }
    
    public void keyReleased(KeyEvent e) {
    }
    
  /*
   * Tessellator callback implemenation with all the callback routines. YOu
   * could use GLUtesselatorCallBackAdapter instead. But
   */
    class tessell
            implements GLUtessellatorCallback {
        private GL gl;
        private GLU glu;
        
        public tessell(GL gl, GLU glu) {
            this.gl = gl;
            this.glu = glu;
        }
        
        public void begin(int type) {
            gl.glBegin(type);
        }
        
        public void end() {
            gl.glEnd();
        }
        
        public void vertex(Object vertexData) {
            double[] pointer;
            if (vertexData instanceof double[]) {
                pointer = (double[]) vertexData;
                if (pointer.length == 6) gl.glColor3dv(pointer, 3);
                gl.glVertex3dv(pointer, 0);
            }
            
        }
        
        public void vertexData(Object vertexData, Object polygonData) {
        }
        
    /*
     * combineCallback is used to create a new vertex when edges intersect.
     * coordinate location is trivial to calculate, but weight[4] may be used to
     * average color, normal, or texture coordinate data. In this program, color
     * is weighted.
     */
        public void combine(double[] coords, Object[] data, //
                float[] weight, Object[] outData) {
            double[] vertex = new double[3];
            
            vertex[0] = coords[0];
            vertex[1] = coords[1];
            vertex[2] = coords[2];
            outData[0] = vertex;
        }
        
        public void combineData(double[] coords, Object[] data, //
                float[] weight, Object[] outData, Object polygonData) {
        }
        
        public void error(int errnum) {
            String estring;
            
            estring = glu.gluErrorString(errnum);
            System.err.println("Tessellation Error: " + estring);
            System.exit(0);
        }
        
        public void beginData(int type, Object polygonData) {
        }
        
        public void endData(Object polygonData) {
        }
        
        public void edgeFlag(boolean boundaryEdge) {
        }
        
        public void edgeFlagData(boolean boundaryEdge, Object polygonData) {
        }
        
        public void errorData(int errnum, Object polygonData) {
        }
    }// tessellCallBack
    
}// tess
