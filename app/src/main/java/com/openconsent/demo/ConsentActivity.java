package com.openconsent.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ConsentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        // UserFragment = pantalla de bienvenida con "Tu privacidad, tu decisión"
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.consentFragmentContainer, new UserFragment())
                .commitAllowingStateLoss();
    }

    public void onConsentAccepted() {
        SharedPreferences prefs = getSharedPreferences("openconsent", MODE_PRIVATE);
        prefs.edit().putBoolean("consent_given", true).apply();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}