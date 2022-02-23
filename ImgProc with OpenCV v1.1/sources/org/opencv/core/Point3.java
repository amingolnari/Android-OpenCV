package org.opencv.core;

public class Point3 {
    public double x;
    public double y;
    public double z;

    public Point3(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public Point3() {
        this(0.0d, 0.0d, 0.0d);
    }

    public Point3(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = 0.0d;
    }

    public Point3(double[] vals) {
        this();
        set(vals);
    }

    public void set(double[] vals) {
        double d;
        double d2 = 0.0d;
        if (vals != null) {
            this.x = vals.length > 0 ? vals[0] : 0.0d;
            if (vals.length > 1) {
                d = vals[1];
            } else {
                d = 0.0d;
            }
            this.y = d;
            if (vals.length > 2) {
                d2 = vals[2];
            }
            this.z = d2;
            return;
        }
        this.x = 0.0d;
        this.y = 0.0d;
        this.z = 0.0d;
    }

    public Point3 clone() {
        return new Point3(this.x, this.y, this.z);
    }

    public double dot(Point3 p) {
        return (this.x * p.x) + (this.y * p.y) + (this.z * p.z);
    }

    public Point3 cross(Point3 p) {
        return new Point3((this.y * p.z) - (this.z * p.y), (this.z * p.x) - (this.x * p.z), (this.x * p.y) - (this.y * p.x));
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.x);
        int result = ((int) ((temp >>> 32) ^ temp)) + 31;
        long temp2 = Double.doubleToLongBits(this.y);
        int result2 = (result * 31) + ((int) ((temp2 >>> 32) ^ temp2));
        long temp3 = Double.doubleToLongBits(this.z);
        return (result2 * 31) + ((int) ((temp3 >>> 32) ^ temp3));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point3)) {
            return false;
        }
        Point3 it = (Point3) obj;
        if (this.x == it.x && this.y == it.y && this.z == it.z) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "{" + this.x + ", " + this.y + ", " + this.z + "}";
    }
}
