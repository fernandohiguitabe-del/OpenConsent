package com.openconsent.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.openconsent.sdk.ConsentEvent;
import com.openconsent.sdk.ConsentManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminFragment extends Fragment {

    private ConsentManager manager;
    private View rootView;  // ← guardamos referencia para recargar tras limpiar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_admin, container, false);
        manager  = ConsentManager.getInstance(requireContext(), "CineApp");

        cargarTodo();

        // Exportar JSON
        rootView.findViewById(R.id.btnExportLog).setOnClickListener(v -> {
            String json = manager.exportLogsAsJson();
            Toast.makeText(requireContext(),
                    "✅ JSON listo:\n" + json.substring(0, Math.min(json.length(), 100)) + "...",
                    Toast.LENGTH_LONG).show();
        });

        // Exportar CSV
        rootView.findViewById(R.id.btnExportCsv).setOnClickListener(v -> {
            String csv = manager.exportLogsAsCsv();
            Toast.makeText(requireContext(),
                    "✅ CSV listo:\n" + csv.substring(0, Math.min(csv.length(), 100)) + "...",
                    Toast.LENGTH_LONG).show();
        });

        // Limpiar historial
        rootView.findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("🗑️ Limpiar historial")
                    .setMessage("¿Eliminar todos los eventos registrados? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí, limpiar", (dialog, which) -> {
                        manager.clearHistory();
                        cargarTodo();
                        Toast.makeText(requireContext(),
                                "✅ Historial limpiado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return rootView;
    }

    // Método centralizado que recarga todo el panel
    private void cargarTodo() {
        List<ConsentEvent> events = manager.getHistory();
        cargarKpis(events);
        cargarEstadoUsuarios(events);
        cargarHistorial(events);
    }

    private void cargarKpis(List<ConsentEvent> events) {
        int aceptados = 0, revocados = 0, rechazados = 0;
        for (ConsentEvent e : events) {
            switch (e.getAction()) {
                case "ACCEPTED": aceptados++; break;
                case "REVOKED":  revocados++; break;
                case "REJECTED": rechazados++; break;
            }
        }
        ((TextView) rootView.findViewById(R.id.txtTotalConsent)).setText(String.valueOf(aceptados));
        ((TextView) rootView.findViewById(R.id.txtTotalRevoked)).setText(String.valueOf(revocados));
        ((TextView) rootView.findViewById(R.id.txtTotalRejected)).setText(String.valueOf(rechazados));
    }

    private void cargarEstadoUsuarios(List<ConsentEvent> events) {
        LinearLayout container = rootView.findViewById(R.id.containerUsuarios);
        container.removeAllViews();

        Map<String, String> estadoMap = new HashMap<>();
        for (ConsentEvent e : events) {
            String key = e.getUserId() + "_" + e.getPurpose();
            estadoMap.put(key, e.getAction());
        }

        Map<String, Boolean> usuarios = new HashMap<>();
        for (ConsentEvent e : events) {
            usuarios.put(e.getUserId(), true);
        }

        for (String userId : usuarios.keySet()) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(24, 16, 24, 16);
            row.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 8);
            row.setLayoutParams(params);

            TextView tvUser = new TextView(requireContext());
            tvUser.setText("👤 " + userId);
            tvUser.setTextSize(13);
            tvUser.setTextColor(0xFF28251d);
            tvUser.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvUser);

            String[] propositos = {"obligatorio", "analytics", "marketing"};
            for (String p : propositos) {
                String key    = userId + "_" + p;
                String accion = estadoMap.getOrDefault(key, "SIN DATO");
                String emoji  = "ACCEPTED".equals(accion) ? "✅" :
                        "REVOKED".equals(accion)  ? "🔄" :
                        "REJECTED".equals(accion) ? "❌" : "⬜";

                TextView tvProp = new TextView(requireContext());
                tvProp.setText("   " + emoji + " " + p + " → " + accion);
                tvProp.setTextSize(12);
                tvProp.setTextColor(0xFF7a7974);
                row.addView(tvProp);
            }
            container.addView(row);
        }

        if (usuarios.isEmpty()) {
            TextView tvEmpty = new TextView(requireContext());
            tvEmpty.setText("Sin usuarios registrados aún.");
            tvEmpty.setTextSize(12);
            tvEmpty.setTextColor(0xFF7a7974);
            tvEmpty.setPadding(0, 8, 0, 8);
            container.addView(tvEmpty);
        }
    }

    private void cargarHistorial(List<ConsentEvent> events) {
        LinearLayout container = rootView.findViewById(R.id.containerHistorial);
        container.removeAllViews();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        if (events.isEmpty()) {
            TextView tvEmpty = new TextView(requireContext());
            tvEmpty.setText("Sin eventos registrados aún.");
            tvEmpty.setTextSize(12);
            tvEmpty.setTextColor(0xFF7a7974);
            tvEmpty.setPadding(0, 8, 0, 8);
            container.addView(tvEmpty);
            return;
        }

        for (int i = events.size() - 1; i >= 0; i--) {
            ConsentEvent e = events.get(i);

            String emoji = "ACCEPTED".equals(e.getAction()) ? "✅" :
                    "REVOKED".equals(e.getAction())  ? "🔄" :
                    "REJECTED".equals(e.getAction()) ? "❌" : "⬜";

            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(24, 12, 24, 12);
            row.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 6);
            row.setLayoutParams(params);

            TextView tvAccion = new TextView(requireContext());
            tvAccion.setText(emoji + " " + e.getAction() + " — " + e.getPurpose());
            tvAccion.setTextSize(13);
            tvAccion.setTextColor(0xFF28251d);
            tvAccion.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvAccion);

            TextView tvDetalle = new TextView(requireContext());
            tvDetalle.setText(
                    e.getUserId() + "  •  " +
                            sdf.format(new Date(e.getTimestamp())) + "  •  " +
                            e.getPolicyVersion()
            );
            tvDetalle.setTextSize(11);
            tvDetalle.setTextColor(0xFF7a7974);
            row.addView(tvDetalle);

            container.addView(row);
        }
    }
}