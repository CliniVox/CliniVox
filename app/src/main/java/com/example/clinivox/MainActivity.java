package com.example.clinivox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    EditText editTextCrmOuCpf, editTextSenha;
    CheckBox checkboxManterConectado;
    Button btnEntrar, btnSouMedico, btnSouPaciente;
    boolean isMedicoSelecionado = true;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Instância do Firestore

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
                // Aqui você verifica no Firestore se o CRM ou CPF já existe
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

    // Função para verificar se o usuário existe no Firestore
    private void verificarUsuarioNoFirestore(String identificador, String senha) {
        String colecao = isMedicoSelecionado ? "Medicos" : "Pacientes"; // Definir a coleção com base na escolha do usuário

        db.collection(colecao)
                .document(identificador) // Usando CRM ou CPF como ID do documento
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Usuário encontrado, verificar a senha (opcional se for Firebase Auth)
                        String senhaSalva = documentSnapshot.getString("senha");

                        if (senha.equals(senhaSalva)) {
                            // Login bem-sucedido
                            Intent intent;
                            if (isMedicoSelecionado) {
                                intent = new Intent(this, MedicoActivity.class);
                            } else {
                                intent = new Intent(this, PacienteActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Usuário não encontrado
                        Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao verificar usuário", Toast.LENGTH_SHORT).show());
    }
}
