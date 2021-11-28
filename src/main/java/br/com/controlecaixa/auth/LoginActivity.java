package br.com.controlecaixa.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.controlecaixa.R;
import br.com.controlecaixa.activity.HomeActivity;
import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button btnLogar;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setTitle("Login");

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

        String textEmail = campoEmail.getText().toString();
        String textSenha = campoSenha.getText().toString();
        btnLogar = findViewById(R.id.btnLogar);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                //Valida os dados
                if (textEmail.isEmpty() || textSenha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Todos os campos são obrigatorios.", Toast.LENGTH_LONG).show();
                } else {
                    usuario = new Usuario();
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    logar(usuario);
                }

            }
        });
    }

    private void logar(Usuario usuario){
            auth = FirebaseConfig.getFirebaseAutenticacao();
            auth.signInWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        viewHome();
                    }else{
                        String excecao = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            excecao = "USuario ou senha invalido";
                        }catch(FirebaseAuthInvalidUserException e){
                            excecao = "Usuario não cadastrado";
                        }
                        catch(Exception e){
                            excecao = "Erro ao cadastrar usuario: "+e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public void viewHome(){
        startActivity(new Intent(this, HomeActivity.class));
    }
}