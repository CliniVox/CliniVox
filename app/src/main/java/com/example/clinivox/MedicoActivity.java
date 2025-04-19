package com.example.clinivox;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MedicoActivity extends AppCompatActivity {
    TextView tvNome, tvConsultasHoje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);

        tvNome = findViewById(R.id.tvNomeMedico);
        tvConsultasHoje = findViewById(R.id.tvConsultasHoje);

        String nome = getIntent().getStringExtra("nome");
        tvNome.setText("Dr. " + nome);

        // Exemplo: simula consultas do dia
        int consultasHoje = 5; // isso pode vir de um banco depois
        tvConsultasHoje.setText(" Consultas Hoje: " + consultasHoje);


    }

}

