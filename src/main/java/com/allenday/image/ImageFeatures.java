package com.allenday.image;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.Base64;

public class ImageFeatures implements Serializable {
    //TODO enum this
    public static final Integer DIMENSIONS = 5;

    public static final Integer R = 0;
    public static final Integer G = 1;
    public static final Integer B = 2;
    public static final Integer T = 3;
    public static final Integer C = 4;
    public final String id;
    public Double score = null;
    private final List<Vector<Double>> vectors = new ArrayList<>();

    public ImageFeatures(String id, int bins, int blocksPerSide) {
        this.id = id;
        for (Integer d = 0; d < DIMENSIONS; d++) {
            Vector<Double> v = new Vector<>();
            v.setSize(bins);
            vectors.add(v);
        }
    }

    public ImageFeatures(String id, int bins, String encoded) {
        this.id = id;
        String[] encodedDimension = encoded.split("-");
        for (Integer d = 0; d < DIMENSIONS; d++) {
            Vector<Double> v = decodeFeatures(encodedDimension[d]);
            vectors.add(v);
        }
    }

    public Double getScore() {
        return score;
    }

    private void boundsCheck(int a, int b) {
        if (a != b)
            throw new IndexOutOfBoundsException("vector size mismatch: " + a + " != " + b);
    }

    public Vector<Double> getDimension(Integer d) {
        return vectors.get(d);
    }

    public List<Vector<Double>> getDimensions() {
        List<Vector<Double>> dims = new ArrayList<>();
        for (int i = 0; i < DIMENSIONS; i++) {
            dims.add(this.getDimension(i));
        }
        return dims;
    }

    private Vector<Integer> d2i(Vector<Double> d) {
        Vector<Integer> res = new Vector<>();
        for (Double aDouble : d) res.add(aDouble.intValue());
        return res;
    }

    private String getTokens(Vector<Double> x, String prefix, String sep) {
        StringBuilder res = new StringBuilder();
        Vector<Integer> v = d2i(x);
        for (int i = 0; i < v.size(); i++) {
            for (int j = 0; j < v.get(i); j++) {
                res.append(String.format("%s%X%s", prefix, i, sep));
            }
        }
        return res.toString();
    }

    private String getLabeledHex(Vector<Double> x, String prefix, String sep) {
        StringBuilder res = new StringBuilder();
        Vector<Integer> v = d2i(x);
        for (int i = 0; i < v.size(); i++) {
            res.append(String.format("%s%X%X%s", prefix, i, v.get(i) & 0xFFFFF, sep));
        }
        return res.toString();
    }

