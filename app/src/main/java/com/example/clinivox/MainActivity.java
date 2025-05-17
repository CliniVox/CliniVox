package com.example.clinivox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText editTextCrmOuCpf, editTextSenha;
    CheckBox checkboxManterConectado;
    Button btnEntrar, btnSouMedico, btnSouPaciente;
    boolean isMedicoSelecionado = true;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

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
            String identificador = editTextCrmOuCpf.getText().toString().trim();
            String senha = editTextSenha.getText().toString();

            if (identificador.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            } else {
                verificarUsuarioNoFirestore(identificador, senha);
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

    // Verifica se o usuário existe no Firestore
    private void verificarUsuarioNoFirestore(String identificador, String senhaDigitada) {
        String colecao = isMedicoSelecionado ? "medicos" : "pacientes"; // nomes em minúsculo (Firestore padrão)

        db.collection(colecao)
                .document(identificador) // ID é CPF ou CRM
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String senhaSalva = documentSnapshot.getString("senha");

                        if (senhaSalva != null && senhaSalva.equals(senhaDigitada)) {
                            Intent intent = new Intent(this,
                                    isMedicoSelecionado ? MedicoActivity.class : PacienteActivity.class);
                            intent.putExtra("identificador", identificador);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
