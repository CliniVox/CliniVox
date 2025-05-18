package com.example.clinivox;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class CadastroActivity extends AppCompatActivity {

    RadioGroup radioGroupTipo;
    EditText editTextNome, editTextSenha, editTextIdentificador, editTextEspecialidade;
    Button btnCadastrar;
    LinearLayout layoutEspecialidade;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isMedico = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        layoutEspecialidade = findViewById(R.id.layoutEspecialidade);
        radioGroupTipo = findViewById(R.id.radioGroupTipo);
        editTextNome = findViewById(R.id.editTextNome);
        editTextSenha = findViewById(R.id.editTextSenha);
        editTextIdentificador = findViewById(R.id.editTextIdentificador);
        editTextEspecialidade = findViewById(R.id.editTextEspecialidade);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        radioGroupTipo.setOnCheckedChangeListener((group, checkedId) -> {
            isMedico = checkedId == R.id.radioMedico;
            editTextIdentificador.setHint(isMedico ? "CRM" : "CPF");
            layoutEspecialidade.setVisibility(isMedico ? View.VISIBLE : View.GONE);
        });

        btnCadastrar.setOnClickListener(v -> {
            String nome = editTextNome.getText().toString().trim();
            String senha = editTextSenha.getText().toString().trim();
            String identificador = editTextIdentificador.getText().toString().trim();
            String especialidade = editTextEspecialidade.getText().toString().trim();

            if (nome.isEmpty() || senha.isEmpty() || identificador.isEmpty() || (isMedico && especialidade.isEmpty())) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> usuario = new HashMap<>();
            usuario.put("nome", nome);
            usuario.put("senha", senha);
            if (isMedico) usuario.put("especialidade", especialidade);
            usuario.put(isMedico ? "crm" : "cpf", identificador);


            String colecao = isMedico ? "medicos" : "pacientes";

            db.collection(colecao).document(identificador)
                    .set(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        });
    }
}
