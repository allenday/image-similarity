package com.allenday.image;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class ImageIndex implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ImageIndex.class);

    public static final Integer R = 0;
    public static final Integer G = 1;
    public static final Integer B = 2;
    public static final Integer T = 3;
    public static final Integer C = 4;

    private final Integer keySize;
    private final Map<String, ImageFeatures> files = new HashMap<>();
    private final ArrayList<HashMap<String, double[]>> histogram = new ArrayList<>();
    private final ArrayList<HashMap<String, double[]>> tHistogram = new ArrayList<>();
    private List<KDTree<String>> kdTrees = new ArrayList<>();
    private final ArrayList<KDTree<String>> toptree = new ArrayList<>();

    public ImageIndex(Integer keySize) {
        this.keySize = keySize;
        clearIndex();
    }

    private ImageIndex(Integer bins, List<KDTree<String>> trees) {
        keySize = bins;
        kdTrees = trees;
    }

    public static Double getScalarDistance(ImageFeatures a, ImageFeatures b, Distance m) {
        List<Vector<Double>> av = a.getDimensions();
        List<Vector<Double>> bv = b.getDimensions();
        return m.getScalarDistance(av, bv);
    }

    public List<KDTree<String>> getKDTrees() {
        return kdTrees;
    }

    private void clearIndex() {
        kdTrees.clear();
        histogram.clear();
        for (int i = 0; i < ImageFeatures.DIMENSIONS; i++) {
            histogram.add(new HashMap<>());
            kdTrees.add(new KDTree<>(keySize));
        }
        toptree.clear();
        toptree.add(new KDTree<>(keySize));
        toptree.add(new KDTree<>(keySize));
        toptree.add(new KDTree<>(keySize));

        tHistogram.clear();
        tHistogram.add(new HashMap<>());
        tHistogram.add(new HashMap<>());
        tHistogram.add(new HashMap<>());
    }

    void putFile(String fileName, ImageFeatures features) {
        files.put(fileName, features);
    }

    void putPoint(Integer feature, String key, Vector<Double> vector) throws KeySizeException, KeyDuplicateException {
        logger.debug("putPoint " + feature + "," + key + "," + vector);
        double[] x = new double[vector.size()];
        for (int i = 0; i < vector.size(); i++)
            x[i] = vector.get(i);
        if (kdTrees.get(feature).search(x) == null)
            kdTrees.get(feature).insert(x, key);
        histogram.get(feature).put(key, x);
    }

    private double[] getPoint(Integer feature, String key) {
        //logger.debug("feature="+feature);
        //logger.debug("key="+key);
        logger.debug(histogram.get(feature) + "");
        return histogram.get(feature).get(key);
    }

    public Set<ImageFeatures> getHits(ImageFeatures query, Integer howMany) throws KeySizeException, IllegalArgumentException, CloneNotSupportedException {
        Set<ImageFeatures> results = new HashSet<>();
        Set<ImageFeatures> unionHits = new HashSet<>();
        for (int d = 0; d < ImageFeatures.DIMENSIONS; d++) {
            //logger.debug("1: "+query.id);
            //logger.debug("2: "+kdTrees.get(d));
            Double[] a0 = new Double[query.getDimension(d).size()];
            //logger.debug("3: "+query.getDimension(d).size());
            double[] a1 = ArrayUtils.toPrimitive(query.getDimension(d).toArray(a0));
            //logger.debug("4: "+a0[0]);
            //logger.debug("5: "+a1[0]);
            //logger.debug("3: "+kdTrees.get(d).nearest(a1,1));
            //logger.debug("4: ");

            for (String hit : kdTrees.get(d).nearest(a1, howMany)) {
                unionHits.add(files.get(hit));
            }
        }
        return unionHits;
    }

    public ArrayList rankHits(ImageFeatures query, Set<ImageFeatures> hits, Distance ranker) {
        Set<ImageFeatures> results = new HashSet<>();

        for (ImageFeatures hit : hits) {
            Double d = getScalarDistance(query, hit, ranker);
            hit.score = Math.log10(d); //TODO do log transform on L1 and L2
        }

        ArrayList hitsArray = new ArrayList(hits);
        hitsArray.sort(new ImageFeaturesComparator());
        return hitsArray;
    }

    class ImageFeaturesComparator implements Comparator<ImageFeatures> {
        public int compare(ImageFeatures s1, ImageFeatures s2) {
            return Double.compare(s1.score, s2.score); //1,2=sort asc, 2,1=sort desc TODO move this to distance class, L1 and L2 should be asc, corr should be desc
        }
    }
}