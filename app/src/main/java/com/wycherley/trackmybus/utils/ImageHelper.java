package com.wycherley.trackmybus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Helper class for converting images to/from Base64
 * This allows storing images in Firebase Realtime Database without Firebase Storage
 */
public class ImageHelper {
    private static final String TAG = "ImageHelper";
    private static final int MAX_IMAGE_SIZE = 500; // Max width/height in pixels
    private static final int COMPRESSION_QUALITY = 70; // 0-100 (higher = better quality, larger size)

    /**
     * Convert image URI to Base64 string (compressed and resized)
     *
     * @param context Application context
     * @param imageUri URI of the image to convert
     * @return Base64 encoded string or null if failed
     */
    public static String imageUriToBase64(Context context, Uri imageUri) {
        try {
            // Load image from URI
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream");
                return null;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                Log.e(TAG, "Failed to decode bitmap");
                return null;
            }

            // Compress and resize the image
            Bitmap compressedBitmap = compressImage(bitmap);

            // Convert to Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            int sizeKB = base64Image.length() / 1024;
            Log.d(TAG, "Image compressed to " + sizeKB + " KB");

            // Check if image is too large
            if (sizeKB > 900) {
                Log.w(TAG, "Warning: Image is very large (" + sizeKB + " KB)");
            }

            return base64Image;

        } catch (Exception e) {
            Log.e(TAG, "Error converting image to Base64", e);
            return null;
        }
    }

    /**
     * Convert Base64 string back to Bitmap
     *
     * @param base64Image Base64 encoded image string
     * @return Bitmap or null if failed
     */
    public static Bitmap base64ToBitmap(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e(TAG, "Error converting Base64 to Bitmap", e);
            return null;
        }
    }

    /**
     * Compress and resize bitmap to reduce file size
     *
     * @param bitmap Original bitmap
     * @return Compressed bitmap
     */
    private static Bitmap compressImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Log.d(TAG, "Original image size: " + width + "x" + height);

        // Calculate new dimensions while maintaining aspect ratio
        if (width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE) {
            float ratio = Math.min(
                    (float) MAX_IMAGE_SIZE / width,
                    (float) MAX_IMAGE_SIZE / height
            );

            int newWidth = Math.round(width * ratio);
            int newHeight = Math.round(height * ratio);

            Log.d(TAG, "Resizing to: " + newWidth + "x" + newHeight);
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        return bitmap;
    }

    /**
     * Get estimated size of Base64 image in KB
     *
     * @param base64Image Base64 encoded image
     * @return Size in KB
     */
    public static int getBase64SizeKB(String base64Image) {
        if (base64Image == null) return 0;
        return base64Image.length() / 1024;
    }
}