package com.example.pianoproject;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.Display;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainScreen extends Activity implements OnClickListener , OnItemClickListener {
    ImageView start,ins,temp;
    ListView pianoList;
    int width,height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		start=(ImageView) findViewById(R.id.start_play);
		start.setOnClickListener(this);
		ins=(ImageView) findViewById(R.id.instruction);
		ins.setOnClickListener(this);
		temp=(ImageView) findViewById(R.id.template);
		temp.setOnClickListener(this);
		registerForContextMenu(findViewById(R.id.start_play));
		screenSize();
		
	
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		  super.onCreateContextMenu(menu, v, menuInfo);
		   getMenuInflater().inflate(R.menu.context_menu2, menu);
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.paper:
	        	Intent paper=new Intent("com.example.pianoproject.PlayingClass");
	    		startActivity(paper);
	            return true;
	        case R.id.normal:
	        
	        	Intent p=new Intent("com.example.pianoproject.UpdatedNormalPiano");
	    		startActivity(p);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	if (v.getId()==R.id.start_play){
		this.openContextMenu(v);
	}
	if (v.getId()==R.id.instruction){
		Intent play=new Intent("com.example.pianoproject.Instruction");
		startActivity(play);
	}
	if (v.getId()==R.id.template){
		Intent play=new Intent("com.example.pianoproject.Template");
		startActivity(play);
	}
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	public void screenSize(){
		Display display=getWindowManager().getDefaultDisplay();
		width=display.getWidth();
		height=display.getHeight();
		start.getLayoutParams().width=(int)((7*width)/24);
		ins.getLayoutParams().width=(int)((7*width)/24);
		temp.getLayoutParams().width=(int)((7*width)/24);
		start.getLayoutParams().height=(int)height/9;
		ins.getLayoutParams().height=(int)height/9;
		temp.getLayoutParams().height=(int)height/9;
		RelativeLayout.LayoutParams lp =
	            (RelativeLayout.LayoutParams) start.getLayoutParams();
		int dp1=(int)((58*height)/320);
		lp.topMargin=dp1;
		start.setLayoutParams(lp);
		RelativeLayout.LayoutParams lp2 =
	            (RelativeLayout.LayoutParams) ins.getLayoutParams();
		int dp2=(int)((104*height)/320);
		lp2.topMargin=dp2;
		ins.setLayoutParams(lp2);
		RelativeLayout.LayoutParams lp3 =
	            (RelativeLayout.LayoutParams) temp.getLayoutParams();
		int dp3=(int)((150*height)/320);
		lp3.topMargin=dp3;
		temp.setLayoutParams(lp3);
	}
}