    public Vector<Double> decodeFeatures(String encoded) {
        String[] chars = encoded.split("");
        Double m = 0d;
        Double n = 0d;
        Vector<Double> v = new Vector<>();
        int k = 0;
        for (int i = 0; i < chars.length; i++) {
            v.setSize(k+2);

            if (chars[i].compareTo("A") == 0)      { m=0d; n=0d; }
            else if (chars[i].compareTo("B") == 0) { m=0d; n=1d; }
            else if (chars[i].compareTo("C") == 0) { m=0d; n=2d; }
            else if (chars[i].compareTo("D") == 0) { m=0d; n=3d; }
            else if (chars[i].compareTo("E") == 0) { m=0d; n=4d; }
            else if (chars[i].compareTo("F") == 0) { m=0d; n=5d; }
            else if (chars[i].compareTo("G") == 0) { m=0d; n=6d; }
            else if (chars[i].compareTo("H") == 0) { m=0d; n=7d; }
            else if (chars[i].compareTo("I") == 0) { m=1d; n=0d; }
            else if (chars[i].compareTo("J") == 0) { m=1d; n=1d; }
            else if (chars[i].compareTo("K") == 0) { m=1d; n=2d; }
            else if (chars[i].compareTo("L") == 0) { m=1d; n=3d; }
            else if (chars[i].compareTo("M") == 0) { m=1d; n=4d; }
            else if (chars[i].compareTo("N") == 0) { m=1d; n=5d; }
            else if (chars[i].compareTo("O") == 0) { m=1d; n=6d; }
            else if (chars[i].compareTo("P") == 0) { m=1d; n=7d; }
            else if (chars[i].compareTo("Q") == 0) { m=2d; n=0d; }
            else if (chars[i].compareTo("R") == 0) { m=2d; n=1d; }
            else if (chars[i].compareTo("S") == 0) { m=2d; n=2d; }
            else if (chars[i].compareTo("T") == 0) { m=2d; n=3d; }
            else if (chars[i].compareTo("U") == 0) { m=2d; n=4d; }
            else if (chars[i].compareTo("V") == 0) { m=2d; n=5d; }
            else if (chars[i].compareTo("W") == 0) { m=2d; n=6d; }
            else if (chars[i].compareTo("X") == 0) { m=2d; n=7d; }
            else if (chars[i].compareTo("Y") == 0) { m=3d; n=0d; }
            else if (chars[i].compareTo("Z") == 0) { m=3d; n=1d; }
            else if (chars[i].compareTo("a") == 0) { m=3d; n=2d; }
            else if (chars[i].compareTo("b") == 0) { m=3d; n=3d; }
            else if (chars[i].compareTo("c") == 0) { m=3d; n=4d; }
            else if (chars[i].compareTo("d") == 0) { m=3d; n=5d; }
            else if (chars[i].compareTo("e") == 0) { m=3d; n=6d; }
            else if (chars[i].compareTo("f") == 0) { m=3d; n=7d; }
            else if (chars[i].compareTo("g") == 0) { m=4d; n=0d; }
            else if (chars[i].compareTo("h") == 0) { m=4d; n=1d; }
            else if (chars[i].compareTo("i") == 0) { m=4d; n=2d; }
            else if (chars[i].compareTo("j") == 0) { m=4d; n=3d; }
            else if (chars[i].compareTo("k") == 0) { m=4d; n=4d; }
            else if (chars[i].compareTo("l") == 0) { m=4d; n=5d; }
            else if (chars[i].compareTo("m") == 0) { m=4d; n=6d; }
            else if (chars[i].compareTo("n") == 0) { m=4d; n=7d; }
            else if (chars[i].compareTo("o") == 0) { m=5d; n=0d; }
            else if (chars[i].compareTo("p") == 0) { m=5d; n=1d; }
            else if (chars[i].compareTo("q") == 0) { m=5d; n=2d; }
            else if (chars[i].compareTo("r") == 0) { m=5d; n=3d; }
            else if (chars[i].compareTo("s") == 0) { m=5d; n=4d; }
            else if (chars[i].compareTo("t") == 0) { m=5d; n=5d; }
            else if (chars[i].compareTo("u") == 0) { m=5d; n=6d; }
            else if (chars[i].compareTo("v") == 0) { m=5d; n=7d; }
            else if (chars[i].compareTo("w") == 0) { m=6d; n=0d; }
            else if (chars[i].compareTo("x") == 0) { m=6d; n=1d; }
            else if (chars[i].compareTo("y") == 0) { m=6d; n=2d; }
            else if (chars[i].compareTo("z") == 0) { m=6d; n=3d; }
            else if (chars[i].compareTo("0") == 0) { m=6d; n=4d; }
            else if (chars[i].compareTo("1") == 0) { m=6d; n=5d; }
            else if (chars[i].compareTo("2") == 0) { m=6d; n=6d; }
            else if (chars[i].compareTo("3") == 0) { m=6d; n=7d; }
            else if (chars[i].compareTo("4") == 0) { m=7d; n=0d; }
            else if (chars[i].compareTo("5") == 0) { m=7d; n=1d; }
            else if (chars[i].compareTo("6") == 0) { m=7d; n=2d; }
            else if (chars[i].compareTo("7") == 0) { m=7d; n=3d; }
            else if (chars[i].compareTo("8") == 0) { m=7d; n=4d; }
            else if (chars[i].compareTo("9") == 0) { m=7d; n=5d; }
            else if (chars[i].compareTo("+") == 0) { m=7d; n=6d; }
            else if (chars[i].compareTo("/") == 0) { m=7d; n=7d; }

            //System.err.println("k="+k+","+m);
            v.set(k,m);
            k++;
            //System.err.println("k="+k+","+n);
            v.set(k,n);
            k++;
        }
        return v;
    }

