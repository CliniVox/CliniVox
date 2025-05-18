package com.example.clinivox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class MarcarConsultaActivity extends AppCompatActivity {

    private Spinner spinnerEspecialidade;
    private EditText editData;
    private EditText editHora;
    private Button btnConfirmar;
    private FirebaseFirestore db;
    private String cpfPaciente;
    private Spinner spinnerLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcar_consulta);
        spinnerLocal = findViewById(R.id.spinnerLocal);
        String[] locais = {
                "Hospital São Lucas",
                "Hospital Vida e Saúde",
                "Clínica Central",
                "Unidade Vila Mariana",
                "Hospital Coração Amigo"
        };
        ArrayAdapter<String> localAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        localAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(localAdapter);
        spinnerEspecialidade = findViewById(R.id.spinnerEspecialidade);
        editData = findViewById(R.id.editData);
        editHora = findViewById(R.id.editHora);
        btnConfirmar = findViewById(R.id.btnConfirmarConsulta);
        cpfPaciente = getIntent().getStringExtra("cpf");  // Obtém o CPF passado
        db = FirebaseFirestore.getInstance();

        // Configura o spinner de especialidades
        String[] especialidades = {"Clínico Geral", "Cardiologia", "Dermatologia", "Pediatria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, especialidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecialidade.setAdapter(adapter);

        // DatePicker
        editData.setFocusable(false);
        editData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                editData.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
            }, y, m, d).show();
        });

        // TimePicker
        editHora.setFocusable(false);
        editHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                editHora.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, h, min, true).show();
        });

        // Confirmar consulta
        btnConfirmar.setOnClickListener(v -> {
            String especialidade = spinnerEspecialidade.getSelectedItem().toString();
            String data = editData.getText().toString().trim();
            String hora = editHora.getText().toString().trim();
            String local = spinnerLocal.getSelectedItem().toString();
            String crmMedico = gerarCrmPorEspecialidade(especialidade);

            // Validações
            if (data.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setLenient(false);
                Date dataHoraMarcada = sdf.parse(data + " " + hora);
                Date agora = new Date();

                if (dataHoraMarcada.before(agora)) {
                    Toast.makeText(this, "A data e hora devem ser futuras.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cpfPaciente == null || cpfPaciente.isEmpty()) {
                    Toast.makeText(this, "Erro: CPF não encontrado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Montar mapa consulta com nova estrutura
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("cpf_paciente", cpfPaciente);
                consulta.put("especialidade", especialidade);
                consulta.put("data", data);
                consulta.put("hora", hora);
                consulta.put("local", local);
                consulta.put("crm_medico", crmMedico);

                // Salvar na coleção "consultas" com ID auto gerado
                db.collection("consultas")
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
        /*ImageView btnVoltar = findViewById(R.id.btnVoltar);
        ImageView btnConfig = findViewById(R.id.btnConfig);

        btnVoltar.setOnClickListener(v -> finish());

        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(MarcarConsultaActivity.this, AjustesActivity.class);
            startActivity(intent);
        });
*/
    }
    private String gerarCrmPorEspecialidade(String especialidade) {
        switch (especialidade) {
            case "Cardiologia":
                return "CRM12345";  // CRM do Dr. Carlos
            case "Dermatologia":
                return "CRM67890";  // Exemplo de outro CRM
            case "Pediatria":
                return "CRM54321";  // Exemplo
            case "Clínico Geral":
            default:
                return "CRM00000";  // CRM genérico ou padrão
        }
    }
}
