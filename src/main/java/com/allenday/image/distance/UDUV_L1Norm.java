package com.allenday.image.distance;

import com.allenday.image.Distance;
import com.allenday.image.ImageFeatures;

import java.util.List;
import java.util.Vector;

public class UDUV_L1Norm extends AbstractDistance_UDUV implements Distance {
    public Double getScalarDistance(Integer d, Vector<Double> a, Vector<Double> b) {
        Double r = 0d;
        for (int i = 0; i < a.size(); i++) {
            r += super.getVectorWeight(d, i) * (a.get(i) - b.get(i));
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
        double dist = 0d;
        for (Integer d = 0; d < ImageFeatures.DIMENSIONS; d++) {
            double vDist = 0d;
            for (int i = 0; i < a.size(); i++) {
                vDist += getVectorWeight(d, i) * (a.get(d).get(i) - b.get(d).get(i));
            }
            dist += getDimensionWeight(d) * vDist;
        }
        return dist;
    }

    @Override
    public Double getDimensionWeight(Integer dimension) {
        return 1d;
    }

    @Override
    public Double getVectorWeight(Integer dimension, Integer position) {
        return 1d;
    }
}
