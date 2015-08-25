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

@Accessors(prefix = "m")
public abstract class Shape {

    @NonNull
    private final float[] mModelMatrix = new float[16];

    @NonNull
    private final float[] mTranslate = { 0f, 0f, 0f };

    @NonNull
    private final float[] mScale = { 1f, 1f, 1f };

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @Getter
    @NonNull
    private final float[] mColor = new float[4];

    @Getter
    private ShortBuffer mDrawListBuffer;

    @Getter
    private FloatBuffer mVertexBuffer;

    @Getter
    private int mProgram;

    Shape(@NonNull float[] desiredColor, int program) {
        int colorLength = desiredColor.length;
        System.arraycopy(desiredColor, 0, mColor, 0, colorLength);
        if (colorLength < 4) {
            mColor[3] = 1.0f;
        }

        Matrix.setIdentityM(mModelMatrix, 0);
        mProgram = program;
    }

    @NonNull
    public float[] getModelMatrixClone() {
        return mModelMatrix.clone();
    }

    @NonNull
    public float[] getObjectMatrix(@NonNull float[] mvpMatrix) {
        float[] temp = getModelMatrixClone();
        Matrix.translateM(temp, 0, mTranslate[0], mTranslate[1], mTranslate[2]);
        Matrix.scaleM(temp, 0, mScale[0], mScale[1], mScale[2]);
        Matrix.multiplyMM(temp, 0, mvpMatrix, 0, temp, 0);
        return temp;
    }

    public void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    public float getTranslationX() {
        return mTranslate[0];
    }

    public float getTranslationY() {
        return mTranslate[1];
    }

    public float getTranslationZ() {
        return mTranslate[2];
    }

    public void setTranslationX(float x) {
        mTranslate[0] = x;
    }

    public void setTranslationY(float y) {
        mTranslate[1] = y;
    }

    public void setTranslationZ(float z) {
        mTranslate[2] = z;
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

    public void setScaleX(float x) {
        mScale[0] = x;
    }

    public void setScaleY(float y) {
        mScale[1] = y;
    }

    public void setScaleZ(float z) {
        mScale[2] = z;
    }

    void generateDrawListBuffer(@NonNull short[] drawOrder) {
        mDrawListBuffer = toShortBuffer(drawOrder);
    }

    void generateVertexBuffer(@NonNull float[] vertices) {
        mVertexBuffer = toFloatBuffer(vertices);
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

    public static FloatBuffer toFloatBuffer(@NonNull float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }

    public static ShortBuffer toShortBuffer(@NonNull short[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(array);
        sb.position(0);
        return sb;
    }

    protected abstract void draw(@NonNull float[] mvpMatrix);
}
