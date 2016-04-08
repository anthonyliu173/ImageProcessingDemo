package com.anthony.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by anthonyliu on 15/9/13.
 */
public class ImageProcessor {

    Bitmap mImage;

    public ImageProcessor(final Bitmap image) {
        if (image != null) {
            mImage = image.copy(image.getConfig(), image.isMutable());
        }
    }

    public void setImage(final Bitmap image) {
        mImage = image.copy(image.getConfig(), image.isMutable());
    }

    public Bitmap getImage() {
        if (mImage == null) {
            return null;
        }
        return mImage.copy(mImage.getConfig(), mImage.isMutable());
    }

    /**
     * Effect1 - replace one color with another color in the image
     *
     * @param fromColor   the color that will be replaced
     * @param targetColor the color that will be changed to
     *
     * https://xjaphx.wordpress.com/2011/09/28/image-processing-pixel-color-replacement/
     */
    public Bitmap getEffect1(int fromColor, int targetColor) {

        int threshold = 60;

        int R = Color.red(fromColor);
        int G = Color.green(fromColor);
        int B = Color.blue(fromColor);

        if (mImage == null) {
            return null;
        }

        int width = mImage.getWidth();
        int height = mImage.getHeight();

        int[] pixels = new int[width * height];

        mImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int c_R, c_G, c_B = 0;

        for (int index = 0; index < pixels.length; ++index) {

            c_R = Color.red(pixels[index]);
            c_G = Color.green(pixels[index]);
            c_B = Color.blue(pixels[index]);

            if(((R - threshold) <= c_R && c_R <= (R + threshold)) &&
                    ((G - threshold) <= c_G && c_G <= (G + threshold)) &&
                    ((B - threshold) <= c_B && c_B <= (B + threshold))) {
                pixels[index] = targetColor;
            }

        }

        Bitmap newImage = Bitmap.createBitmap(width, height, mImage.getConfig());
        newImage.setPixels(pixels, 0, width, 0, 0, width, height);

        return newImage;

    }


    /**
     * Effect2 - apply hue filter to the image
     *
     * @param level the level of hue
     *
     * https://xjaphx.wordpress.com/2011/10/30/image-processing-hue-filter/
     * */
    public Bitmap getEffect2(int level) {

        // get image size
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        int[] pixels = new int[width * height];
        float[] HSV = new float[3];
        // get pixel array from source
        mImage.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                HSV[0] *= level;
                HSV[0] = (float) Math.max(0.0, Math.min(HSV[0], 360.0));
                // take color back
                pixels[index] |= Color.HSVToColor(HSV);
            }
        }
        // output bitmap
        Bitmap newImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        newImage.setPixels(pixels, 0, width, 0, 0, width, height);

        return newImage;

    }

    /**
     * Effect 3 (Emboss effect) - i have no idea what this is, but it looks damn cool
     *
     * https://xjaphx.wordpress.com/2011/06/22/image-processing-emboss-effect/
     * */
    public Bitmap getEffect3() {

        double[][] EmbossConfig = new double[][] {
                { -1 ,  0, -1 },
                {  0 ,  4,  0 },
                { -1 ,  0, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(EmbossConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 127;

        return ConvolutionMatrix.computeConvolution3x3(mImage, convMatrix);

    }

    /**
     * Effect 4 - smooth effect
     *
     * https://xjaphx.wordpress.com/2011/06/22/image-processing-smooth-effect/
     * */
    public Bitmap getEffect4(double value) {

        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.setAll(1);
        convMatrix.Matrix[1][1] = value;
        convMatrix.Factor = value + 8;
        convMatrix.Offset = 1;

        return ConvolutionMatrix.computeConvolution3x3(mImage, convMatrix);

    }

    /**
     * Effect 5 - brightness effect
     *
     * @param value - level of brightness ... from -100 to 100
     *
     * https://xjaphx.wordpress.com/2011/06/22/image-processing-brightness-over-image/
     * */
    public Bitmap getEffect5(int value) {

        // image size
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        // create output bitmap
        Bitmap newImage = Bitmap.createBitmap(width, height, mImage.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = mImage.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                newImage.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return newImage;

    }

    /**
     * Effect 6 - snow effect
     *
     * https://xjaphx.wordpress.com/2011/10/30/image-processing-snow-effect/
     * */
    public Bitmap getEffect6(int COLOR_MAX) {

        // image size
        int width = mImage.getWidth();
        int height = mImage.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        mImage.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 50;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(COLOR_MAX);
                if(R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        return bmOut;

    }

}