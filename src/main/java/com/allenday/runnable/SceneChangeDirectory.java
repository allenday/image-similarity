package com.allenday.runnable;

import com.allenday.image.Distance;
import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageIndex;
import com.allenday.image.ImageProcessor;
import com.allenday.image.distance.UDUV_L1Norm;
import com.allenday.image.distance.UDWV_L1Norm;
import com.allenday.image.distance.WDUV_PearsonDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

class SceneChangeDirectory {
    private static final Logger logger = LoggerFactory.getLogger(IndexDirectory.class);
    static int bins = 16;
    static int bits = 4;
    static boolean normalize = false;

    public static void main(String[] argv) {
        ImageProcessor processor = new ImageProcessor(16, 4, false);
        String pathname = "/Volumes/.../";
        File path = new File(pathname);
        String[] filenames = path.list();
        Arrays.sort(Objects.requireNonNull(filenames));

        int i = 2430;

        Distance m0 = new UDUV_L1Norm();
        Distance m1 = new UDWV_L1Norm();
        Distance m2 = new WDUV_PearsonDistance();
        ImageFeatures prevFrame = processor.extractFeatures(new File(pathname + filenames[i]));
        while (i < filenames.length) {
            ImageFeatures thisFrame = processor.extractFeatures(new File(pathname + filenames[i]));
            Double d0 = ImageIndex.getScalarDistance(prevFrame, thisFrame, m0);
            Double d1 = ImageIndex.getScalarDistance(prevFrame, thisFrame, m1);
            Double d2 = ImageIndex.getScalarDistance(prevFrame, thisFrame, m2);
            logger.debug(pathname + filenames[i] + "\t" + d0 + "\t" + d1 + "\t" + d2);//+ "\t" + thisFrame.getAllcompact());
            //logger.debug(pathname+filenames[i] + "\t" + distance);
            prevFrame = thisFrame;
            i++;
        }
    }
}
