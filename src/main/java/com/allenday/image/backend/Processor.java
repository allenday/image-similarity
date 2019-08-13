package com.allenday.image.backend;

import com.allenday.image.ImageFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.media.jai.Histogram;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.HistogramDescriptor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//import org.imgscalr.Scalr;
//import org.imgscalr.Scalr.Method;

public class Processor {
    private static final int R = 0;

    //	private KDTree[] kdtree = {new KDTree<String>(8), new KDTree<String>(8), new KDTree<String>(8), new KDTree<String>(8), new KDTree<String>(8)};

//	public static final int Y  = 0;
//	public static final int Cb = 1;
//	public static final int Cr = 2;
//	public static final int T  = 3;
//	public static final int C  = 4;
//	public static final int O  = 5;
    private static final int G = 1;
    private static final int B = 2;
    public static final int T = 3;
    public static final int C = 4;
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    //always 4x4
    private final int blocksPerSide = 4;
    private double[] texture;
    private double[] curviness;
    private char[] topologyLabel;
    private double[] topologyValue;
    private boolean hasEdgeHistograms = false;
    private BufferedImage bufferedImage;
    private SampleModel sampleModel;
    private Histogram histogram;
    private PlanarImage image;
    private int bandGlobalMax = 1;
    private final boolean normalize;
    private final int bins;
    private final int bitsPerBin;

    public Processor(InputStream inputStream, int bins, int bitsPerBin, boolean normalize) throws IOException {
        this.bins = bins;
        this.bitsPerBin = bitsPerBin;
        this.normalize = normalize;

        bufferedImage = ImageIO.read(inputStream);

        processImage(bufferedImage);
    }

    public Processor(File file, int bins, int bitsPerBin, boolean normalize) throws IOException {
        this.bins = bins;
        this.bitsPerBin = bitsPerBin;
        this.normalize = normalize;
        //logger.debug("file="+file);
        bufferedImage = ImageIO.read(file);

        processImage(bufferedImage);

    }

    public Processor(InputStream inputStream, boolean normalize) throws IOException {
        this(inputStream, 8, 8, normalize);
    }

    public Processor(File file, boolean normalize) throws IOException {
        this(file, 8, 8, normalize);
    }

    public ImageFeatures getImageFeatures() {
        ImageFeatures f = new ImageFeatures(".", bins, blocksPerSide);
        f.setR(getRedHistogram());
        f.setG(getBlueHistogram());
        f.setB(getBlueHistogram());
        f.setT(getTextureHistogram());
        f.setC(getCurvatureHistogram());
        return f;
    }

    private void processImage(BufferedImage b) throws IOException {
        texture = new double[bins];
        curviness = new double[bins];
        topologyLabel = new char[blocksPerSide * blocksPerSide];
        topologyValue = new double[blocksPerSide * blocksPerSide];

        if (bufferedImage == null)
            throw new IOException("cannot read file");

//		BufferedImage thumbnail = Scalr.resize(bufferedImage, Method.ULTRA_QUALITY, 480, 480);
        BufferedImage thumbnail = bufferedImage;

        image = PlanarImage.wrapRenderedImage(thumbnail);
        sampleModel = image.getSampleModel();
        int bandCount = sampleModel.getNumBands();
        int bits = DataBuffer.getDataTypeSize(sampleModel.getDataType());
        int[] binz = new int[bandCount];
        double[] min = new double[bandCount];
        double[] max = new double[bandCount];
        int maxxx = 1 << bits;

        for (int i = 0; i < bandCount; i++) {
            //bins[i] = maxxx;
            binz[i] = bins;
            min[i] = 0;
            max[i] = maxxx;
        }
        RenderedOp op = HistogramDescriptor.create(image, null, 1, 1, binz, min, max, null);
        histogram = (Histogram) op.getProperty("histogram");

        if (sampleModel.getNumBands() > 0)
            getBandHistogram(histogram, 0, bins, normalize);
        makeTopologies();
        makeEdgeHistograms();
    }

