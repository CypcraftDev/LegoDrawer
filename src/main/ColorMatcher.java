package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ColorMatcher {
    public Colors findClosestColor(int[] targetRGB, List<Colors> colorList) {
        double minDistance = Double.MAX_VALUE;
        Colors closestColor = null;

        for (Colors color : colorList) {
            double distance = calculateDistance(targetRGB, color.rgb);
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = color;
            }
        }

        return closestColor;
    }

    private double calculateDistance(int[] rgb1, int[] rgb2) {
        double sum = 0;
        for (int i = 0; i < rgb1.length; i++) {
            sum += Math.pow(rgb1[i] - rgb2[i], 2);
        }
        return Math.sqrt(sum);
    }

    public Color getAverageColorAroundPixel(BufferedImage image, int x, int y) {
        int sumRed = 0, sumGreen = 0, sumBlue = 0;
        int count = 0;
        int WINDOW_SIZE = 7;

        for (int dx = -WINDOW_SIZE/2; dx <= WINDOW_SIZE/2; dx++) {
            for (int dy = -WINDOW_SIZE/2; dy <= WINDOW_SIZE/2; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < image.getWidth() && ny >= 0 && ny < image.getHeight()) {
                    int pixel = image.getRGB(nx, ny);
                    Color color = new Color(pixel);
                    sumRed += color.getRed();
                    sumGreen += color.getGreen();
                    sumBlue += color.getBlue();
                    count++;
                }
            }
        }

        int avgRed = sumRed / count;
        int avgGreen = sumGreen / count;
        int avgBlue = sumBlue / count;

        return new Color(avgRed, avgGreen, avgBlue);
    }

}
