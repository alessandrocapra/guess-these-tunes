package com.guessthesetunes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class Canzone extends Activity implements Runnable, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener
{
	MediaPlayer mediaPlayer = null;
	ProgressBar progressBar;
	ImageView ivAlbumCover;
	TextView tvSongInfo;
	EditText editText;
	TextView classifica;
	TextView migliorPunteggio;
	
	Intent intentGenere;
	String genereScelto;
	String nomeUtente;
	int recordGenere;
	int[] arrayGenere;
	
	int duration = 30000;
	int countCanzone;
	long timeStart;
	long timeStop;
	long tempoRisposta = 0;
	long tempoRispTotale = 0;
	int punteggio = 0;
	JsonDB jParser = new JsonDB();
	boolean indovinatoArtista = false;
	boolean indovinatoTitolo = false;
	Toast toast = null;
	
	
	static List<List<String>> dieciCanzoni = new ArrayList<List<String>>(10);
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
    	dieciCanzoni.clear();
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canzone);
        
        // Richiamo degli elementi UI dal layout XML
           
        ivAlbumCover = (ImageView) findViewById(R.id.albumImage);
        tvSongInfo = (TextView) findViewById(R.id.songInfo);
        editText = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        classifica = (TextView) findViewById(R.id.classifica);
        migliorPunteggio = (TextView) findViewById(R.id.migliorPunteggio);
        
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        countCanzone = 0;
        
        intentGenere = getIntent();
        genereScelto = intentGenere.getStringExtra("genereScelto");
        nomeUtente = intentGenere.getStringExtra("nomeGiocatore");
        arrayGenere = intentGenere.getIntArrayExtra("arrayGenere");
        
        
        // Richiamo task che si occupa di scegliere le canzoni random
        
        JsonCanzoni recuperoCanzoni = new JsonCanzoni(arrayGenere,Canzone.this);
        
        try {
			recuperoCanzoni.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        RetrieveUserScore punteggioMigliore = new RetrieveUserScore(Canzone.this, nomeUtente,genereScelto);
        
        try {
			punteggioMigliore.execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        migliorPunteggio.setText(Html.fromHtml("<b>Miglior punteggio</b>: "+Classifica.migliorPunteggio+"<br><b>Miglior Tempo</b>: "+Classifica.migliorTempo+"<br><b>Miglior utente</b>: "+Classifica.migliorUtente));
        

        // Setto l'inizio delle statistiche di gioco
        
        classifica.setText(Html.fromHtml("<b>Canzoni</b>: 1/10 \n<b>Punteggio</b>: 0"));
        
        
        //Riproduzione stream audio (1° volta che il programma viene eseguito)
        
       	PlayStream(dieciCanzoni.get(countCanzone).get(0));
        
        
        // Gestione input utente e conseguente verifica dati inseriti
            
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() 
        {
        	@Override
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
    		{
        		if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || 
        				event.getAction() == KeyEvent.ACTION_DOWN &&
		                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) 
        		{
            		VerificaCorrettezzaDatiInseriti( v.getText().toString().trim(),dieciCanzoni.get(countCanzone).get(1),dieciCanzoni.get(countCanzone).get(2));	
            		v.setText("");
            	}
    			return true;
    		}
        });	
        
        
	}
    
    /**
	 * 
	 * METODI
	 * 
	 */
	
	// Riproduzione dello stream audio
	
	private void PlayStream(String url)
	{
		editText.setText("");
		
        try 
        {
        	mediaPlayer = MediaPlayer.create(Canzone.this, Uri.parse(url));
        	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch (IllegalArgumentException e) 
        {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        catch (SecurityException e) 
        {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        catch (IllegalStateException e) 
        {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(duration);
            
        new Thread(this).start();
	}
	
	
	
	
	
	
	
	
	// Scarica e converte in bitmap l'url contentente l'immagine dell'album
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> 
	{
		  ImageView bmImage;

		  public DownloadImageTask(ImageView bmImage) 
		  {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) 
		  {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try 
		      {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      }
		      catch (Exception e) 
		      {
		          e.printStackTrace();
		      }
		      
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) 
		  {
		      bmImage.setImageBitmap(result);
		  }
	}
	
	
	
	
    private void toast(String tag, String testo) 
    {
    	LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_id));
    	ImageView image = (ImageView) layout.findViewById(R.id.image);
    	
    	if(tag.compareTo("OK") == 0)
    	{
    		image.setImageResource(R.drawable.rating_good);
    	}
    	else if(tag.compareTo("INFO") == 0)
    	{
    		image.setImageResource(R.drawable.action_about);
    	}
    	else if(tag.compareTo("NO") == 0)
    	{
    		image.setImageResource(R.drawable.rating_bad);
    	}
    	
    	TextView text = (TextView) layout.findViewById(R.id.textview);
    	text.setText(testo);
    	
        Toast toast = Toast.makeText(Canzone.this, testo, Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 215);
        toast.show();
    }
	
	
	
	
	// Controlla che i dati inseriti siano corretti e chiama il metodo per l'assegnamento del punteggio
	
	protected void VerificaCorrettezzaDatiInseriti(String stringa, String artistName, String trackName) 
	{	
			
		// Espressione regolare che toglie la parte del titolo fra parentesi, la punteggiatura e il 'The' all'inizio della stringa
	    String artistNoBrackets = artistName.replaceAll("\\(.+?\\)", "").trim();
	    
	    // Espressione regolare che toglie la punteggiatura dalla stringa artistNoBrackets
	    String artistCleaned = artistNoBrackets.replaceAll("[^\\w\\s]", "").trim();
	    
	    // Espressione regolare che toglie l'eventuale 'The' all'inizio della stringa artistCleaned
	    String artistWithoutThe = artistCleaned.replaceAll("^The|^the", "").trim();
	    
	    // Espressione regolare che toglie la parte del nome artista fra parentesi
	    String trackNoBrackets = trackName.replaceAll("\\(.+?\\)", "").trim();
	    
	    // Espressione regolare che toglie la punteggiatura dalla stringa trackNoBrackets
	    String trackCleaned = trackNoBrackets.replaceAll("[^\\w\\s]", "").trim();
		
		if(stringa.equalsIgnoreCase(artistName) || stringa.equalsIgnoreCase(trackName) || stringa.equalsIgnoreCase(artistCleaned) || 
				stringa.equalsIgnoreCase(artistWithoutThe)|| stringa.equalsIgnoreCase(trackCleaned))
		{
			if((stringa.equalsIgnoreCase(artistName) ||  stringa.equalsIgnoreCase(artistCleaned) || stringa.equalsIgnoreCase(artistWithoutThe))
					&& !indovinatoArtista)
			{
				toast("OK","OK,hai indovinato l'artista!");
				
				indovinatoArtista = true;
				
				Calendar cal = Calendar.getInstance();
				timeStop = cal.getTimeInMillis();
				
				AssegnamentoPunteggioRound();
			}
			else if((stringa.equalsIgnoreCase(artistName) ||  stringa.equalsIgnoreCase(artistCleaned) || stringa.equalsIgnoreCase(artistWithoutThe))
					&& indovinatoArtista)
			{
				toast("INFO","Hai già indovinato l'artista!");
			}
			else if((stringa.equalsIgnoreCase(trackName) ||  stringa.equalsIgnoreCase(trackCleaned)) && !indovinatoTitolo)
			{
				toast("OK","OK,hai indovinato il titolo!");
				
				indovinatoTitolo = true;
				
				Calendar cal = Calendar.getInstance();
				timeStop = cal.getTimeInMillis();

				AssegnamentoPunteggioRound();
			}	
			else if((stringa.equalsIgnoreCase(trackName) ||  stringa.equalsIgnoreCase(trackCleaned)) && indovinatoTitolo)
			{
				toast("INFO","Hai già indovinato il titolo!");
			}	
		}
		else
		{
			toast("NO","Sbagliato!");
		}	
	}
	
	
	
	

	
	


	// Metodo per assegnamento punteggio:
	
	private void AssegnamentoPunteggioRound()
	{
		String dettagliTempo = null;
		
		if(indovinatoArtista && indovinatoTitolo)
		{
			toast("OK","Complimenti!!!");
			
			punteggio+=1;
			
			InputMethodManager inputManager = (InputMethodManager) Canzone.this.getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(Canzone.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 
			
			tempoRisposta = (timeStop-timeStart)/1000;
			
			if(tempoRispTotale == 0)
			{
				tempoRispTotale = tempoRisposta;
			}
			else
			{
				tempoRispTotale = (tempoRispTotale + tempoRisposta)/2;
			}
			
			dettagliTempo = "\n\nIndovinato in: "+tempoRisposta+" sec!\n\nTempo risposta medio round: "+tempoRispTotale+" sec!";
		}
		
		punteggio+=1;
		classifica.setText(Html.fromHtml("<b>Canzoni</b>: "+String.valueOf(countCanzone+1)+"/10 \n<b>Punteggio</b>: "+punteggio));
		
		
		if(dettagliTempo!=null)
		{
			classifica.append(dettagliTempo);
		}
	}
	
	
	
	
	
	
	
	// Schermata di dialogo visualizzata alla fine del round
	
	protected void FineRound(String nome,String genere,String punteggio)
	{
		String message;
		
		new InsertScore(nomeUtente, genereScelto, punteggio, tempoRispTotale).execute();
		
		if(tempoRispTotale == 0)
		{
			message = "Punteggio: "+punteggio+"\nTempo di risposta: non rilevato";
		}
		else
		{
			message = "Punteggio: "+punteggio+"\nTempo di risposta: "+tempoRispTotale;
		}
		
		new AlertDialog.Builder(Canzone.this)
        .setTitle("Fine del Round!")
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(android.R.string.ok, new OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1) 
            {
            	GenereMusicale.etCaricamento.setText("");
            	finish();
            }
        }).create().show();
	}	

	
	
	/*
	 * Override del metodo per chiedere all'utente se è sicuro di volere uscire dal round.
	 * 
	 */
	@Override
	public void onBackPressed()
	{	
		GenereMusicale.etCaricamento.setText("");
		
		new AlertDialog.Builder(this)
        .setTitle("Uscita dal round")
        .setMessage("Terminare round?")
        .setNegativeButton(android.R.string.no, null)
        .setPositiveButton(android.R.string.yes, new OnClickListener() 
        {
            public void onClick(DialogInterface arg0, int arg1) 
            {
            	mediaPlayer.stop();
        		mediaPlayer.release();
        		mediaPlayer = null;
            	
            	Intent in = new Intent(Canzone.this,GenereMusicale.class);
            	startActivity(in);
            	
            	finish();
            }
        }).create().show();
	}
	
	
	
	
	
	
	
	/**
	 * 	utilizzato per matchare il progresso del media player con la progress bar!
	 * 
	 */
	@Override
    public void run() 
	{
        int currentPosition= 0;
        while (mediaPlayer!=null && currentPosition<duration) 
        {
            try 
            {
                Thread.sleep(100);
                currentPosition= mediaPlayer.getCurrentPosition();
            }
            catch (InterruptedException e)
            {
                return;
            }
            catch (Exception e) 
            {
                return;
            }            
            
            progressBar.setProgress(currentPosition);
        }
    }
	
	
	
	
	
	

	@Override
	public void onPrepared(MediaPlayer mp) 
	{
		duration = mediaPlayer.getDuration();
		
		Calendar cal = Calendar.getInstance();
		timeStart = cal.getTimeInMillis();
		
		mediaPlayer.start();
		Log.d("MPLAY", "MP Started");
	}
	
	
	
	
	
	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		indovinatoArtista = false;
		indovinatoTitolo = false;
		
		countCanzone++;
		
		mediaPlayer.stop();	
		mediaPlayer.release();
		
		if(countCanzone < 10)
		{	
			classifica.setText(Html.fromHtml("<b>Canzoni</b>: "+String.valueOf(countCanzone+1)+"/10 <br><b>Punteggio</b>: "+punteggio));
        
			PlayStream(dieciCanzoni.get(countCanzone).get(0));
        
			VisualizzaInfoCanzone();
		}
		else
		{
			InputMethodManager inputManager = (InputMethodManager) Canzone.this.getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(Canzone.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 
			
			VisualizzaInfoCanzone();
			FineRound(nomeUtente,genereScelto,String.valueOf(punteggio));
		}
		
	}
	
	
	
	
	
	
	private void VisualizzaInfoCanzone() 
	{
		tvSongInfo.setText(Html.fromHtml("<b>"+dieciCanzoni.get(countCanzone-1).get(1) + "</b><br>" + dieciCanzoni.get(countCanzone-1).get(2)));
        
		new DownloadImageTask((ImageView) findViewById(R.id.albumImage)).execute(dieciCanzoni.get(countCanzone-1).get(4));
		
		ivAlbumCover.setOnClickListener(new View.OnClickListener() 
		{		
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(dieciCanzoni.get(countCanzone-1).get(3)));
				startActivity(intent);
			}
		});
	}
}