package com.example.userasus.http_uceva;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userasus.http_uceva.datos.Conexion;


public class EliminarFeed extends AppCompatActivity {

    EliminarFeedTask eliminar=null;
    SQLiteDatabase dbDrop;
    Conexion con;
    boolean tareaEliminar=false;
    TextView tvDatosXeliminar;
    //Datos consulta;
    String dConsultados="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_feed);
        con = new Conexion(this,"http",null,1);
        //consulta = new Datos();
        dbDrop = con.getReadableDatabase();
        eliminar = new EliminarFeedTask();
        eliminar.execute();
        tareaEliminar=true;
        tvDatosXeliminar = (TextView)findViewById(R.id.tvDatosXeliminar);
        //dConsultados=getIntent().getStringExtra("consulta").toString().trim();
    }

    public void volver2(View v){
        Intent back = new Intent(EliminarFeed.this,MainActivity.class);
        if(tareaEliminar){
            eliminar.cancel(true);
        }
        startActivity(back.addFlags(back.FLAG_ACTIVITY_CLEAR_TOP | back.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tareaEliminar){
            eliminar.cancel(true);
        }
    }

    public class EliminarFeedTask extends AsyncTask<Void,String,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //String datos =consulta.getResultado();
            publishProgress(dConsultados);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            tvDatosXeliminar.setText(values[0]);
            Toast.makeText(getApplicationContext(),values[0],Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            eliminar=null;
            tareaEliminar=false;
        }
    }
}