    private double[] getBandHistogram(Histogram h, int band, int bins, boolean normalize) {
        if (band >= getNumBands()) {
            logger.info("no band " + band + ", using band 0");
            band = 0;
        }

        if (bandGlobalMax == 1) {
            for (int i = 0; i < getNumBands(); i++) {
                int[] frequencies = h.getBins(band);
                for (int frequency : frequencies) {
                    bandGlobalMax = bandGlobalMax > frequency ? bandGlobalMax : frequency;
                }

            }
        }

        int[] frequencies = h.getBins(band);

        int bandMax = 1;
        for (int frequency : frequencies) {
            bandMax = bandMax > frequency ? bandMax : frequency;
            //logger.debug("band="+band+",freq="+frequencies[f]);
        }
        for (int f = 0; f < frequencies.length; f++) {
            if (normalize) {
                frequencies[f] = (int) ((Math.pow(2, bitsPerBin) - 1) * (float) frequencies[f] / bandGlobalMax);
            } else {
                frequencies[f] = (int) ((Math.pow(2, bitsPerBin) - 1) * (float) frequencies[f] / bandMax);
            }
        }

        double[] result = new double[frequencies.length];
        for (int f = 0; f < frequencies.length; f++) {
            result[f] = (double) frequencies[f];
            //logger.debug("freq="+result[f]);
        }
        return result;
    }


    private Float getLowBand() {
        return 1.0f;
    }

    private Float getHighBand() {
        return 3.0f;
    }

    private Integer getNumBands() {
        return sampleModel.getNumBands();
    }

    public double[] getTextureHistogram() {
        return texture;
    }

    public double[] getCurvatureHistogram() {
        return curviness;
    }

    public double[] getRedHistogram() {
        return getBandHistogram(histogram, R, bins, normalize);
    }

    public double[] getGreenHistogram() {
        return getBandHistogram(histogram, G, bins, normalize);
    }

    public double[] getBlueHistogram() {
        return getBandHistogram(histogram, B, bins, normalize);
    }

    private BufferedImage getEdgesImage() {
        CannyEdgeDetector detector = new CannyEdgeDetector();
        detector.setSourceImage(bufferedImage);
        detector.setLowThreshold(getLowBand()); //1.0f
        detector.setHighThreshold(getHighBand()); //3.0f
        detector.process();
        return detector.getEdgesImage();
    }

    /**
     * topology breaks the image into an n*n grid
     * calculates the max color per grid in RGB space
     * and assigns a letter (RGB) and value (normalized by bins)
     * to each cell
     */

    private void makeTopologies() {
        for (int i = 0; i < topologyValue.length; i++) {
            topologyLabel[i] = '.';
            topologyValue[i] = 0;
        }

        int maxH = image.getHeight();
        int maxW = image.getWidth();

        int stepH = (int) Math.floor((double) maxH / blocksPerSide);
        int stepW = (int) Math.floor((double) maxW / blocksPerSide);

//		BufferedImage img = image.getAsBufferedImage().getScaledInstance(width, height, 0);

        String hash = "";

        int tileNum = 0;
        for (int y = 0; y < blocksPerSide; y++) {
            for (int x = 0; x < blocksPerSide; x++) {
                tileNum++;
                Rectangle rect = new Rectangle();
                rect.width = stepW;
                rect.height = stepH;
                rect.x = x * stepW;
                rect.y = y * stepH;
                Raster raster = image.getData(rect);
//				BufferedImage img = new BufferedImage(image.getColorModel(), raster.createCompatibleWritableRaster(), true, null);
//				PlanarImage pimg = PlanarImage.wrapRenderedImage(img);

                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                int p = 0;
                double[] pixel = new double[4];
                for (int j = x * stepW; j < (x + 1) * stepW; j++) {
                    for (int k = y * stepH; k < (y + 1) * stepH; k++) {
                        raster.getPixel(j, k, pixel);
                        sumR += pixel[R];
                        sumG += pixel[G];
                        sumB += pixel[B];
                        p++;
                    }
                }

                Integer cell;
                Character label;

                if (sumR >= sumG && sumR >= sumB) {
                    label = 'r';
                    cell = (int) ((sumR / ((float) 256 * p)) * (int) (Math.pow(2, bitsPerBin) - 1));
                } else if (sumG < sumR || sumG < sumB) {
                    label = 'b';
                    cell = (int) ((sumB / ((float) 256 * p)) * (int) (Math.pow(2, bitsPerBin) - 1));
                } else {
                    label = 'g';
                    cell = (int) ((sumG / ((float) 256 * p)) * (int) (Math.pow(2, bitsPerBin) - 1));
                }

                topologyValue[y * blocksPerSide + x] = cell;
                topologyLabel[y * blocksPerSide + x] = label;
            }
        }
    }


