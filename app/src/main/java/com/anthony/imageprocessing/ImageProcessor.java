package com.anthony.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;

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

        if (mImage == null) {
            return null;
        }

        int width = mImage.getWidth();
        int height = mImage.getHeight();

        int[] pixels = new int[width * height];

        mImage.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int x = 0; x < pixels.length; ++x) {

            pixels[x] = (pixels[x] <= 1.2*fromColor && pixels[x] >= 0.8*fromColor) ? targetColor : pixels[x];

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

}