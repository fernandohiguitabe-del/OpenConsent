package com.openconsent.sdk;

import android.app.AlertDialog;
import android.content.Context;

public class ConsentDialog {

    public interface ConsentCallback {
        void onAccepted();
        void onRejected();
    }

    public static void show(
            Context context,
            String appId,
            String purpose,
            String policyVersion,
            ConsentCallback callback) {

        String message = "App: " + appId + "\n"
                + "Finalidad: " + purpose + "\n"
                + "Política: " + policyVersion + "\n\n"
                + "¿Deseas otorgar tu consentimiento para el tratamiento de tus datos?";

        new AlertDialog.Builder(context)
                .setTitle("Solicitud de Consentimiento")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> callback.onAccepted())
                .setNegativeButton("Rechazar", (dialog, which) -> callback.onRejected())
                .show();
    }
}