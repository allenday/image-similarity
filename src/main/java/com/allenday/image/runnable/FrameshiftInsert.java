package com.allenday.image.runnable;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class FrameshiftInsert {
    HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr");

}
