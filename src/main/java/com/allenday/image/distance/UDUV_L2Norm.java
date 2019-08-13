package com.allenday.image.distance;

import com.allenday.image.Distance;

import java.util.Vector;

class UDUV_L2Norm extends UDUV_L1Norm implements Distance {
    public Double distance(Integer dimension, Vector<Double> vec) {
        Double result = 0d;
        for (Double aDouble : vec) result += aDouble;
        return Math.sqrt(result);
    }

    @Override
    public Double getVectorNorm(Integer d, Vector<Double> v) {
        return null;
    }
}
