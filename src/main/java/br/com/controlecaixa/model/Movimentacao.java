package br.com.controlecaixa.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;


public class Movimentacao {

    List<DadosMovimetacao> movimentacao = new ArrayList<>();

    public Movimentacao() {
    }

    public List<DadosMovimetacao> getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(List<DadosMovimetacao> movimentacao) {
        this.movimentacao = movimentacao;
    }
}
