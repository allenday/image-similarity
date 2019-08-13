package com.allenday.image.distance;

public abstract class AbstractDistance_UDUV extends AbstractDistance {
    @Override
    protected Double getDimensionWeight(Integer d) {
        return 1d;
    }

    @Override
    protected Double getVectorWeight(Integer d, Integer Position) {
        return 1d;
    }
}
