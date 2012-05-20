package com.allenday.image;

import java.util.Vector;

public class ImageFeatures {
	public Vector<Double> R = new Vector<Double>();
	public Vector<Double> G = new Vector<Double>();
	public Vector<Double> B = new Vector<Double>();
	public Vector<Double> T = new Vector<Double>();
	public Vector<Double> C = new Vector<Double>();
	public Vector<Double> M = new Vector<Double>();
	public String id = null;
	
	public ImageFeatures(String id) {
		this.id = id;
		R.setSize(8);
		G.setSize(8);
		B.setSize(8);
		T.setSize(8);
		C.setSize(8);
		M.setSize(16);
	}

	public void boundsCheck(int a, int b) {
		if (a != b)
			throw new IndexOutOfBoundsException("vector size mismatch");
	}
	
	public void setR(double[] n) {
		boundsCheck(n.length, R.size());
		for (int i = 0; i < n.length; i++) {
			R.set(i, n[i]);
		}
	}
	
	public void setG(double[] n) {
		boundsCheck(n.length, G.size());
		for (int i = 0; i < n.length; i++) {
			G.set(i, n[i]);
		}
	}

	public void setB(double[] n) {
		boundsCheck(n.length, B.size());
		for (int i = 0; i < n.length; i++) {
			B.set(i, n[i]);
		}
	}

	public void setT(double[] n) {
		boundsCheck(n.length, T.size());
		for (int i = 0; i < n.length; i++) {
			T.set(i, n[i]);
		}
	}

	public void setC(double[] n) {
		boundsCheck(n.length, C.size());
		for (int i = 0; i < n.length; i++) {
			C.set(i, n[i]);
		}
	}

	public void setM(double[] n) {
		boundsCheck(n.length, M.size());
		for (int i = 0; i < n.length; i++) {
			M.set(i, n[i]);
		}
	}
}
