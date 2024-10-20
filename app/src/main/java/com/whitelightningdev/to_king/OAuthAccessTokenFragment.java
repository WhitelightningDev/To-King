package com.whitelightningdev.to_king;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class OAuthAccessTokenFragment extends Fragment {

    private TextInputEditText clientIdInput;
    private Spinner clientTypeSpinner, expirationTimeSpinner;
    private MaterialButton generateTokenButton, copyTokenButton, shareTokenButton;
    private TextView tokenOutputTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_oauth_access, container, false);

        // Initialize views
        clientIdInput = view.findViewById(R.id.client_id_input);
        clientTypeSpinner = view.findViewById(R.id.client_type_spinner);
        expirationTimeSpinner = view.findViewById(R.id.expiration_time_spinner);
        generateTokenButton = view.findViewById(R.id.generate_token_button);
        copyTokenButton = view.findViewById(R.id.copy_token_button);
        shareTokenButton = view.findViewById(R.id.share_token_button);
        tokenOutputTextView = view.findViewById(R.id.token_output);

        // Setup spinner for client types
        ArrayAdapter<CharSequence> clientTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.client_types_array, android.R.layout.simple_spinner_item);
        clientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientTypeSpinner.setAdapter(clientTypeAdapter);

        // Setup spinner for expiration times
        ArrayAdapter<CharSequence> expirationTimeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.expiration_times_array, android.R.layout.simple_spinner_item);
        expirationTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expirationTimeSpinner.setAdapter(expirationTimeAdapter);

        // Set button click listeners
        generateTokenButton.setOnClickListener(v -> generateOAuthToken());
        copyTokenButton.setOnClickListener(v -> copyTokenToClipboard());
        shareTokenButton.setOnClickListener(v -> shareToken());

        return view;
    }

    private void generateOAuthToken() {
        String clientId = clientIdInput.getText().toString().trim();
        String clientType = clientTypeSpinner.getSelectedItem().toString();
        String expirationTimeStr = expirationTimeSpinner.getSelectedItem().toString().replaceAll("[^\\d]", "");

        // Validate input
        if (TextUtils.isEmpty(clientId)) {
            showToast("Please enter Client ID");
            return;
        }

        int expirationTimeInMinutes;
        try {
            expirationTimeInMinutes = Integer.parseInt(expirationTimeStr);
        } catch (NumberFormatException e) {
            showToast("Invalid expiration time");
            return;
        }

        // Generate expiration date
        Date expirationDate = calculateExpirationDate(expirationTimeInMinutes);

        // Generate the OAuth access token based on the client type
        String accessToken = generateJwtToken(clientId, clientType, expirationDate);

        // Show the generated token
        tokenOutputTextView.setText(accessToken);
    }

    private Date calculateExpirationDate(int expirationTimeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationTimeInMinutes);
        return calendar.getTime();
    }

    private String generateJwtToken(String clientId, String clientType, Date expirationDate) {
        Claims claims = Jwts.claims().setSubject(clientId);
        claims.put("client_type", clientType);
        claims.put("scope", "read write");
        claims.put("aud", "https://yourapi.com");

        // Generate the JWT token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, generateRandomSecretKey().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    private String generateRandomSecretKey() {
        byte[] randomBytes = new byte[24]; // 192 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    private void copyTokenToClipboard() {
        String token = tokenOutputTextView.getText().toString();
        if (!TextUtils.isEmpty(token)) {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("OAuth Token", token);
            clipboard.setPrimaryClip(clip);
            showToast("Token copied to clipboard");
        } else {
            showToast("No token to copy");
        }
    }

    private void shareToken() {
        String token = tokenOutputTextView.getText().toString();
        if (!TextUtils.isEmpty(token)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, token);
            startActivity(Intent.createChooser(shareIntent, "Share OAuth Token"));
        } else {
            showToast("No token to share");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
