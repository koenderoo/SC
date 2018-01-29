import edu.princeton.cs.algs4.Picture;
// import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture picture;
    private double[][] allEnergy;
    private double[][] energyTo;
    private int[][] fromPixel;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        this.picture = picture;
        // calculate the energy of every pixel (once)
        allEnergy = new double[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                allEnergy[x][y] = energy(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture newPicture = picture;
        return newPicture;
    }

    // width of current picture
    public     int width() {
        return picture.width();
    }

    // height of current picture
    public     int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        if (x < 0 || x >= width()) {
            throw new IllegalArgumentException();
        }

        if (y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }
        if (x == 0 || y == 0 || x == width() -1|| y == height() -1) return 1000;
        return Math.sqrt(calculateEnergyOfX(x, y) + calculateEnergyOfY(x, y));
    }

    private int calculateEnergyOfX (int x, int y) {
        int left = picture.getRGB(x - 1, y);
        int right = picture.getRGB(x + 1, y);
        int rX = ((right >> 16) & 0xFF) - ((left >> 16) & 0xFF);
        int gX = ((right >> 8) & 0xFF) - ((left >> 8) & 0xFF);
        int bX = ((right) & 0xFF) - ((left) & 0xFF);
        return rX * rX + gX * gX + bX * bX;
    }

    private int calculateEnergyOfY (int x, int y) {
        int top = picture.getRGB(x, y - 1);
        int bottom = picture.getRGB(x, y + 1);
        int rY = ((bottom >> 16) & 0xFF) - ((top >> 16) & 0xFF);
        int gY = ((bottom >> 8) & 0xFF) - ((top >> 8) & 0xFF);
        int bY = ((bottom) & 0xFF) - ((top) & 0xFF);
        return rY * rY + gY * gY + bY * bY;
    }

    // sequence of indices for horizontal seam
    public   int[] findHorizontalSeam() {
        // Transpose picture.
        Picture original = picture;
        Picture transpose = new Picture(original.height(), original.width());

        for (int w = 0; w < transpose.width(); w++) {
            for (int h = 0; h < transpose.height(); h++) {
                transpose.set(w, h, original.get(h, w));
            }
        }

        this.picture = transpose;

        // recalculate energy for horizontal
        allEnergy = new double[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                allEnergy[x][y] = energy(x, y);
            }
        }

        int[] seam =  findVerticalSeam();

        // Transpose back.
        this.picture = original;
        // Back to vertical energy
        allEnergy = new double[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                allEnergy[x][y] = energy(x, y);
            }
        }

        return seam;
    }

    // sequence of indices for vertical seam
    public   int[] findVerticalSeam() {
        int[] result = new int[height()];
        double least = Double.POSITIVE_INFINITY;
        energyTo = new double[width()][height()];
        fromPixel = new int[width()][height()];

        for (int x = 0; x < width(); x++) { // start a search at every first pixel in the first row (not including the first and last)
            for (int y = 0; y < height(); y++) {
                energyTo[x][y] = Double.POSITIVE_INFINITY;
            }
        }

        for (int x =0; x < width(); x++) {
            energyTo[x][0] = 1000;
        }

        for (int y =0; y < height() -1; y++){
            for (int x = 0; x < width(); x++) {
                if (x > 0) {
                    relax(x, y, x -1, y + 1);
                }
                relax(x, y, x, y+ 1);

                if (x < width() -1) {
                    relax(x, y, x + 1, y + 1);
                }
            }
        }

        // vind de seam met de minste energy (is de fromPixel op de onderste rij met de laagste waarde)
        int currentMin = -1;
        for (int x = 0; x < width(); x ++) {
            if(energyTo[x][height() - 1] < least) {
                currentMin = x;
                least = energyTo[x][height() -1];
            }
        }
        // zet eerste waarde van x op de resultlijst als laatste
        result[height() -1] = currentMin;

        // loop terug via de energyTo
        int prevX = fromPixel[currentMin][height() -1];
        for (int y = height() - 2; y >= 0; y--) {
            result[y] = prevX;
            prevX = fromPixel[prevX][y];
        }

        return result;
    }

    private void relax (int x1, int y1, int x2, int y2) {
        if (energyTo[x2][y2] > energyTo[x1][y1] + allEnergy[x2][y2]) {
            energyTo[x2][y2] = energyTo[x1][y1] + allEnergy[x2][y2];
            fromPixel[x2][y2] = x1;
        }
    }
    // remove horizontal seam from current picture
    public    void removeHorizontalSeam(int[] seam) {
        // Transpose picture.
        Picture original = picture;
        Picture transpose = new Picture(original.height(), original.width());

        for (int w = 0; w < transpose.width(); w++) {
            for (int h = 0; h < transpose.height(); h++) {
                transpose.set(w, h, original.get(h, w));
            }
        }

        this.picture = transpose;
        transpose = null;
        original = null;

        removeVerticalSeam(seam);

        // Transpose back.
        original = picture;
        transpose = new Picture(original.height(), original.width());

        for (int w = 0; w < transpose.width(); w++) {
            for (int h = 0; h < transpose.height(); h++) {
                transpose.set(w, h, original.get(h, w));
            }
        }

        this.picture = transpose;
        transpose = null;
        original = null;
    }

    // remove vertical seam from current picture
    public    void removeVerticalSeam(int[] seam) {
        /*
        Test 9a: check removeVerticalSeam() with invalid seam * picture = 10x10.png
        - fails to throw an exception when calling removeVerticalSeam() with an invalid seam
        - failed on trial 1 of 100 - distance between pixel 5 and pixel 6 is 2
        - invalid seam = { 9, 9, 9, 8, 7, 7, 5, 6, 7, 6 }
        - distance between pixel 0 and pixel 1 is 2
        - invalid seam length
        - should throw a java.lang.IllegalArgumentException
         - throws wrong exception when calling removeHorizontalSeam() with a null argument
         - throws a java.lang.NullPointerException
         - should throw a java.lang.IllegalArgumentException
         */

        Picture original = this.picture;
        Picture newPicture = new Picture(width() -1, height());
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < seam[y]; x++) {
                newPicture.set(x, y, original.get(x, y));
            }
            for (int x = seam[y]; x < newPicture.width(); x++) {
                newPicture.set(x, y, original.get(x + 1, y));
            }
        }

        this.picture = newPicture;
        // recalculate energy along the seam
        allEnergy = new double[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                allEnergy[x][y] = energy(x, y);
            }
        }

    }

