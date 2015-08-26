package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

@SuppressWarnings("unused")
public class Rectangle extends Shape {

    public static class RectangleHelper extends Helper<Rectangle> {

        @NonNull
        private final FloatBuffer mVertexBuffer;

        @NonNull
        private final ShortBuffer mDrawListBuffer;

        private final int mProgram;

        public RectangleHelper(float width, float height, float depth) {
            mProgram = createProgram();
            mDrawListBuffer = createDrawListBuffer();
            mVertexBuffer = createVertexBuffer(width, height, depth);
        }

        public void preDraw() {
            Rectangle.preDraw(mProgram);
        }

        public void render(@NonNull Rectangle rectangle, @NonNull float[] mvpMatrix) {
            rectangle.render(mvpMatrix, mProgram, mVertexBuffer, mDrawListBuffer);
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

    private static final int COORDS_PER_VERTEX = 3;

    private static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };

    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    @NonNull
    private static FloatBuffer createVertexBuffer(float width, float height, float depth) {

        float right = width / 2;
        float left = -right;

        float bottom = height / 2;
        float top = -bottom;

        float[] vertices = new float[] {
                left, top, depth, left, bottom, depth,
                right, bottom, depth, right, top, depth
        };

        return Shape.generateFloatBuffer(vertices);
    }

    @NonNull
    private static ShortBuffer createDrawListBuffer() {
        return Shape.generateShortBuffer(DRAW_ORDER);
    }

    private static void preDraw(int program) {
        GLES20.glUseProgram(program);
    }

    private void render(@NonNull float[] mvpMatrix, int program, @NonNull FloatBuffer vertexBuffer,
                             @NonNull ShortBuffer drawListBuffer) {
        float[] objectMatrix = getObjectMatrix(mvpMatrix);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE,
                vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, getColorMatrix(), 0);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, objectMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private static int createProgram() {
        return createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
    }
}
