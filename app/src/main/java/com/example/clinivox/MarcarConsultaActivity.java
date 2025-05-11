package com.example.clinivox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class MarcarConsultaActivity extends AppCompatActivity {

    private Spinner spinnerEspecialidade;
    private EditText editData;
    private EditText editHora;
    private Button btnConfirmar;
    private FirebaseFirestore db;
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_consulta);

        spinnerEspecialidade = findViewById(R.id.spinnerEspecialidade);
        editData = findViewById(R.id.editData);
        editHora = findViewById(R.id.editHora);
        btnConfirmar = findViewById(R.id.btnConfirmarConsulta);
        cpf = getIntent().getStringExtra("cpf");  // Obtém o CPF passado
        db = FirebaseFirestore.getInstance();

        // Configura o spinner de especialidades
        String[] especialidades = {"Clínico Geral", "Cardiologia", "Dermatologia", "Pediatria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, especialidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidade.setAdapter(adapter);

        // Setar o clique para o campo de data
        editData.setFocusable(false);  // Impede edição manual
        editData.setOnClickListener(v -> {
            // Obter a data atual
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Exibir o DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                // Setar a data escolhida no campo de texto
                editData.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }, year, month, day);

            // Exibir o diálogo
            datePickerDialog.show();
        });

        // Setar o clique para o campo de hora
        editHora.setFocusable(false);  // Impede edição manual
        editHora.setOnClickListener(v -> {
            // Obter a hora atual
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Exibir o TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                // Setar a hora escolhida no campo de texto
                editHora.setText(String.format("%02d:%02d", hourOfDay, minute1));
            }, hour, minute, true);

            // Exibir o diálogo
            timePickerDialog.show();
        });

        // Ação ao clicar no botão de confirmar
        btnConfirmar.setOnClickListener(v -> {
            String especialidade = spinnerEspecialidade.getSelectedItem().toString();
            String data = editData.getText().toString().trim();
            String hora = editHora.getText().toString().trim();
            String medico = gerarMedicoPorEspecialidade(especialidade);
            String local = "Clínica Central";

            // Validações
            if (data.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validação de data e hora
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setLenient(false);  // Garante que datas inválidas como 31/02/2025 sejam rejeitadas
                Date dataHoraMarcada = sdf.parse(data + " " + hora);

                Date agora = new Date();
                if (dataHoraMarcada.before(agora)) {
                    Toast.makeText(this, "A data e hora devem ser futuras.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cpf == null || cpf.isEmpty()) {
                    Toast.makeText(this, "Erro: CPF não encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Criar o mapa com os dados da consulta
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("especialidade", especialidade);
                consulta.put("data", data);
                consulta.put("hora", hora);
                consulta.put("medico", medico);
                consulta.put("local", local);

                // Salvar no Firestore
                db.collection("consultas")
                        .document(cpf)
                        .collection("agendadas")
                        .add(consulta)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Consulta marcada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Erro ao agendar: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                Toast.makeText(this, "Formato de data ou hora inválido. Use dd/MM/yyyy e HH:mm.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botões de navegação
        ImageView btnVoltar = findViewById(R.id.btnVoltar);
        ImageView btnConfig = findViewById(R.id.btnConfig);

        btnVoltar.setOnClickListener(v -> finish());

        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(MarcarConsultaActivity.this, AjustesActivity.class);
            startActivity(intent);
        });
    }

    private String gerarMedicoPorEspecialidade(String especialidade) {
        switch (especialidade) {
            case "Cardiologia":
                return "Ana Castela";
            case "Dermatologia":
                return "João Antônio";
            default:
                return "Davi Brito";
        }
    }
}
