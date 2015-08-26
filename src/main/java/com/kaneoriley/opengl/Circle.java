package com.kaneoriley.opengl;

@SuppressWarnings("unused")
public class Circle extends Oval {

    public static class CircleHelper extends OvalHelper {

        public CircleHelper(float size, float depth) {
            super(size, size, depth);
        }
    }
}
