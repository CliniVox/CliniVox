package com.example.clinivox;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class PacienteActivity extends AppCompatActivity{
    TextView textNome;
    private Button btnConsultas;
    String cpf;
    private Button btnFalar;
    private VapiHelper vapiHelper;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnDesligar;
    private boolean isAssistantActive = false;
    private String nomePaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        vapiHelper = new VapiHelper(this, getLifecycle());
        btnFalar = findViewById(R.id.btnFalar);
        btnConsultas = findViewById(R.id.btnConsultas);
        textNome = findViewById(R.id.textNomePaciente);
        btnDesligar = findViewById(R.id.btnDesligar);
        btnDesligar.setVisibility(View.GONE);


        cpf = getIntent().getStringExtra("identificador");

        carregarDadosPaciente();

        btnConsultas.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConsultasActivity.class);
            intent.putExtra("cpf", cpf);
            startActivity(intent);
        });

        ImageButton btnConfig = findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, AjustesActivity.class);
            startActivity(intent);
        });

        btnFalar.setOnClickListener(v -> {
            vapiHelper.startAssistant(
                    "a9cd3f10-031a-4dd5-b7f5-dc27ccc0bbd7",
                    nomePaciente,
                    new OnAssistantSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(PacienteActivity.this, "Assistente iniciado com sucesso", Toast.LENGTH_SHORT).show();
                            isAssistantActive = true;
                            btnDesligar.setVisibility(View.VISIBLE);
                            btnFalar.setVisibility(View.GONE);
                        }

                    },
                    new OnAssistantFailure() {
                        @Override
                        public void onFailure(Throwable error) {
                            Toast.makeText(PacienteActivity.this, "Erro ao iniciar assistente: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });


        btnDesligar.setOnClickListener(v -> {

            vapiHelper.stopAssistant();
            Toast.makeText(PacienteActivity.this, "Assistente desligado", Toast.LENGTH_SHORT).show();
            isAssistantActive = false;
            btnDesligar.setVisibility(View.GONE);
            btnFalar.setVisibility(View.VISIBLE);
        });

    }

    private void carregarDadosPaciente() {
        db.collection("pacientes").document(cpf)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        textNome.setText("Olá, " + nome + "!");
                        nomePaciente = documentSnapshot.getString("nome");
                    } else {
                        textNome.setText("Olá, paciente!");
                        Toast.makeText(this, "Paciente não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    textNome.setText("Olá!");
                    Toast.makeText(this, "Erro ao buscar dados do paciente", Toast.LENGTH_SHORT).show();
                });
    }


}