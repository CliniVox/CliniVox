package com.example.clinivox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PacienteActivity extends AppCompatActivity {
    TextView textNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        // Botão de configurações
        ImageButton btnConfig = findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(v -> {
            Intent intent = new Intent(this, AjustesActivity.class);
            startActivity(intent);
        });

        textNome = findViewById(R.id.textNomePaciente);

        String nome = getIntent().getStringExtra("nome");
        textNome.setText("Olá, " + nome + "!");
    }
}
