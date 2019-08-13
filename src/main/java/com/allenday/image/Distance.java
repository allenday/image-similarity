package com.allenday.image;

import java.util.List;
import java.util.Vector;

public interface Distance {
    Double getScalarDistance(Integer d, Vector<Double> a, Vector<Double> b);

    Vector<Double> getVectorDistance(Integer d, Vector<Double> a, Vector<Double> b);

    Double getVectorNorm(Integer d, Vector<Double> v);

    Double getScalarDistance(List<Vector<Double>> a, List<Vector<Double>> b);
    //public abstract Double distance(Integer dimension, Vector<Double> vec);
    //public abstract Vector<Double> distance(Integer dimension, Vector<Double> a, Vector<Double> b);
    //public List<SearchResult> reorder(ImageFeatures query, List<ImageFeatures> inputItems);
}
