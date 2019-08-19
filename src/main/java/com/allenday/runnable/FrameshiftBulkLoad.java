package com.allenday.runnable;

import com.allenday.image.ImageFeatures;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FrameshiftBulkLoad {
    public static void main(String[] args) throws SolrServerException, IOException {
        int bins = 8;
        Integer batchSize = 100;
        SolrClient solr = new HttpSolrClient.Builder(args[0]).build();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String record;

        Integer batchIndex = 0;
        Integer totalRecords = 0;

        while ((record = br.readLine()) != null) {
            System.err.println(totalRecords + " " + record);
            String[] fields = record.split("\t");
            ImageFeatures x = new ImageFeatures(null,8,fields[1]);

            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", fields[0]);
            document.addField("file_id", fields[0]);
            document.addField("time_offset", 0);
            document.addField("rgbtc", x.getLabeledHexAll());
            document.addField("xception", fields[2]);
            UpdateResponse response = solr.add(document);
            batchIndex++;
            totalRecords++;
            if (batchIndex >= batchSize) {
                solr.commit();
            }
        }
        solr.commit();
    }
}
