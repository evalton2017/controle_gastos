package br.com.controlecaixa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import br.com.controlecaixa.activity.HomeActivity;
import br.com.controlecaixa.auth.CadastrarActivity;
import br.com.controlecaixa.auth.LoginActivity;
import br.com.controlecaixa.confg.FirebaseConfig;

public class MainActivity extends IntroActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .fragment(R.layout.slide_01)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .fragment(R.layout.slide_02)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .fragment(R.layout.slide_03)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.holo_blue_bright)
                .fragment(R.layout.slide_cadastro)
                .canGoBackward(false)
                .canGoForward(false)
                .build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaUsuarioLogado();
    }

    public void btLogar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void btCadastrar(View view){
        startActivity(new Intent(this, CadastrarActivity.class));
    }

    public void verificaUsuarioLogado(){
        auth = FirebaseConfig.getFirebaseAutenticacao();
        //auth.signOut();
        if(auth.getCurrentUser() != null){
            viewHome();
        }
    }

    public void viewHome(){
        startActivity(new Intent(this, HomeActivity.class));
    }
}