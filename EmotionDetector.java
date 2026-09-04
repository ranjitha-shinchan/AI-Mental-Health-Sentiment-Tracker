package com.example.mentalhealth;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Facial-Emotion Recognition Module
 * Loads fer_model.tflite from assets and classifies a face Bitmap into one
 * of four emotion categories: happy, sad, excited, neutral.
 *
 * Model input: 96x96 grayscale image, normalized to [0, 1]
 * Model output: 4-class softmax
 */
public class EmotionDetector {

    private static final String MODEL_FILE = "fer_model.tflite";
    private static final int IMG_SIZE = 96;
    public static final String[] LABELS = {"happy", "sad", "excited", "neutral"};

    private final Interpreter interpreter;

    public EmotionDetector(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context));
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Runs inference on a cropped face bitmap.
     * @param faceBitmap a Bitmap already cropped to the face region
     * @return the predicted label from LABELS, e.g. "happy"
     */
    public String predict(Bitmap faceBitmap) {
        ByteBuffer input = preprocess(faceBitmap);
        float[][] output = new float[1][LABELS.length];
        interpreter.run(input, output);
        return LABELS[argMax(output[0])];
    }

    /** Same as predict(), but also returns the confidence score (0-1). */
    public Result predictWithConfidence(Bitmap faceBitmap) {
        ByteBuffer input = preprocess(faceBitmap);
        float[][] output = new float[1][LABELS.length];
        interpreter.run(input, output);
        int idx = argMax(output[0]);
        return new Result(LABELS[idx], output[0][idx]);
    }

    private ByteBuffer preprocess(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMG_SIZE, IMG_SIZE, true);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * IMG_SIZE * IMG_SIZE);
        buffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[IMG_SIZE * IMG_SIZE];
        resized.getPixels(pixels, 0, IMG_SIZE, 0, 0, IMG_SIZE, IMG_SIZE);

        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            // convert to grayscale using standard luminance weights
            float gray = (0.299f * r + 0.587f * g + 0.114f * b) / 255.0f;
            buffer.putFloat(gray);
        }
        return buffer;
    }

    private int argMax(float[] arr) {
        int maxIdx = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }

    public void close() {
        if (interpreter != null) interpreter.close();
    }

    public static class Result {
        public final String label;
        public final float confidence;
        public Result(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }
    }
}
