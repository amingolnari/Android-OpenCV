package org.opencv.core;

public class Point {
    public double x;
    public double y;

    public Point(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

    public Point() {
        this(0.0d, 0.0d);
    }

    public Point(double[] vals) {
        this();
        set(vals);
    }

    public void set(double[] vals) {
        double d = 0.0d;
        if (vals != null) {
            this.x = vals.length > 0 ? vals[0] : 0.0d;
            if (vals.length > 1) {
                d = vals[1];
            }
            this.y = d;
            return;
        }
        this.x = 0.0d;
        this.y = 0.0d;
    }

    public Point clone() {
        return new Point(this.x, this.y);
    }

    public double dot(Point p) {
        return (this.x * p.x) + (this.y * p.y);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = ((int) ((temp >>> 32) ^ temp)) + 31;
        long temp2 = Double.doubleToLongBits(this.y);
        return (result * 31) + ((int) ((temp2 >>> 32) ^ temp2));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point it = (Point) obj;
        if (this.x == it.x && this.y == it.y) {
            return true;
        }
        return false;
    }

    public boolean inside(Rect r) {
        return r.contains(this);
    }

    public String toString() {
        return "{" + this.x + ", " + this.y + "}";
    }
}
