package br.com.controlecaixa.confg;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConfig {

    private static FirebaseAuth autenticacao;
    private static FirebaseFirestore databaseReference;

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
       return autenticacao;
    }

    //Retorna a instancia do FirebaseDatabase
    public static FirebaseFirestore getFirebaseData(){
        if(databaseReference == null){
            databaseReference = FirebaseFirestore.getInstance();
            //FirebaseDatabase.getInstance("https://vax-in-60807-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        }
        return databaseReference;
    }



}
