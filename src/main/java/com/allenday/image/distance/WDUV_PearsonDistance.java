package com.allenday.image.distance;

import com.allenday.image.Distance;
import com.allenday.image.ImageFeatures;

import java.util.List;
import java.util.Vector;

public class WDUV_PearsonDistance extends AbstractDistance_WDUV implements Distance {
    private static final Double[] COR_WEIGHT = {0.6d, 0.8d, 0.8d, 1.2d, 1.6d, 1.0d}; //NB 6th dimension is a hack for the full cor

    @Override
    public Double getScalarDistance(List<Vector<Double>> a, List<Vector<Double>> b) {
        Vector<Double> aConcat = new Vector<>();
        Vector<Double> bConcat = new Vector<>();
        aConcat.setSize(ImageFeatures.DIMENSIONS * a.get(0).size());
        bConcat.setSize(ImageFeatures.DIMENSIONS * b.get(0).size());

        // do cor across all dimensions
        for (int d = 0; d < ImageFeatures.DIMENSIONS; d++) {
            for (int i = 0; i < a.get(d).size(); i++) {
                aConcat.set(d * a.get(0).size() + i, a.get(d).get(i));
                bConcat.set(d * b.get(0).size() + i, b.get(d).get(i));
            }
        }
        return getScalarDistance(5, aConcat, bConcat); //uses fake 6th dimension
    }

    @Override
    protected Double getDimensionWeight(Integer d) {
        return COR_WEIGHT[d];
    }


    @Override
    public Double getScalarDistance(Integer dimension, Vector<Double> a, Vector<Double> b) {
        double sumXxY = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXxX = 0;
        double sumYxY = 0;

        for (int i = 0; i < a.size(); i++) {
            double value1 = a.get(i) * getVectorWeight(dimension, i);
            double value2 = b.get(i) * getVectorWeight(dimension, i);
            sumXxY += value1 * value2;
            sumX += value1;
            sumY += value2;
            sumXxX += value1 * value1;
            sumYxY += value2 * value2;
        }

        return getDimensionWeight(dimension) * (a.size() * sumXxY - sumX * sumY) /
                Math.sqrt((a.size() * sumXxX - sumX * sumX) * (a.size() * sumYxY - sumY * sumY));
    }

    @Override
    public Double getVectorNorm(Integer d, Vector<Double> v) {
        //FIXME
        return null;
    }
}
