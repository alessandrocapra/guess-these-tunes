package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

class InsertScore extends AsyncTask<String,String,JSONObject>
{
	String nome;
	String genere;
	String punteggio;
	long tempoMedioRisposta;
	
	JsonDB jParser = new JsonDB();
	
	private static String roundURL = "http://www.missionecondominio.it/db_datiround.php";
	
	public InsertScore(String nome,String genere,String punteggio, long tempoMedioRisposta)
	{
		this.nome = nome;
		this.genere = genere;
		this.punteggio = punteggio;
		this.tempoMedioRisposta = tempoMedioRisposta;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) 
	{
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("nome",nome));
        param.add(new BasicNameValuePair("genere", "p_"+genere));
        param.add(new BasicNameValuePair("punteggioRound", String.valueOf(punteggio)));
        param.add(new BasicNameValuePair("genereTempoMedio", "t_"+genere));
        param.add(new BasicNameValuePair("tempoMedio", String.valueOf(tempoMedioRisposta)));
        param.add(new BasicNameValuePair("generePartiteGiocate", "n_"+genere));
        
        // getting JSON string from URL
        JSONObject json;
        
        json = jParser.getJSONFromUrl(roundURL, param);

        // Check your log cat for JSON response
        Log.d("All Users: ", json.toString());
		return null;
	}
}