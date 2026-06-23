package com.openconsent.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class PrivacyPanelActivity extends AppCompatActivity {

    private Button btnUser;
    private Button btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_panel);

        // Aplica el inset de la status bar al topBar
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.topBar),
                (v, insets) -> {
                    int topInset = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()).top;
                    v.setPadding(
                            v.getPaddingLeft(),
                            topInset + 8,   // 8dp de padding propio + altura status bar
                            v.getPaddingRight(),
                            v.getPaddingBottom()
                    );
                    return insets;
                }
        );

        btnUser  = findViewById(R.id.btnSwitchUser);
        btnAdmin = findViewById(R.id.btnSwitchAdmin);

        setActive(btnUser, btnAdmin);
        if (savedInstanceState == null) {
            replaceFragment(new UserPrivacyFragment());
        }

        btnUser.setOnClickListener(v -> {
            setActive(btnUser, btnAdmin);
            replaceFragment(new UserPrivacyFragment());
        });

        btnAdmin.setOnClickListener(v -> {
            setActive(btnAdmin, btnUser);
            Toast.makeText(this, "Cargando Admin...", Toast.LENGTH_SHORT).show();
            replaceFragment(new AdminFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.privacyFragmentContainer, fragment);
        tx.commitAllowingStateLoss();
    }

    private void setActive(Button active, Button inactive) {
        active.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#01696f")));
        active.setTextColor(Color.WHITE);

        inactive.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#C8D8D6")));
        inactive.setTextColor(Color.parseColor("#7a7974"));
    }
}