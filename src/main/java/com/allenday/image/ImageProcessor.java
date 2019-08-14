package com.allenday.image;

import com.allenday.image.backend.Processor;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ImageProcessor {
    public static final int R = 0;
    public static final int G = 1;
    public static final int B = 2;
    public static final int T = 3;
    public static final int C = 4;
    public static final int M = 5;
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private final int bins;
    private final int bits;
    private final boolean normalize;


    private Processor processor;

    //static KDTree[] kdtree = {new KDTree<String>(8), new KDTree<String>(8), new KDTree<String>(8)};

    ImageProcessor() {
        this(8, 8, false);
    }

    public ImageProcessor(int bins, int bits, boolean normalize) {
        this.bins = bins;
        this.bits = bits;
        this.normalize = normalize;
    }


    public ImageFeatures extractFeatures(File file) throws IOException {
        //disallow non jpg
        //TODO check for extension, not only string occurrence
        if (!file.toString().contains("jpg") && !file.toString().contains("jpeg") && !file.toString().contains("gif")) {
            //readLuminance() failed
            //&& file.toString().indexOf("png") == -1
            throw new IOException("skipping file not matching (jpg, jpeg, gif): " + file);
        }

        try {
            processor = new Processor(file, bins, bits, normalize);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e) {
            logger.debug("failed to process file: " + file);
            e.printStackTrace();
        }

        double[] r = processor.getRedHistogram();
        double[] g = processor.getGreenHistogram();
        double[] b = processor.getBlueHistogram();
        double[] t = processor.getTextureHistogram();
        double[] c = processor.getCurvatureHistogram();
        double[] m = processor.getTopologyValues();
        char[] ml = processor.getTopologyLabels();

        // TODO parameterize blocksPerSide
        ImageFeatures features = new ImageFeatures(file.toString(), bins, 16);
        features.setR(r);
        features.setG(g);
        features.setB(b);
        features.setT(t);
        features.setC(c);

        return features;
    }
}
