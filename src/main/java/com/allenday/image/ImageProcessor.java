package com.allenday.image;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allenday.image.backend.Processor;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class ImageProcessor {
	private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;
	public static final int T = 3;
	public static final int C = 4;
	public static final int M = 5;

	private int bins = 0;
	private int bits = 0;
	private boolean normalize = false;


	Map<File, ImageFeatures> files = new HashMap<File,ImageFeatures>();
	Processor processor;

	//static KDTree[] kdtree = {new KDTree<String>(8), new KDTree<String>(8), new KDTree<String>(8)}; 

	public ImageProcessor() {
		this(8, 8, false);
	}

	public ImageProcessor(int bins, int bits, boolean normalize) {
		this.bins = bins;
		this.bits = bits;
		this.normalize = normalize;
	}

	public void clearFiles() {
		files.clear();
	}

	public void setFiles(List<File> files) {
		clearFiles();
		addFiles( files );
	}

	public void addFile(File file) {
		if ( file.isDirectory() )
			recurse(file);
		else if ( ! files.containsKey(file) )
			files.put(file,null);
	}

	public void addFiles(List<File> files) {
		for (File file : files) {
			addFile(file);
		}
	}

	public void recurse(File directory) {
		if (directory.isDirectory()) {
			String[] ents = directory.list();
			int i;
			for (i = 0; i < ents.length; i++) {
				File f = new File(ents[i]);
				if (f.isDirectory()) {
					File ff = new File(directory+"/"+f);
					//System.err.println("recurse="+ff);
					recurse(ff);
				}
				else {
					File ff = new File(directory+"/"+f);
					//System.err.println("add file1="+ff);
					if ( ! files.containsKey(ff) )
						files.put(ff,null);
				}
			}
		}
		else if ( ! files.containsKey(directory) ) {
			//System.err.println("add file2="+directory);
			files.put(directory,null);
		}
	}

	public Map<File,ImageFeatures> getResults() {
		Map<File,ImageFeatures> res = new HashMap<File,ImageFeatures>();
		for (Entry<File,ImageFeatures> e : files.entrySet()) {
			if (e.getValue() != null)
				res.put(e.getKey(), e.getValue());
		}
		return res;
	}

	public void processImages() {		
		for (File f : files.keySet()) {

			//disallow non jpg
			if ( f.toString().indexOf("jpg") == -1 && f.toString().indexOf("jpeg") == -1)
				continue;

			//skip already processed
			if ( files.get(f) != null )
				continue;

			try {
				processor = new Processor(f, bins, bits, normalize);
			} catch (KeySizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyDuplicateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			double[] r  = processor.getRedHistogram();
			double[] g  = processor.getGreenHistogram();
			double[] b  = processor.getBlueHistogram();
			double[] t  = processor.getTextureHistogram();
			double[] c  = processor.getCurvatureHistogram();
			double[] m  = processor.getTopologyValues();
			char[]   ml = processor.getTopologyLabels();

			double[] mm = new double[m.length];
			for (int z = 0; z < m.length; z++)
				mm[z] = m[z];

			ImageFeatures features = new ImageFeatures(f.toString(), bins, 16);
			for (int i = 0; i < r.length; i++){
				logger.debug("r="+r[i]);					
			}
			features.setR(r);
			features.setG(g);
			features.setB(b);
			features.setT(t);
			features.setC(c);
			//features.setM(mm);
			//features.setMlabel(ml);

			files.put(f, features);

		}
	}
}
