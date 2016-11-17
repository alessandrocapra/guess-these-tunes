package com.guessthesetunes;


import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends Activity 
{
	// Elementi della UI
	Button btnRegister;
    Button btnLinkToLogin;
    Button btnNazionalita;
    EditText inputFullName;
    public static EditText inputEmail;
    public static EditText inputPassword;
    TextView tvNazionalita;
    public static EditText verifyPassword;
    
    // Variabili varie
    ArrayAdapter<String> adapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.password);
	    verifyPassword = (EditText) findViewById(R.id.verifyPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnNazionalita = (Button) findViewById(R.id.btnNazionalita);
        tvNazionalita = (TextView) findViewById(R.id.tvNazionalita);
 
        btnNazionalita.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				adapter = new ArrayAdapter<String>(Register.this,
		                android.R.layout.simple_spinner_dropdown_item, Register.this.getResources().getStringArray(R.array.nazionalita));
				
				new AlertDialog.Builder(Register.this)
				  .setTitle("Nazionalità:")
				  .setAdapter(adapter, new DialogInterface.OnClickListener() 
				  {
				    @Override
				    public void onClick(DialogInterface dialog, int which)
				    {
				      tvNazionalita.setText(adapter.getItem(which));
				      dialog.dismiss();
				    }
				  }).create().show();
				
			}
		});
        
        // Click sul pulsante Registrazione
        btnRegister.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) 
            {
            	
            	final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            	          "\\@" +
            	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            	          "(" +
            	          "\\." +
            	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            	          ")+"
            	      );
            	
            	String password = inputPassword.getText().toString().trim();
            	String verify = verifyPassword.getText().toString().trim();
            	boolean email = EMAIL_ADDRESS_PATTERN.matcher(inputEmail.getText().toString().toLowerCase().trim()).matches();
            	
            	if(password.compareTo(verify) < 0 || password.compareTo(verify) > 0)
            	{
            		AlertDialog.Builder passMatch = new AlertDialog.Builder(Register.this);
            		
            		passMatch
            		.setTitle("Errore!")
            		.setMessage("Password non corrispondenti!")
            		.setCancelable(false)
            		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
            		{	
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							inputPassword.setText("");
							verifyPassword.setText("");
							dialog.cancel();
						}
					});
            		
    				AlertDialog viewError = passMatch.create();
     
    				viewError.show();
            	}
            	else if(!email)
            	{
            		AlertDialog.Builder emailPattern = new AlertDialog.Builder(Register.this);
            		
            		emailPattern
            		.setTitle("Formato email scorretto!")
            		.setMessage("La mail deve essere nel formato:\n\nexample@example.com")
            		.setCancelable(false)
            		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
            		{	
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							inputEmail.setText("");
							dialog.cancel();
						}
					});
            		
    				AlertDialog viewError = emailPattern.create();
     
    				viewError.show();
            	}
            	else if(inputFullName.getText().toString().trim().equals("") || inputEmail.getText().toString().trim().equals("") || 
            			verifyPassword.getText().toString().trim().equals("") || tvNazionalita.getText().toString().equals("Scegliere nazionalità..") || inputPassword.getText().toString().trim().equals(""))
            	{
            		AlertDialog.Builder errMessage = new AlertDialog.Builder(Register.this);
            		
            		errMessage
            		.setTitle("Errore!")
            		.setMessage("Completare tutti i campi!")
            		.setCancelable(false)
            		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
            		{	
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							dialog.cancel();
						}
					});
            		
    				AlertDialog showError = errMessage.create();
     
    				showError.show();
            	}
            	else
            	{	
            		new CheckRegistration(Register.this,inputFullName.getText().toString().trim(),inputEmail.getText().toString().trim(),inputPassword.getText().toString().trim(),tvNazionalita.getText().toString().trim()).execute();
            	}
            }
            
        });
 
        // Link alla schermata di login
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() 
        {
 
            public void onClick(View view) 
            {
                Intent intentToLogin = new Intent(getApplicationContext(),Login.class);
                startActivity(intentToLogin);
                finish();
            }
        });
    }
}
