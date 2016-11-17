package com.guessthesetunes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

class JsonCanzoni extends AsyncTask<Void, Void, Void>
{
    List<String> canzoni = new ArrayList<String>(5);
    ProgressDialog pDialog;
    int[] arrayGenere;
    Context context;
    
	public JsonCanzoni(int[] arrayGenere,Context context) 
	{
		this.arrayGenere = arrayGenere;
		this.context = context;
	}
	
	
	
	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
		
		pDialog.dismiss();
	}



	@Override
	protected void onPreExecute() 
	{
		super.onPreExecute();
		
		pDialog = new ProgressDialog(context);
        pDialog.setMessage("Preparazione round...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
	}



	protected Void doInBackground(Void... params) 
	{
		try
		{		
			int randomLookupId = 0;
			JSONObject obj;							
			JSONArray jsonArray;	
			
			for(int i = 0; i < 10; i++)
			{
				canzoni = new ArrayList<String>();
				
				Log.d("GENERE", arrayGenere.toString());
			
				obj = getJSONObject(scegliClassifica(arrayGenere));
				jsonArray = obj.getJSONArray("resultIds");

				Log.d("dimensione JsonArray", String.valueOf(jsonArray.length()));
				try
				{
					randomLookupId = new Random().nextInt(jsonArray.length()-1);
				}
				catch(IllegalArgumentException errore)
				{
				}
				Log.d("randomLookupID", String.valueOf(randomLookupId));
				
				JSONObject finalObj = getJSONObject("http://itunes.apple.com/lookup?id="+jsonArray.getString(randomLookupId)); 
				Log.d("URL","http://itunes.apple.com/lookup?id="+jsonArray.getString(randomLookupId));
				
				while(finalObj.getJSONArray("results").length() == 0)
				{
					Log.d("Array VUOTO!!","Non è possibile!!!!");
					randomLookupId = new Random().nextInt(jsonArray.length()-1);
					Log.d("randomID rigenerato", String.valueOf(randomLookupId));
					
					finalObj = getJSONObject("http://itunes.apple.com/lookup?id="+jsonArray.getString(randomLookupId));
					Log.d("URL Rigenerato","http://itunes.apple.com/lookup?id="+jsonArray.getString(randomLookupId));
				}
				
				JSONArray finalJsonArray = finalObj.getJSONArray("results");
				
				JSONObject returnObj = finalJsonArray.getJSONObject(0);
				Log.d("returnObj.length",String.valueOf(returnObj.length()));
			
				canzoni.add(returnObj.getString("previewUrl"));
				canzoni.add(returnObj.getString("artistName"));
				canzoni.add(returnObj.getString("trackName"));
				canzoni.add(returnObj.getString("artistViewUrl"));
				canzoni.add(returnObj.getString("artworkUrl100"));
//				GTTapp app=(GTTapp) ((Activity)context).getApplication();
//				app.dieciCanzoni;
				Canzone.dieciCanzoni.add(i, new ArrayList<String>(canzoni));
			}
		}	
		catch (JSONException ignored)
		{
			ignored.getCause();
		}
		catch (MalformedURLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String scegliClassifica(int[] arrayGenere)
	{
		int randomArrayPosition = new Random().nextInt(arrayGenere.length);
		return "http://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/charts?cc=us&g="+arrayGenere[randomArrayPosition]+"&name=Songs&limit=200";
	}
	
	JSONObject getJSONObject(String url) throws IOException, MalformedURLException, JSONException
	{
		
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		InputStream in = conn.getInputStream();

		try
		{
			StringBuilder sb = new StringBuilder();
			BufferedReader r = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(in)));
			for (String line = r.readLine(); line != null; line = r.readLine())
			{
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		}
		finally
		{
			in.close();
		}
	}
}
	