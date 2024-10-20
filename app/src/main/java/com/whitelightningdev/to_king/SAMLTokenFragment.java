package com.whitelightningdev.to_king;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SAMLTokenFragment extends Fragment {

    private TextInputEditText userIdInput;
    private TextView tokenOutput;
    private MaterialButton generateTokenButton;
    private Spinner clientTypeSpinner;
    private Spinner expirationTimeSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_saml_tokens, container, false);

        // Initialize views
        userIdInput = view.findViewById(R.id.user_id_input);
        tokenOutput = view.findViewById(R.id.token_output);
        generateTokenButton = view.findViewById(R.id.generate_token_button);
        clientTypeSpinner = view.findViewById(R.id.client_type_spinner);
        expirationTimeSpinner = view.findViewById(R.id.expiration_time_spinner);

        // Setup Spinners
        setupSpinners();

        // Set button click listener
        generateTokenButton.setOnClickListener(v -> generateSAMLToken());

        return view;
    }

    private void setupSpinners() {
        // Example data for the Spinners
        String[] clientTypes = {"Public", "Confidential"};
        String[] expirationTimes = {"1 hour", "2 hours", "4 hours", "1 day"};

        // Setup adapter for Client Type Spinner
        ArrayAdapter<String> clientTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clientTypes);
        clientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientTypeSpinner.setAdapter(clientTypeAdapter);

        // Setup adapter for Expiration Time Spinner
        ArrayAdapter<String> expirationTimeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, expirationTimes);
        expirationTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expirationTimeSpinner.setAdapter(expirationTimeAdapter);
    }

    private void generateSAMLToken() {
        String userId = userIdInput.getText().toString().trim();

        // Validate user ID input
        if (TextUtils.isEmpty(userId)) {
            showToast("Please enter User ID");
            return;
        }

        // Additional logic for generating SAML token...
        // Displaying the generated token for now
        tokenOutput.setText("Generated SAML token for user: " + userId);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
