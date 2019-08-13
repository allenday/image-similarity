// Hyper-Point class supporting KDTree class

package edu.wlu.cs.levy.CG;

import java.io.Serializable;

class HPoint implements Serializable {

    final double[] coord;

    HPoint(int n) {
        coord = new double[n];
    }

    HPoint(double[] x) {

        coord = new double[x.length];
        System.arraycopy(x, 0, coord, 0, x.length);
    }

    static double sqrdist(HPoint x, HPoint y) {

        return EuclideanDistance.sqrdist(x.coord, y.coord);
    }

    protected Object clone() throws CloneNotSupportedException {
        Object o = super.clone();

        return new HPoint(coord);
    }

    boolean equals(HPoint p) {

        // seems faster than java.util.Arrays.equals(), which is not
        // currently supported by Matlab anyway
        for (int i = 0; i < coord.length; ++i)
            if (coord[i] != p.coord[i])
                return false;

        return true;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (double v : coord) {
            s.append(v).append(" ");
        }
        return s.toString();
    }

}
