package com.allenday.runnable;

import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageIndex;
import com.allenday.image.ImageIndexFactory;
import com.allenday.image.ImageProcessor;
import com.allenday.image.distance.UDWV_L1Norm;
import com.allenday.image.distance.WDUV_PearsonDistance;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class IndexDirectory {
    private static final Logger logger = LoggerFactory.getLogger(IndexDirectory.class);
    private static final Integer bins = 16;
    private static final Integer bits = 4;
    private static final Boolean normalize = false;

    public static void main(String[] argv) throws KeySizeException, IOException, ClassNotFoundException, CloneNotSupportedException {
        //createIndex();
        searchIndex();
    }

    private static void searchIndex() throws IOException, ClassNotFoundException, KeySizeException, CloneNotSupportedException {
        FileInputStream fis = new FileInputStream(new File("/Volumes/SL-EG5/empornium/Karen-Uehara-MYMN-007.pickle"));
        ObjectInputStream ois = new ObjectInputStream(fis);
        ImageIndex index = (ImageIndex) ois.readObject();

        ImageProcessor processor = new ImageProcessor(16, 4, false);
        String pathname = "/Volumes/SL-EG5/empornium/Karen-Uehara-MYMN-007-pmv/";
        File path = new File(pathname);
        String[] filenames = path.list();
        Arrays.sort(Objects.requireNonNull(filenames));

        for (String filename : filenames) {
            ImageFeatures query = processor.extractFeatures(new File(pathname + filename));
            Set<ImageFeatures> hits = index.getHits(query, 1);
            List<ImageFeatures> rankedHits = index.rankHits(query, hits, new UDWV_L1Norm());//WDUV_PearsonDistance());
            ImageFeatures topHit = rankedHits.get(0);
            //for (ImageFeatures hit : rankedHits) {
            logger.debug(pathname + filename + " HIT " + topHit.id + ", score=" + topHit.score);
            //}
        }
    }

    public static void createIndex() throws KeySizeException, KeyDuplicateException, IOException, CloneNotSupportedException {
        File path = new File("/Volumes/SL-EG5/empornium/Karen-Uehara-MYMN-007");///13115288");

        ImageIndexFactory indexFactory = new ImageIndexFactory(bins, bits, normalize);
        indexFactory.addFile(path);
        ImageIndex index = indexFactory.createIndex(0, 30);

        File[] sFiles = indexFactory.files.keySet().toArray(new File[0]);
        Arrays.sort(sFiles);
        File queryFile = sFiles[0];
        ImageFeatures query = indexFactory.getProcessor().extractFeatures(queryFile);
        logger.debug("query=" + query.id);

        Set<ImageFeatures> hits = index.getHits(query, 1);
        List<ImageFeatures> rankedHits = index.rankHits(query, hits, new WDUV_PearsonDistance());
        for (ImageFeatures hit : rankedHits) {
            logger.debug("HIT: " + hit.id + ", score=" + hit.score);
        }

        FileOutputStream fos = new FileOutputStream(new File("/Volumes/SL-EG5/empornium/Karen-Uehara-MYMN-007.pickle"));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(index);
        oos.close();
    }
}
