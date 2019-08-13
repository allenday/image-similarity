package com.allenday.image;

import edu.wlu.cs.levy.CG.KDTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

class Ranker {
    private static final Logger logger = LoggerFactory.getLogger(Ranker.class);

    public static String[] LABEL = {"RED", "GREEN", "BLUE", "TEXTURE", "CURVATURE" };
    private static Double[] COR_WEIGHT = {0.6d, 0.8d, 0.8d, 1.2d, 1.6d};
    private static Double[] COL_WEIGHT = {0.6d, 0.8d, 0.8d, 1.2d, 1.6d};

    private static double[][] MAT_WEIGHT = {
            {0.2d, 0.3d, 0.3d, 0.4d, 0.5d, 0.7d, 0.8d, 0.3d}, //R
            {0.2d, 0.5d, 0.7d, 0.9d, 1.0d, 1.0d, 0.9d, 0.4d}, //G
            {0.2d, 0.5d, 0.7d, 0.9d, 1.0d, 0.9d, 0.9d, 0.4d}, //B
            {1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d}, //T
            {1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d}, //C
    };

    private ArrayList<HashMap<String, double[]>> histogram = new ArrayList<>();
    private ArrayList<HashMap<String, double[]>> tHistogram = new ArrayList<>();
    private ArrayList<KDTree<String>> kdTrees = new ArrayList<>();
    private ArrayList<KDTree<String>> toptree = new ArrayList<>();

    private double getWeightedPearsonCorrelationSimilarity(double[] weight, double[] vector1, double[] vector2) {
        double sumXxY = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXxX = 0;
        double sumYxY = 0;

        for (int i = 0; i < vector1.length; i++) {
            double value1 = vector1[i] * weight[i];
            double value2 = vector2[i] * weight[i];
            sumXxY += value1 * value2;
            sumX += value1;
            sumY += value2;
            sumXxX += value1 * value1;
            sumYxY += value2 * value2;
        }
        return (vector1.length * sumXxY - sumX * sumY)
                / Math.sqrt((vector1.length * sumXxX - sumX * sumX)
                * (vector1.length * sumYxY - sumY * sumY));
    }

    /*
    private Map<String, Integer> getHits(Integer feature, String query, Integer howMany, Boolean boost) throws KeySizeException, IllegalArgumentException {
        List<String> hits = toptree.get(feature).nearest(topPoint(feature, query), howMany);
        for (String hit : hits) {
            double[] q = topPoint(feature, query);
            double[] h = topPoint(feature, hit);
            Integer distance;
            if (boost) {
                distance = getWeightedEuclideanDistanceSimilarity(MAT_WEIGHT[feature], q, h);
            } else {
                distance = getEuclideanDistanceSimilarity(q, h);
            }
            results.put(hit, distance);
        }
        return results;
    }
    */
    private int getEuclideanDistanceSimilarity(double[] a, double[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++)
            distance += Math.abs(a[i] - b[i]);
        return distance;
    }

    private int getWeightedEuclideanDistanceSimilarity(double[] w, double[] a, double[] b) {
        int distance = 0;
        for (int i = 0; i < a.length; i++)
            distance += Math.abs(w[i] * (a[i] - b[i]));
        return distance;
    }


    /*
    private double[] getPoint(Integer feature, String key) {
        return histogram.get(feature).get(key);
    }
    */

    /*
    @SuppressWarnings("unused")
    private String getPointString(Integer feature, String key) {
        StringBuilder result = new StringBuilder();
        double[] point = getPoint(feature, key);
        for (double aPoint : point) result.append(aPoint).append(",");
        return result.toString();
    }
    */

