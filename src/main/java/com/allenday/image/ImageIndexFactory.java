package com.allenday.image;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageIndexFactory {
    private static final Logger logger = LoggerFactory.getLogger(ImageIndexFactory.class);
    public final Map<File, ImageFeatures> files = new HashMap<>();
    private final ImageProcessor processor;
    private final ImageIndex index;

    public ImageIndexFactory(Integer bins, Integer bits, Boolean normalize) {
        processor = new ImageProcessor(bins, bits, normalize);
        index = new ImageIndex(bins);
    }

    public ImageIndex createIndex() throws KeySizeException, KeyDuplicateException, IOException {
        return createIndex(0, 1);
    }

    public ImageIndex createIndex(Integer howMany, Integer stepSize) throws KeySizeException, KeyDuplicateException, IOException {
        List<File> forRemoval = new ArrayList<>();
        Integer added = 0;
        Integer substep = 0;
        File[] sFiles = files.keySet().toArray(new File[0]);
        Arrays.sort(sFiles);
        for (File file : sFiles) {
            substep++;
            if (substep < stepSize) {
                //logger.debug("substep "+substep+" remove "+file);
                forRemoval.add(file);
                continue;
            }
            substep = 0;

            ImageFeatures features = processor.extractFeatures(file);
            if (features != null) {
                index.putFile(file.toString(), features);
                index.putPoint(ImageIndex.R, features.id, features.getDimension(ImageFeatures.R));
                index.putPoint(ImageIndex.G, features.id, features.getDimension(ImageFeatures.G));
                index.putPoint(ImageIndex.B, features.id, features.getDimension(ImageFeatures.B));
                index.putPoint(ImageIndex.T, features.id, features.getDimension(ImageFeatures.T));
                index.putPoint(ImageIndex.C, features.id, features.getDimension(ImageFeatures.C));
            } else {
                forRemoval.add(file);
                logger.debug("removing file from consideration: " + file);
            }
            added++;
            if (howMany > 0 && added >= howMany)
                break;
        }
        for (File f : forRemoval) {
            files.remove(f);
            //logger.debug("removed "+f);
        }
        return index;
    }

    public ImageProcessor getProcessor() {
        return processor;
    }

    private void clearFiles() {
        files.clear();
    }

    private void setFiles(List<File> files) {
        clearFiles();
        addFiles(files);
    }

    private void addFiles(List<File> files) {
        for (File file : files) {
            addFile(file);
        }
    }

    public void addFile(File file) {
        if (file.isDirectory()) {
            logger.debug("processing directory: " + file);
            recurse(file);
        } else if (!files.containsKey(file)) {
            files.put(file, null);
        }
    }

    private void recurse(File directory) {
        if (directory.isDirectory()) {
            String[] ents = directory.list();
            Arrays.sort(Objects.requireNonNull(ents));
            int i;
            for (String ent : ents) {
                File f = new File(directory + "/" + ent);
                if (f.isDirectory()) {
                    recurse(f);
                } else {
                    //logger.debug("processing file: " + f);
                    if (!files.containsKey(f))
                        files.put(f, null);
                }
            }
        }
    }
}
