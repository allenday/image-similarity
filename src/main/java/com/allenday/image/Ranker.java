package com.allenday.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class Ranker {
	public static String[] LABEL = {"RED","GREEN","BLUE","TEXTURE","CURVATURE","RTOP","GTOP","BTOP"};
	public static Double[] COR_WEIGHT = {0.6d, 0.8d, 0.8d, 1.2d, 1.6d};
	public static Double[] COL_WEIGHT = {0.6d, 0.8d, 0.8d, 1.2d, 1.6d};
	
	public static double[][] MAT_WEIGHT = {
		{0.2d, 0.3d, 0.3d, 0.4d, 0.5d, 0.7d, 0.8d, 0.3d}, //R
		{0.2d, 0.5d, 0.7d, 0.9d, 1.0d, 1.0d, 0.9d, 0.4d}, //G
		{0.2d, 0.5d, 0.7d, 0.9d, 1.0d, 0.9d, 0.9d, 0.4d}, //B
		
		{1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d}, //T
		{1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1.0d}, //C
	};
	
	public static int R = 0;
	public static int G = 1;
	public static int B = 2;
	public static int T = 3;
	public static int C = 4;
	public static int P = 5;
	public static int r = 0;
	public static int g = 1;
	public static int b = 2;
	
	private ArrayList<HashMap<String,double[]>> histogram = new ArrayList<HashMap<String,double[]>>();
	private ArrayList<HashMap<String,double[]>> tHistogram = new ArrayList<HashMap<String,double[]>>();
	private ArrayList<KDTree<String>> kdtree = new ArrayList<KDTree<String>>();
	private ArrayList<KDTree<String>> toptree = new ArrayList<KDTree<String>>();

    public double getWeightedPearsonCorrelationSimilarity(double[] weight, double[] vector1, double[] vector2) {
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
    	double d = (vector1.length * sumXxY - sumX * sumY)
    	/ Math.sqrt((vector1.length * sumXxX - sumX * sumX)
    			* (vector1.length * sumYxY - sumY * sumY));
    	
//    	d++;
//    	d /= 2;
    	
    	return d;
    }

	
	private List<List<String>> getHits(Integer feature, String query, Integer howMany) throws KeySizeException, IllegalArgumentException {
		List<List<String>> results = new ArrayList<List<String>>();
		
		List<String> hits = kdtree.get(feature).nearest(getPoint(feature, query), howMany);
		
		for (String hit : hits) {
			List<String> result = new ArrayList<String>();
			result.add(hit);
			double[] q = getPoint(feature, query);
			double[] h = getPoint(feature, hit);
			Integer distance = getWeightedEuclideanDistanceSimilarity(MAT_WEIGHT[feature], q, h);
			result.add(""+distance);
			results.add(result);
		}
		return results;
	}

	private List<List<String>> topHits(Integer feature, String query, Integer howMany) throws KeySizeException, IllegalArgumentException {
		List<List<String>> results = new ArrayList<List<String>>();
		
		List<String> hits = toptree.get(feature).nearest(topPoint(feature, query), howMany);
		
		for (String hit : hits) {
			List<String> result = new ArrayList<String>();
			result.add(hit);
			double[] q = topPoint(feature, query);
			double[] h = topPoint(feature, hit);
			Integer distance = getEuclideanDistanceSimilarity(q, h);
			result.add(""+distance);
			results.add(result);
		}
		return results;
	}

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
	
	private double[] getPoint(Integer feature, String key) {
		return histogram.get(feature).get(key);
	}

	private double[] topPoint(Integer feature, String key) {
		return tHistogram.get(feature).get(key);
	}

	private String getPointString(Integer feature, String key) {
		String result = "";
		double[] point = getPoint(feature, key);
		for (int i = 0; i < point.length; i++)
			result += point[i]+",";
		return result;
	}
	
	private void topPoint(String key, Vector<Double> vector) throws KeySizeException, KeyDuplicateException {
		double[] r = new double[16];
		double[] g = new double[16];
		double[] b = new double[16];
		
		for (int i = 0; i < vector.size(); i++) {
			Double v = vector.get(i);

			
			if (v < 5) {
				r[i] = v;
				g[i] = 0;
				b[i] = 0;
			}
			else if (v < 10) {
				r[i] = 0;
				g[i] = v-5;
				b[i] = 0;
			}
			else {
				r[i] = 0;
				g[i] = 0;
				b[i] = v-10;
			}
		}
		
		if (toptree.get(R).search(r) == null)
			toptree.get(R).insert(r, key);
		if (toptree.get(G).search(g) == null)
			toptree.get(G).insert(g, key);
		if (toptree.get(B).search(b) == null)
			toptree.get(B).insert(b, key);
		
		tHistogram.get(R).put(key, r);
		tHistogram.get(G).put(key, g);
		tHistogram.get(B).put(key, b);
	}
	
	private void putPoint(Integer feature, String key, Vector<Double> vector) throws KeySizeException, KeyDuplicateException {
		double[] x = new double[vector.size()];
		for (int i = 0; i < vector.size(); i++)
			x[i] = vector.get(i);
		if (kdtree.get(feature).search(x) == null)
			kdtree.get(feature).insert(x, key);
		histogram.get(feature).put(key, x);
	}

	public Ranker(List<ImageFeatures> images) throws KeySizeException, KeyDuplicateException {
		for (int i = 0; i < 5; i++) {		
			histogram.add(new HashMap<String,double[]>());
			kdtree.add(new KDTree<String>(8));
		}

		toptree.add(new KDTree<String>(16));
		toptree.add(new KDTree<String>(16));
		toptree.add(new KDTree<String>(16));
		tHistogram.add(new HashMap<String,double[]>());
		tHistogram.add(new HashMap<String,double[]>());
		tHistogram.add(new HashMap<String,double[]>());

		for (ImageFeatures image : images) {
			putPoint(R, image.id, image.R);
			putPoint(G, image.id, image.G);
			putPoint(B, image.id, image.B);
			putPoint(T, image.id, image.T);
			putPoint(C, image.id, image.C);
			topPoint(   image.id, image.M);
		}
	}
	
	
	public List<SearchResult> rank(ImageFeatures query) throws KeySizeException, IllegalArgumentException {
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		Map<String,Double> combine    = new HashMap<String,Double>();
		Map<Double,String> merged     = new TreeMap<Double, String>();//Collections.reverseOrder());
		Map<String, Double> euclidean = new HashMap<String, Double>();
		Map<String, Double> pearson   = new HashMap<String, Double>();
		Map<String, Double> topology  = new HashMap<String, Double>();

		for (int i = 0; i < 5; i++) {
			List<List<String>> hits = getHits(i, query.id, 500);
			for (List<String> hit : hits) {
				String j = hit.get(0);
				String d = hit.get(1);
				
				if (!combine.containsKey(j)) {
					double value = 0;
					double euclid = 0;
					for (int k = 0; k < 5; k++) {
						euclid += 1.0 * COL_WEIGHT[k] * getWeightedEuclideanDistanceSimilarity(MAT_WEIGHT[k], getPoint(k, query.id), getPoint(k, j)) / 255;
						value  += 0.2 * COR_WEIGHT[k] * getWeightedPearsonCorrelationSimilarity(MAT_WEIGHT[k], getPoint(k, query.id), getPoint(k, j));
						
					}					
					combine.put(j,(1.0*value)*(0.5*euclid));
					pearson.put(j, value);
					euclidean.put(j,euclid);
				}
			}
		}
		
		for (int i = 0; i < 3; i++) {
			List<List<String>> hits = topHits(i, query.id, 10000);
			for (List<String> hit : hits) {
				String j = hit.get(0);
				String d = hit.get(1);
				//System.out.println(j+"\t"+d);
				double sum = 0;
				for (int k = 0; k < 3; k++) {
					sum += getEuclideanDistanceSimilarity(topPoint(k, query.id), topPoint(k, j));
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
			if (pearson.get(merged.get(d)) > 0.85 && euclidean.get(merged.get(d)) < 5 ) {//&& topology.get(merged.get(d)) != null && topology.get(merged.get(d)) < 30)
				SearchResult sr = new SearchResult();
				sr.id = merged.get(d);
				sr.score = d;
				results.add(sr);
				/*
				results.add(ff);
				System.out.println((m++)
						+ "\t" + d
						+ "\t" + merged.get(d)
						+ "\t" + pearson.get(merged.get(d))
						+ "\t" + euclidean.get(merged.get(d))
						+ "\t" + topology.get(merged.get(d))
						+ "\t" + ""
				);
				*/
			}
		}
		
		return results;
	}

	/*
	public static void main(String[] args) throws IOException, KeySizeException, KeyDuplicateException {

		String query = args[0];
		
//		String query = "/Users/allenday/Sites/tmp/21K/ce85aabe68b9449bc0b799a6505c3031.300x.jpg";
		Ranker ranker = new Ranker();

		Map<String,Double> combine    = new HashMap<String,Double>();
		Map<Double,String> merged     = new TreeMap<Double, String>();//Collections.reverseOrder());
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
