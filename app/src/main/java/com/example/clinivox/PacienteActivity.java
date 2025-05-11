package com.example.clinivox;

import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//import ai.vapi.sdk.Vapi;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PacienteActivity extends AppCompatActivity {
    TextView textNome;
    private Button btnConsultas;
    String cpf;

    //private Vapi vapi;
    FirebaseFirestore db = FirebaseFirestore.getInstance();  // Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
        //vapi = new Vapi("49ef0e94-198b-4fa0-961f-364461d1cda4"); // Substitua pela sua chave de API do Vapi
        cpf = getIntent().getStringExtra("identificador");

        btnConsultas = findViewById(R.id.btnConsultas);
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


        Button btnFalar = findViewById(R.id.btnFalar); // seu botão
        btnFalar.setOnClickListener(v -> {
           // vapi.start("YOUR_ASSISTANT_ID"); // Substitua pelo ID da sua IA no Vapi
        });

        textNome = findViewById(R.id.textNomePaciente);

        // 🔽 Consulta no Firestore para buscar o nome do paciente
        db.collection("Pacientes").document(cpf)
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