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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;  // Adicionada a importação
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

        db.collection("consultas")
                .document(cpf)
                .collection("agendadas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String especialidade = doc.getString("especialidade");
                        String data = doc.getString("data");
                        String hora = doc.getString("hora");
                        String medico = doc.getString("medico");
                        String local = doc.getString("local");
                        String docId = doc.getId();

                        adicionarCardConsulta(especialidade, medico, data + " às " + hora, local, docId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar consultas", Toast.LENGTH_SHORT).show());
    }

    private void adicionarCardConsulta(String especialidade, String medico, String dataHora, String local, String docId) {
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

        TextView txtMedico = new TextView(this);
        txtMedico.setText("Médico: " + medico);
        txtMedico.setTextSize(16);
        txtMedico.setTextColor(Color.DKGRAY);

        TextView txtDataHora = new TextView(this);
        txtDataHora.setText("Data e Hora: " + dataHora);
        txtDataHora.setTextSize(16);
        txtDataHora.setTextColor(Color.DKGRAY);

        TextView txtLocal = new TextView(this);
        txtLocal.setText("Local: " + local);
        txtLocal.setTextSize(16);
        txtLocal.setTextColor(Color.DKGRAY);

        layout.addView(txtEspecialidade);
        layout.addView(txtMedico);
        layout.addView(txtDataHora);
        layout.addView(txtLocal);

        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 40);
        card.setLayoutParams(params);

        card.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ConsultasActivity.this);
            builder.setTitle("O que deseja fazer?");
            builder.setItems(new CharSequence[]{"Editar", "Cancelar consulta"}, (dialog, which) -> {
                if (which == 0) {
                    // EDITAR
                    final TextView txtNovaData = new TextView(this);
                    txtNovaData.setHint("Selecionar nova data");
                    txtNovaData.setText("Toque para escolher a nova data");
                    txtNovaData.setTextSize(16);
                    txtNovaData.setPadding(20, 30, 20, 20);
                    txtNovaData.setTextColor(Color.BLACK);

                    final TextView txtNovaHora = new TextView(this);
                    txtNovaHora.setHint("Selecionar nova hora");
                    txtNovaHora.setText("Toque para escolher a nova hora");
                    txtNovaHora.setTextSize(16);
                    txtNovaHora.setPadding(20, 10, 20, 30);
                    txtNovaHora.setTextColor(Color.BLACK);

                    txtNovaData.setOnClickListener(view -> {
                        final Calendar c = Calendar.getInstance();
                        int ano = c.get(Calendar.YEAR);
                        int mes = c.get(Calendar.MONTH);
                        int dia = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePicker = new DatePickerDialog(ConsultasActivity.this, (view1, year, month, dayOfMonth) -> {
                            String dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                            txtNovaData.setText("Data: " + dataSelecionada);
                            txtNovaData.setTag(dataSelecionada);
                        }, ano, mes, dia);
                        datePicker.show();
                    });

                    txtNovaHora.setOnClickListener(view -> {
                        final Calendar c = Calendar.getInstance();
                        int hora = c.get(Calendar.HOUR_OF_DAY);
                        int minuto = c.get(Calendar.MINUTE);

                        TimePickerDialog timePicker = new TimePickerDialog(ConsultasActivity.this, (view12, hourOfDay, minute) -> {
                            String horaSelecionada = String.format("%02d:%02d", hourOfDay, minute);
                            txtNovaHora.setText("Hora: " + horaSelecionada);
                            txtNovaHora.setTag(horaSelecionada);
                        }, hora, minuto, true);
                        timePicker.show();
                    });

                    LinearLayout layoutEdit = new LinearLayout(this);
                    layoutEdit.setOrientation(LinearLayout.VERTICAL);
                    layoutEdit.setPadding(50, 40, 50, 10);
                    layoutEdit.addView(txtNovaData);
                    layoutEdit.addView(txtNovaHora);

                    new AlertDialog.Builder(ConsultasActivity.this)
                            .setTitle("Editar Consulta")
                            .setView(layoutEdit)
                            .setPositiveButton("Salvar", (dialog1, which1) -> {
                                String novaData = (String) txtNovaData.getTag();
                                String novaHora = (String) txtNovaHora.getTag();

                                if (novaData == null || novaHora == null) {
                                    Toast.makeText(ConsultasActivity.this, "Selecione a nova data e hora", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Verificação de data/hora futura
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    sdf.setLenient(false);
                                    Date dataHoraNova = sdf.parse(novaData + " " + novaHora);

                                    Date agora = new Date();
                                    if (dataHoraNova.before(agora)) {
                                        Toast.makeText(ConsultasActivity.this, "A nova data e hora devem ser futuras.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    db.collection("consultas")
                                            .document(cpf)
                                            .collection("agendadas")
                                            .document(docId)
                                            .update("data", novaData, "hora", novaHora)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ConsultasActivity.this, "Consulta atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                                recreate();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ConsultasActivity.this, "Erro ao atualizar", Toast.LENGTH_SHORT).show());
                                } catch (Exception e) {
                                    Toast.makeText(ConsultasActivity.this, "Formato de data ou hora inválido. Use dd/MM/yyyy e HH:mm.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();

                } else if (which == 1) {
                    // EXCLUIR
                    db.collection("consultas")
                            .document(cpf)
                            .collection("agendadas")
                            .document(docId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ConsultasActivity.this, "Consulta excluída", Toast.LENGTH_SHORT).show();
                                recreate();
                            })
                            .addOnFailureListener(e -> Toast.makeText(ConsultasActivity.this, "Erro ao excluir", Toast.LENGTH_SHORT).show());
                }
            });
            builder.show();
        });

        consultasContainer.addView(card);
    }
}
