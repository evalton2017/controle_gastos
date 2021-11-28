package br.com.controlecaixa.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;

import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.model.DadosMovimetacao;
import br.com.controlecaixa.model.Movimentacao;
import br.com.controlecaixa.model.Usuario;

public class MovimentacaoRepository {

    FirebaseFirestore db = FirebaseConfig.getFirebaseData();
    private DocumentReference docRef;

    public void salvar(Movimentacao movimentacao, String id) {
        db.collection("movimentacao").document(id)
                .set(movimentacao)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Sucesso", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Errro", "Error writing document", e);
                    }
                });
    }

    public void atualizar(Movimentacao movimentacao, String id) {
        db.collection("movimentacao").document(id)
                .set(movimentacao, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Sucesso", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Errro", "Error writing document", e);
                    }
                });
    }


    public Boolean merge(DadosMovimetacao movimentacao, String id) {
        try{
            DocumentReference movimentacaoRef =  db.collection("movimentacao").document(id);
            movimentacaoRef.update("movimentacao", FieldValue.arrayUnion(movimentacao));
            return true;
        }catch (Exception e){
            Log.w("Erro", e.getLocalizedMessage());
            return false;
        }

    }

    public Boolean excluir(DadosMovimetacao movimentacao, String id) {
        try{
            DocumentReference movimentacaoRef =  db.collection("movimentacao").document(id);
            movimentacaoRef.update("movimentacao", FieldValue.arrayRemove(movimentacao));
            return true;
        }catch (Exception e){
            Log.w("Erro", e.getLocalizedMessage());
            return false;
        }

    }

    public Task<DocumentSnapshot> getMovimentacoes(String idDoc){
        docRef = db.collection("movimentacao").document(idDoc);
        final Usuario[] usuario = {new Usuario()};
        return docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Sucesso", "Retornou as movimentações ", task.getException());
                    }
                } else {
                    Log.d("Erro", "falha ao buscar as movimentações ", task.getException());
                }
            }
        });
    }

}
