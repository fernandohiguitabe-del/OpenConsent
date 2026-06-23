package com.openconsent.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.openconsent.sdk.ConsentEvent;
import com.openconsent.sdk.ConsentManager;
import java.util.List;

public class UserPrivacyFragment extends Fragment {

    private ConsentManager manager;
    private SwitchCompat switchAnalytics;
    private SwitchCompat switchMarketing;
    private String policyVersion;  // ← campo de clase, accesible en todos los métodos

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_privacy, container, false);

        manager        = ConsentManager.getInstance(requireContext(), "CineApp");
        policyVersion  = "v" + getString(R.string.policy_version);  // lee de strings.xml

        switchAnalytics = view.findViewById(R.id.switchAnalytics);
        switchMarketing = view.findViewById(R.id.switchMarketing);

        Button btnOnlyRequired = view.findViewById(R.id.btnOnlyRequired);
        Button btnRevokeAll    = view.findViewById(R.id.btnRevokeAll);

        switchAnalytics.setChecked(isActive("analytics"));
        switchMarketing.setChecked(isActive("marketing"));

        btnOnlyRequired.setOnClickListener(v -> saveChanges());
        btnRevokeAll.setOnClickListener(v -> showRevokeAllDialog());

        return view;
    }

    private boolean isActive(String purpose) {
        List<ConsentEvent> events = manager.getHistory();
        String lastAction = null;
        for (ConsentEvent e : events) {
            if (e.getPurpose().equals(purpose)) {
                lastAction = e.getAction();
            }
        }
        return "ACCEPTED".equals(lastAction);
    }

    private void saveChanges() {
        if (switchAnalytics.isChecked())
            manager.recordConsent("analytics", policyVersion);
        else
            manager.recordRevocation("analytics", policyVersion);

        if (switchMarketing.isChecked())
            manager.recordConsent("marketing", policyVersion);
        else
            manager.recordRevocation("marketing", policyVersion);

        proceedAfterConsent();
    }

    private void proceedAfterConsent() {
        if (getActivity() instanceof ConsentActivity) {
            ((ConsentActivity) getActivity()).onConsentAccepted();
        } else {
            requireActivity().finish();
        }
    }

    private void showRevokeAllDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Revocar todo el consentimiento")
                .setMessage(
                        "Serás redirigido a la pantalla de consentimiento.\n\n" +
                                "Los datos ya recopilados se conservan conforme al Art. 7.3 GDPR. " +
                                "Para solicitar supresión escribe a privacidad@cineapp.demo.\n\n" +
                                "¿Deseas continuar?"
                )
                .setPositiveButton("Sí, revocar todo", (dialog, which) -> {
                    manager.recordRevocation("obligatorio", policyVersion);
                    manager.recordRevocation("analytics",   policyVersion);
                    manager.recordRevocation("marketing",   policyVersion);

                    SharedPreferences prefs = requireContext()
                            .getSharedPreferences("openconsent", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("consent_given", false).apply();

                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}