    private String getLabeledB64(Vector<Double> x, String prefix, String sep) {
        Vector<Integer> v = d2i(x);
        StringBuilder res = new StringBuilder();

        //TODO this assumes bits=3, but bits is never passed to ImageFeatures constructor
        for (int i = 0; i < v.size(); i+= 2) {
            String e = null;
            int j = i+1;

            if (v.get(i) == 0 && v.get(j) == 0)      { e = "A"; }
            else if (v.get(i) == 0 && v.get(j) == 1) { e = "B"; }
            else if (v.get(i) == 0 && v.get(j) == 2) { e = "C"; }
            else if (v.get(i) == 0 && v.get(j) == 3) { e = "D"; }
            else if (v.get(i) == 0 && v.get(j) == 4) { e = "E"; }
            else if (v.get(i) == 0 && v.get(j) == 5) { e = "F"; }
            else if (v.get(i) == 0 && v.get(j) == 6) { e = "G"; }
            else if (v.get(i) == 0 && v.get(j) == 7) { e = "H"; }

            else if (v.get(i) == 1 && v.get(j) == 0) { e = "I"; }
            else if (v.get(i) == 1 && v.get(j) == 1) { e = "J"; }
            else if (v.get(i) == 1 && v.get(j) == 2) { e = "K"; }
            else if (v.get(i) == 1 && v.get(j) == 3) { e = "L"; }
            else if (v.get(i) == 1 && v.get(j) == 4) { e = "M"; }
            else if (v.get(i) == 1 && v.get(j) == 5) { e = "N"; }
            else if (v.get(i) == 1 && v.get(j) == 6) { e = "O"; }
            else if (v.get(i) == 1 && v.get(j) == 7) { e = "P"; }

            else if (v.get(i) == 2 && v.get(j) == 0) { e = "Q"; }
            else if (v.get(i) == 2 && v.get(j) == 1) { e = "R"; }
            else if (v.get(i) == 2 && v.get(j) == 2) { e = "S"; }
            else if (v.get(i) == 2 && v.get(j) == 3) { e = "T"; }
            else if (v.get(i) == 2 && v.get(j) == 4) { e = "U"; }
            else if (v.get(i) == 2 && v.get(j) == 5) { e = "V"; }
            else if (v.get(i) == 2 && v.get(j) == 6) { e = "W"; }
            else if (v.get(i) == 2 && v.get(j) == 7) { e = "X"; }

            else if (v.get(i) == 3 && v.get(j) == 0) { e = "Y"; }
            else if (v.get(i) == 3 && v.get(j) == 1) { e = "Z"; }
            else if (v.get(i) == 3 && v.get(j) == 2) { e = "a"; }
            else if (v.get(i) == 3 && v.get(j) == 3) { e = "b"; }
            else if (v.get(i) == 3 && v.get(j) == 4) { e = "c"; }
            else if (v.get(i) == 3 && v.get(j) == 5) { e = "d"; }
            else if (v.get(i) == 3 && v.get(j) == 6) { e = "e"; }
            else if (v.get(i) == 3 && v.get(j) == 7) { e = "f"; }

            else if (v.get(i) == 4 && v.get(j) == 0) { e = "g"; }
            else if (v.get(i) == 4 && v.get(j) == 1) { e = "h"; }
            else if (v.get(i) == 4 && v.get(j) == 2) { e = "i"; }
            else if (v.get(i) == 4 && v.get(j) == 3) { e = "j"; }
            else if (v.get(i) == 4 && v.get(j) == 4) { e = "k"; }
            else if (v.get(i) == 4 && v.get(j) == 5) { e = "l"; }
            else if (v.get(i) == 4 && v.get(j) == 6) { e = "m"; }
            else if (v.get(i) == 4 && v.get(j) == 7) { e = "n"; }

            else if (v.get(i) == 5 && v.get(j) == 0) { e = "o"; }
            else if (v.get(i) == 5 && v.get(j) == 1) { e = "p"; }
            else if (v.get(i) == 5 && v.get(j) == 2) { e = "q"; }
            else if (v.get(i) == 5 && v.get(j) == 3) { e = "r"; }
            else if (v.get(i) == 5 && v.get(j) == 4) { e = "s"; }
            else if (v.get(i) == 5 && v.get(j) == 5) { e = "t"; }
            else if (v.get(i) == 5 && v.get(j) == 6) { e = "u"; }
            else if (v.get(i) == 5 && v.get(j) == 7) { e = "v"; }

            else if (v.get(i) == 6 && v.get(j) == 0) { e = "w"; }
            else if (v.get(i) == 6 && v.get(j) == 1) { e = "x"; }
            else if (v.get(i) == 6 && v.get(j) == 2) { e = "y"; }
            else if (v.get(i) == 6 && v.get(j) == 3) { e = "z"; }
            else if (v.get(i) == 6 && v.get(j) == 4) { e = "0"; }
            else if (v.get(i) == 6 && v.get(j) == 5) { e = "1"; }
            else if (v.get(i) == 6 && v.get(j) == 6) { e = "2"; }
            else if (v.get(i) == 6 && v.get(j) == 7) { e = "3"; }

            else if (v.get(i) == 7 && v.get(j) == 0) { e = "4"; }
            else if (v.get(i) == 7 && v.get(j) == 1) { e = "5"; }
            else if (v.get(i) == 7 && v.get(j) == 2) { e = "6"; }
            else if (v.get(i) == 7 && v.get(j) == 3) { e = "7"; }
            else if (v.get(i) == 7 && v.get(j) == 4) { e = "8"; }
            else if (v.get(i) == 7 && v.get(j) == 5) { e = "9"; }
            else if (v.get(i) == 7 && v.get(j) == 6) { e = "+"; }
            else if (v.get(i) == 7 && v.get(j) == 7) { e = "/"; }
            res.append(e);
        }

        return res.toString();
    }


