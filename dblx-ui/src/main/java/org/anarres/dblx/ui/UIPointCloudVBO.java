/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import com.jogamp.opengl.GL2;
import heronarts.lx.model.LXPoint;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;
import processing.opengl.PGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;

/**
 *
 * @author shevek
 */
public class UIPointCloudVBO {

    private final Core core;
    PShader shader;
    FloatBuffer vertexData;
    int vertexBufferObjectName;

    UIPointCloudVBO(@Nonnull Core core) {
        // Load shader
        shader = core.lx.applet.loadShader("frag.glsl", "vert.glsl");
        // Create a buffer for vertex data
        vertexData = ByteBuffer
                .allocateDirect(model.points.size() * 7 * Float.SIZE / 8)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        // Put all the points into the buffer
        vertexData.rewind();
        for (LXPoint point : model.points) {
            // Each point has 7 floats, XYZRGBA
            vertexData.put(point.x);
            vertexData.put(point.y);
            vertexData.put(point.z);
            vertexData.put(0f);
            vertexData.put(0f);
            vertexData.put(0f);
            vertexData.put(1f);
        }
        vertexData.position(0);

        // Generate a buffer binding
        IntBuffer resultBuffer = ByteBuffer
                .allocateDirect(1 * Integer.SIZE / 8)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        PGL pgl = core.lx.applet.beginPGL();
        pgl.genBuffers(1, resultBuffer); // Generates a buffer, places its id in resultBuffer[0]
        vertexBufferObjectName = resultBuffer.get(0); // Grab our buffer name
        core.lx.applet.endPGL();
    }

    void draw(color[] colors) {
        // Put our new colors in the vertex data
        for (int i = 0; i < colors.length; ++i) {
            color c = colors[i];

            vertexData.put(7 * i + 3, (0xff & (c >> 16)) / 255f); // R
            vertexData.put(7 * i + 4, (0xff & (c >> 8)) / 255f); // G
            vertexData.put(7 * i + 5, (0xff & (c)) / 255f); // B
        }

        PGL pgl = core.lx.applet.beginPGL();

        // Bind to our vertex buffer object, place the new color data
        pgl.bindBuffer(PGL.ARRAY_BUFFER, vertexBufferObjectName);
        pgl.bufferData(PGL.ARRAY_BUFFER, colors.length * 7 * Float.SIZE / 8, vertexData, PGL.DYNAMIC_DRAW);

        shader.bind();
        int vertexLocation = pgl.getAttribLocation(shader.glProgram, "vertex");
        int colorLocation = pgl.getAttribLocation(shader.glProgram, "color");
        pgl.enableVertexAttribArray(vertexLocation);
        pgl.enableVertexAttribArray(colorLocation);
        pgl.vertexAttribPointer(vertexLocation, 3, PGL.FLOAT, false, 7 * Float.SIZE / 8, 0);
        pgl.vertexAttribPointer(colorLocation, 4, PGL.FLOAT, false, 7 * Float.SIZE / 8, 3 * Float.SIZE / 8);
        GL2 gl2 = ((PJOGL) pgl).gl.getGL2();
        gl2.glPointSize(2);
        pgl.drawArrays(PGL.POINTS, 0, colors.length);
        pgl.disableVertexAttribArray(vertexLocation);
        pgl.disableVertexAttribArray(colorLocation);
        shader.unbind();

        pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        core.lx.applet.endPGL();
    }
}
