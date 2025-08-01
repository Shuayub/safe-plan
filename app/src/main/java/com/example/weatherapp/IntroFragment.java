package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weatherapp.R;
import com.example.weatherapp.databinding.IntroFragmentBinding;


public class IntroFragment extends Fragment {

    private IntroFragmentBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = IntroFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(requireContext());

        binding.GetStartedButton.setOnClickListener(v -> {
            if (sharedPreferences.getBoolean("createdAccount", false)) {
                NavHostFragment.findNavController(IntroFragment.this)
                        .navigate(R.id.action_intro_to_login_pin);
            } else {
                NavHostFragment.findNavController(IntroFragment.this)
                        .navigate(R.id.action_intro_to_register);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}