    /*
    @SuppressWarnings("unused")
    List<SearchResult> rank(ImageFeatures query, Boolean boost) throws KeySizeException, IllegalArgumentException {
        List<SearchResult> results = new ArrayList<SearchResult>();

        Map<String, Double> combine = new HashMap<String, Double>();
        Map<Double, String> merged = new TreeMap<Double, String>();//Collections.reverseOrder());
        Map<String, Double> euclidean = new HashMap<String, Double>();
        Map<String, Double> pearson = new HashMap<String, Double>();

        for (int i = 0; i < 5; i++) {
            Map<String, Integer> hits = getHits(i, query.id, 500, boost);
            for (String j : hits.keySet()) {
                Integer d = hits.get(j);

                if (!combine.containsKey(j)) {
                    double value = 0;
                    double euclid = 0;
                    for (int k = 0; k < 5; k++) {
                        euclid += 1.0 * COL_WEIGHT[k] * getWeightedEuclideanDistanceSimilarity(MAT_WEIGHT[k], getPoint(k, query.id), getPoint(k, j)) / 255;
                        value += 0.2 * COR_WEIGHT[k] * getWeightedPearsonCorrelationSimilarity(MAT_WEIGHT[k], getPoint(k, query.id), getPoint(k, j));

                    }
                    combine.put(j, (1.0 * value) * (0.5 * euclid));
                    pearson.put(j, value);
                    euclidean.put(j, euclid);
                }
            }
        }

        for (String k : combine.keySet()) {
            Double v = combine.get(k);
            while (merged.containsKey(v))
                v += 0.1d;
            merged.put(v, k);
        }

        int m = 0;
        for (Double d : merged.keySet()) {
            if (pearson.get(merged.get(d)) > 0.85 && euclidean.get(merged.get(d)) < 5) {//&& topology.get(merged.get(d)) != null && topology.get(merged.get(d)) < 30)
                SearchResult sr = new SearchResult();
                sr.id = merged.get(d);
                sr.score = d;
                results.add(sr);
				//results.add(ff);
				//System.out.println((m++)
				//		+ "\t" + d
				//		+ "\t" + merged.get(d)
				//		+ "\t" + pearson.get(merged.get(d))
				//		+ "\t" + euclidean.get(merged.get(d))
				//		+ "\t" + topology.get(merged.get(d))
				//		+ "\t" + ""
				//);
            }
        }

        return results;
    }
    */

	/*
	public static void main(String[] args) throws IOException, KeySizeException, KeyDuplicateException {

		String query = args[0];
		
//		String query = "/Users/allenday/Sites/tmp/21K/ce85aabe68b9449bc0b799a6505c3031.300x.jpg";
		Ranker ranker = new Ranker();

		Map<String,Double>  combine   = new HashMap<String,Double>();
		Map<Double,String>  merged    = new TreeMap<Double, String>();//Collections.reverseOrder());
		Map<String, Double> euclidean = new HashMap<String, Double>();
		Map<String, Double> pearson   = new HashMap<String, Double>();
		Map<String, Double> topology  = new HashMap<String, Double>();
		
		for (int i = 0; i < 5; i++) {
			List<List<String>> hits = ranker.getHits(i, query, 500);
			for (List<String> hit : hits) {
				String j = hit.get(0);
				String d = hit.get(1);
				
				if (!combine.containsKey(j)) {
					double value = 0;
					double euclid = 0;
					for (int k = 0; k < 5; k++) {
						euclid += 1.0 * COL_WEIGHT[k] * ranker.getWeightedEuclideanDistanceSimilarity(MAT_WEIGHT[k], ranker.getPoint(k, query), ranker.getPoint(k, j)) / 255;
						value  += 0.2 * COR_WEIGHT[k] * ranker.getWeightedPearsonCorrelationSimilarity(MAT_WEIGHT[k], ranker.getPoint(k, query), ranker.getPoint(k, j));
					}					
					combine.put(j,(1.0*value)*(0.5*euclid));
					pearson.put(j, value);
					euclidean.put(j,euclid);
				}
			}
		}
		
		for (int i = 0; i < 3; i++) {
			List<List<String>> hits = ranker.topHits(i, query, 10000);
			for (List<String> hit : hits) {
				String j = hit.get(0);
				String d = hit.get(1);
				System.out.println(j+"\t"+d);
				double sum = 0;
				for (int k = 0; k < 3; k++) {
					sum += ranker.getEuclideanDistanceSimilarity(ranker.topPoint(k, query), ranker.topPoint(k, j));
				}
				topology.put(j, sum);
			}
		}
		
		for (String k : combine.keySet()) {
			Double v = combine.get(k);
			while (merged.containsKey(v))
				v += 0.1d;
			merged.put(v, k);
		}
		int m = 0;
		for (Double d : merged.keySet()) {
			if (pearson.get(merged.get(d)) > 0.85 && euclidean.get(merged.get(d)) < 5 )//&& topology.get(merged.get(d)) != null && topology.get(merged.get(d)) < 30)
				System.out.println((m++) + "\t" + d + "\t" + merged.get(d) + "\t" + pearson.get(merged.get(d)) + "\t" + euclidean.get(merged.get(d)) + "\t" + topology.get(merged.get(d)) + "\t" +
//					ranker.getPointString(0, merged.get(d)).toString() + "\t" +
//					ranker.getPointString(1, merged.get(d)).toString() + "\t" +
//					ranker.getPointString(2, merged.get(d)).toString() + "\t" +
//					ranker.getPointString(3, merged.get(d)).toString() + "\t" +
//					ranker.getPointString(4, merged.get(d)).toString() + "\t" +
						""
				);
		}
	}
	*/
}
