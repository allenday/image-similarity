package com.allenday.image.runnable;

import java.io.File;
import java.util.Map.Entry;

import com.allenday.image.*;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class ImageVec {
  public static void main(String[] argv) throws KeySizeException, KeyDuplicateException {
    ImageProcessor processor = new ImageProcessor(16, 4, false);
    processor.addFile(new File(argv[0]));
    //processor.addFile(new File("/Users/allenday/Downloads/x.jpg"));
    processor.processImages();

    for (Entry<File,ImageFeatures> e : processor.getResults().entrySet()) {
    	File image = e.getKey();
    	ImageFeatures features = e.getValue();
        /*
        System.err.println( image + "\t" + features.getR() );
        System.err.println( image + "\t" + features.getG() );
        System.err.println( image + "\t" + features.getB() );
        System.err.println( image + "\t" + features.getT() );
        System.err.println( image + "\t" + features.getC() );
        System.err.println();
        */
        System.err.println( image + "\t" + features.getRcompact() );
        System.err.println( image + "\t" + features.getGcompact() );
        System.err.println( image + "\t" + features.getBcompact() );
        System.err.println( image + "\t" + features.getTcompact() );
        System.err.println( image + "\t" + features.getCcompact() );
        //System.err.println( image + "\t" + features.getMcompact() );
        System.err.println();
        /*
        System.err.println( image + "\t" + features.getRtokens() );
        System.err.println( image + "\t" + features.getGtokens() );
        System.err.println( image + "\t" + features.getBtokens() );
        System.err.println( image + "\t" + features.getTtokens() );
        System.err.println( image + "\t" + features.getCtokens() );
        */
    }    
  }
}
