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
import com.example.weatherapp.databinding.ActivityIntroBinding;
import com.example.weatherapp.databinding.FragmentTipsBinding;


public class tips extends AppCompatActivity {

    private FragmentTipsBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTipsBinding binding = FragmentTipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}