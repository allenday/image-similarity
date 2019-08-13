package com.allenday.image.distance;

import com.allenday.image.Distance;

import java.util.Vector;

public class UDWV_L2Norm extends UDWV_L1Norm implements Distance {
    private static double[][] MAT_WEIGHT = {
            {0.2d, 0.25d, 0.3d, 0.3d, 0.3d, 0.35d, 0.4d, 0.45d, 0.5d, 0.60d, 0.7d, 0.75d, 0.8d, 0.55d, 0.3d, 0.4d}, //R
            {0.2d, 0.35d, 0.5d, 0.6d, 0.7d, 0.80d, 0.9d, 0.95d, 1.0d, 1.00d, 1.0d, 0.95d, 0.9d, 0.65d, 0.4d, 0.4d}, //G
            {0.2d, 0.35d, 0.5d, 0.6d, 0.7d, 0.80d, 0.9d, 0.95d, 1.0d, 0.95d, 0.9d, 0.90d, 0.9d, 0.65d, 0.4d, 0.4d}, //B
            {1.0d, 1.00d, 1.0d, 1.0d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.0d}, //T
            {1.0d, 1.00d, 1.0d, 1.0d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.0d}, //C
    };

    @Override
    public Double getVectorNorm(Integer d, Vector<Double> v) {
        Double L1 = super.getVectorNorm(d, v);
        return Math.sqrt(L1);
    }
}
