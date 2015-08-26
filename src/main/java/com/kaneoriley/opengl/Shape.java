package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

@Accessors(prefix = "m")
abstract class Shape {

    abstract static class Helper<T extends Shape> {

        public abstract void preDraw();

        public abstract void render(@NonNull T shape, @NonNull float[] mvpMatrix);

        public abstract void postDraw();
    }

    @Getter
    static class Attribute {

        @NonNull
        private final String mName;

        private final int mIndex;

        Attribute(@NonNull String name, int index) {
            mName = name;
            mIndex = index;
        }
    }

    @NonNull
    private final float[] mModelMatrix = new float[16];

    @NonNull
    private final float[] mTranslation = { 0f, 0f, 0f };

    @NonNull
    private final float[] mScale = { 1f, 1f, 1f };

    @NonNull
    private final float[] mColor = { 1f, 1f, 1f, 1f };

    Shape() {
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    @NonNull
    protected float[] getObjectMatrix(@NonNull float[] mvpMatrix) {
        float[] temp = mModelMatrix.clone();
        Matrix.translateM(temp, 0, mTranslation[0], mTranslation[1], mTranslation[2]);
        Matrix.scaleM(temp, 0, mScale[0], mScale[1], mScale[2]);
        Matrix.multiplyMM(temp, 0, mvpMatrix, 0, temp, 0);
        return temp;
    }

    @NonNull
    float[] getColorMatrix() {
        return mColor;
    }

    @NonNull
    public float[] getColor() {
        return Arrays.copyOfRange(mColor, 0, 3);
    }

    public void setColor(@NonNull float[] color) {
        if (color.length != 3) {
            throw new IllegalArgumentException("color matrix must have exactly 3 values, for RGB");
        } else {
            System.arraycopy(color, 0, mColor, 0, 3);
        }
    }

    public void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    @NonNull
    public float[] getTranslation() {
        return mTranslation;
    }

    public float getTranslationX() {
        return mTranslation[0];
    }

    public float getTranslationY() {
        return mTranslation[1];
    }

    public float getTranslationZ() {
        return mTranslation[2];
    }

    public void setTranslation(float x, float y, float z) {
        setTranslationX(x);
        setTranslationY(y);
        setTranslationZ(z);
    }

    public void setTranslationX(float x) {
        mTranslation[0] = x;
    }

    public void setTranslationY(float y) {
        mTranslation[1] = y;
    }

    public void setTranslationZ(float z) {
        mTranslation[2] = z;
    }

    @NonNull
    public float[] getScale() {
        return mScale;
    }

    public float getScaleX() {
        return mScale[0];
    }

    public float getScaleY() {
        return mScale[1];
    }

    public float getScaleZ() {
        return mScale[2];
    }

    public void setScale(float x, float y, float z) {
        setScaleX(x);
        setScaleY(y);
        setScaleZ(z);
    }

    public void setScaleX(float x) {
        mScale[0] = x;
    }

    public void setScaleY(float y) {
        mScale[1] = y;
    }

    public void setScaleZ(float z) {
        mScale[2] = z;
    }

    protected static int createProgram(@NonNull String vertexShaderCode, @NonNull String fragmentShaderCode) {
        int vertexShader = Renderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = Renderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

    static FloatBuffer generateFloatBuffer(@NonNull float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    static ShortBuffer generateShortBuffer(@NonNull short[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(array);
        sb.position(0);
        return sb;
    }
}
