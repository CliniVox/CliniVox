package com.example.clinivox;

import android.graphics.Color;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    LinearLayout layoutConsultas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        layoutConsultas = findViewById(R.id.layoutConsultas);


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
                    //LinearLayout layoutConsultas = findViewById(R.id.layoutConsultas);
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
        // Infla o layout do card personalizado
        View cardView = getLayoutInflater().inflate(R.layout.card_consulta_medico, null);

        TextView textEspecialidade = cardView.findViewById(R.id.textEspecialidade);
        TextView textPaciente = cardView.findViewById(R.id.textPaciente);
        TextView textDataHora = cardView.findViewById(R.id.textDataHora);
        TextView textLocal = cardView.findViewById(R.id.textLocal);

        textEspecialidade.setText("Especialidade: " + especialidade);
        textPaciente.setText("Paciente: " + paciente);
        textDataHora.setText("Data e Hora: " + data + " - " + hora);
        textLocal.setText("Local: " + local);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);
        cardView.setLayoutParams(params);

        layoutConsultas.addView(cardView);
    }

}
