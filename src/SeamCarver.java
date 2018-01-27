import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;

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
        if (x == 0 || y == 0 || x == width() || y == height()) return 1000;
        return Math.sqrt(calculateEnergyOfX(x, y) + calculateEnergyOfY(x, y));
    }

    private int calculateEnergyOfX (int x, int y) {
        int left = picture.getRGB(x - 1, y);
        int right = picture.getRGB(x + 1, y);
        int rX = ((right >> 16) & 0xFF) - ((left >> 16) & 0xFF);
        int gX = ((right >> 8) & 0xFF) - ((left >> 8) & 0xFF);
        int bX = ((right) & 0xFF) - ((left) & 0xFF);
        return (rX * rX) + (gX * gX) + (bX * bX);
    }

    private int calculateEnergyOfY (int x, int y) {
        int top = picture.getRGB(x, y -1);
        int bottom = picture.getRGB(x, y -1);
        int rY = ((bottom >> 16) & 0xFF) - ((top >> 16) & 0xFF);
        int gY = ((bottom >> 8) & 0xFF) - ((top >> 8) & 0xFF);
        int bY = ((bottom) & 0xFF) - ((top) & 0xFF);
        return (rY * rY) + (gY * gY) + (bY * bY);
    }

    // sequence of indices for horizontal seam
    public   int[] findHorizontalSeam() {
        picture.setOriginLowerLeft();
        int[] seam =  findVerticalSeam();
        picture.setOriginUpperLeft();
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
                if (allEnergy[x-1][y] <= allEnergy[x][y] && allEnergy[x-1][y] <= allEnergy[x+1][y]) current[y] = (x -1);
                else if (allEnergy[x][y] <= allEnergy[x-1][y] && allEnergy[x][y] <= allEnergy[x+1][y]) current[y] = (x);
                else current[y] = (x+1);
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
        picture.setOriginLowerLeft();
        removeVerticalSeam(seam);
        picture.setOriginUpperLeft();
    }

    // remove vertical seam from current picture
    public    void removeVerticalSeam(int[] seam) {
        double[][] newAllEnergy = new double[width()-1][height()];
        Picture original = this.picture;
        Picture newPicture = new Picture(width() -1, height());
        for (int x = 0; x < width(); x ++) {
            System.arraycopy(allEnergy[x], 0, newAllEnergy[x], 0, seam[x]);
            newAllEnergy[x][seam[x]] = energy(x, seam[x]);
            newAllEnergy[x + 1][seam[x]] = energy((x +1), seam[x]);

        }
        allEnergy = newAllEnergy;
    }

}
