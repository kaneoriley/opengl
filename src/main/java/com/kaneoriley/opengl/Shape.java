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

@SuppressWarnings("MismatchedReadAndWriteOfArray")
@Accessors(prefix = "m")
public abstract class Shape {

    @NonNull
    private final float[] mModelMatrix = new float[16];

    @Getter
    @NonNull
    private final float[] mColor = new float[4];

    @Getter
    private ShortBuffer mDrawListBuffer;

    @Getter
    private FloatBuffer mVertexBuffer;

    @Getter
    private float[] mTranslation;

    @Getter
    private float[] mRotation;

    @Getter
    private float[] mScale;

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
    public float[] getModelClone() {
        return mModelMatrix.clone();
    }

    public void setAlpha(float alpha) {
        mColor[3] = alpha;
    }

    public void setTranslation(float x, float y, float z) {
        mTranslation = new float[] { x, y, z };
    }

    public void setRotation(float a, float x, float y, float z) {
        mRotation = new float[] { a, x, y, z };
    }

    public void setScale(float x, float y, float z) {
        mScale = new float[] { x, y, z };
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

    public void draw(float[] mvpMatrix) {
        float[] scratch = getModelClone();
        Matrix.translateM(scratch, 0, mTranslation[0], mTranslation[1], mTranslation[2]);
        Matrix.scaleM(scratch, 0, mScale[0], mScale[1], mScale[2]);
        Matrix.rotateM(scratch, 0, mRotation[0], mRotation[1], mRotation[2], mRotation[3]);
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, scratch, 0);
        baseDraw(scratch);
    }

    protected abstract void baseDraw(float[] mvpMatrix);
}
