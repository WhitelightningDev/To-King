package com.whitelightningdev.to_king;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class JwtTokenFragment extends Fragment {

    private EditText expirationTimeEditText;
    private MaterialButton createTokenButton, copyButton, shareButton;
    private TextView tokenResultTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jwt_token, container, false);

        // Initialize views
        expirationTimeEditText = view.findViewById(R.id.expiration_time);
        createTokenButton = view.findViewById(R.id.create_token_button);
        tokenResultTextView = view.findViewById(R.id.token_result);
        copyButton = view.findViewById(R.id.copy_button);
        shareButton = view.findViewById(R.id.share_button);

        // Set button click listener
        createTokenButton.setOnClickListener(v -> createJwtToken());

        // Copy the generated token
        copyButton.setOnClickListener(v -> copyTokenToClipboard());

        // Share the generated token
        shareButton.setOnClickListener(v -> shareToken());

        return view;
    }

    private void createJwtToken() {
        String expirationTimeStr = expirationTimeEditText.getText().toString().trim();

        // Validate the expiration time input
        if (TextUtils.isEmpty(expirationTimeStr)) {
            showToast("Please enter expiration time");
            return;
        }

        int expirationTimeInMinutes;
        try {
            expirationTimeInMinutes = Integer.parseInt(expirationTimeStr);
            if (expirationTimeInMinutes <= 0) {
                showToast("Expiration time must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            showToast("Invalid number");
            return;
        }

        // Calculate expiration date
        Date expirationDate = calculateExpirationDate(expirationTimeInMinutes);

        // Generate the JWT token
        String jwtToken = generateJwtToken(expirationDate);

        // Show the generated token
        tokenResultTextView.setText(jwtToken);
    }

    private Date calculateExpirationDate(int expirationTimeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationTimeInMinutes);
        return calendar.getTime();
    }

    private String generateJwtToken(Date expirationDate) {
        // Generate a new secret key each time
        String secretKey = generateRandomSecretKey();

        // Create the claims
        Claims claims = Jwts.claims().setSubject("user"); // Set the subject or any custom claim
        claims.put("role", "user"); // You can add custom claims here

        // Generate the JWT token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) // Set the issue date
                .setExpiration(expirationDate) // Set the expiration date
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8)) // Sign the token with the secret key
                .compact(); // Compact to a single string
    }

    private String generateRandomSecretKey() {
        // Generate a secure random key
        byte[] randomBytes = new byte[24]; // 192 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes); // Return the key as a Base64 encoded string
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void copyTokenToClipboard() {
        String token = tokenResultTextView.getText().toString();
        if (!TextUtils.isEmpty(token)) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("JWT Token", token);
            clipboard.setPrimaryClip(clip);
            showToast("Token copied to clipboard");
        } else {
            showToast("No token to copy");
        }
    }

    private void shareToken() {
        String token = tokenResultTextView.getText().toString();
        if (!TextUtils.isEmpty(token)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, token);
            startActivity(Intent.createChooser(shareIntent, "Share JWT Token via"));
        } else {
            showToast("No token to share");
        }
    }
}
