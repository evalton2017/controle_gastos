package br.com.controlecaixa.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import br.com.controlecaixa.MainActivity;
import br.com.controlecaixa.R;
import br.com.controlecaixa.adapter.AdapterMovimentacao;
import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.databinding.ActivityHomeBinding;
import br.com.controlecaixa.model.DadosMovimetacao;
import br.com.controlecaixa.model.Movimentacao;
import br.com.controlecaixa.model.Usuario;
import br.com.controlecaixa.repository.MovimentacaoRepository;
import br.com.controlecaixa.repository.UsuarioRepository;
import br.com.controlecaixa.util.Base64Coder;
import br.com.controlecaixa.util.DateUtil;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityHomeBinding binding;
    private TextView textoHeader, textoSaldo;
    private FirebaseAuth auth;
    private MaterialCalendarView calendarView;
    private Usuario usuario;
    private UsuarioRepository usuarioRepository = new UsuarioRepository();
    private MovimentacaoRepository movimentacaoRepository = new MovimentacaoRepository();
    private AdapterMovimentacao adapterMovimentacao;
    private List<DadosMovimetacao> movimentacao = new ArrayList<>();
    private Movimentacao listMovimentacao = new Movimentacao();
    private String mesAnoSelecionado;
    private Context context;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        calendarView = findViewById(R.id.calendarView);
        textoSaldo = findViewById(R.id.textSaldo);
        textoHeader = findViewById(R.id.textHeader);
        recyclerView = findViewById(R.id.recyclerMovimentos);
        configuraCalendario();
        swipe();

        context= this;
        //Configuração do adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacao, context);
        //Configuração RecyclewView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaDadosUsuario();
    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void recuperarSaldo(){
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        Double resumo = usuario.getReceitaTotal() - usuario.getDespesataTotal();
        String saldoFormatado = decimalFormat.format(resumo);
        textoSaldo.setText("R$: "+ saldoFormatado);
        textoHeader.setText(usuario.getNome());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                auth = FirebaseConfig.getFirebaseAutenticacao();
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesasActivity.class));
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void configuraCalendario(){
        CharSequence meses[] = {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
        calendarView.setTitleMonths(meses);
        CalendarDay dataAtual = calendarView.getCurrentDate();
        mesAnoSelecionado = String.valueOf(dataAtual.getMonth()+""+dataAtual.getYear());
        recuperaDadosMovimentacoes(mesAnoSelecionado);
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mesAnoSelecionado = String.valueOf(date.getMonth()+""+date.getYear());
                filtrarMesSelecionado(mesAnoSelecionado);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filtrarMesSelecionado(String mesAno){
        movimentacao = new ArrayList<>();
        movimentacao = listMovimentacao.getMovimentacao().stream()
                .filter(m -> m.getMesAno().equals(mesAno)).collect(Collectors.toList());
        adapterMovimentacao.updateReceiptsList(movimentacao);
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
                                Log.i("Info", "Recuperando dados do usuario ", task.getException());
                                usuario = document.toObject(Usuario.class);
                                recuperarSaldo();
                            }
                        } else {
                            Log.d("Erro", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void recuperaDadosMovimentacoes(String mesAno) {
        usuario = new Usuario();
        FirebaseAuth auth = FirebaseConfig.getFirebaseAutenticacao();
        List<Movimentacao> movimentacoes = new ArrayList();
        List<HashMap> lista = new ArrayList();
        movimentacaoRepository.getMovimentacoes(Base64Coder.codificarBase64(auth.getCurrentUser().getEmail()))
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                listMovimentacao = document.toObject(Movimentacao.class);
                                movimentacao = listMovimentacao.getMovimentacao().stream()
                                        .filter(m -> m.getMesAno().equals(mesAno)).collect(Collectors.toList());
                                Log.i("Info", "Recuperando dados de mobimentacoes");
                                if(!movimentacao.isEmpty()){
                                    adapterMovimentacao.updateReceiptsList(movimentacao);
                                }
                            }
                        } else {
                            Log.d("Erro", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        //Confirmação de exclusão
        alertDialog.setTitle("Excluir movimentacao");
        alertDialog.setMessage("Tem certeza que deseja excluir movimentacao?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();

                FirebaseAuth auth = FirebaseConfig.getFirebaseAutenticacao();
                String idReferencia = Base64Coder.codificarBase64(auth.getCurrentUser().getEmail());
                DadosMovimetacao objMovimetacao = new DadosMovimetacao();
                try{
                    movimentacaoRepository = new MovimentacaoRepository();
                    objMovimetacao = movimentacao.get(position);
                    if(objMovimetacao != null){
                        if(validarExclusao(objMovimetacao)){
                            if(objMovimetacao.getTipo().equals("D")){
                                usuario.setDespesataTotal(usuario.getDespesataTotal()-objMovimetacao.getValor());
                            }else{
                                usuario.setReceitaTotal(usuario.getReceitaTotal()-objMovimetacao.getValor());
                            }
                            Boolean resposta = movimentacaoRepository.excluir(objMovimetacao, idReferencia);
                            if(resposta){
                                usuarioRepository.salvar(usuario.getIdUsuario(), usuario);
                                recuperaDadosUsuario();
                                recuperaDadosMovimentacoes(mesAnoSelecionado);
                                Toast.makeText(context, "Dados atualizados.", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(context, "Somente lançamentos do dia poderão ser excluídos.", Toast.LENGTH_LONG).show();
                            adapterMovimentacao.notifyDataSetChanged();
                        }

                    }

                }catch (Exception e){
                    Toast.makeText(context, "Erro ao cadastrar despesas.", Toast.LENGTH_LONG).show();
                }

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(context, "Solicitacao cancelada.", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public Boolean validarExclusao(DadosMovimetacao movimetacao){
        String hoje = DateUtil.dataAtual();
        if(hoje.equals(movimetacao.getData())){
            return true;
        }
        return false;
    }


}