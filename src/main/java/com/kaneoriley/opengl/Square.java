package com.kaneoriley.opengl;

import android.support.annotation.NonNull;

@SuppressWarnings("unused")
public class Square extends Rectangle {

    public Square(@NonNull float[] desiredColor, float size, int program) {
        this(desiredColor, size, 0f, program);
    }

    public Square(@NonNull float[] desiredColor, float size, float depth, int program) {
        super(desiredColor, size, size, depth, program);
    }
}
