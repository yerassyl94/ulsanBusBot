package com.google.zxing;

public class ResultPoint {
    /* renamed from: x */
    private final float f3x;
    /* renamed from: y */
    private final float f4y;

    public ResultPoint(float x, float y) {
        this.f3x = x;
        this.f4y = y;
    }

    public final float getX() {
        return this.f3x;
    }

    public final float getY() {
        return this.f4y;
    }

    public boolean equals(Object other) {
        if (!(other instanceof ResultPoint)) {
            return false;
        }
        ResultPoint otherPoint = (ResultPoint) other;
        if (this.f3x == otherPoint.f3x && this.f4y == otherPoint.f4y) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (Float.floatToIntBits(this.f3x) * 31) + Float.floatToIntBits(this.f4y);
    }

    public String toString() {
        StringBuffer result = new StringBuffer(25);
        result.append('(');
        result.append(this.f3x);
        result.append(',');
        result.append(this.f4y);
        result.append(')');
        return result.toString();
    }

    public static void orderBestPatterns(ResultPoint[] patterns) {
        ResultPoint pointB;
        ResultPoint pointA;
        ResultPoint pointC;
        float zeroOneDistance = distance(patterns[0], patterns[1]);
        float oneTwoDistance = distance(patterns[1], patterns[2]);
        float zeroTwoDistance = distance(patterns[0], patterns[2]);
        if (oneTwoDistance >= zeroOneDistance && oneTwoDistance >= zeroTwoDistance) {
            pointB = patterns[0];
            pointA = patterns[1];
            pointC = patterns[2];
        } else if (zeroTwoDistance < oneTwoDistance || zeroTwoDistance < zeroOneDistance) {
            pointB = patterns[2];
            pointA = patterns[0];
            pointC = patterns[1];
        } else {
            pointB = patterns[1];
            pointA = patterns[0];
            pointC = patterns[2];
        }
        if (crossProductZ(pointA, pointB, pointC) < 0.0f) {
            ResultPoint temp = pointA;
            pointA = pointC;
            pointC = temp;
        }
        patterns[0] = pointA;
        patterns[1] = pointB;
        patterns[2] = pointC;
    }

    public static float distance(ResultPoint pattern1, ResultPoint pattern2) {
        float xDiff = pattern1.getX() - pattern2.getX();
        float yDiff = pattern1.getY() - pattern2.getY();
        return (float) Math.sqrt((double) ((xDiff * xDiff) + (yDiff * yDiff)));
    }

    private static float crossProductZ(ResultPoint pointA, ResultPoint pointB, ResultPoint pointC) {
        float bX = pointB.f3x;
        float bY = pointB.f4y;
        return ((pointC.f3x - bX) * (pointA.f4y - bY)) - ((pointC.f4y - bY) * (pointA.f3x - bX));
    }
}
