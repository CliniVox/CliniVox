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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        db = FirebaseFirestore.getInstance();

        // Botão de configurações
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
        db.collection("Medicos")
                .document(crm)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        String crmMedico = String.valueOf(documentSnapshot.get("crm"));

                        Log.d("MEDICO_DEBUG", "Nome do médico: " + nome);
                        Log.d("MEDICO_DEBUG", "CRM do médico: " + crmMedico);

                        if (nome != null && crmMedico != null) {
                            tvNome.setText("Dr. " + nome);
                            carregarConsultas(nome);
                        } else {
                            Toast.makeText(this, "Dados do médico incompletos.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MedicoActivity.this, "Médico não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MedicoActivity.this, "Erro ao carregar nome do médico", Toast.LENGTH_SHORT).show();
                    Log.e("MEDICO_DEBUG", "Erro ao carregar nome do médico: " + e.getMessage());
                });
    }

    private void carregarConsultas(String nomeMedico) {
        db.collectionGroup("agendadas")
                .whereEqualTo("medico", nomeMedico)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LinearLayout layoutConsultas = findViewById(R.id.layoutConsultas);
                    layoutConsultas.removeAllViews();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String especialidade = doc.getString("especialidade");
                        String data = doc.getString("data");
                        String hora = doc.getString("hora");
                        String local = doc.getString("local");

                        // Obter CPF do paciente a partir do caminho do documento
                        String path = doc.getReference().getPath();  // Ex: consultas/12345678900/agendadas/docId
                        String[] partes = path.split("/");
                        String cpfPaciente = partes[1];  // Está sempre na posição 1

                        // Buscar o nome do paciente usando o CPF
                        db.collection("Pacientes").document(cpfPaciente).get()
                                .addOnSuccessListener(pacienteDoc -> {
                                    String nomePaciente = pacienteDoc.getString("nome");
                                    adicionarCardConsulta(especialidade, nomePaciente, data, hora, local);
                                })
                                .addOnFailureListener(e -> {
                                    adicionarCardConsulta(especialidade, "Paciente não encontrado", data, hora, local);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MedicoActivity.this, "Erro ao carregar consultas.", Toast.LENGTH_SHORT).show();
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
        txtDataHora.setText("Data e Hora: " + data + " - " + hora);
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