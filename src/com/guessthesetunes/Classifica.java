package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Classifica extends Activity 
{
	Button btnSceltaGenere;
	TextView tvGenere;
	Intent intentGenere;
	String nome;
	String scelta;
	static String punteggio;
	static String tempo;
	static String partite;
	static String migliorPunteggio;
	static String migliorTempo;
	static String migliorUtente;
	
	static String classificaURL = "http://www.missionecondominio.it/db_classifica.php";
	
	ArrayAdapter<String> adapter;
	List<String> listaGeneri = new ArrayList<String>(8);	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);
        
        btnSceltaGenere = (Button) findViewById(R.id.btnSceltaGenere);
        tvGenere = (TextView) findViewById(R.id.tvClassifica);
        
        intentGenere = getIntent();
        nome = intentGenere.getStringExtra("nomeGiocatore");
        
        listaGeneri.add("Rock");
        listaGeneri.add("Pop");
        listaGeneri.add("Country");
        listaGeneri.add("Jazz");
        listaGeneri.add("Dance");
        listaGeneri.add("Rap");
        listaGeneri.add("Metal");
        listaGeneri.add("Reggae");
        
        btnSceltaGenere.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				adapter = new ArrayAdapter<String>(Classifica.this,
		                android.R.layout.simple_spinner_dropdown_item,listaGeneri);
				
				new AlertDialog.Builder(Classifica.this)
				  .setTitle("Genere:")
				  .setAdapter(adapter, new DialogInterface.OnClickListener() 
				  {
				    @Override
				    public void onClick(DialogInterface dialog, int which)
				    {
				    	scelta = adapter.getItem(which).toString().toLowerCase();
				    	
				    	try {
							new RetrieveUserScore(Classifica.this,nome,scelta).execute().get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	
				    	tvGenere.setText(Html.fromHtml("<big><b>Giocatore</b>: "+nome+
				    								   "</big><br><br><b>Genere</b>: "+scelta+
				    								   "<br><b>Punteggio</b>: "+punteggio+
				    								   "<br><b>Tempo di risposta medio</b>: "+tempo+
				    								   "<br><b>Round giocati</b>: "+partite+
				    								   "<br><br><b>Miglior punteggio del genere</b>: "+migliorPunteggio+
				    								   "<br><b>Miglior tempo del genere</b>: "+migliorTempo));
				        
				    	dialog.dismiss();
				    }
				  }).create().show();
			}
			
			
		});
    }
}
