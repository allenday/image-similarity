package com.allenday.runnable;

import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageProcessor;
import org.apache.commons.io.DirectoryWalker;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.awt.color.CMMException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageVectors {
    public static void main(String[] argv) throws IOException, SolrServerException {
        SolrClient solr = null;
        int batchSize = 10;
        int bins = 8;
        int bits = 3; //specifically set to 3 to enable base64 packing
        boolean normalize = false;
        ImageProcessor processor = new ImageProcessor(bins, bits, false);

        //String input = "/Users/allenday/Downloads/01";//"src/test/resources/image/";//pictures-of-nasa-s-atlantis-shuttle-launch-photos-video.jpeg";//bad.gif";
        String input = argv[0];
        String frameshiftUrl = "";
        if (argv.length > 1)
            frameshiftUrl = argv[1];
        Boolean loadFrameshift = frameshiftUrl.compareTo("") == 1 ? true : false;

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

        Integer batchIndex = 0;
        for (File file : files) {
            System.err.println(file.getAbsolutePath());
            ImageFeatures features = null;
            try {
                features = processor.extractFeatures(file);
                System.out.println(file.getAbsolutePath() + " " + features.getRawB64All() + " " + features.getLabeledHexAll());
                //ImageFeatures x = new ImageFeatures("asdf",bins,features.getRawB64All());
                //System.out.println(file.getAbsolutePath() + " " + features.getRawB64All() + " " + x.getLabeledHexAll());

                if (loadFrameshift) {
                    if (solr == null) {
                        solr = new HttpSolrClient.Builder(frameshiftUrl).build();
                    }
                    SolrInputDocument document = new SolrInputDocument();
                    document.addField("id", features.id);
                    document.addField("file_id", features.id);
                    document.addField("time_offset", 0);
                    document.addField("rgbtc", features.getLabeledHexAll());
                    UpdateResponse response = solr.add(document);
                    //System.err.println(response);
                    //solr.commit();
                    batchIndex++;
                }
            } catch (CMMException e) {
                System.out.println("CMMException failed to process: " + file.getAbsolutePath());
            } catch (IllegalArgumentException e) {
                System.out.println("IllegalArgumentException failed to process: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("IOException failed to process: " + file.getAbsolutePath());
            } catch (SolrServerException e) {
                System.out.println("SolrServerException failed to process: " + file.getAbsolutePath());
                e.printStackTrace();
            }
            if (loadFrameshift && batchIndex >= batchSize) {
                solr.commit();
                batchIndex = 0;
            }
        }
        if (loadFrameshift)
            solr.commit();

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
    }
}
