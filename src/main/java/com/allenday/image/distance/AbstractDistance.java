package com.allenday.image.distance;

import java.util.Vector;

abstract class AbstractDistance {
    protected abstract Double getDimensionWeight(Integer dimension);

    protected abstract Double getVectorWeight(Integer dimension, Integer position);

    Double getVectorNorm(Integer d, Vector<Double> vec) {
        Double result = 0d;
        for (int i = 0; i < vec.size(); i++)
            result += getVectorWeight(d, i) * vec.get(i);
        return getDimensionWeight(d) * result;
    }

    public Vector<Double> getVectorDistance(Integer d, Vector<Double> a, Vector<Double> b) {
        Vector<Double> result = new Vector<>();
        result.setSize(a.size());
        for (int i = 0; i < a.size(); i++) {
            result.set(i, getVectorWeight(d, i) * (a.get(i) - b.get(i)));
        }
        return result;
    }

/*
    public List<SearchResult> reorder(ImageFeatures query, List<ImageFeatures> inputItems) {
        TreeMap<Double, List<ImageFeatures>> tree = new TreeMap<Double, List<ImageFeatures>>();

        for (ImageFeatures item : inputItems) {
            Double distance = 0d;
            for (Integer d = 0 ; d < ImageFeatures.DIMENSIONS ; d++) {
                distance += distance(d, distance(d, query.getDimension(d), item.getDimension(d))) / 255;
            }
            if (!tree.containsKey(distance))
                tree.put(distance,new ArrayList<ImageFeatures>());
            tree.get(distance).add(item);
        }
        List<SearchResult> results = new ArrayList<SearchResult>();
        for (Map.Entry<Double,List<ImageFeatures>> e : tree.entrySet())
            for (ImageFeatures f : e.getValue())
                results.add(new SearchResult(f.id, e.getKey()));
        return results;
    }
*/
}
