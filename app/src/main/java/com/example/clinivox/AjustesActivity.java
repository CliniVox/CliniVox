package com.example.clinivox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AjustesActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // Botão voltar
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Botão Sobre Nós
        findViewById(R.id.btnSobreNos).setOnClickListener(v -> {
            Toast.makeText(this, "Sobre nós ainda não implementado!", Toast.LENGTH_SHORT).show();
        });

        // Botão Sair
        findViewById(R.id.btnSair).setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class); // ou Splash/Login
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
