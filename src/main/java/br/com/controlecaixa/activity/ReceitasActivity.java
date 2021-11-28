package br.com.controlecaixa.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.controlecaixa.R;
import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.model.DadosMovimetacao;
import br.com.controlecaixa.model.Movimentacao;
import br.com.controlecaixa.model.Usuario;
import br.com.controlecaixa.repository.MovimentacaoRepository;
import br.com.controlecaixa.repository.UsuarioRepository;
import br.com.controlecaixa.util.Base64Coder;
import br.com.controlecaixa.util.DateUtil;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoCategoria, campoData, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DadosMovimetacao dadosMovimetacao;
    private MovimentacaoRepository movimentacaoRepository;
    private UsuarioRepository usuarioRepository = new UsuarioRepository();
    private Double receitaTotal;
    private Double receitaAdicionada;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);

        //Seta data atual
        campoData.setText(DateUtil.dataAtual());
        //Recupera a despesaTotal
        recuperaDadosUsuario();
    }

    public void salvarReceita(View view) {
        FirebaseAuth auth = FirebaseConfig.getFirebaseAutenticacao();
        String idReferencia = Base64Coder.codificarBase64(auth.getCurrentUser().getEmail());
        dadosMovimetacao = new DadosMovimetacao();
        movimentacao = new Movimentacao();
        if (validaDados()) {
            try{
                movimentacaoRepository = new MovimentacaoRepository();
                receitaAdicionada = Double.parseDouble(campoValor.getText().toString());
                dadosMovimetacao.setValor(Double.parseDouble(campoValor.getText().toString()));
                dadosMovimetacao.setCategoria(campoCategoria.getText().toString());
                dadosMovimetacao.setDescricao(campoDescricao.getText().toString());
                dadosMovimetacao.setData(campoData.getText().toString());
                dadosMovimetacao.setTipo("R");
                dadosMovimetacao.setMesAno(DateUtil.mesAno(campoData.getText().toString()));
                movimentacao.getMovimentacao().add(dadosMovimetacao);
                Boolean resposta = movimentacaoRepository.merge(dadosMovimetacao, idReferencia);
                if(resposta){
                    usuario.setReceitaTotal(receitaTotal+receitaAdicionada);
                    usuarioRepository.salvar(usuario.getIdUsuario(), usuario);
                    Toast.makeText(this, "Receita cadastrada com sucesso.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }else{
                    Toast.makeText(this, "Erro ao cadastrar despesas.", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                Toast.makeText(this, "Erro ao cadastrar despesas.", Toast.LENGTH_LONG).show();
            }

        }

    }

    public Boolean validaDados() {
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();
        if (textoValor.isEmpty() || textoData.isEmpty() || textoCategoria.isEmpty() || textoDescricao.isEmpty()) {
            Toast.makeText(this, "Todos os campos são obrigatorios.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    public void recuperaDadosUsuario() {
        usuario = new Usuario();
        FirebaseAuth auth = FirebaseConfig.getFirebaseAutenticacao();
        usuarioRepository.recuperarReceitaDespesa(Base64Coder.codificarBase64(auth.getCurrentUser().getEmail()))
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                usuario = document.toObject(Usuario.class);
                                receitaTotal = usuario.getReceitaTotal();
                            }
                        } else {
                            Log.d("Erro", "falha ao recuperar usuario", task.getException());
                        }
                    }
                });
    }
}