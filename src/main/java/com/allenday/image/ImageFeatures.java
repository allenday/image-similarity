package com.allenday.image;

import java.util.Vector;
import org.apache.commons.lang3.StringUtils;

public class ImageFeatures {
	public Vector<Double> R = new Vector<Double>();
	public Vector<Double> G = new Vector<Double>();
	public Vector<Double> B = new Vector<Double>();
	public Vector<Double> T = new Vector<Double>();
	public Vector<Double> C = new Vector<Double>();
	public Vector<Double> M = new Vector<Double>();
	public String id = null;
	
	public ImageFeatures(String id, int bins) {
		this.id = id;
		R.setSize(bins);
		G.setSize(bins);
		B.setSize(bins);
		T.setSize(bins);
		C.setSize(bins);
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

	private Vector<Integer> d2i(Vector<Double> d) {
		Vector<Integer> res = new Vector<Integer>();
		for (int i = 0; i < d.size(); i++)
			res.add(d.get(i).intValue());
		return res;
	}

	private String getTokens(Vector<Double> x, String p) {
		String res = "";
		Vector<Integer> v = d2i(x);
		for (int i = 0; i < v.size(); i++) {
			for (int j = 0; j < v.get(i); j++) {
				res += String.format("%s%X ", p, i);				
			}
		}
		return res;
	}

	private String getCompact(Vector<Double> x, String p) {
		String res = "";
		Vector<Integer> v = d2i(x);
		for (int i = 0; i < v.size(); i++) {
			res += String.format("%s%X%X ", p, i, v.get(i) & 0xFFFFF);
		}
		return res;
	}
	
	public String getRtokens() {
		return getTokens(R,"r");
	}

	public String getRcompact() {
		return getCompact(R,"r");
	}

	public String getGtokens() {
		return getTokens(G,"g");
	}

	public String getGcompact() {
		return getCompact(G,"g");
	}

	public String getBtokens() {
		return getTokens(B,"b");
	}

	public String getBcompact() {
		return getCompact(B,"b");
	}

	public String getTtokens() {
		return getTokens(T,"t");
	}

	public String getTcompact() {
		return getCompact(T,"t");
	}

	public String getCtokens() {
		return getTokens(C,"c");
	}

	public String getCcompact() {
		return getCompact(C,"c");
	}

	// Red.  double[8], 0..255
	public String getR() {
		return StringUtils.join(d2i(R), ",");
	}
	// Green.  double[8], 0..255
	public String getG() {
		return StringUtils.join(d2i(G), ",");
	}
	// Blue.  double[8], 0..255
	public String getB() {
		return StringUtils.join(d2i(B), ",");
	}
	// Texture.  double[8], 0..255
	public String getT() {
		return StringUtils.join(d2i(T), ",");
	}
	// Curvature.  double[8], 0..255
	public String getC() {
		return StringUtils.join(d2i(C), ",");
	}
	// Topology.  double[16]
	public String getM() {
		return StringUtils.join(d2i(M), ",");
	}
/*
                                        "\t" + r[0] + "," + r[1] + "," + r[2] + "," + r[3] + "," + r[4] + "," + r[5] + "," + r[6] + "," + r[7] +
                                        "\t" + g[0] + "," + g[1] + "," + g[2] + "," + g[3] + "," + g[4] + "," + g[5] + "," + g[6] + "," + g[7] +
                                        "\t" + b[0] + "," + b[1] + "," + b[2] + "," + b[3] + "," + b[4] + "," + b[5] + "," + b[6] + "," + b[7] +
                                        "\t" + t[0] + "," + t[1] + "," + t[2] + "," + t[3] + "," + t[4] + "," + t[5] + "," + t[6] + "," + t[7] +
                                        "\t" + c[0] + "," + c[1] + "," + c[2] + "," + c[3] + "," + c[4] + "," + c[5] + "," + c[6] + "," + c[7] +
                                        "\t" + m[0] + "," + m[1] + "," + m[2] + "," + m[3] + "," + m[4] + "," + m[5] + "," + m[6] + "," + m[7] + "," + m[8] + "," + m[9] + "," + m[10] + "," + m[11] + "," + m[12] + "," + m[13] + "," + m[14] + "," + m[15] +
                                        "";
*/
}
