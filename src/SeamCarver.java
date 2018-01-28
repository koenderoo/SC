import edu.princeton.cs.algs4.Picture;
// import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private double[][] allEnergy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
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
        return picture;
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
            throw new IndexOutOfBoundsException();
        }

        if (y < 0 || y >= height()) {
            throw new IndexOutOfBoundsException();
        }
        if (x == 0 || y == 0 || x == width() -1|| y == height() -1) return 1000;
        return Math.sqrt(calculateEnergyOfX(x, y) + calculateEnergyOfY(x, y));
    }

    private int calculateEnergyOfX (int x, int y) {
        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        int rX = Math.abs(right.getRed() - left.getRed());
        int gX = Math.abs(right.getGreen() - left.getGreen());
        int bX = Math.abs(right.getBlue() - left.getBlue());
        return rX * rX + gX * gX + bX * bX;
    }

    private int calculateEnergyOfY (int x, int y) {
        Color top = picture.get(x, y -1);
        Color bottom = picture.get(x, y +1);
        int rY = Math.abs(bottom.getRed() - top.getRed());
        int gY = Math.abs(bottom.getGreen() - top.getGreen());
        int bY = Math.abs(bottom.getBlue() - top.getBlue());
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

        int[] seam =  findVerticalSeam();

        // Transpose back.
        this.picture = original;

        return seam;
    }

    // sequence of indices for vertical seam
    public   int[] findVerticalSeam() {
        int[] result = new int[height()];
        double least = Double.MAX_VALUE;

        for (int x = 1; x < width() - 1; x++) {
            int[] current = new int[height()];
            double energyOfSeam = 0.0;
            current[0] = x;
            for (int y = 1; y < height(); y++) {
                int runner = current[y-1];
                if (allEnergy[runner-1][y] < allEnergy[runner][y] && allEnergy[runner-1][y] < allEnergy[runner+1][y]) current[y] = (runner -1);
                else if (allEnergy[runner][y] <= allEnergy[runner-1][y] && allEnergy[runner][y] <= allEnergy[runner+1][y]) current[y] = (runner);
                else current[y] = (runner+1);
                energyOfSeam += allEnergy[current[y]][y];
            }
            if (energyOfSeam < least) {
                least = energyOfSeam;
                result = current;
            }
        }
        return result;
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
        double[][] newAllEnergy = new double[width()-1][height()];
        Picture original = this.picture;
        Picture newPicture = new Picture(width() -1, height());
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < seam[y]; x++) {
                newPicture.set(x, y, original.get(x, y));
                newAllEnergy[x][y] = allEnergy[x][y];
            }
            for (int x = seam[y]; x < newPicture.width(); x++) {
                newPicture.set(x, y, original.get(x + 1, y));
                newAllEnergy[x][y] = allEnergy[x + 1][y];
            }
        }

        this.picture = newPicture;
        this.allEnergy = newAllEnergy;
        // recalculate energy along the seam
        for (int y = 0; y < height(); y++) {
            allEnergy[seam[y]][y - 1] = energy(seam[y], y -1);
            allEnergy[seam[y]][y] = energy(seam[y], y);
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
//        Picture picture = new Picture("/10x10.png");
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
//
//        StdOut.printf("Horizontal seam: { ");
//        int[] horizontalSeam = carver.findHorizontalSeam();
//        for (int y : horizontalSeam)
//            StdOut.print(y + " ");
//        StdOut.println("}");
//        printSeam(carver, horizontalSeam, HORIZONTAL);
//    }

}
