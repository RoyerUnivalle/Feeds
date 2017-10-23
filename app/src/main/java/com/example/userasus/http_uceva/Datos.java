package com.example.userasus.http_uceva;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userasus.http_uceva.datos.Conexion;
import com.example.userasus.http_uceva.datos.ContractFeed;

public class Datos extends AppCompatActivity {

    ConsultarFeeds consulta=null;
    SQLiteDatabase  dbConsulta;
    Conexion con;
    TextView datos;
    boolean tarea=false;
    public String resultado="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        con = new Conexion(this,"http",null,1);
        dbConsulta = con.getReadableDatabase();
        datos = (TextView)findViewById(R.id.tvDatos);
        consulta = new ConsultarFeeds();
        consulta.execute();
        tarea=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tarea){
            consulta.cancel(true);
        }
    }

    public void volver(View v){
        Intent back = new Intent(Datos.this,MainActivity.class);
        if (tarea){
            consulta.cancel(true);
        }
        Bundle datos = new Bundle();
        datos.putString("datos",resultado);
        back.putExtras(datos);
        startActivity(back.addFlags(back.FLAG_ACTIVITY_CLEAR_TOP | back.FLAG_ACTIVITY_SINGLE_TOP));
    }

    public class ConsultarFeeds extends AsyncTask<Void,String,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dbConsulta.beginTransaction();
            Cursor datos = dbConsulta.query(ContractFeed.FeedEnrty.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,null,null);
            while (datos.moveToNext()){
                if (isCancelled()){
                    break;
                }else{
                    int feed_id = datos.getInt(datos.getColumnIndex(ContractFeed.FeedEnrty.FEED_ID));
                    String feed = datos.getString(datos.getColumnIndexOrThrow(ContractFeed.FeedEnrty.FEED));
                    resultado += feed_id+" - "+feed;
                    resultado += "\n------\n";
                }
            }
            publishProgress(resultado);
            dbConsulta.endTransaction();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            datos.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setResultado(resultado);
            Toast.makeText(getApplicationContext(),"Consulta realizada con exito",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            consulta=null;
            tarea=false;
        }
    }

    public String getResultado() {
        return resultado;
    }
    public void  setResultado(String result){
        resultado=result;
    }
}
