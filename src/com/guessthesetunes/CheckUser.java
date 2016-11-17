package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
import android.util.Log;
import android.widget.Toast;

/**
 * Background Async Task to Load all product by making HTTP Request
 * */

public class CheckUser extends AsyncTask<String, String, Integer> 
{
    /**
     * Before starting background thread Show Progress Dialog
     * */
	
	private static String loginURL = "http://www.missionecondominio.it/db_login.php";
	private static String KEY_SUCCESS = "success";
	private static String KEY_NAME = "nome";
	private static String KEY_PASSWORD = "password";
	
	Context context;
	String emailInserted;
	String passwordInserted;
	String nomeGiocatore;
	
	ProgressDialog pDialog;
	JsonDB jParser = new JsonDB();
	JSONArray user = null;
	
	public CheckUser(Context context,String email,String password)
	{
		this.context=context;
		emailInserted=email;
		passwordInserted=password;
	}
	
    @Override
    protected void onPreExecute() 
    {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Verifica dati inseriti...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    protected Integer doInBackground(String... args)
    {
    	int valoreOnPostExecute = 0;
    	
    	// Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", emailInserted));
        // getting JSON string from URL
        JSONObject json = null;
        
		json = jParser.getJSONFromUrl(loginURL, params);

        // Check your log cat for JSON response
        Log.d("All Users: ", json.toString());

        try 
        {
            // Checking for SUCCESS TAG
            int success = json.getInt(KEY_SUCCESS);

            if (success == 1) 
            {
                // users found
                // Getting Array of users
                user = json.getJSONArray("utenti");
                
                JSONObject c = user.getJSONObject(0);
                
                // Storing each json item in variable
                nomeGiocatore = c.getString(KEY_NAME);
                String password = c.getString(KEY_PASSWORD);
               
                if(passwordInserted.compareTo(password) == 0 && emailInserted.compareTo(nomeGiocatore) == 0)
                {
                	valoreOnPostExecute = success;
                }
            } 

        }
        catch (JSONException e) 
        {
            e.printStackTrace();
            Log.d("User ARRAY", "user array: "+user);
        }
        
		return valoreOnPostExecute;
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    protected void onPostExecute(Integer valoreOnPostExecute) 
    {
        // dismiss the dialog after getting all products
        pDialog.dismiss();
        // updating UI from Background Thread

    	if(valoreOnPostExecute == 1)
    	{
    		Toast.makeText(context, "Login effettuato!", Toast.LENGTH_SHORT).show();
    		
    		Intent intent = new Intent(context,GenereMusicale.class);
    		intent.putExtra("nomeGiocatore", nomeGiocatore);
    		Login.inputPassword.setText("");
        	context.startActivity(intent);
        	((Activity) context).finish();
    	}
    	else
    	{
    		new AlertDialog.Builder(context)
            .setTitle("Login fallito!")
            .setMessage("Dati non corretti. Ritenta!")
            //.setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.ok, new OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) 
                {
                	Login.inputEmail.requestFocus();
                	Login.inputPassword.setText("");
                }
            }).create().show();
    	}
    }
    
}

