package com.guessthesetunes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity 
{
	String nomeGiocatore;
	Intent intentRegistrazione;
	JsonDB jParser = new JsonDB();
	Button btnLogin;
	Button btnLinkToRegister;
	public static EditText inputEmail;
	public static EditText inputPassword;
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{    
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.login);
	    
	    inputEmail = (EditText) findViewById(R.id.loginEmail);
	    inputPassword = (EditText) findViewById(R.id.loginPassword);
	    btnLogin = (Button) findViewById(R.id.btnLogin);
	    btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
	    
	    intentRegistrazione = getIntent();
	    String username = intentRegistrazione.getStringExtra("nomeGiocatore");
	    
	    if(username != null)
	    	inputEmail.setText(username);
	    else
	    	inputEmail.setText("");
		
		inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() 
		{	
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || 
        				event.getAction() == KeyEvent.ACTION_DOWN &&
		                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) 
        		{
					new CheckUser(Login.this,inputEmail.getText().toString().trim(),inputPassword.getText().toString().trim()).execute();
        		}
				return true;
			}
		});
		
		inputPassword.setImeActionLabel("Invia", KeyEvent.KEYCODE_ENTER);
	    	
	    btnLogin.setOnClickListener(new View.OnClickListener() 
	    {	
			@Override
			public void onClick(View v) 
			{	
				// controllo se esiste una connessione
		        
		        ConnectivityManager manager = (ConnectivityManager)getSystemService(Login.CONNECTIVITY_SERVICE);
		        Boolean isWifiConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		        Boolean isMobileConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		        
		        Log.d("Connessione", "wifi:"+isWifiConnected+"-dati:"+isMobileConnected);
		        
		        if(!isWifiConnected && !isMobileConnected)
		        {
		        	new AlertDialog.Builder(Login.this)
		            .setTitle("Connessione non attiva!")
		            .setMessage("Per giocare a Guess These Tunes è necessaria la connessione alla rete. Vuoi andare alla pagina delle impostazioni?")
		            .setNegativeButton("Attiva Wifi", new DialogInterface.OnClickListener() 
		            {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							dialog.dismiss();
						}
					})
		            .setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() 
		            {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.dismiss();
							finish();
						}
					})
					.setNeutralButton("Attiva rete mobile", new DialogInterface.OnClickListener() 
		            {
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
							dialog.dismiss();
						}
					}).create().show();
		        }
		        else
		        {
		        	Log.d("checkuser:Connessione", "wifi:"+isWifiConnected+"-dati:"+isMobileConnected);
		        	new CheckUser(Login.this,inputEmail.getText().toString().trim(),inputPassword.getText().toString().trim()).execute();
		        }
			}
		});
	    
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() 
		{ 
			@Override
			public void onClick(View view) 
			{
				Intent intentToRegistration = new Intent(getApplicationContext(),Register.class);
				startActivity(intentToRegistration);
				finish();
			}
		}); 
	}
}	
