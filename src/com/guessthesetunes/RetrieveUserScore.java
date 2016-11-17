package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class RetrieveUserScore extends AsyncTask <String,String,Void> 
{
	ProgressDialog pDialog;
	Context context;
	String genereScelto;
	String nome;
	JSONArray dettagli = null;

	JsonDB jParser = new JsonDB();
	
	public RetrieveUserScore(Context context, String nome, String genereScelto)
	{
		this.context = context;
		this.genereScelto = genereScelto;
		this.nome = nome;
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Recupero dati...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
	}
	
	@Override
	protected Void doInBackground(String... arg0) 
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("generePunteggio", "p_"+genereScelto.toLowerCase()));
        params.add(new BasicNameValuePair("genereTempo", "t_"+genereScelto.toLowerCase()));
        params.add(new BasicNameValuePair("generePartite", "n_"+genereScelto.toLowerCase()));
        params.add(new BasicNameValuePair("nome", nome));
        
        JSONObject json = null;
        
        json = jParser.getJSONFromUrl(Classifica.classificaURL, params);
        
        try 
        {
			dettagli = json.getJSONArray("dettagliUtente");
		}
        catch (JSONException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try 
        {
        	JSONObject c = dettagli.getJSONObject(0);
				
			Classifica.punteggio = c.getString("punteggio");
			Classifica.tempo = c.getString("tempo");
			Classifica.partite = c.getString("partite");
			Classifica.migliorPunteggio = c.getString("migliorPunteggio");
			Classifica.migliorTempo = c.getString("migliorTempo");
			Classifica.migliorUtente = c.getString("nomeUtente");
		} 
        catch (JSONException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void value)
	{
		pDialog.dismiss();
	}
	
}
