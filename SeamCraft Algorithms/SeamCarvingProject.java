import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Scanner;

public class SeamCarvingProject {

    public static void main(String[] args) {
        try {
            Scanner in = new Scanner(System.in);
            System.out.println("Choose the algorithm for seam carving:");
            System.out.println("1 - Brute Force");
            System.out.println("2 - Dynamic Programming");
            System.out.println("3 - Greedy");
            System.out.print("Please Enter choice: ");
            int ch = in.nextInt();
            System.out.print("Enter number of seams to remove: ");
            int seamsToRemove = in.nextInt();
            in.close();

            String imagePath ="C:\\Users\\mahac\\OneDrive\\Desktop\\museum.jpg";

            File inputFile = new File(imagePath);
            BufferedImage image = ImageIO.read(inputFile);

            String type = "";

            for (int i = 0; i < seamsToRemove; i++) {
                double[][] energyMatrix = computeEnergyMatrix(image);
                int[] seam;
                if (ch == 1) {
                    seam = findLowestEnergySeam_BF(energyMatrix);
                    type = "_BF";
                } else if (ch == 2) {
                    seam = findLowestEnergySeam_DP(energyMatrix);
                    type = "_DP";
                } else if (ch == 3) {
                    seam = findLowestEnergySeam_Greedy(energyMatrix);
                    type = "_Greedy";
                } else {
                    System.out.println("Invalid choice !!!!!!!! ");
                    return;
                }
                image = removeSeam(image, seam);
            }

            String outputImagePath ="C:\\Users\\mahac\\OneDrive\\Desktop\\museum.jpg" + type + ".png";

            ImageIO.write(image, "jpg", new File(outputImagePath));
            System.out.println("seam carving is Completed !!! , can be found in this path: " + outputImagePath);
        } catch (IOException e) {
            System.out.println(" Error processing the image file.");
            e.printStackTrace();
        }
    }





    public static double[][] computeEnergyMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] energy = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                energy[y][x] = computePixelEnergy(image, x, y);
            }
        }
        return energy;
    }




    public static double computePixelEnergy(BufferedImage image, int x, int y) {
        int[][] b = new int[3][3];// grid of brightness values for the current pixel and its neighbors

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                                                                     //A B C
                int nx = x + i;                                      //D E F
                int ny = y + j;                                      //H I J
                // If out of bounds, assume black (brightness = 0)
                if (nx < 0 || nx >= image.getWidth() || ny < 0 || ny >= image.getHeight()) {
                    b[i + 1][j + 1] = 0;
                } else {
                    Color c = new Color(image.getRGB(nx, ny));
                    b[i + 1][j + 1] = c.getRed() + c.getGreen() + c.getBlue();
                }
            }
        }
        // Compute energy using Sobel filter
        int gx = b[0][0] + 2 * b[1][0] + b[2][0] - b[0][2] - 2 * b[1][2] - b[2][2];
        int gy = b[0][0] + 2 * b[0][1] + b[0][2] - b[2][0] - 2 * b[2][1] - b[2][2];

        return Math.sqrt(gx * gx + gy * gy);
    }





    // this method finds the lowest-energy vertical seam using brute force
    public static int[] findLowestEnergySeam_BF(double[][] energy) {
        int h = energy.length;
        int w = energy[0].length;

        int[] bestSeam = new int[h];
        double minEnergy = Double.POSITIVE_INFINITY;

        for (int startX = 0; startX < w; startX++) {
            int[] seam = new int[h];
            double seamEnergy = findSeam(energy, startX, 0, seam);
            // If this seam has lower energy, or if the energy is the same but the seam starts to the left
            if (seamEnergy < minEnergy ||
                    (seamEnergy == minEnergy && startX < bestSeam[0])) {// if equal take the leftmost
                minEnergy = seamEnergy;
                System.arraycopy(seam, 0, bestSeam, 0, h);
            }
        }
        return bestSeam;
    }





    // Recursively traces the lowest-energy vertical seam
    public static double findSeam(double[][] e, int x, int y, int[] seam) {
        int h = e.length;
        int w = e[0].length;

        seam[y] = x;

        if (y == h - 1)
            return e[y][x];

        double minEnergy = Double.POSITIVE_INFINITY;

        for (int dx = -1; dx <= 1; dx++) {
            int nx = x + dx;

            if (nx >= 0 && nx < w) {
                int[] temp = new int[h];
                System.arraycopy(seam, 0, temp, 0, h);

                double nextEnergy = findSeam(e, nx, y + 1, temp);

                if (nextEnergy < minEnergy) {
                    minEnergy = nextEnergy;
                    System.arraycopy(temp, 0, seam, 0, h);
                }
            }
        }
        return e[y][x] + minEnergy;
    }





    // This method is to find the lowest energy seam by Dynamic Programming
    public static int[] findLowestEnergySeam_DP(double[][] energy) {
        int rows = energy.length;
        int cols = energy[0].length;

        double[][] dp = new double[rows][cols];
        int[][] parent = new int[rows][cols];

        for (int j = 0; j < cols; j++)
            dp[0][j] = energy[0][j];

        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double minPrev = dp[i - 1][j];
                parent[i][j] = j;

                if (j > 0 && dp[i - 1][j - 1] < minPrev) {
                    minPrev = dp[i - 1][j - 1];
                    parent[i][j] = j - 1;
                }

                if (j < cols - 1 && dp[i - 1][j + 1] < minPrev) {
                    minPrev = dp[i - 1][j + 1];
                    parent[i][j] = j + 1;
                }

                dp[i][j] = minPrev + energy[i][j];
            }
        }

        int minCol = 0;
        for (int j = 1; j < cols; j++)
            if (dp[rows - 1][j] < dp[rows - 1][minCol])
                minCol = j;

        int[] seam = new int[rows];
        seam[rows - 1] = minCol;

        for (int i = rows - 1; i > 0; i--)
            seam[i - 1] = parent[i][seam[i]];

        return seam;
    }







    // This method is to find the lowest energy seam by greedy
    public static int[] findLowestEnergySeam_Greedy(double[][] e) {
        int h = e.length;
        int w = e[0].length;

        int[] seam = new int[h];

        int minX = 0;
        for (int x = 1; x < w; x++)
            if (e[0][x] < e[0][minX])
                minX = x;

        seam[0] = minX;

        for (int y = 1; y < h; y++) {
            // chooses the locally optimal pixel at each row
            int px = seam[y - 1];
            int nx = px; // Default: stay the same column
            double best = e[y][px];
            // Check left
            if (px > 0 && e[y][px - 1] <= best) {
                best = e[y][px - 1];
                nx = px - 1;
            }

            // Check right
            if (px < w - 1 && e[y][px + 1] < best) {
                best = e[y][px + 1];
                nx = px + 1;
            }

            seam[y] = nx;
        }

        return seam;
    }





    // this method is to removes the identified seam from the image
    public static BufferedImage removeSeam(BufferedImage image, int[] seam) {
        int w = image.getWidth();
        int h = image.getHeight();

        BufferedImage out = new BufferedImage(w - 1, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            int nx = 0;
            for (int x = 0; x < w; x++) {
                if (x != seam[y]) {
                    out.setRGB(nx, y, image.getRGB(x, y));
                    nx++;
                }
            }
        }

        return out;
    }

}//end of class
