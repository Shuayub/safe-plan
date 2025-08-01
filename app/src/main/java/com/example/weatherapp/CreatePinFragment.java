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
import com.example.weatherapp.databinding.RegistrationPinBinding;
import com.google.firebase.auth.FirebaseAuth;


public class CreatePinFragment extends Fragment {

    private RegistrationPinBinding binding;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = RegistrationPinBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.FinishButton.setOnClickListener(v -> {
            String pin = binding.pinEditText.getText().toString();

            if (pin.length() == 4 || pin.length() == 6) {
                //Get the user UID from the firebase
                mAuth = FirebaseAuth.getInstance();
                String userUID = mAuth.getUid();

                //Initialize the instance of the sharedPreferences
                sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences
                        (requireContext());
                SharedPreferences.Editor userPinEditor = sharedPreferences.edit();

                //Apply the user entered pin and and a boolean value to show that the user has logged
                // in before
                userPinEditor.putBoolean("createdAccount", true);
                userPinEditor.putString("userPin", pin);
                userPinEditor.putString("userUID", userUID);
                userPinEditor.apply();

                NavHostFragment.findNavController(CreatePinFragment.this)
                        .navigate(R.id.action_create_pin_to_survey);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}