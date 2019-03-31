package com.example.pianoproject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class Instruction extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instruction);
	//	RelativeLayout lay=(RelativeLayout) findViewById(R.id.instrLayout);
	//	lay.getBackground().setAlpha(125);
View v=findViewById(R.id.instView);
v.getBackground().setAlpha(125);
	}

}
