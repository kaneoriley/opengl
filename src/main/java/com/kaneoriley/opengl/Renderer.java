package com.kaneoriley.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.annotation.CallSuper;
import lombok.extern.slf4j.Slf4j;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressWarnings("unused")
@Slf4j
public abstract class Renderer implements GLSurfaceView.Renderer {

    @CallSuper
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public static void checkGlError(String glOperation) {
        int error= GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            log.error(glOperation + ": glError {}", error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
