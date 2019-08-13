package com.allenday.runnable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

class FrameshiftInsert {
    public static void main(String[] args) throws SolrServerException, IOException {
        SolrClient solr = new HttpSolrClient.Builder("http://localhost:8983/solr/frameshift").build();
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "");
        document.addField("file_id", "");
        document.addField("time_offset", "");
        document.addField("rgbtc", "");
        UpdateResponse response = solr.add(document);


        solr.commit();
    }
}
