package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Rectangle extends Shape {

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private static final int COORDS_PER_VERTEX = 3;

    private static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };

    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    public Rectangle(@NonNull float[] desiredColor, float width, float height, int program) {
        this(desiredColor, width, height, 0f, program);
    }

    public Rectangle(@NonNull float[] desiredColor, float width, float height, float depth, int program) {
        super(desiredColor, program);

        float right = width / 2;
        float left = -right;

        float bottom = height / 2;
        float top = -bottom;

        float[] vertices = new float[] { left, top, depth, left, bottom, depth,
                                         right, bottom, depth, right, top, depth };

        generateVertexBuffer(vertices);
        generateDrawListBuffer(DRAW_ORDER);
    }

    public final void baseDraw(float[] mvpMatrix) {
        GLES20.glUseProgram(getProgram());

        int positionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, getVertexBuffer());

        int colorHandle = GLES20.glGetUniformLocation(getProgram(), "vColor");
        GLES20.glUniform4fv(colorHandle, 1, getColor(), 0);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, getDrawListBuffer());
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public static int createProgram() {
        return createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}
