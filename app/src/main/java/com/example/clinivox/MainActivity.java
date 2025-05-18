package com.example.clinivox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText editTextCrmOuCpf, editTextSenha;
    //CheckBox checkboxManterConectado;
    Button btnEntrar, btnSouMedico, btnSouPaciente;
    boolean isMedicoSelecionado = true;

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Apenas para garantir que tudo seja limpo enquanto a função 'manter conectado' está desativada
        getSharedPreferences("loginPrefs", MODE_PRIVATE).edit().clear().apply();

        // SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        // boolean manterConectado = preferences.getBoolean("manterConectado", false);
        // Log.d("MainActivity", "manterConectado: " + manterConectado);
        // String identificadorSalvo = preferences.getString("identificador", null);
        // Log.d("MainActivity", "identificadorSalvo: " + identificadorSalvo);

        //checkboxManterConectado = findViewById(R.id.checkboxManterConectado);
        //checkboxManterConectado.setChecked(false);  // Força desmarcar sempre que abrir

        // if (manterConectado) {
        //     boolean isMedico = preferences.getBoolean("isMedico", true);
        //     if (identificadorSalvo != null) {
        //         Intent intent = new Intent(this, isMedico ? MedicoActivity.class : PacienteActivity.class);
        //         intent.putExtra("identificador", identificadorSalvo);
        //         startActivity(intent);
        //         finish();
        //         return;
        //     }
        // }

        TextView textViewEsqueceuSenha = findViewById(R.id.textViewEsqueceuSenha);
        TextView textViewCriarConta = findViewById(R.id.textViewCriarConta);

        textViewEsqueceuSenha.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecuperarSenhaActivity.class);
            intent.putExtra("isMedico", isMedicoSelecionado);
            startActivity(intent);
        });

        textViewCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            startActivity(intent);
        });

        editTextCrmOuCpf = findViewById(R.id.editTextCrmOuCpf);
        editTextSenha = findViewById(R.id.editTextSenha);
        //checkboxManterConectado = findViewById(R.id.checkboxManterConectado);
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

    private void verificarUsuarioNoFirestore(String identificador, String senhaDigitada) {
        String colecao = isMedicoSelecionado ? "medicos" : "pacientes";

        db.collection(colecao)
                .document(identificador)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String senhaSalva = documentSnapshot.getString("senha");

                        if (senhaSalva != null && senhaSalva.equals(senhaDigitada)) {
                            Intent intent = new Intent(this,
                                    isMedicoSelecionado ? MedicoActivity.class : PacienteActivity.class);
                            intent.putExtra("identificador", identificador);

                            // if (checkboxManterConectado.isChecked()) {
                            //     SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                            //     editor.putBoolean("manterConectado", true);
                            //     editor.putString("identificador", identificador);
                            //     editor.putBoolean("isMedico", isMedicoSelecionado);
                            //     editor.apply();
                            // }

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
