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
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.controlecaixa.R;
import br.com.controlecaixa.activity.HomeActivity;
import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.model.Usuario;
import br.com.controlecaixa.repository.UsuarioRepository;
import br.com.controlecaixa.util.Base64Coder;

public class CadastrarActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btnCadastrar;
    private FirebaseAuth auth;
    private Usuario usuario;
    private UsuarioRepository repository = new UsuarioRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        getSupportActionBar().setTitle("Cadastro");

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNome = campoNome.getText().toString();
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                //Valida os dados
                if (textNome.isEmpty() || textEmail.isEmpty() || textSenha.isEmpty()) {
                    Toast.makeText(CadastrarActivity.this, "Todos os campos são obrigatorios.", Toast.LENGTH_LONG).show();
                } else {
                    usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    cadastrarUsuario(usuario);
                }

            }
        });
    }

    private void cadastrarUsuario(Usuario usuario) {
        auth = FirebaseConfig.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String idUsuario = Base64Coder.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    repository.salvar(idUsuario, usuario);
                    finish();
                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um email valido";
                    }
                    catch(FirebaseAuthUserCollisionException e){
                        excecao = "Email já cadastrado";
                    }
                    catch(Exception e){
                        excecao = "Erro ao cadastrar usuario: "+e.getMessage();
                        e.printStackTrace();
                    }
                Toast.makeText(CadastrarActivity.this, excecao, Toast.LENGTH_LONG).show();
            }
        }
    });
    }
    public void viewLogin(){
        startActivity(new Intent(this, LoginActivity.class));
    }

}