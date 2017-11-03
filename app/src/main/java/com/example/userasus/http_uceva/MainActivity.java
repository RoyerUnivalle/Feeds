package com.example.userasus.http_uceva;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.userasus.http_uceva.datos.Conexion;
import com.example.userasus.http_uceva.datos.ContractFeed;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.BufferUnderflowException;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    Button btnClear,btnSicn,BtnStop,btnVerFeed;
    TextView resultado;
    String datosConsultados="";

    Sincronizar sicn=null;
    boolean flagSinc = false;
    String URLConnect="http://ep00.epimg.net/rss/elpais/portada.xml";

    //---
    ProgressDialog progreso;
    //https://developer.android.com/guide/topics/ui/dialogs.html?hl=es-419
    //---

    //FIREBASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Para leer y escribir en la base de datos, necesitas una instancia de DatabaseReference:
    DatabaseReference myRef = database.getReference();
    //FIREBASE

    //VOLLEY
    RequestQueue queue;
    String url ="http://www.google.com";
    //VOLLEY


    Conexion con;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClear = (Button) findViewById(R.id.bntClear);
        btnClear.setOnClickListener(this);

/*        btnSicn = (Button) findViewById(R.id.btnSicn);
        btnSicn.setOnClickListener(this);

        BtnStop = (Button) findViewById(R.id.btnStop);
        BtnStop.setOnClickListener(this);*/

        btnVerFeed =(Button)findViewById(R.id.btnVerFeed);
        btnVerFeed.setOnClickListener(this);



        resultado = (TextView) findViewById(R.id.tvResultado);

        con = new Conexion(this,"http",null,1);
        db = con.getWritableDatabase();
        resultado.setMovementMethod(LinkMovementMethod.getInstance());


        //VOLLEY
        queue = Volley.newRequestQueue(this);
        //VOLLEY


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bntClear:
                //hacemos algo
                limpiar();
                break;
            case R.id.btnSicn:
                //Toast.makeText(this,"Iniciando",Toast.LENGTH_SHORT).show();
                sicn = new Sincronizar();
                flagSinc=true;
                sicn.execute();
                break;
            case R.id.btnStop:
                //Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();
                if(flagSinc){
                    sicn.cancel(true);
                }
                break;
            case R.id.btnVerFeed:
                Intent consulta = new Intent(MainActivity.this,Datos.class);
                startActivity(consulta.addFlags(consulta.FLAG_ACTIVITY_CLEAR_TOP | consulta.FLAG_ACTIVITY_SINGLE_TOP));
                break;
            case R.id.btnVolley:
                ProbarVolley();
                break;
            default:
                break;
        }
    }

    public void irEliminar(View v){
        Intent drop = new Intent(MainActivity.this,EliminarFeed.class);
        String datos = getDatosConsultados();
        /*Bundle parametros = new Bundle();
        if(datos.length()>0){
            parametros.putString("consulta",datos);
            drop.putExtras(parametros);
        }else{
            parametros.putString("consulta","ningun dato");
            drop.putExtras(parametros);
        }*/
        startActivity(drop.addFlags(drop.FLAG_ACTIVITY_CLEAR_TOP | drop.FLAG_ACTIVITY_SINGLE_TOP));
    }

    public String getDatosConsultados() {
        return datosConsultados;
    }

    public void setDatosConsultados(String datosConsultados) {
        this.datosConsultados = datosConsultados;
    }

    @Override
    public  void onResume(){
        super.onResume();
        //Toast.makeText(this,"Hola resumen",Toast.LENGTH_SHORT).show();
        /*if((getIntent().getStringExtra("datos").toString().trim()).length()>0){
            setDatosConsultados(getIntent().getStringExtra("datos").toString().trim());
        }*/
        /*if(retorno.length()>=0 || retorno!=""){
            setDatosConsultados(retorno);
        }*/
    }

    public void ejecutar(View v){
        boolean isConnected=false;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork!=null){
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        ///https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html#DetermineType
        if(activeNetwork!=null && isConnected){
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            //Toast.makeText(this,"Iniciando : "+isConnected+" wifi:"+isWiFi+" movil: "+isMobile,Toast.LENGTH_SHORT).show();

            if(isMobile){
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿ Acepta la ejecución de este programa con datos móviles ?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar2", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        sicn = new Sincronizar();
                        flagSinc=true;
                        sicn.execute();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                dialogo1.show();
            }else{
                sicn = new Sincronizar();
                flagSinc=true;
                sicn.execute();
            }
        }else{
            Toast.makeText(this,"No hay conexión : "+isConnected,Toast.LENGTH_SHORT).show();
        }
    }

    public  void detener (View v){
        //Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();
        if(flagSinc){
            sicn.cancel(true);
        }
    }

    public void limpiar(){
        resultado.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(flagSinc){
            sicn.cancel(true);
        }
        try {
            con.BD_backup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this,"OnDestroy",Toast.LENGTH_SHORT).show();
    }

    public void insertarFeed(String feed){
            ContentValues values = new ContentValues();
            values.put(ContractFeed.FeedEnrty.FEED,feed);
            db.insert(ContractFeed.FeedEnrty.TABLE_NAME,null,values);
            //String sql=" insert into feed (feed) values ('"+feed+"');";
            //db.execSQL(sql);
        }


    public class Sincronizar extends AsyncTask<Void,String,String>{

        int datosRespaldados=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //http://jtomlinson.blogspot.com.co/2010/03/textview-and-html.html
            //resultado.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "Visit my <a href=\"http://www.google.com\">Google</a>";
            //progreso.show();
            resultado.setText(Html.fromHtml(text));
        }
        @Override
        protected String doInBackground(Void... voids) {

            int i=0,j=0;
            int k=0,l=0;
            String salida ="";
            String salidaBd="";
            String link ="";
            try{
                URL urlConexion = new URL(URLConnect);
                //primer paso segun https://developer.android.com/reference/java/net/HttpURLConnection.html
                HttpURLConnection conexion = (HttpURLConnection) urlConexion.openConnection();
                //segundo paso -headers
                conexion.setRequestProperty("User-Agent","Mozilla/5.0" +
                        "(Linux; Android 1.5; es-Es) Ejemplo Uceva Http");
                //segundo paso -headers
                int conectado= conexion.getResponseCode();
                if(conectado==HttpURLConnection.HTTP_OK){//NOS CONECTAMOS
                    BufferedReader xml = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String linea = xml.readLine(); // leemos la primera linea del xml que contiene datos de la fuente
                    while (linea!=null || linea!=""){
                        if(!isCancelled()){ //SI NO HA SIDO DETENIDA LA TASK pues entonces siga con la iteración
                            //procesar el contenido de xml
                            if(linea.indexOf("<title><![CDATA[")>=0){// si es >= 0 es porque en la posiciçon 0 o superior inicia la cadena comparada
                                i = linea.indexOf("<title><![CDATA[")+16;// inicio de cadena a capturar
                                j = linea.indexOf("</title>")-3;// fin  de cadena a capturar
                                /*if(linea.indexOf("<link><![CDATA[")>=0){
                                    k = linea.indexOf("<link><![CDATA[")+15;// inicio de cadena a capturar
                                    l = linea.indexOf("</link>")-3;// fin  de cadena a capturar
                                    link = linea.substring(k,l);
                                }else{
                                    link="#";
                                }*/
                                //salida  = "<a href=\""+link+"\">"+linea.substring(i,j)+"</a>";

                                salidaBd  = linea.substring(i,j);
                                salidaBd += "\n-----------------\n";

                                salida  = linea.substring(i,j);
                                salida += "<br>-----------------<br>";

                                //--respaldo bd

                                insertarFeed(salidaBd);
                                datosRespaldados++;
                                //--respaldo bd
                                k++;
                                EscribirFirebase(k,salida);
                                publishProgress(salida);
                                Thread.sleep(2000);//2 segundo entre cada iteración
                            }
                            linea = xml.readLine(); //la siguiente liena
                            //procesar el contenido de xml
                        }else{ //close -> else isCancelled
                            break;
                        }
                    } //close while
                    xml.close();
                }else{
                    salida ="Fuente no encontrada";
                }
                conexion.disconnect();
                return  salida;
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            /*if(aVoid.length()==0 || aVoid==null){
                aVoid="";
            }
            resultado.append(aVoid);*/
            Toast.makeText(getApplicationContext(),aVoid+"Sincronización terminada, "+datosRespaldados+" Feeds respaldados",Toast.LENGTH_SHORT).show();
            //progreso.hide();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            resultado.append(Html.fromHtml(values[0]));
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
            sicn=null;
            flagSinc=false;
        }
    }


    public void EscribirFirebase(Integer id, String vale){
        myRef.child("feeds").child(""+id).setValue(vale);
    }

    public void  ProbarVolley(){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        resultado.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultado.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
