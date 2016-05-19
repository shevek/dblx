/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.swing;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.annotation.Nonnull;
import javax.swing.JFrame;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author shevek
 */
@Ignore
public class PatternPanelTest {

    private final GLU glu = new GLU();

    public static class OneTriangle implements GLEventListener {

        protected static void setup(@Nonnull GL2 gl2, int width, int height) {
            gl2.glMatrixMode(GL2.GL_PROJECTION);
            gl2.glLoadIdentity();

            // coordinate system origin at lower left with width and height same as the window
            GLU glu = new GLU();
            glu.gluOrtho2D(0.0f, width, 0.0f, height);

            gl2.glMatrixMode(GL2.GL_MODELVIEW);
            gl2.glLoadIdentity();

            gl2.glViewport(0, 0, width, height);
        }

        protected static void render(@Nonnull GL2 gl2, int width, int height) {
            gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

            // draw a triangle filling the window
            gl2.glLoadIdentity();
            gl2.glBegin(GL.GL_TRIANGLES);
            gl2.glColor3f(1, 0, 0);
            gl2.glVertex2f(0, 0);
            gl2.glColor3f(0, 1, 0);
            gl2.glVertex2f(width, 0);
            gl2.glColor3f(0, 0, 1);
            gl2.glVertex2f(width / 2, height);
            gl2.glEnd();
        }

        @Override
        public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
            setup(glautodrawable.getGL().getGL2(), width, height);
        }

        @Override
        public void init(GLAutoDrawable glautodrawable) {
        }

        @Override
        public void dispose(GLAutoDrawable glautodrawable) {
        }

        @Override
        public void display(GLAutoDrawable glautodrawable) {
            render(glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight());
        }
    }

    public class SimpleScene implements GLEventListener {

        private double theta = 0;
        private double s = 0;
        private double c = 0;

        @Override
        public void display(GLAutoDrawable drawable) {
            update();
            render(drawable);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
        }

        @Override
        public void init(GLAutoDrawable drawable) {
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        }

        private void update() {
            theta += 0.01;
            s = Math.sin(theta);
            c = Math.cos(theta);
        }

        private void render(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.glClear(GL.GL_COLOR_BUFFER_BIT);

            // draw a triangle filling the window
            gl.glBegin(GL.GL_TRIANGLES);
            gl.glColor3f(1, 0, 0);
            gl.glVertex2d(-c, -c);
            gl.glColor3f(0, 1, 0);
            gl.glVertex2d(0, c);
            gl.glColor3f(0, 0, 1);
            gl.glVertex2d(s, -s);
            gl.glEnd();
        }
    }

    @Test
    public void testGL() throws InterruptedException {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas glcanvas = new GLCanvas(caps);

        glcanvas.addGLEventListener(new SimpleScene());

        final JFrame jframe = new JFrame("One Triangle Swing GLCanvas");
        jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowevent) {
                jframe.dispose();
                System.exit(0);
            }
        });

        jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
        jframe.setSize(640, 480);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        {
            FPSAnimator animator = new FPSAnimator(glcanvas, 60);
            animator.start();
        }

        Thread.sleep(10000);
    }

}
