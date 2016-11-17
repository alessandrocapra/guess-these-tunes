package com.guessthesetunes;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class GTTapp extends Application{
	GTTapp instance;

	static List<List<String>> dieciCanzoni = new ArrayList<List<String>>(10);
	
	public GTTapp() {
		instance = this;
	}

}
