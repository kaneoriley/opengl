package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class Oval extends Shape {

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 mvpMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = mvpMatrix * vPosition;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform vec4 color;" +
                    "void main() {" +
                    "  gl_FragColor = color;" +
                    "}";

    private static final int COORDS_PER_VERTEX = 2;

    private static final int VERTEX_STRIDE = 12;

    public Oval(@NonNull float[] desiredColor, float width, float height, int program) {
        this(desiredColor, width, height, 0f, program);
    }

    public Oval(@NonNull float[] desiredColor, float width, float height, float depth, int program) {
        super(desiredColor, program);

        float[] vertices = new float[364 * 3];

        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for (int i = 1; i < 364; i++) {
            vertices[(i * 3)] = (float) (Math.cos((Math.PI / 180) * (float) i)) * (width / 2f);
            vertices[(i * 3) + 1] = (float) (Math.sin((Math.PI / 180) * (float) i)) * (height / 2f);
            vertices[(i * 3) + 2] = depth;
        }

        generateVertexBuffer(vertices);
    }

    public final void draw(@NonNull float[] mvpMatrix) {
        GLES20.glUseProgram(getProgram());

        float[] objectMatrix = getObjectMatrix(mvpMatrix);

        int positionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, getVertexBuffer());

        int colorHandle = GLES20.glGetUniformLocation(getProgram(), "color");
        GLES20.glUniform4fv(colorHandle, 1, getColor(), 0);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "mvpMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, objectMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public static int createProgram() {
        return createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}
