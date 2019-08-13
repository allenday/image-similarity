package com.allenday.image.distance;

public abstract class AbstractDistance_UDWV extends AbstractDistance {
    @Override
    protected Double getDimensionWeight(Integer dimension) {
        return 1d;
    }
}
