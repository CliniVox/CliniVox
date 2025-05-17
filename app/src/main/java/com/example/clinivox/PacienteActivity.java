package com.example.clinivox;

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

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        btnFalar = findViewById(R.id.btnFalar);
        btnConsultas = findViewById(R.id.btnConsultas);
        textNome = findViewById(R.id.textNomePaciente);

        cpf = getIntent().getStringExtra("identificador");

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

        });

        carregarDadosPaciente();
    }

    private void carregarDadosPaciente() {
        db.collection("pacientes").document(cpf)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nome = documentSnapshot.getString("nome");
                        textNome.setText("Olá, " + nome + "!");
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