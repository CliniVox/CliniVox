package com.example.clinivox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editTextCrmOuCpf, editTextSenha;
    CheckBox checkboxManterConectado;
    Button btnEntrar, btnSouMedico, btnSouPaciente;
    boolean isMedicoSelecionado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCrmOuCpf = findViewById(R.id.editTextCrmOuCpf);
        editTextSenha = findViewById(R.id.editTextSenha);
        checkboxManterConectado = findViewById(R.id.checkboxManterConectado);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnSouMedico = findViewById(R.id.btnSouMedico);
        btnSouPaciente = findViewById(R.id.btnSouPaciente);

        btnEntrar.setOnClickListener(v -> {
            String identificador = editTextCrmOuCpf.getText().toString();
            String senha = editTextSenha.getText().toString();

            if (identificador.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent;
                if (isMedicoSelecionado) {
                    intent = new Intent(this, MedicoActivity.class);
                } else {
                    intent = new Intent(this, PacienteActivity.class);
                }
                startActivity(intent);
                finish(); // Opcional: fecha a tela de login
            }
        });


        btnSouMedico.setOnClickListener(v -> {
            isMedicoSelecionado = true;
            editTextCrmOuCpf.setHint("CRM");

            btnSouMedico.setBackgroundResource(R.drawable.botao_selecionado);
            btnSouPaciente.setBackgroundResource(R.drawable.botao_desmarcado);
        });

        btnSouPaciente.setOnClickListener(v -> {
            isMedicoSelecionado = false;
            editTextCrmOuCpf.setHint("CPF");

            btnSouPaciente.setBackgroundResource(R.drawable.botao_selecionado);
            btnSouMedico.setBackgroundResource(R.drawable.botao_desmarcado);
        });
    }
}
