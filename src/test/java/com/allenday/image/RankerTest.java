package com.allenday.image;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class RankerTest {
	ImageProcessor processor = new ImageProcessor();
	Ranker ranker;
	
	@Test
	public void test() throws KeySizeException, KeyDuplicateException {
		processor.setFiles(new File("src/test/resources/image"));
		List<ImageFeatures> images = processor.processImages();
		Ranker ranker = new Ranker(images);
		List<SearchResult> match = ranker.rank(images.get(1));
		
		for (SearchResult m : match) {
			System.err.println(m.score + "\t" + m.id);
		}
		
		fail("Not yet implemented");
	}

}