    public String getJsonAll() {
        return "{" +
                "\"red\":[" + getR() + "]," +
                "\"green\":[" + getG() + "]," +
                "\"blue\":[" + getB() + "]," +
                "\"texture\":[" + getT() + "]," +
                "\"curvature\":[" + getC() + "]" +
                "}";
    }

    //This is for TF.IDF style search
    public String getTokensAll() {
        return getTokensR() + getTokensG() + getTokensB() + getTokensT() + getTokensC();

    }
    public String getRawHexAll() {
        return getRawHexR() + getRawHexG() + getRawHexB() + getRawHexT() + getRawHexC();
    }
    public String getLabeledHexAll() {
        return getLabeledHexR() + getLabeledHexG() + getLabeledHexB() + getLabeledHexT() + getLabeledHexC();
    }
    public String getRawB64All() {
        return getRawB64R() + "-" + getRawB64G() + "-" + getRawB64B() + "-" + getRawB64T() + "-" + getRawB64C();
    }
//    public String getLabeledBase64All() {
//        return getLabeledBase64R() + getLabeledBase64G() + getLabeledBase64B() + getLabeledBase64T() + getLabeledBase64C();
//    }

    public String getTokensR() {
        return getTokens(vectors.get(R), "r", " ");
    }
    private String getLabeledHexR() {
        return getLabeledHex(vectors.get(R), "r", " ");
    }
    private String getRawHexR() {
        return getLabeledHex(vectors.get(R), "", " ");
    }
    private String getRawB64R() {
        return getLabeledB64(vectors.get(R), "", " ");
    }

    public String getTokensG() {
        return getTokens(vectors.get(G), "g", " ");
    }
    private String getLabeledHexG() {
        return getLabeledHex(vectors.get(G), "g", " ");
    }
    private String getRawHexG() {
        return getLabeledHex(vectors.get(G), "", " ");
    }
    private String getRawB64G() {
        return getLabeledB64(vectors.get(G), "", " ");
    }

    public String getTokensB() {
        return getTokens(vectors.get(B), "b", " ");
    }
    private String getLabeledHexB() {
        return getLabeledHex(vectors.get(B), "b", " ");
    }
    private String getRawHexB() {
        return getLabeledHex(vectors.get(B), "", " ");
    }
    private String getRawB64B() {
        return getLabeledB64(vectors.get(B), "", " ");
    }

    public String getTokensT() {
        return getTokens(vectors.get(T), "t", " ");
    }
    private String getLabeledHexT() {
        return getLabeledHex(vectors.get(T), "t", " ");
    }
    private String getRawHexT() {
        return getLabeledHex(vectors.get(T), "", " ");
    }
    private String getRawB64T() {
        return getLabeledB64(vectors.get(T), "", " ");
    }

    public String getTokensC() {
        return getTokens(vectors.get(C), "c", " ");
    }
    private String getLabeledHexC() {
        return getLabeledHex(vectors.get(C), "c", " ");
    }
    private String getRawHexC() {
        return getLabeledHex(vectors.get(C), "", " ");
    }
    private String getRawB64C() {
        return getLabeledB64(vectors.get(C), "", " ");
    }

    // Red.  double[8], 0..255
	private String getR() {
        return StringUtils.join(d2i(vectors.get(R)), ",");
    }

    public void setR(double[] n) {
        boundsCheck(n.length, vectors.get(R).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(R).set(i, n[i]);
        }
    }

    // Green.  double[8], 0..255
	private String getG() {
        return StringUtils.join(d2i(vectors.get(G)), ",");
    }

    public void setG(double[] n) {
        boundsCheck(n.length, vectors.get(G).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(G).set(i, n[i]);
        }
    }

    // Blue.  double[8], 0..255
	private String getB() {
        return StringUtils.join(d2i(vectors.get(B)), ",");
    }

    public void setB(double[] n) {
        boundsCheck(n.length, vectors.get(B).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(B).set(i, n[i]);
        }
    }

    // Texture.  double[8], 0..255
	private String getT() {
        return StringUtils.join(d2i(vectors.get(T)), ",");
    }

    public void setT(double[] n) {
        boundsCheck(n.length, vectors.get(T).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(T).set(i, n[i]);
        }
    }

    // Curvature.  double[8], 0..255
	private String getC() {
        return StringUtils.join(d2i(vectors.get(C)), ",");
    }

    public void setC(double[] n) {
        boundsCheck(n.length, vectors.get(C).size());
        for (int i = 0; i < n.length; i++) {
            vectors.get(C).set(i, n[i]);
        }
    }
}
