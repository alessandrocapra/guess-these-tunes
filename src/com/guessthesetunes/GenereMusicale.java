package com.guessthesetunes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GenereMusicale extends Activity implements OnClickListener
{
	Intent intentToCanzone,intentLogin;
	static String url;
	String giocatore = null;
	static AlertDialog.Builder preparazioneRound;
	
	int[] genereRock = new int[]{20,21,1003,1004,1005,1006,1147,1148,1150,1152,1155,1156,1157,1158,1160};
	int[] generePop = new int[]{14,1132,1133,1134,1135};
	int[] genereReggae = new int[]{24,1183,1192,1193,1194};
	int[] genereJazz = new int[]{11,1106,1107,1108,1109,1110,1114,1207,1208,1209};
	int[] genereCountry = new int[]{6,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043};
	int[] genereDance = new int[]{17,1046,1047,1048,1049,1050,1051};
	int[] genereRap = new int[]{18,1068,1069,1070,1071,1072,1073,1075,1076,1077,1078};
	int[] genereMetal = new int[]{1149,1151,1153};
	
	int randomArrayPosition;
	int[] arrayScelto;
	
	static TextView etCaricamento;
	Button btnCondivisione;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genere_musicale);
        
        ImageButton imgRock = (ImageButton) findViewById(R.id.imgRock);
        ImageButton imgPop = (ImageButton) findViewById(R.id.imgPop);
        ImageButton imgReggae = (ImageButton) findViewById(R.id.imgReggae);
        ImageButton imgJazz = (ImageButton) findViewById(R.id.imgJazz);
        ImageButton imgCountry = (ImageButton) findViewById(R.id.imgCountry);
        ImageButton imgDance = (ImageButton) findViewById(R.id.imgDance);
        ImageButton imgRap = (ImageButton) findViewById(R.id.imgRap);
        ImageButton imgMetal = (ImageButton) findViewById(R.id.imgMetal);
        
        TextView tvWelcome = (TextView) findViewById(R.id.welcomeText);
        Button btnClassifica = (Button) findViewById(R.id.btnClassifica);
        etCaricamento = (TextView) findViewById(R.id.etCaricamento);
        btnCondivisione = (Button) findViewById(R.id.btnCondivisione);
        
        imgRock.setOnClickListener(this);
        imgPop.setOnClickListener(this);
        imgReggae.setOnClickListener(this);
        imgJazz.setOnClickListener(this);
        imgCountry.setOnClickListener(this);
        imgDance.setOnClickListener(this);
        imgRap.setOnClickListener(this);
        imgMetal.setOnClickListener(this);
        
        intentLogin = getIntent();
        giocatore = intentLogin.getStringExtra("nomeGiocatore");
        
        btnCondivisione.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
		        final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "Guess These Tunes! on Play Store.");
				intent.putExtra(Intent.EXTRA_TEXT, "Ti consiglio di scaricare Guess These Tunes dal Play Store, è bellissimo!");
				startActivity(Intent.createChooser(intent,"Condividi"));
			}
		});
        
        if(giocatore != null)
        	tvWelcome.setText("Benvenuto "+giocatore+"!\n\nScegli uno dei seguenti generi e preparati all'inizio del round!");
        else
        	tvWelcome.setText("Benvenuto!\n\nScegli uno dei seguenti generi e preparati all'inizio del round!");        
        
        btnClassifica.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				Intent intentToClassifica = new Intent(GenereMusicale.this,Classifica.class);
				intentToClassifica.putExtra("nomeGiocatore", giocatore);
				startActivity(intentToClassifica);
			}
		});
        
        // controllo se il wifi è accesso e connesso
        
        ConnectivityManager manager = (ConnectivityManager)getSystemService(GenereMusicale.CONNECTIVITY_SERVICE);
        Boolean isWifiConnected = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if(!isWifiConnected)
        {
        	new AlertDialog.Builder(GenereMusicale.this)
            .setTitle("WiFi non connesso!")
            .setMessage("Per evitare costi aggiuntivi ti consigliamo di connetterti tramite wifi. Vuoi andare alla pagina delle impostazioni?")
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
            {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					dialog.dismiss();
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() 
            {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			}).create().show();
        }
    }
    
    public void onClick(View v) 
    {	
    	intentToCanzone = new Intent(GenereMusicale.this,Canzone.class);
    	
    	if (v.getId() == R.id.imgRock) 
    	{
    		intentToCanzone.putExtra("genereScelto", "rock");
    		intentToCanzone.putExtra("arrayGenere", genereRock);
		}
    	else if (v.getId() == R.id.imgPop)    	
    	{
    		intentToCanzone.putExtra("arrayGenere", generePop);
    		intentToCanzone.putExtra("genereScelto", "pop");
		}
    	else if (v.getId() == R.id.imgJazz)    
    	{
    		intentToCanzone.putExtra("genereScelto", "jazz");
    		intentToCanzone.putExtra("arrayGenere", genereJazz);
    	}
    	else if (v.getId() == R.id.imgReggae)
    	{
    		intentToCanzone.putExtra("genereScelto", "reggae");
    		intentToCanzone.putExtra("arrayGenere", genereReggae);
    	}
    	else if (v.getId() == R.id.imgCountry)
    	{
    		intentToCanzone.putExtra("genereScelto", "country");
    		intentToCanzone.putExtra("arrayGenere", genereCountry);
    	}
    	else if (v.getId() == R.id.imgDance)
    	{
    		intentToCanzone.putExtra("genereScelto", "dance");
    		intentToCanzone.putExtra("arrayGenere", genereDance);
    	}
    	else if (v.getId() == R.id.imgRap)
    	{
    		intentToCanzone.putExtra("genereScelto", "rap");
    		intentToCanzone.putExtra("arrayGenere", genereRap);
    	}
    	else if (v.getId() == R.id.imgMetal)
    	{
    		intentToCanzone.putExtra("genereScelto", "metal");
    		intentToCanzone.putExtra("arrayGenere", genereMetal);
    	}
    	
    	preparazioneRound = new AlertDialog.Builder(this); 
        preparazioneRound.setTitle("Preparazione round");
        preparazioneRound.setMessage("Attendere qualche secondo...");
        preparazioneRound.create().show();
    	
    	intentToCanzone.putExtra("nomeGiocatore", giocatore);
    	
    	startActivity(intentToCanzone);

    }
    
	/*
	 * Override del metodo per chiedere all'utente se è sicuro di volere uscire dal round.
	 * 
	 */
	@Override
	public void onBackPressed()
	{	
		new AlertDialog.Builder(this)
        .setTitle("Uscita dall'applicazione")
        .setMessage("Sei sicuro di voler uscire?")
        .setNegativeButton(android.R.string.no, null)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() 
        {
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				finish();
			}
		}).create().show();
	}
}