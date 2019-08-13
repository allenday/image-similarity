package com.allenday.image.distance;


public abstract class AbstractDistance_WDUV extends AbstractDistance {
    @Override
    protected Double getVectorWeight(Integer dimension, Integer Position) {
        return 1d;
    }
}
