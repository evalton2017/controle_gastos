package br.com.controlecaixa.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.controlecaixa.confg.FirebaseConfig;
import br.com.controlecaixa.model.Usuario;

public class UsuarioRepository {

    private FirebaseFirestore db = FirebaseConfig.getFirebaseData();
    private DocumentReference docRef;

    public void salvar(String id, Usuario usuario){
        FirebaseFirestore db = FirebaseConfig.getFirebaseData();
        db.collection("usuarios").document(id)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Sucesso", "Usuario cadastrado com sucesso!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Errro", "Erro ao cadastrar usuario", e);
                    }
                });
    }

    public Task<DocumentSnapshot> recuperarReceitaDespesa(String idDoc){
        docRef = db.collection("usuarios").document(idDoc);
        final Usuario[] usuario = {new Usuario()};
        return docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Sucesso", "Retornou os dados do documento ", task.getException());
                    }
                } else {
                    Log.d("Erro", "get failed with ", task.getException());
                }
            }
        });
    }

}
