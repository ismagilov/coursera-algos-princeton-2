import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int width;
    private int height;

    private int[][] colors;
    private double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("picture object can not be null");
        if (picture.width() == 0) throw new IllegalArgumentException("picture's width can not be zero");
        if (picture.height() == 0) throw new IllegalArgumentException("picture's height can not be zero");

        width = picture.width();
        height = picture.height();

        colors = new int[height][width];
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
            colors[r][c] = picture.getRGB(c, r);

        energy = new double[height][width];
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                energy[r][c] = calculateEnergy(r, c);
    }

    // current picture
    public Picture picture() {
        Picture p = new Picture(width, height);

        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                p.setRGB(c, r, colors[r][c]);

        return p;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1) throw new IllegalArgumentException("x is out of range [0; " + (width - 1) + "]");
        if (y < 0 || y > height - 1) throw new IllegalArgumentException("y is out of range [0; " + (height - 1) + "]");

        return energy[y][x];
    }

    private double diff2(int c1, int c2) {
        int r1 = c1 & 0xFF, r2 = c2 & 0xFF;
        int g1 = c1 >> 8 & 0xFF, g2 = c2 >> 8 & 0xFF;
        int b1 = c1 >> 16 & 0xFF, b2 = c2 >> 16 & 0xFF;

        int r = r1 - r2;
        int g = g1 - g2;
        int b = b1 - b2;

        return r * r + g * g + b * b;
    }

    private double calculateEnergy(int r, int c) {
        if (r == 0 || r == height - 1)
            return 1000;

        if (c == 0 || c == width - 1)
            return 1000;

        double d2x = diff2(colors[r][c - 1], colors[r][c + 1]);
        double d2y = diff2(colors[r - 1][c], colors[r + 1][c]);

        return Math.sqrt(d2x + d2y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] rotatedEnergy = new double[width][height];

        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                //r-rotated = c
                //c-rotated = h - 1 - r
                rotatedEnergy[r][c] = energy[height - 1 - c][r];
            }
        }

        int[] rotatedPath = findSeam(rotatedEnergy, width, height);

        //r = height - 1 - c-rotated
        //c = r-rotated
        int[] path = new int[rotatedPath.length];
        for (int r = 0; r < rotatedPath.length; r++) {
            int c = rotatedPath[r];

            path[r] = height - 1 - c;
        }

        return path;

        /* Explicit calculation: */
        /*
        int[] path = new int[width];

        int[][] dp = new int[height][width];
        double[][] de = new double[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (c == 0) {
                    dp[r][0] = r;
                    de[r][0] = energy[r][0];
                } else {
                    de[r][c] = Double.MAX_VALUE;
                }
            }
        }

        for (int c = 1; c < width; c++) {
            for (int r = 0; r < height; r++) {
                for (int i = -1; i <= 1; i++) {
                    int row = r + i;
                    if (row < 0 || row > height - 1) continue;

                    System.out.println(r + "," + c + ": " + de[r][c] + " *** " + row + "," + (c - 1) + ": " + (energy[r][c] + de[row][c - 1]));

                    if (energy[r][c] + de[row][c - 1] < de[r][c]) {
                        de[r][c] = energy[r][c] + de[row][c - 1];
                        dp[r][c] = row;
                    }
                }
            }
        }

        for (int r = 0; r < height; r++)
            System.out.println(Arrays.toString(de[r]));


        double min = Double.MAX_VALUE;
        int rowWithMin = -1;
        for (int r = 0; r < height; r++) {
            if (de[r][width - 1] < min) {
                min = de[r][width - 1];
                rowWithMin = r;
            }
        }

        System.out.println(rowWithMin);

        for (int c = width - 1, r = rowWithMin; c >= 0; r = dp[r][c], c--)
            path[c] = r;

        return path;*/
    }

    public int[] findVerticalSeam() {
        return findSeam(energy, height, width);
    }

    // sequence of indices for vertical seam
    private int[] findSeam(double[][] energy, int height, int width) {
        int[] path = new int[height];

        int[][] dp = new int[height][width];
        double[][] de = new double[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (r == 0) {
                    dp[0][c] = c;
                    de[0][c] = energy[0][c];
                } else {
                    de[r][c] = Double.MAX_VALUE;
                }
            }
        }

        for (int r = 1; r < height; r++) {
            for (int c = 0; c < width; c++) {
                for (int i = -1; i <= 1; i++) { // r-1,c-1; r-1,c; r-1,c+1
                    int col = c + i;
                    if (col < 0 || col > width - 1)
                        continue;

                    //System.out.println(r + "," + c + ": " + de[r][c] + " *** " + (r - 1) + "," + col + ": " + (energy[r][c] + de[r - 1][col]));

                    if (energy[r][c] + de[r - 1][col] < de[r][c]) {
                        de[r][c] = energy[r][c] + de[r - 1][col];
                        dp[r][c] = col;
                    }
                }
            }
        }

        int colWithMin = -1;
        double min = Double.MAX_VALUE;
        for (int c = 0; c < width; c++) {
            if (de[height - 1][c] < min) {
                min = de[height - 1][c];
                colWithMin = c;
            }
        }

