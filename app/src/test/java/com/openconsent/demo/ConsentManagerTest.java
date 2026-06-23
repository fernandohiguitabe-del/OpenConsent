package com.openconsent.demo;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import com.openconsent.sdk.ConsentEvent;
import com.openconsent.sdk.ConsentManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)

public class ConsentManagerTest {
    private ConsentManager manager;
    private Context ctx;
    private static final String APP_ID     = "CineApp";
    private static final String POLICY_V1  = "v1.0";
    private static final String POLICY_V2  = "v2.0";
    private static final String PREFS_NAME = "openconsent";

    @Before
    public void setUp() {
        ctx = ApplicationProvider.getApplicationContext();

        // 1. Limpia SharedPreferences entre tests
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().clear().commit();

        // 2. Resetea el Singleton para evitar contaminación entre tests
        ConsentManager.resetInstance();

        // 3. Instancia limpia desde cero
        manager = ConsentManager.getInstance(ctx, APP_ID);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-01 | recordConsent — registro básico
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc01_recordConsent_debeCrearEventoACCEPTED() {
        manager.recordConsent("analytics", POLICY_V1);
        List<ConsentEvent> history = manager.getHistory();

        assertEquals("Debe haber exactamente 1 evento", 1, history.size());
        ConsentEvent ev = history.get(0);
        assertEquals("ACCEPTED",  ev.getAction());
        assertEquals("analytics", ev.getPurpose());
        assertEquals(POLICY_V1,   ev.getPolicyVersion());
        assertNotNull("El userId no debe ser nulo", ev.getUserId());
        assertTrue("El timestamp debe ser positivo", ev.getTimestamp() > 0);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-02 | recordConsent — múltiples propósitos
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc02_recordConsent_multiplesPropósitos_debenRegistrarseIndependientemente() {
        manager.recordConsent("analytics",   POLICY_V1);
        manager.recordConsent("marketing",   POLICY_V1);
        manager.recordConsent("obligatorio", POLICY_V1);

        List<ConsentEvent> history = manager.getHistory();
        assertEquals("Deben existir 3 eventos", 3, history.size());

        boolean hasAnalytics   = history.stream().anyMatch(e -> e.getPurpose().equals("analytics"));
        boolean hasMarketing   = history.stream().anyMatch(e -> e.getPurpose().equals("marketing"));
        boolean hasObligatorio = history.stream().anyMatch(e -> e.getPurpose().equals("obligatorio"));

        assertTrue(hasAnalytics);
        assertTrue(hasMarketing);
        assertTrue(hasObligatorio);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-03 | recordRevocation — registro básico
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc03_recordRevocation_debeCrearEventoREVOKED() {
        manager.recordConsent("marketing",    POLICY_V1);
        manager.recordRevocation("marketing", POLICY_V1);

        List<ConsentEvent> history = manager.getHistory();
        assertEquals("Deben existir 2 eventos (consent + revocation)", 2, history.size());

        ConsentEvent revEv = history.get(1);
        assertEquals("REVOKED",   revEv.getAction());
        assertEquals("marketing", revEv.getPurpose());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-04 | recordRevocation — no sobreescribe historial previo
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc04_recordRevocation_noDebeEliminarEventosPrevios() {
        manager.recordConsent("analytics",    POLICY_V1);
        manager.recordRevocation("analytics", POLICY_V1);

        List<ConsentEvent> history = manager.getHistory();
        assertEquals("El historial debe conservar ambos eventos", 2, history.size());
        assertEquals("ACCEPTED", history.get(0).getAction());
        assertEquals("REVOKED",  history.get(1).getAction());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-05 | getHistory — orden cronológico
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc05_getHistory_debeRetornarEventosEnOrdenCronologico() throws InterruptedException {
        manager.recordConsent("analytics", POLICY_V1);
        Thread.sleep(10);
        manager.recordConsent("marketing", POLICY_V1);
        Thread.sleep(10);
        manager.recordRevocation("analytics", POLICY_V1);

        List<ConsentEvent> history = manager.getHistory();
        assertEquals(3, history.size());

        for (int i = 1; i < history.size(); i++) {
            assertTrue(
                    "El evento " + i + " debe ser posterior al " + (i - 1),
                    history.get(i).getTimestamp() >= history.get(i - 1).getTimestamp()
            );
        }
    }

    // ─────────────────────────────────────────────────────────────
    // TC-06 | getHistory — historial vacío
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc06_getHistory_sinEventos_debeRetornarListaVacia() {
        List<ConsentEvent> history = manager.getHistory();
        assertNotNull("getHistory nunca debe retornar null", history);
        assertTrue("La lista debe estar vacía", history.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-07 | clearHistory — limpia eventos y SharedPreferences
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc07_clearHistory_vaciaSharedPreferences() {
        manager.recordConsent("analytics", POLICY_V1);
        manager.recordConsent("marketing", POLICY_V1);
        assertFalse("Debe haber eventos antes de limpiar",
                manager.getHistory().isEmpty());

        manager.clearHistory();

        // Verifica que la lista en memoria queda vacía
        assertTrue("getHistory() debe retornar lista vacía",
                manager.getHistory().isEmpty());

        // Verifica que SharedPreferences también quedó limpio
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        assertFalse("La clave de eventos no debe existir en SharedPreferences",
                prefs.contains("consent_events"));
    }                                                               // ← }

    // ─────────────────────────────────────────────────────────────
    // TC-08 | recordConsent — cambio de versión de política
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc08_recordConsent_nuevaVersionPolitica_debeRegistrarseSeparadamente() {
        manager.recordConsent("analytics", POLICY_V1);
        manager.recordConsent("analytics", POLICY_V2);

        List<ConsentEvent> history = manager.getHistory();
        assertEquals("Deben existir 2 eventos con versiones distintas", 2, history.size());
        assertEquals(POLICY_V1, history.get(0).getPolicyVersion());
        assertEquals(POLICY_V2, history.get(1).getPolicyVersion());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-09 | exportLogsAsJson — formato válido
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc09_exportLogsAsJson_debeRetornarCadenaJson() {
        manager.recordConsent("analytics", POLICY_V1);
        String json = manager.exportLogsAsJson();

        assertNotNull("El JSON no debe ser nulo", json);
        assertFalse("El JSON no debe estar vacío", json.isEmpty());
        assertTrue("Debe comenzar con '[' (array JSON)", json.trim().startsWith("["));
        assertTrue("Debe terminar con ']'",              json.trim().endsWith("]"));
        assertTrue("Debe contener el propósito",         json.contains("analytics"));
        assertTrue("Debe contener la acción",            json.contains("ACCEPTED"));
    }

    // ─────────────────────────────────────────────────────────────
    // TC-10 | exportLogsAsCsv — formato válido
    // ─────────────────────────────────────────────────────────────
    @Test
    public void tc10_exportLogsAsCsv_debeRetornarCadenaCsv() {
        manager.recordConsent("marketing", POLICY_V1);
        String csv = manager.exportLogsAsCsv();

        assertNotNull("El CSV no debe ser nulo", csv);
        assertFalse("El CSV no debe estar vacío", csv.isEmpty());
        assertTrue("Debe contener cabecera con 'userId'",  csv.contains("userId"));
        assertTrue("Debe contener cabecera con 'purpose'", csv.contains("purpose"));
        assertTrue("Debe contener el propósito registrado", csv.contains("marketing"));
    }
}
