package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

import java.nio.FloatBuffer;

@SuppressWarnings("unused")
public class Oval extends Shape {

    public static class OvalHelper extends Helper<Oval> {

        @NonNull
        private final FloatBuffer mVertexBuffer;

        private final int mProgram;

        public OvalHelper(float width, float height, float depth) {
            mProgram = createProgram();
            mVertexBuffer = createVertexBuffer(width, height, depth);
        }

        public void preDraw() {
            Oval.preDraw(mProgram);
        }

        public void render(@NonNull Oval oval, @NonNull float[] mvpMatrix) {
            oval.render(mvpMatrix, mProgram, mVertexBuffer);
        }

        public void postDraw() {
            // Do nothing
        }
    }

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

    private static final int COORDS_PER_VERTEX = 2;

    private static final int VERTEX_STRIDE = 12;

    @NonNull
    private static FloatBuffer createVertexBuffer(float width, float height, float depth) {

        float[] vertices = new float[364 * 3];

        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for (int i = 1; i < 364; i++) {
            vertices[(i * 3)] = (float) (Math.cos((Math.PI / 180) * (float) i)) * (width / 2f);
            vertices[(i * 3) + 1] = (float) (Math.sin((Math.PI / 180) * (float) i)) * (height / 2f);
            vertices[(i * 3) + 2] = depth;
        }

        return Shape.generateFloatBuffer(vertices);
    }

    private static void preDraw(int program) {
        GLES20.glUseProgram(program);
    }

    private void render(@NonNull float[] mvpMatrix, int program, @NonNull FloatBuffer vertexBuffer) {
        float[] objectMatrix = getObjectMatrix(mvpMatrix);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE,
                vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, getColorMatrix(), 0);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, objectMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private static int createProgram() {
        return createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}
