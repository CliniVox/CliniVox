package com.example.clinivox;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConsultasActivity extends AppCompatActivity {

    LinearLayout consultasContainer;
    FirebaseFirestore db;
    String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultas);

        cpf = getIntent().getStringExtra("cpf");
        db = FirebaseFirestore.getInstance();

        consultasContainer = findViewById(R.id.consultasContainer);
        ImageView btnVoltar = findViewById(R.id.btnVoltar);
        TextView btnMarcarConsulta = findViewById(R.id.btnMarcarConsulta);

        btnVoltar.setOnClickListener(v -> finish());

        btnMarcarConsulta.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarcarConsultaActivity.class);
            intent.putExtra("cpf", cpf);
            startActivity(intent);
        });

        if (cpf == null || cpf.isEmpty()) {
            Toast.makeText(this, "Erro ao carregar dados do usuário. Faça login novamente.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        carregarConsultas();
    }

    private void carregarConsultas() {
        db.collection("consultas")
                .whereEqualTo("cpf_paciente", cpf)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String especialidade = doc.getString("especialidade");
                        String data = doc.getString("data");
                        String hora = doc.getString("hora");
                        String local = doc.getString("local");
                        String crmMedico = doc.getString("crm_medico");
                        String docId = doc.getId();

                        // Buscar nome do médico
                        db.collection("medicos")
                                .document(crmMedico)
                                .get()
                                .addOnSuccessListener(medicoDoc -> {
                                    String nomeMedico = medicoDoc.exists() ? medicoDoc.getString("nome") : "Médico não encontrado";
                                    adicionarCardConsulta(especialidade, nomeMedico, data + " às " + hora, local, docId);
                                })
                                .addOnFailureListener(e -> {
                                    adicionarCardConsulta(especialidade, "Erro ao buscar médico", data + " às " + hora, local, docId);
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar consultas", Toast.LENGTH_SHORT).show());
    }

    private void adicionarCardConsulta(String especialidade, String medico, String dataHora, String local, String docId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.consulta_card, consultasContainer, false);

        TextView txtEspecialidade = cardView.findViewById(R.id.textEspecialidade);
        TextView txtMedico = cardView.findViewById(R.id.textPaciente); // Ou crie um campo médico no XML
        TextView txtDataHora = cardView.findViewById(R.id.textDataHora);
        TextView txtLocal = cardView.findViewById(R.id.textLocal);

        txtEspecialidade.setText(especialidade);
        txtMedico.setText("Médico: " + medico);  // Se quiser "Médico: " antes, concatene aqui
        txtDataHora.setText(dataHora);
        txtLocal.setText(local);

        cardView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConsultasActivity.this);
            builder.setTitle("O que deseja fazer?");
            builder.setItems(new CharSequence[]{"Editar", "Cancelar consulta"}, (dialog, which) -> {
                if (which == 0) {
                    editarConsulta(docId);
                } else if (which == 1) {
                    db.collection("consultas")
                            .document(docId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ConsultasActivity.this, "Consulta cancelada", Toast.LENGTH_SHORT).show();
                                recreate();
                            })
                            .addOnFailureListener(e -> Toast.makeText(ConsultasActivity.this, "Erro ao cancelar", Toast.LENGTH_SHORT).show());
                }
            });
            builder.show();
        });

        consultasContainer.addView(cardView);
    }


    private void editarConsulta(String docId) {
        final TextView txtNovaData = new TextView(this);
        txtNovaData.setText("Toque para escolher a nova data");
        txtNovaData.setTextSize(16);
        txtNovaData.setPadding(20, 30, 20, 20);
        txtNovaData.setTextColor(Color.BLACK);

        final TextView txtNovaHora = new TextView(this);
        txtNovaHora.setText("Toque para escolher a nova hora");
        txtNovaHora.setTextSize(16);
        txtNovaHora.setPadding(20, 10, 20, 30);
        txtNovaHora.setTextColor(Color.BLACK);

        txtNovaData.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                String dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                txtNovaData.setText("Data: " + dataSelecionada);
                txtNovaData.setTag(dataSelecionada);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        txtNovaHora.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view12, hourOfDay, minute) -> {
                String horaSelecionada = String.format("%02d:%02d", hourOfDay, minute);
                txtNovaHora.setText("Hora: " + horaSelecionada);
                txtNovaHora.setTag(horaSelecionada);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        LinearLayout layoutEdit = new LinearLayout(this);
        layoutEdit.setOrientation(LinearLayout.VERTICAL);
        layoutEdit.setPadding(50, 40, 50, 10);
        layoutEdit.addView(txtNovaData);
        layoutEdit.addView(txtNovaHora);

        new AlertDialog.Builder(this)
                .setTitle("Editar Consulta")
                .setView(layoutEdit)
                .setPositiveButton("Salvar", (dialog1, which1) -> {
                    String novaData = (String) txtNovaData.getTag();
                    String novaHora = (String) txtNovaHora.getTag();

                    if (novaData == null || novaHora == null) {
                        Toast.makeText(this, "Selecione a nova data e hora", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Date novaDataHora = sdf.parse(novaData + " " + novaHora);
                        if (novaDataHora.before(new Date())) {
                            Toast.makeText(this, "A nova data e hora devem ser futuras.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.collection("consultas")
                                .document(docId)
                                .update("data", novaData, "hora", novaHora)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Consulta atualizada", Toast.LENGTH_SHORT).show();
                                    recreate();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar consulta", Toast.LENGTH_SHORT).show());
                    } catch (Exception e) {
                        Toast.makeText(this, "Formato de data/hora inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
