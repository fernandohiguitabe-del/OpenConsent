package com.openconsent.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.openconsent.sdk.ConsentManager;

public class UserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String policyVersion = "v" + getString(R.string.policy_version);
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        CheckBox cbAnalytics = view.findViewById(R.id.cbAnalytics);
        CheckBox cbMarketing = view.findViewById(R.id.cbMarketing);
        Button btnAccept     = view.findViewById(R.id.btnAcceptAll);
        Button btnReject     = view.findViewById(R.id.btnRejectOptional);

        setupToggle(view, R.id.headerObligatorio, R.id.detailObligatorio, R.id.arrowObligatorio);
        setupToggle(view, R.id.headerAnalytics,   R.id.detailAnalytics,   R.id.arrowAnalytics);
        setupToggle(view, R.id.headerMarketing,   R.id.detailMarketing,   R.id.arrowMarketing);
        setupToggle(view, R.id.headerDerechos,    R.id.detailDerechos,    R.id.arrowDerechos);

        ConsentManager manager = ConsentManager.getInstance(requireContext(), "CineApp");

        btnAccept.setOnClickListener(v -> {
            manager.recordConsent("obligatorio", policyVersion);
            if (cbAnalytics.isChecked())
                manager.recordConsent("analytics", policyVersion);
            else
                manager.recordRejection("analytics", policyVersion);
            if (cbMarketing.isChecked())
                manager.recordConsent("marketing", policyVersion);
            else
                manager.recordRejection("marketing", policyVersion);

            if (getActivity() instanceof ConsentActivity) {
                ((ConsentActivity) getActivity()).onConsentAccepted();
            }
        });

        btnReject.setOnClickListener(v -> {
            manager.recordConsent("obligatorio",   policyVersion);
            manager.recordRejection("analytics",   policyVersion);
            manager.recordRejection("marketing",   policyVersion);

            if (getActivity() instanceof ConsentActivity) {
                ((ConsentActivity) getActivity()).onConsentAccepted();
            }
        });

        return view;
    }

    private void setupToggle(View root, int headerId, int detailId, int arrowId) {
        LinearLayout header = root.findViewById(headerId);
        LinearLayout detail = root.findViewById(detailId);
        TextView arrow      = root.findViewById(arrowId);

        header.setOnClickListener(v -> {
            boolean visible = detail.getVisibility() == View.VISIBLE;
            detail.setVisibility(visible ? View.GONE : View.VISIBLE);
            arrow.setText(visible ? "▼" : "▲");
        });
    }
}