package com.example.clinivox;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecuperarSenhaActivity extends AppCompatActivity {

    EditText editTextIdentificador, editTextNovaSenha;
    Button btnAlterarSenha;
    boolean isMedico;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        editTextIdentificador = findViewById(R.id.editTextIdentificador);
        editTextNovaSenha = findViewById(R.id.editTextNovaSenha);
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha);

        isMedico = getIntent().getBooleanExtra("isMedico", true);

        btnAlterarSenha.setOnClickListener(v -> {
            String id = editTextIdentificador.getText().toString().trim();
            String novaSenha = editTextNovaSenha.getText().toString();

            if (id.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String colecao = isMedico ? "medicos" : "pacientes";

            db.collection(colecao).document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            db.collection(colecao).document(id)
                                    .update("senha", novaSenha)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    });
                        } else {
                            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });

        });
    }
}
