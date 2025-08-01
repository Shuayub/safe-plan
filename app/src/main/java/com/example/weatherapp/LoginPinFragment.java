package com.example.weatherapp;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weatherapp.R;
import com.example.weatherapp.databinding.LoginPinBinding;


public class LoginPinFragment extends Fragment {

    private LoginPinBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = LoginPinBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.LoginButton.setOnClickListener(v -> {
            String pin = binding.pinEditText.getText().toString();
            //Initialize an instance of shared preference
            sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences
                    (requireContext());

            if (pin.equals(sharedPreferences.getString("userPin", null))) {
                NavHostFragment.findNavController(LoginPinFragment.this)
                        .navigate(R.id.action_sign_in_pin_to_tips);
            }
            else {
                binding.pinError.setVisibility(View.VISIBLE);
                binding.pinEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        });

        binding.pinEditText.setOnKeyListener((v, keyCode, event) -> {
            if (binding.pinError.getVisibility() == View.VISIBLE) {
                binding.pinEditText.setBackgroundTintList(null);
                binding.pinError.setVisibility(View.INVISIBLE);
            }
            return false;
        });

        binding.emailSignInButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginPinFragment.this)
                    .navigate(R.id.action_sign_in_pin_to_sign_in_email);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}