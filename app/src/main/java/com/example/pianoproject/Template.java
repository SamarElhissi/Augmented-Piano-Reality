package com.example.pianoproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Template extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template);
		RelativeLayout lay=(RelativeLayout) findViewById(R.id.tempLayout);
		lay.getBackground().setAlpha(125);
	}

}