//    private static final boolean HORIZONTAL   = true;
//    private static final boolean VERTICAL     = false;
//
//    private static void printSeam(SeamCarver carver, int[] seam, boolean direction) {
//        double totalSeamEnergy = 0.0;
//
//        for (int row = 0; row < carver.height(); row++) {
//            for (int col = 0; col < carver.width(); col++) {
//                double energy = carver.energy(col, row);
//                String marker = " ";
//                if ((direction == HORIZONTAL && row == seam[col]) ||
//                        (direction == VERTICAL   && col == seam[row])) {
//                    marker = "*";
//                    totalSeamEnergy += energy;
//                }
//                StdOut.printf("%7.2f%s ", energy, marker);
//            }
//            StdOut.println();
//        }
//        // StdOut.println();
//        StdOut.printf("Total energy = %f\n", totalSeamEnergy);
//        StdOut.println();
//        StdOut.println();
//    }
//
//    public static void main(String[] args) {
//        Picture picture = new Picture("/6x5.png");
//        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
//
//        SeamCarver carver = new SeamCarver(picture);
//
//        StdOut.printf("Vertical seam: { ");
//        int[] verticalSeam = carver.findVerticalSeam();
//        for (int x : verticalSeam)
//            StdOut.print(x + " ");
//        StdOut.println("}");
//        printSeam(carver, verticalSeam, VERTICAL);
//        carver.removeVerticalSeam(verticalSeam);
//        StdOut.printf("Vertical seam after remove: { ");
//        verticalSeam = carver.findVerticalSeam();
//        for (int x : verticalSeam)
//            StdOut.print(x + " ");
//        StdOut.println("}");
//        printSeam(carver, verticalSeam, VERTICAL);
//
//        StdOut.printf("Horizontal seam: { ");
//        int[] horizontalSeam = carver.findHorizontalSeam();
//        for (int y : horizontalSeam)
//            StdOut.print(y + " ");
//        StdOut.println("}");
//        printSeam(carver, horizontalSeam, HORIZONTAL);
//        carver.removeHorizontalSeam(horizontalSeam);
//        StdOut.printf("Horizontal seam after remove: { ");
//        horizontalSeam = carver.findHorizontalSeam();
//        for (int y : horizontalSeam)
//            StdOut.print(y + " ");
//        StdOut.println("}");
//        printSeam(carver, horizontalSeam, HORIZONTAL);
//    }

}
