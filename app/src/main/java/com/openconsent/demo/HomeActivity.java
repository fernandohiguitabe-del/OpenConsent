package com.openconsent.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton fabPrivacy = findViewById(R.id.fabPrivacy);
        fabPrivacy.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPanelActivity.class))
        );
    }
}