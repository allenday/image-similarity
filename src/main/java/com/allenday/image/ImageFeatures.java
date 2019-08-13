package com.allenday.image;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ImageFeatures implements Serializable {
    //TODO enum this
    public static final Integer DIMENSIONS = 5;

    public static final Integer R = 0;
    public static final Integer G = 1;
    public static final Integer B = 2;
    public static final Integer T = 3;
    public static final Integer C = 4;
    public final String id;
    public Double score = null;
    private final List<Vector<Double>> vectors = new ArrayList<>();


    public ImageFeatures(String id, int bins, int blocksPerSide) {
        this.id = id;
        for (Integer d = 0; d < DIMENSIONS; d++) {
            Vector<Double> v = new Vector<>();
            v.setSize(bins);
            vectors.add(v);
        }
    }

    public Double getScore() {
        return score;
    }

    private void boundsCheck(int a, int b) {
        if (a != b)
            throw new IndexOutOfBoundsException("vector size mismatch: " + a + " != " + b);
    }

    public Vector<Double> getDimension(Integer d) {
        return vectors.get(d);
    }

    public List<Vector<Double>> getDimensions() {
        List<Vector<Double>> dims = new ArrayList<>();
        for (int i = 0; i < DIMENSIONS; i++) {
            dims.add(this.getDimension(i));
        }
        return dims;
    }

    private Vector<Integer> d2i(Vector<Double> d) {
        Vector<Integer> res = new Vector<>();
        for (Double aDouble : d) res.add(aDouble.intValue());
        return res;
    }

    private String getTokens(Vector<Double> x, String p) {
        StringBuilder res = new StringBuilder();
        Vector<Integer> v = d2i(x);
        for (int i = 0; i < v.size(); i++) {
            for (int j = 0; j < v.get(i); j++) {
                res.append(String.format("%s%X ", p, i));
            }
        }
        return res.toString();
    }

    private String getCompact(Vector<Double> x, String p) {
        StringBuilder res = new StringBuilder();
        Vector<Integer> v = d2i(x);
        for (int i = 0; i < v.size(); i++) {
            res.append(String.format("%s%X%X ", p, i, v.get(i) & 0xFFFFF));
        }
        return res.toString();
    }

    public String getAllJson() {
        return "{" +
                "\"red\":[" + getR() + "]," +
                "\"green\":[" + getG() + "]," +
                "\"blue\":[" + getB() + "]," +
                "\"texture\":[" + getT() + "]," +
                "\"curvature\":[" + getC() + "]" +
                "}";
    }

    public String getAllcompact() {
        return getRcompact() + getGcompact() + getBcompact() + getTcompact() + getCcompact();
    }

    public String getRtokens() {
        return getTokens(vectors.get(R), "r");
    }

    private String getRcompact() {
        return getCompact(vectors.get(R), "r");
    }

    public String getGtokens() {
        return getTokens(vectors.get(G), "g");
    }

    private String getGcompact() {
        return getCompact(vectors.get(G), "g");
    }

    public String getBtokens() {
        return getTokens(vectors.get(B), "b");
    }

    private String getBcompact() {
        return getCompact(vectors.get(B), "b");
    }

    public String getTtokens() {
        return getTokens(vectors.get(T), "t");
    }

    private String getTcompact() {
        return getCompact(vectors.get(T), "t");
    }

    public String getCtokens() {
        return getTokens(vectors.get(C), "c");
    }

    private String getCcompact() {
        return getCompact(vectors.get(C), "c");
    }

    // Red.  double[8], 0..255
	private String getR() {
        return StringUtils.join(d2i(vectors.get(R)), ",");
    }

    public void setR(double[] n) {
        boundsCheck(n.length, vectors.get(R).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(R).set(i, n[i]);
        }
    }

    // Green.  double[8], 0..255
	private String getG() {
        return StringUtils.join(d2i(vectors.get(G)), ",");
    }

    public void setG(double[] n) {
        boundsCheck(n.length, vectors.get(G).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(G).set(i, n[i]);
        }
    }

    // Blue.  double[8], 0..255
	private String getB() {
        return StringUtils.join(d2i(vectors.get(B)), ",");
    }

    public void setB(double[] n) {
        boundsCheck(n.length, vectors.get(B).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(B).set(i, n[i]);
        }
    }

    // Texture.  double[8], 0..255
	private String getT() {
        return StringUtils.join(d2i(vectors.get(T)), ",");
    }

    public void setT(double[] n) {
        boundsCheck(n.length, vectors.get(T).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(T).set(i, n[i]);
        }
    }

    // Curvature.  double[8], 0..255
	private String getC() {
        return StringUtils.join(d2i(vectors.get(C)), ",");
    }

    public void setC(double[] n) {
        boundsCheck(n.length, vectors.get(C).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(C).set(i, n[i]);
        }
    }
}
