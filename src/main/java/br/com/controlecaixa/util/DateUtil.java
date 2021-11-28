package br.com.controlecaixa.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String mesAno(String data){
        String dataReferencia[] = data.split("/");
        return dataReferencia[1]+dataReferencia[2];
    }

    public static List<String> getChavesMes(){
        Integer c =1;
        String data = dataAtual();
        String dataReferencia[] = data.split("/");
        List<String> chaves = new ArrayList<>();
        while(c<13){
            chaves.add(c.toString()+dataReferencia[2].toString());
            c++;
        }
        return chaves;
    }
}
