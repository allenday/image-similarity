package com.allenday.image;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class RankerTest {
	ImageProcessor processor = new ImageProcessor();
	Ranker ranker;
	
	@Test
	public void test() throws KeySizeException, KeyDuplicateException {
		processor.addFile(new File("src/test/resources/image"));
		processor.processImages();
		Map<File,ImageFeatures> res = processor.getResults();
		Ranker ranker = new Ranker(res.values());
		List<SearchResult> match = ranker.rank(res.values().iterator().next());
		
		for (SearchResult m : match) {
			System.err.println(m.score + "\t" + m.id);
		}
		
		//fail("Not yet implemented");
	}

}
