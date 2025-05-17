package com.example.clinivox;

import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MedicoActivity extends AppCompatActivity {
    TextView tvNome;
    FirebaseFirestore db;
    String crmMedicoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        db = FirebaseFirestore.getInstance();

        ImageButton btnConfig = findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, AjustesActivity.class);
            startActivity(intent);
        });

        tvNome = findViewById(R.id.tvNomeMedico);

        String crm = getIntent().getStringExtra("identificador");
        Log.d("CRM_DEBUG", "CRM recebido: " + crm);

        if (crm != null && !crm.isEmpty()) {
            carregarNomeMedico(crm);
        } else {
            Toast.makeText(this, "Erro: CRM não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void carregarNomeMedico(String crm) {
        db.collection("medicos")
                .document(crm)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        crmMedicoAtual = crm;

                        if (nome != null) {
                            tvNome.setText("Dr. " + nome);
                            carregarConsultas(crm);
                        } else {
                            Toast.makeText(this, "Dados do médico incompletos.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Médico não encontrado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar médico.", Toast.LENGTH_SHORT).show();
                    Log.e("MEDICO_DEBUG", "Erro: " + e.getMessage());
                });
    }

    private void carregarConsultas(String crmMedico) {
        db.collection("consultas")
                .whereEqualTo("crm_medico", crmMedico)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LinearLayout layoutConsultas = findViewById(R.id.layoutConsultas);
                    layoutConsultas.removeAllViews();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String especialidade = doc.getString("especialidade");
                        String data = doc.getString("data");
                        String hora = doc.getString("hora");
                        String local = doc.getString("local");
                        String cpfPaciente = doc.getString("cpf_paciente");

                        if (cpfPaciente != null) {
                            db.collection("pacientes")
                                    .document(cpfPaciente)
                                    .get()
                                    .addOnSuccessListener(pacienteDoc -> {
                                        String nomePaciente = pacienteDoc.getString("nome");
                                        adicionarCardConsulta(especialidade, nomePaciente, data, hora, local);
                                    })
                                    .addOnFailureListener(e -> {
                                        adicionarCardConsulta(especialidade, "Paciente não encontrado", data, hora, local);
                                    });
                        } else {
                            adicionarCardConsulta(especialidade, "CPF do paciente não disponível", data, hora, local);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar consultas.", Toast.LENGTH_SHORT).show();
                    Log.e("CONSULTA_DEBUG", "Erro: " + e.getMessage());
                });
    }

    private void adicionarCardConsulta(String especialidade, String paciente, String data, String hora, String local) {
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        card.setCardElevation(8);
        card.setRadius(20);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        layout.setBackgroundColor(Color.WHITE);

        TextView txtEspecialidade = new TextView(this);
        txtEspecialidade.setText("Especialidade: " + especialidade);
        txtEspecialidade.setTextSize(18);
        txtEspecialidade.setTypeface(null, Typeface.BOLD);
        txtEspecialidade.setTextColor(Color.BLACK);

        TextView txtPaciente = new TextView(this);
        txtPaciente.setText("Paciente: " + paciente);
        txtPaciente.setTextSize(16);
        txtPaciente.setTextColor(Color.DKGRAY);

        TextView txtDataHora = new TextView(this);
        txtDataHora.setText("Data e Hora: " + data + " - " + hora );
        txtDataHora.setTextSize(16);
        txtDataHora.setTextColor(Color.DKGRAY);

        TextView txtLocal = new TextView(this);
        txtLocal.setText("Local: " + local);
        txtLocal.setTextSize(16);
        txtLocal.setTextColor(Color.DKGRAY);

        layout.addView(txtEspecialidade);
        layout.addView(txtPaciente);
        layout.addView(txtDataHora);
        layout.addView(txtLocal);

        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 40);
        card.setLayoutParams(params);

        LinearLayout layoutConsultas = findViewById(R.id.layoutConsultas);
        if (layoutConsultas != null) {
            layoutConsultas.addView(card);
        }
    }
}
