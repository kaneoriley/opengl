package com.kaneoriley.opengl;

@SuppressWarnings("unused")
public class Square extends Rectangle {

    public static class SquareHelper extends RectangleHelper {

        public SquareHelper(float size, float depth) {
            super(size, size, depth);
        }
    }
}