    private void makeEdgeHistograms() {
        if (hasEdgeHistograms)
            return;

        Raster r = getEdgesImage().getData();
        int[] pixels = null;
        int[] SSa = null;
        int[] SEa = null;
        int[] EEa = null;
        int[] SWa = null;
        int[] WWa = null;
        int[] NWa = null;
        int[] NNa = null;
        int[] NEa = null;

        int width = r.getWidth();
        int height = r.getHeight();
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                pixels = r.getPixel(x, y, pixels);


                NNa = r.getPixel(x, y - 1, NNa);
                SSa = r.getPixel(x, y + 1, SSa);
                EEa = r.getPixel(x + 1, y, EEa);
                WWa = r.getPixel(x - 1, y, WWa);
                NEa = r.getPixel(x + 1, y - 1, NEa);
                SEa = r.getPixel(x + 1, y + 1, SEa);
                NWa = r.getPixel(x - 1, y - 1, NWa);
                SWa = r.getPixel(x - 1, y + 1, SWa);

                int NN = NNa[0] + NNa[1] + NNa[2];
                int SS = SSa[0] + SSa[1] + SSa[2];
                int EE = EEa[0] + EEa[1] + EEa[2];
                int WW = WWa[0] + WWa[1] + WWa[2];
                int NE = NEa[0] + NEa[1] + NEa[2];
                int SE = SEa[0] + SEa[1] + SEa[2];
                int NW = NWa[0] + NWa[1] + NWa[2];
                int SW = SWa[0] + SWa[1] + SWa[2];

                //System.err.println(pixels[0]+":"+pixels[1]+":"+pixels[2]);
                if (pixels[0] == 0xff && pixels[1] == 0x00 && pixels[2] == 0x00) {
                    texture[0]++;
                    if (WW > 0 && EE > 0)
                        curviness[0]++;
                } else if (pixels[0] == 0xff && pixels[1] == 0x88 && pixels[2] == 0x00) { //0xff ff8800
                    texture[1]++;
                    if ((WW > 0 || SW > 0) && (EE > 0 || NE > 0))
                        curviness[1]++;
                } else if (pixels[0] == 0xff && pixels[1] == 0xff && pixels[2] == 0x00) { //0xff ffff00
                    texture[2]++;
                    if (NE > 0 && SW > 0)
                        curviness[2]++;
                } else if (pixels[0] == 0x00 && pixels[1] == 0xff && pixels[2] == 0x00) { //0xff 00ff00
                    texture[3]++;
                    if ((NN > 0 || NE > 0) && (SS > 0 || SW > 0))
                        curviness[3]++;
                } else if (pixels[0] == 0x00 && pixels[1] == 0xff && pixels[2] == 0xff) { //0xff 00ffff
                    texture[4]++;
                    if (NN > 0 && SS > 0)
                        curviness[4]++;
                } else if (pixels[0] == 0x00 && pixels[1] == 0x00 && pixels[2] == 0xff) { //0xff 0000ff
                    texture[5]++;
                    if ((NN > 0 || NW > 0) && (SS > 0 || SE > 0))
                        curviness[5]++;
                } else if (pixels[0] == 0x00 && pixels[1] == 0x88 && pixels[2] == 0xff) { //0xff 0088ff
                    texture[6]++;
                    if (NW > 0 && SE > 0)
                        curviness[6]++;
                } else if (pixels[0] == 0xff && pixels[1] == 0x00 && pixels[2] == 0xff) { //0xff ff00ff
                    texture[7]++;
                    if ((WW > 0 || NW > 0) && (EE > 0 || SE > 0))
                        curviness[7]++;
                }
            }
        }
        transformVector(texture);
        transformVector(curviness);
        hasEdgeHistograms = true;
    }

    private void transformVector(double[] curviness) {
        double curvMax = 1;
        for (double v : curviness) {
            curvMax = curvMax > v ? curvMax : v;
        }
        for (int c = 0; c < curviness.length; c++) {
            curviness[c] = (int) ((Math.pow(2, bitsPerBin) - 1) * (float) curviness[c] / curvMax);
        }
    }

    public double[] getTopologyValues() {
        return topologyValue;
    }

    public char[] getTopologyLabels() {
        return topologyLabel;
    }
}
