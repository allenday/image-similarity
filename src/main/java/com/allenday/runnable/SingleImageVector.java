package com.allenday.runnable;

import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageProcessor;

import java.io.File;

@SuppressWarnings("SpellCheckingInspection")
class SingleImageVector {
    public static void main(String[] argv) {
        int bins = 16;
        int bits = 4;
        boolean normalize = false;
        String imageFile = argv[0];
        //String imageFile = "src/test/resources/image/artists_03.jpeg";
        ImageProcessor processor = new ImageProcessor(bins, bits, false);
        ImageFeatures features = processor.extractFeatures(new File(imageFile));

        System.out.println(features.getAllJson());

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
    }
}