package com.example.mentalhealth;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aniketjain.weatherapp.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Facial-Emotion Recognition Module
 * Opens the front camera, runs each frame through EmotionDetector,
 * and displays the predicted emotion label live on screen.
 */
public class EmotionDetectionFragment extends Fragment {

    private static final String TAG = "EmotionDetection";

    private PreviewView previewView;
    private TextView resultText;
    private EmotionDetector emotionDetector;
    private ExecutorService cameraExecutor;

    // simple throttle so we don't run inference on every single frame
    private long lastAnalyzedTimestamp = 0L;
    private static final long ANALYSIS_INTERVAL_MS = 500L;

    public EmotionDetectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emotion_detection, container, false);

        previewView = v.findViewById(R.id.previewView);
        resultText = v.findViewById(R.id.resultText);

        try {
            emotionDetector = new EmotionDetector(requireContext());
        } catch (IOException e) {
            Log.e(TAG, "Failed to load fer_model.tflite", e);
            Toast.makeText(requireContext(), "Could not load emotion model", Toast.LENGTH_SHORT).show();
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();

        return v;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeFrame);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e(TAG, "Camera bind failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeFrame(@NonNull ImageProxy imageProxy) {
        long now = System.currentTimeMillis();
        if (now - lastAnalyzedTimestamp < ANALYSIS_INTERVAL_MS || emotionDetector == null) {
            imageProxy.close();
            return;
        }
        lastAnalyzedTimestamp = now;

        try {
            Bitmap bitmap = ImageUtils.imageProxyToBitmap(imageProxy);
            if (bitmap != null) {
                EmotionDetector.Result result = emotionDetector.predictWithConfidence(bitmap);
                requireActivity().runOnUiThread(() ->
                        resultText.setText(String.format("%s (%.0f%%)",
                                result.label, result.confidence * 100)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Frame analysis failed", e);
        } finally {
            imageProxy.close();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (emotionDetector != null) emotionDetector.close();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }
}
