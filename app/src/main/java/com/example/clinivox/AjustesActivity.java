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

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        findViewById(R.id.btnSobreNos).setOnClickListener(v -> {
            Intent intent = new Intent(this, SobreNosActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btnSair).setOnClickListener(v -> {
            // Comentado: limpar preferências de manter conectado
            // getSharedPreferences("loginPrefs", MODE_PRIVATE)
            //         .edit()
            //         .clear()
            //         .apply();

            Toast.makeText(this, "Você saiu da conta", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}
