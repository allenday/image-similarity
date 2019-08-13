package com.allenday.image.distance;

import com.allenday.image.Distance;
import com.allenday.image.ImageFeatures;

import java.util.List;
import java.util.Vector;

public class UDWV_L1Norm extends AbstractDistance_UDWV implements Distance {
    private static final double[][] MAT_WEIGHT = {
            {0.2d, 0.25d, 0.3d, 0.3d, 0.3d, 0.35d, 0.4d, 0.45d, 0.5d, 0.60d, 0.7d, 0.75d, 0.8d, 0.55d, 0.3d, 0.4d}, //R
            {0.2d, 0.35d, 0.5d, 0.6d, 0.7d, 0.80d, 0.9d, 0.95d, 1.0d, 1.00d, 1.0d, 0.95d, 0.9d, 0.65d, 0.4d, 0.4d}, //G
            {0.2d, 0.35d, 0.5d, 0.6d, 0.7d, 0.80d, 0.9d, 0.95d, 1.0d, 0.95d, 0.9d, 0.90d, 0.9d, 0.65d, 0.4d, 0.4d}, //B
            {1.0d, 1.00d, 1.0d, 1.0d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.0d}, //T
            {1.0d, 1.00d, 1.0d, 1.0d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.00d, 1.0d, 1.0d}, //C
    };

    public Double getScalarDistance(Integer d, Vector<Double> a, Vector<Double> b) {
        Double r = 0d;
        for (int i = 0; i < a.size(); i++) {
            r += getVectorWeight(d, i) * (a.get(i) - b.get(i));
        }
        return super.getDimensionWeight(d) * r;
    }

    @Override
    public Double getVectorNorm(Integer d, Vector<Double> v) {
        //FIXME
        return null;
    }

    @Override
    public Double getScalarDistance(List<Vector<Double>> a, List<Vector<Double>> b) {
        Double totalDist = 0d;
        for (Integer d = 0; d < ImageFeatures.DIMENSIONS; d++) {
            totalDist += getScalarDistance(d, a.get(d), b.get(d));
        }
        return totalDist;
    }

    @Override
    protected Double getVectorWeight(Integer d, Integer p) {
        return MAT_WEIGHT[d][p];
    }
}
