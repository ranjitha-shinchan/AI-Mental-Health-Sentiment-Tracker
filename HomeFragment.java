package com.example.mentalhealth;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aniketjain.weatherapp.R;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private Uri selectedVideoUri;

    private VideoView videoPreview;
    private TextView tvVideoName;
    private MaterialButton btnSelectVideo;
    private MaterialButton btnProcessVideo;

    private final ActivityResultLauncher<String> videoPickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedVideoUri = uri;
                            showSelectedVideo(uri);
                        }
                    }
            );

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        videoPreview = view.findViewById(R.id.videoPreview);
        tvVideoName = view.findViewById(R.id.tvVideoName);
        btnSelectVideo = view.findViewById(R.id.btnSelectVideo);
        btnProcessVideo = view.findViewById(R.id.btnProcessVideo);

        btnSelectVideo.setOnClickListener(v ->
                videoPickerLauncher.launch("video/*")
        );

        btnProcessVideo.setOnClickListener(v -> {
            if (selectedVideoUri == null) {
                Toast.makeText(
                        requireContext(),
                        "Please select a video first",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Work in progress",
                    Toast.LENGTH_SHORT
            ).show();
        });

        return view;
    }

    private void showSelectedVideo(Uri videoUri) {

        String videoName = getFileName(videoUri);

        tvVideoName.setText(videoName);
        videoPreview.setVisibility(View.VISIBLE);
        btnProcessVideo.setEnabled(true);

        MediaController mediaController =
                new MediaController(requireContext());

        mediaController.setAnchorView(videoPreview);

        videoPreview.setMediaController(mediaController);
        videoPreview.setVideoURI(videoUri);

        videoPreview.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.setLooping(false);

            // Show the first frame without automatically playing.
            videoPreview.seekTo(100);
        });

        Toast.makeText(
                requireContext(),
                "Video selected successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private String getFileName(Uri uri) {

        String fileName = "Selected video";

        Cursor cursor = requireContext()
                .getContentResolver()
                .query(uri, null, null, null, null);

        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                );

                if (cursor.moveToFirst() && nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }

        return fileName;
    }

    @Override
    public void onDestroyView() {
        if (videoPreview != null) {
            videoPreview.stopPlayback();
        }

        videoPreview = null;
        tvVideoName = null;
        btnSelectVideo = null;
        btnProcessVideo = null;

        super.onDestroyView();
    }
}