package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * 
 * CHECKREGISTRATION:
 * 
 * Classe che eredita da AsyncTask, utilizzata per il reperimento dei dati dalle EditText e collegamento al server MySQL per controllare
 * se un utente (username o email) risulta già iscritto.
 * 
 * I controlli sull'unicità dell'username e della email non vengono fatti qui, ma grazie alla specifica SQL UNIQUE, che non permette di creare
 * righe con quei valori già presenti in un'altra riga.
 * 
 * I messaggi di errore vengono gestiti nel file PHP che permette di effettuare la query al server, e vengono reperiti sempre tramite un JSONParser.
 *
 */

class CheckRegistration extends AsyncTask<String, String, Integer>
{
    // Codici stringa usati per il parsing JSON
    private static String KEY_SUCCESS = "success";
    private static String registrationURL = "http://www.missionecondominio.it/db_registrazione.php";
    
    String strError = null;
    String nomeUtente= null;
	
	ProgressDialog pDialog;
	JsonDB jParser = new JsonDB();
	
	Context context;
	String nome;
	String email;
	String password;
	String nazionalita;
	
	public CheckRegistration(Context context, String nome, String email, String password, String nazionalita)
	{
		this.context = context;
		this.nome = nome;
		this.email = email;
		this.password= password;
		this.nazionalita= nazionalita;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		
		// Visualizzo box di attesa
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Verifica dati inseriti...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
	}
	
	@Override
	protected Integer doInBackground(String... arg0) 
	{
		int valoreOnPostExecute = 0;

        // Aggiungo i dati sopra reperiti ad una lista, che verrà inviata alla pagina PHP predisposta.
        // Quest'ultima prenderà i valori passati e proverà ad inserirli all'interno del database.
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("nome", nome));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("nazionalita", nazionalita));
        
        JSONObject json = null;
        json = jParser.getJSONFromUrl(registrationURL, params);
        
        try 
        {
        	// Salvo il risultato dell'operazione in success (1 = OK, 0 = ERRORE)
            int success = json.getInt(KEY_SUCCESS);
            
            // Salvo il messaggio di errore che viene restituito dal file JSON
            strError = json.getString("message");
            
            if (success == 1) 
            {                    
            	// Se la registrazione è andata a buon fine, cambio il valore di valoreOnPostExecute a 1 per passare il valore
            	// al metodo onPostExecute
                valoreOnPostExecute = success;
                
                // Salvo il nome dell'utente per passarlo all'activity successiva tramite un Intent
                nomeUtente= nome;
            }
        } 
        catch (JSONException e) 
        {
            e.printStackTrace();
        }   
        
		return valoreOnPostExecute;
	}
	
	@Override
	protected void onPostExecute(Integer valoreOnPostExecute)
	{
		// chiudo il box di attesa
        pDialog.dismiss();

        
        // Comunico all'utente l'esito dell'operazione in base al valore di valoreOnPostExecute
        if(valoreOnPostExecute == 1)
    	{
    		Toast.makeText(context, "Registrazione effettuata!", Toast.LENGTH_SHORT).show();
    		
    		Intent intent = new Intent(context,Login.class);
    		intent.putExtra("nomeGiocatore", nomeUtente);
        	context.startActivity(intent);
        	((Activity) context).finish();
    	}
    	else
    	{
    		new AlertDialog.Builder(context)
            .setTitle("Registrazione fallita!")
            .setMessage(strError)
            //.setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.ok, new OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) 
                {
                	Register.inputEmail.requestFocus();
                	Register.inputPassword.setText("");
                	Register.verifyPassword.setText("");
                }
            }).create().show();
    	}
		
	}
	
	
}