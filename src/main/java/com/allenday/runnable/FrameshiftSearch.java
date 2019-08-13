package com.allenday.runnable;

class FrameshiftSearch {
	/*
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
	*/
}
