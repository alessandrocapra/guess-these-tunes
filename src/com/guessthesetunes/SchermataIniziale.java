package com.guessthesetunes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SchermataIniziale extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schermata_iniziale);
        
        Button startButton = (Button) findViewById(R.id.btnIniziale);
        ImageView logo = (ImageView) findViewById(R.id.logogtt);
        
        startButton.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				Intent intentToLogin = new Intent(SchermataIniziale.this,Login.class);
				startActivity(intentToLogin);
				finish();
			}
		});
    }
    
    
    
}
