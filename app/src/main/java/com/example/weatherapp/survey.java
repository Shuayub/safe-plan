package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.databinding.FragmentSurveyBinding;
import com.example.weatherapp.databinding.FragmentTipsBinding;


public class survey extends AppCompatActivity {

    private FragmentSurveyBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentSurveyBinding binding = FragmentSurveyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}