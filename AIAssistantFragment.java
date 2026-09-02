package com.example.mentalhealth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aniketjain.weatherapp.R;


public class AIAssistantFragment extends Fragment {


    Button btnSubmit;
    public AIAssistantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_a_i_assistant, container, false);


        btnSubmit= v.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Work in progress", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getActivity(),ChatActivity.class));
            }
        });





        return v;
    }
}