//        System.out.println("DP energy:");
//        for (int r = 0; r < height; r++)
//            System.out.println(Arrays.toString(de[r]));

        for (int r = height - 1, c = colWithMin; r >= 0; c = dp[r][c], r--)
            path[r] = c;

        return path;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (null == seam) throw new IllegalArgumentException("horizontal seam is null");
        if (height <= 1) throw new IllegalArgumentException("picture's height is less or equal to 1");
        if (seam.length != width) throw new IllegalArgumentException("horizontal seam length is not equal to picture's width");

        for (int c = 0; c < seam.length; c++) {
            if (seam[c] < 0 || seam[c] > height - 1)
                throw new IllegalArgumentException("horizontal seam has rows outside picture's height");
            if (c > 0 && Math.abs(seam[c] - seam[c - 1]) > 1)
                throw new IllegalArgumentException("horizontal seam is broken");
        }

        for (int c = 0; c < seam.length; c++) {
            int r = seam[c];

            while (r < height - 1) {
                colors[r][c] = colors[r + 1][c];
                energy[r][c] = energy[r + 1][c];
                r++;
            }
        }

        for(int c = 0; c < width; c++) {
            energy[seam[c]][c] = calculateEnergy(seam[c], c);
            if (seam[c] - 1 >= 0)
                energy[seam[c] - 1][c] = calculateEnergy(seam[c] - 1, c);
        }

        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (null == seam) throw new IllegalArgumentException("vertical seam is null");
        if (width <= 1) throw new IllegalArgumentException("picture's width is less or equal to 1");
        if (seam.length != height) throw new IllegalArgumentException("vertical seam length is not equal to picture's height");

        for (int r = 0; r < seam.length; r++) {
            if (seam[r] < 0 || seam[r] > width - 1)
                throw new IllegalArgumentException("vertical seam has rows outside picture's width");
            if (r > 0 && Math.abs(seam[r] - seam[r - 1]) > 1)
                throw new IllegalArgumentException("vertical seam is broken");
        }

//        System.out.println("before");
//        for (int r = 0; r < height; r++)
//            System.out.println(Arrays.toString(colors[r]));
        for (int r = 0; r < seam.length; r++) {
            int c = seam[r];

            while (c < width - 1) {
                colors[r][c] = colors[r][c + 1];
                energy[r][c] = energy[r][c + 1];
                c++;
            }
        }
//        System.out.println("after");
//        for (int r = 0; r < height; r++)
//            System.out.println(Arrays.toString(colors[r]));


        for(int r = 0; r < height; r++) {
            energy[r][seam[r]] = calculateEnergy(r, seam[r]);
            if (seam[r] - 1 >= 0)
                energy[r][seam[r] - 1] = calculateEnergy(r,seam[r] - 1);
        }

        width--;
    }
}