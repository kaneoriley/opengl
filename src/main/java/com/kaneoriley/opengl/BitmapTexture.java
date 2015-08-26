package com.kaneoriley.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

@SuppressWarnings("unused")
public class BitmapTexture extends Shape {

    public static class BitmapTextureHelper extends Helper<BitmapTexture> {

        @NonNull
        private final FloatBuffer mTextureBuffer;

        @NonNull
        private final FloatBuffer mVertexBuffer;

        @NonNull
        private final ShortBuffer mDrawListBuffer;

        private final int mProgram;

        private final int mTextureDataHandle;

        public BitmapTextureHelper(@NonNull Context context,
                                   float width,
                                   float height,
                                   float depth,
                                   @DrawableRes int resourceId) {
            mProgram = createProgram();
            mTextureBuffer = createTextureBuffer();
            mVertexBuffer = createVertexBuffer(width, height, depth);
            mDrawListBuffer = createDrawListBuffer();
            mTextureDataHandle = loadTexture(context, resourceId);
        }

        public void preDraw() {
            BitmapTexture.preDraw(mProgram);
        }

        public void render(@NonNull BitmapTexture bitmapTexture, @NonNull float[] mvpMatrix) {
            bitmapTexture.render(mvpMatrix, mProgram, mVertexBuffer, mDrawListBuffer, mTextureDataHandle, mTextureBuffer);
        }

        public void postDraw() {
            // Do nothing
        }
    }

    private static final String VERTEX_SHADER_CODE =
            "attribute vec2 aTexCoordinate;" +
                    "varying vec2 vTexCoordinate;" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  vTexCoordinate = aTexCoordinate;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D uTexture;" +
                    "varying vec2 vTexCoordinate;" +
                    "void main() {" +
                    "  gl_FragColor = (vColor * texture2D(uTexture, vTexCoordinate));" +
                    "}";

    private static final int COORDS_PER_VERTEX = 3;

    private static final short DRAW_ORDER[] = { 0, 1, 2, 0, 2, 3 };

    private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    private static final int TEXTURE_COORD_DATA_SIZE = 2;

    private static final int TEXTURE_VERTEX_STRIDE = TEXTURE_COORD_DATA_SIZE * 4;

    @NonNull
    private static FloatBuffer createTextureBuffer() {

        float[] vertices = new float[] {
                0f, 1f, 0f, 0f,
                1f, 0f, 1f, 1f
        };

        return Shape.generateFloatBuffer(vertices);
    }

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

    private void render(@NonNull float[] mvpMatrix,
                        int program,
                        @NonNull FloatBuffer vertexBuffer,
                        @NonNull ShortBuffer drawListBuffer,
                        int textureDataHandle,
                        @NonNull FloatBuffer textureBuffer) {
        float[] objectMatrix = getObjectMatrix(mvpMatrix);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE,
                vertexBuffer);

        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, getColorMatrix(), 0);

        int textureUniformHandle = GLES20.glGetAttribLocation(program, "uTexture");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(program, "aTexCoordinate");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORD_DATA_SIZE, GLES20.GL_FLOAT, false,
                TEXTURE_VERTEX_STRIDE, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, objectMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, DRAW_ORDER.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
    }

    private static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    private static int createProgram() {
        Attribute attribute = new Attribute("aTexCoordinate", 0);
        return createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE, attribute);
    }
}
