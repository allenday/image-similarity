package com.allenday.runnable;

import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageProcessor;
import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageVectors {
    public static void main(String[] argv) {
        int bins = 8;
        int bits = 3; //specifically set to 3 to enable base64 packing
        boolean normalize = false;
        ImageProcessor processor = new ImageProcessor(bins, bits, false);

        //String input = "src/test/resources/image/";//bad.gif";
        String input = argv[0];

        List<File> files = new ArrayList<>();
        File f0 = new File(input);
        if (f0.isDirectory()) {
            for (String f1 : f0.list()){
                files.add(new File(f0.getAbsolutePath()+"/"+f1));
            }
        }
        else {
            files.add(f0);
        }

        for (File file : files) {
            System.err.println(file.getAbsolutePath());
            ImageFeatures features = null;
            try {
                features = processor.extractFeatures(file);
                System.out.println(file.getAbsolutePath() + " " + features.getRawB64All());
            } catch (IOException e) {
                System.out.println(file.getAbsolutePath());
            }
        }
        //String imageFile = argv[0];

        //System.out.println(features.getJsonAll());
        //System.out.println(features.getTokensAll());
        //System.out.println(features.getLabeledHexAll());
        //System.out.println(features.getRawHexAll());

//    for (Entry<File,ImageFeatures> e : processor.getResults().entrySet()) {
//    	File image = e.getKey();
//    	ImageFeatures features = e.getValue();
//        System.err.println( imageFile + "\t" + features.getRcompact() );
//        System.err.println( imageFile + "\t" + features.getGcompact() );
//        System.err.println( imageFile + "\t" + features.getBcompact() );
//        System.err.println( imageFile + "\t" + features.getTcompact() );
//        System.err.println( imageFile + "\t" + features.getCcompact() );
//        System.err.println();
//    }
    }}
