package com.allenday.image.runnable;

import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.allenday.image.ImageFeatures;
import com.allenday.image.ImageProcessor;

public class FrameshiftSearch {
    public static void main( String[] args ) throws SolrServerException, IOException {
      	SolrClient solr = new HttpSolrClient.Builder("http://localhost:8983/solr/frameshift").build();

      	ImageProcessor processor = new ImageProcessor(16,4,false);
      	processor.addFile(new File(args[0]));
    		processor.processImages();
        for (Entry<File,ImageFeatures> e : processor.getResults().entrySet()) {
        	File image = e.getKey();
        	ImageFeatures f = e.getValue();
        	
					SolrQuery query = new SolrQuery();
					String qq =							
							f.getRcompact() + " " +
							f.getGcompact() + " " +
							f.getBcompact() + " " +
							f.getTcompact() + " " +
							f.getCcompact() + " " +
							"";

					System.err.println("query: "+qq);
					query.setQuery(qq);
	        query.set("fl", "id,file_id,score");

					QueryResponse response = solr.query(query);
					
					SolrDocumentList list = response.getResults();
					ListIterator<SolrDocument> resultsIterator = list.listIterator();
					while (resultsIterator.hasNext()) {
						SolrDocument result = resultsIterator.next();
						for (String fieldName : result.getFieldNames()) {
							System.err.println(fieldName + "\t" + result.getFieldValue(fieldName));
						}
					}        	
        }
    }
}
