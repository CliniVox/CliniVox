package com.example.clinivox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
            Intent intent = new Intent(this, SobreNosActivity.class);
            startActivity(intent);
        });


        // Botão Sair
        findViewById(R.id.btnSair).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class); // ou SplashActivity, dependendo do seu fluxo
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
