package com.example.mentalhealth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aniketjain.weatherapp.R;


public class SOSFragment extends Fragment {

    private GridLayout gridLayout;

    private static class SOS {
        int iconRes;
        String title;
        String number;

        public SOS(int iconRes, String title, String number) {
            this.iconRes = iconRes;
            this.title = title;
            this.number = number;
        }
    }

    private final SOS[] sosList = new SOS[]{
            new SOS(R.drawable.ic_police, "Police", "100"),
            new SOS(R.drawable.ic_fire, "Fire", "101"),
            new SOS(R.drawable.ic_women, "Women Helpline", "1091"),
            new SOS(R.drawable.ic_child, "Child Helpline", "1098"),
            new SOS(R.drawable.ic_ambulance, "Ambulance", "102"),
            new SOS(R.drawable.ic_disaster, "Disaster Mgmt", "108")
    };


    public SOSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_s_o_s, container, false);

        gridLayout = view.findViewById(R.id.gridLayout);

        for (SOS sos : sosList) {
            View card = LayoutInflater.from(getContext()).inflate(R.layout.item_sos_card, gridLayout, false);

            ImageView sosImage = card.findViewById(R.id.sosImage);
            TextView sosTitle = card.findViewById(R.id.sosTitle);

            sosImage.setImageResource(sos.iconRes);
            sosTitle.setText(sos.title);

            card.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + sos.number));
                startActivity(intent);
            });

            gridLayout.addView(card);
        }



        return view;

    }



}