package com.example.pianoproject;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.leff.midi.MidiTrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.RelativeLayout;


public class NormalPiano extends Activity implements OnClickListener, OnTouchListener{
	RelativeLayout pianoKeysContainer;
	ImageView i1,i2,i3,i4,i5,i6,i7,record, b1,b2,b3,b4,b5, forw , bak ,piano;
    int pianoInt=4, width,height;
    MidiTrack noteTrack=new MidiTrack();
    int tick=0;
    boolean big=false;
    String recordType="";
    boolean midiRecord=false, rec=false;
    RecordingManager recordClass=new RecordingManager();
    float volume;
	Drawable white_down,white_up,black_down,black_up,nnpiano1,nnpiano2,nnpiano3,nnpiano4,nnpiano5,nnpiano6,nnpiano7;
	boolean white=true,black=true, click=true;
	 private long startTime = 0L;
     private Handler customHandler = new Handler();
     long timeInMilliseconds = 0L;
     long timeSwapBuff = 0L;
     long updatedTime = 0L;
     TextView timerText,c,d,e,f,g,a,b,title;
     private Runnable updateTimerThread = new Runnable() {
    	         public void run() {
    	 
    	             timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
    	             updatedTime = timeSwapBuff + timeInMilliseconds;
    	             int secs = (int) (updatedTime / 1000);
    	             int mins = secs / 60;
    	
    	             secs = secs % 60;
    	
//    	             int milliseconds = (int) (updatedTime % 1000);
    	 
    	             timerText.setText("" + mins + ":"
    	
    	                     + String.format("%02d", secs) );
    	
    	               //      + String.format("%03d", milliseconds));
    	        //     if(updatedTime % 220==0) 
    	          //  	 i++;
    	
    	             customHandler.postDelayed(this, 0);
    		    	         }
    	
    	     };


SoundPool   sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
int sound=0;
	    @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.normal_piano);
	        i1=(ImageView) findViewById(R.id.wkey1);
	        i1.setOnTouchListener(this);
	        i2=(ImageView) findViewById(R.id.wkey2);
	        i2.setOnTouchListener(this);
	        i3=(ImageView) findViewById(R.id.wkey3);
	        i3.setOnTouchListener(this);
	        i4=(ImageView) findViewById(R.id.wkey4);
	        i4.setOnTouchListener(this);
	        i5=(ImageView) findViewById(R.id.wkey5);
	        i5.setOnTouchListener(this);
	        i6=(ImageView) findViewById(R.id.wkey6);
	        i6.setOnTouchListener(this);
	        i7=(ImageView) findViewById(R.id.wkey7);
	        i7.setOnTouchListener(this);
	        b1=(ImageView) findViewById(R.id.bkey1);
	        b1.setOnTouchListener(this);
	        b2=(ImageView) findViewById(R.id.bkey2);
	        b2.setOnTouchListener(this);
	        b3=(ImageView) findViewById(R.id.bkey3);
	        b3.setOnTouchListener(this);
	        b4=(ImageView) findViewById(R.id.bkey4);
	        b4.setOnTouchListener(this);
	        b5=(ImageView) findViewById(R.id.bkey5);
	        b5.setOnTouchListener(this);
	        forw=(ImageView) findViewById(R.id.forw);
	        forw.setOnTouchListener(this);
	        bak=(ImageView) findViewById(R.id.bak);
	        bak.setOnTouchListener(this);
	        piano=(ImageView) findViewById(R.id.npiano);
	        record=(ImageView) findViewById(R.id.nrec);
	        record.setOnClickListener(this);
	        AudioManager audio=(AudioManager) getSystemService(AUDIO_SERVICE);
			float actualVolume = (float) audio
		          .getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audio
		          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			volume = actualVolume / maxVolume;
			white_down = getResources().getDrawable(R.drawable.white_down);
			white_up = getResources().getDrawable(R.drawable.piano_key);
		/*	nnpiano1 = getResources().getDrawable(R.drawable.npiano1);
			nnpiano2 = getResources().getDrawable(R.drawable.npiano2);
			nnpiano3 = getResources().getDrawable(R.drawable.npiano3);
			nnpiano4 = getResources().getDrawable(R.drawable.npiano4);
			nnpiano5 = getResources().getDrawable(R.drawable.npiano5);
			nnpiano6 = getResources().getDrawable(R.drawable.npiano6);
			nnpiano7 = getResources().getDrawable(R.drawable.npiano7);
			*/
			registerForContextMenu(findViewById(R.id.nrec));
			timerText=(TextView) findViewById(R.id.timer);
			c=(TextView) findViewById(R.id.ck);
			d=(TextView) findViewById(R.id.dk);
			e=(TextView) findViewById(R.id.ek);
			f=(TextView) findViewById(R.id.fk);
			g=(TextView) findViewById(R.id.gk);
			a=(TextView) findViewById(R.id.ak);
			b=(TextView) findViewById(R.id.bk);
			title=(TextView) findViewById(R.id.title);
			screenSize();
			
	    }
	    /*public int dpToPx(int dp) {
	        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
	        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	        return px;
	    }*/
	    @Override
		public void onCreateContextMenu(ContextMenu menu, View v,
		                                ContextMenuInfo menuInfo) {
			  super.onCreateContextMenu(menu, v, menuInfo);
			   getMenuInflater().inflate(R.menu.context_menu, menu);
		}
	    @Override
		public boolean onContextItemSelected(MenuItem item) {
		//    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		    switch (item.getItemId()) {
		        case R.id.midi:
		            recordType="midi";
		            startMidiRecording();
		            startTime = SystemClock.uptimeMillis();
			    	customHandler.postDelayed(updateTimerThread, 0);
		            return true;
		        case R.id.mic:
		        	recordType="mic";
		        	 try {

							recordClass.startRecording();
							  startTime = SystemClock.uptimeMillis();
					    	   customHandler.postDelayed(updateTimerThread, 0);
							 Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
				        
				     } catch (IllegalStateException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				     } catch (IOException e) {
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				     }
				    
		        	
		            return true;
		        default:
		            return super.onContextItemSelected(item);
		    }
		}
	    public AlertDialog AskOption(){

	        final AlertDialog ad=new AlertDialog.Builder(NormalPiano.this).create();
	 		// TODO Auto-generated method stub
	 		ad.setTitle("Save");
	 		ad.setMessage("Save File?");
	 		ad.setButton("Yes", new DialogInterface.OnClickListener() {
	 			
	 			@Override
	 			public void onClick(DialogInterface arg0, int arg1) {
	 				// TODO Auto-generated method stub
	 				
	 				if(recordType.equals("midi")){
	 					recordClass.stopMidiRecording(noteTrack);
	 					 Toast.makeText(getApplicationContext(), "File Saved ", Toast.LENGTH_LONG).show();
	 					tick=0;
	 					midiRecord=false;
	 				
	 				}
	 				else if(recordType.equals("mic"))
						recordClass.stopRecording();
	 			}
	 		});
	 		
	 ad.setButton2("No", new DialogInterface.OnClickListener() {
	 			
	 			@Override
	 			public void onClick(DialogInterface arg0, int arg1) {
	 				// TODO Auto-generated method stub
	 				if(recordType.equals("midi")) {
	 					tick=0;
	 					midiRecord=false;
	 				}
	 				else if(recordType.equals("mic"))
						recordClass.stopRecording();
	 			ad.dismiss();
	 			
	 			}
	 		});
	         return ad;
	     }
	 	
	 	
		
	    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
	    	if(v.getId()==R.id.nrec){
				if(rec==true || midiRecord==true)
				{
					// stop timer
					
			    	 customHandler.removeCallbacks(updateTimerThread);
			    // restart timer
			    	 startTime = 0L;
			         timeInMilliseconds = 0L;
			         timeSwapBuff = 0L;
			         updatedTime = 0L;
			         timerText.setText("00:00");
				}
				if(rec==true){
					rec=false; 
					//if(big)
		//				record.setImageResource(R.drawable.bigrecord);
			//		else
					record.setImageResource(R.drawable.recordpressed);
					
					if(recordType.equals("mic")){
						
				// stop mic recording
						
					recordClass.stopRecording();
				        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				        Toast.LENGTH_LONG).show();
					}
					AlertDialog diaBox = AskOption();
					diaBox.show();
				}
				else if (rec==false){
					rec=true;
				//	if(big)
					//	record.setImageResource(R.drawable.bigstop);
				//	else
						record.setImageResource(R.drawable.stop);
					this.openContextMenu(v);
				//	 Toast.makeText(getApplicationContext(), "kasso   "+x, Toast.LENGTH_LONG).show();	
				}
				
			}
	    	if(v.getId()==R.id.forw){
	    		if(pianoInt==1){
	    			piano.setImageResource(R.drawable.npiano2);
					//piano.setBackgroundDrawable(nnpiano2);
				//	pianoSize();
					pianoInt=2;
				}
				else if(pianoInt==2){
					piano.setImageResource(R.drawable.npiano3);
					//piano.setBackgroundDrawable(nnpiano3);
				//	pianoSize();
					pianoInt=3;
				}
				else if(pianoInt==3){
					piano.setImageResource(R.drawable.npiano4);
				//	piano.setBackgroundDrawable(nnpiano4);
					//pianoSize();
					pianoInt=4;
				}
				else if(pianoInt==4){
					piano.setImageResource(R.drawable.npiano5);
			//		piano.setBackgroundDrawable(nnpiano5);
					//pianoSize();
					pianoInt=5;
				}
				else if(pianoInt==5){
					piano.setImageResource(R.drawable.npiano6);
				//	piano.setBackgroundDrawable(nnpiano6);
		//			pianoSize();
					pianoInt=6;
				}
				else if(pianoInt==6){
					piano.setImageResource(R.drawable.npiano7);
				//	piano.setBackgroundDrawable(nnpiano7);
	//				pianoSize();
					pianoInt=7;
				}
	    	}
            if(v.getId()==R.id.bak){
            	if(pianoInt==7){
    				piano.setImageResource(R.drawable.npiano6);
            	//	piano.setBackgroundDrawable(nnpiano6);
    				pianoInt=6;
    			}
            	else if(pianoInt==6){
    				piano.setImageResource(R.drawable.npiano5);
            	//	piano.setBackgroundDrawable(nnpiano5);
    				pianoInt=5;
    			}
    			else if(pianoInt==5){
    				piano.setImageResource(R.drawable.npiano4);
    				//piano.setBackgroundDrawable(nnpiano4);
    				pianoInt=4;
    			}
    			else if(pianoInt==4){
    				piano.setImageResource(R.drawable.npiano3);
    			//	piano.setBackgroundDrawable(nnpiano3);
    				pianoInt=3;
    			}
    			else if(pianoInt==3){
    				piano.setImageResource(R.drawable.npiano2);
    			//	piano.setBackgroundDrawable(nnpiano2);
    				pianoInt=2;
    			}
    			else if(pianoInt==2){
    				piano.setImageResource(R.drawable.npiano1);
    			//	piano.setBackgroundDrawable(nnpiano1);
    				pianoInt=1;
    			}
	    	}
	  
			if(v.getId()==R.id.wkey3){
				if(pianoInt==1)
					playKey(getApplicationContext(), 3, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 10, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 17, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 24, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 31, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 38, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 45, midiRecord);
			}
			if(v.getId()==R.id.wkey4){
				if(pianoInt==1)
					playKey(getApplicationContext(), 4, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 11, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 18, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 25, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 32, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 39, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 46, midiRecord);
			}
			if(v.getId()==R.id.wkey5){
				if(pianoInt==1)
					playKey(getApplicationContext(), 5, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 12, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 19, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 26, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 33, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 40, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 47, midiRecord);
			}
			if(v.getId()==R.id.wkey6){
				if(pianoInt==1)
					playKey(getApplicationContext(), 6, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 13, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 20, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 27, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 34, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 41, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 48, midiRecord);
			}
			if(v.getId()==R.id.wkey7){
				if(pianoInt==1)
					playKey(getApplicationContext(), 7, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 14, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 21, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 28, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 35, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 42, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 49, midiRecord);
			}
		
			if(v.getId()==R.id.bkey2){
				if(pianoInt==1)
					playKey(getApplicationContext(), 51, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 56, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 61, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 66, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 71, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 76, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 81, midiRecord);
			}
			if(v.getId()==R.id.bkey3){
				if(pianoInt==1)
					playKey(getApplicationContext(), 52, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 57, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 62, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 67, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 72, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 77, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 82, midiRecord);
			}
			if(v.getId()==R.id.bkey4){
				if(pianoInt==1)
					playKey(getApplicationContext(), 53, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 58, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 63, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 68, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 73, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 78, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 83, midiRecord);
			}
			if(v.getId()==R.id.bkey5){
				if(pianoInt==1)
					playKey(getApplicationContext(), 54, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 59, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 64, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 69, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 74, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 79, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 84, midiRecord);
			}
	
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
	   
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(click==false){
				click=true;
				return false;
			}
			if(event.getAction()==MotionEvent.ACTION_DOWN){
			if(v.getId()==R.id.forw){
	    		if(pianoInt==1){
					piano.setImageResource(R.drawable.npiano2);
					pianoInt=2;
				keyColor(2);
					
				}
				else if(pianoInt==2){
					piano.setImageResource(R.drawable.npiano3);	
					pianoInt=3;
					keyColor(3);
				}
				else if(pianoInt==3){
					piano.setImageResource(R.drawable.npiano4);
					pianoInt=4;
					keyColor(4);
				}
				else if(pianoInt==4){
					piano.setImageResource(R.drawable.npiano5);
					pianoInt=5;
					keyColor(5);
				}
				else if(pianoInt==5){
					piano.setImageResource(R.drawable.npiano6);
					pianoInt=6;
					keyColor(6);
				}
				else if(pianoInt==6){
					piano.setImageResource(R.drawable.npiano7);
					pianoInt=7;
					keyColor(7);
				}
	    	}
            if(v.getId()==R.id.bak){
            	if(pianoInt==7){
    				piano.setImageResource(R.drawable.npiano6);
    				pianoInt=6;
    				keyColor(6);
    			}
            	else if(pianoInt==6){
    				piano.setImageResource(R.drawable.npiano5);
    				pianoInt=5;
    				keyColor(5);
    			}
    			else if(pianoInt==5){
    				piano.setImageResource(R.drawable.npiano4);
    				pianoInt=4;
    				keyColor(4);
    			}
    			else if(pianoInt==4){
    				piano.setImageResource(R.drawable.npiano3);
    				pianoInt=3;
    				keyColor(3);
    			}
    			else if(pianoInt==3){
    				piano.setImageResource(R.drawable.npiano2);
    				pianoInt=2;
    				keyColor(2);
    			}
    			else if(pianoInt==2){
    				piano.setImageResource(R.drawable.npiano1);
    				pianoInt=1;
    				keyColor(1);
    			}
	    	}
			if(v.getId()==R.id.bkey1){
				if(pianoInt==1)
					playKey(getApplicationContext(), 50, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 55, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 60, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 65, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 70, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 75, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 80, midiRecord);
				click=false;
				return true;
			}
			else if(v.getId()==R.id.bkey2){
				if(pianoInt==1)
					playKey(getApplicationContext(), 51, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 56, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 61, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 66, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 71, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 76, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 81, midiRecord);
				click=false;
				return true;
			}
			else if(v.getId()==R.id.bkey3){
				if(pianoInt==1)
					playKey(getApplicationContext(), 52, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 57, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 62, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 67, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 72, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 77, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 82, midiRecord);
				//b3.setBackgroundDrawable(black_down);
				click=false;
				return true;
			}
			else	if(v.getId()==R.id.bkey4){
				if(pianoInt==1)
					playKey(getApplicationContext(), 53, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 58, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 63, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 68, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 73, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 78, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 83, midiRecord);
				//b4.setBackgroundDrawable(black_down);
				click=false;
				return true;
			}
			else if(v.getId()==R.id.bkey5){
				if(pianoInt==1)
					playKey(getApplicationContext(), 54, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 59, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 64, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 69, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 74, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 79, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 84, midiRecord);
		//		b5.setBackgroundDrawable(black_down);
				click=false;
				return true;
			}
			else if(v.getId()==R.id.wkey1){
				if(pianoInt==1)
				playKey(getApplicationContext(), 1, midiRecord);
				else if(pianoInt==2)
				playKey(getApplicationContext(), 8, midiRecord);
				else if(pianoInt==3)
					playKey(getApplicationContext(), 15, midiRecord);
				else if(pianoInt==4)
					playKey(getApplicationContext(), 22, midiRecord);
				else if(pianoInt==5)
					playKey(getApplicationContext(), 29, midiRecord);
				else if(pianoInt==6)
					playKey(getApplicationContext(), 36, midiRecord);
				else if(pianoInt==7)
					playKey(getApplicationContext(), 43, midiRecord);
			    i1.setBackgroundDrawable(white_down);	
			      
		//		
			}
			else if(v.getId()==R.id.wkey2){
				if(pianoInt==1)
					playKey(getApplicationContext(), 2, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 9, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 16, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 23, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 30, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 37, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 44, midiRecord);
				 i2.setBackgroundDrawable(white_down);	
			}
			else	if(v.getId()==R.id.wkey3){
				if(pianoInt==1)
					playKey(getApplicationContext(), 3, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 10, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 17, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 24, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 31, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 38, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 45, midiRecord);
				 i3.setBackgroundDrawable(white_down);	
			}
			else	if(v.getId()==R.id.wkey4){
				if(pianoInt==1)
					playKey(getApplicationContext(), 4, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 11, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 18, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 25, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 32, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 39, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 46, midiRecord);
				 i4.setBackgroundDrawable(white_down);	
			}
			else	if(v.getId()==R.id.wkey5){
				if(pianoInt==1)
					playKey(getApplicationContext(), 5, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 12, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 19, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 26, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 33, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 40, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 47, midiRecord);
				 i5.setBackgroundDrawable(white_down);	
			}
			else	if(v.getId()==R.id.wkey6){
				if(pianoInt==1)
					playKey(getApplicationContext(), 6, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 13, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 20, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 27, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 34, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 41, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 48, midiRecord);
				 i6.setBackgroundDrawable(white_down);	
			}
			else	if(v.getId()==R.id.wkey7){
				if(pianoInt==1)
					playKey(getApplicationContext(), 7, midiRecord);
					else if(pianoInt==2)
					playKey(getApplicationContext(), 14, midiRecord);
					else if(pianoInt==3)
						playKey(getApplicationContext(), 21, midiRecord);
					else if(pianoInt==4)
						playKey(getApplicationContext(), 28, midiRecord);
					else if(pianoInt==5)
						playKey(getApplicationContext(), 35, midiRecord);
					else if(pianoInt==6)
						playKey(getApplicationContext(), 42, midiRecord);
					else if(pianoInt==7)
						playKey(getApplicationContext(), 49, midiRecord);
				 i7.setBackgroundDrawable(white_down);	
			
	     }
			}
			else if(event.getAction()==MotionEvent.ACTION_UP){
				i1.setBackgroundDrawable(white_up);
				i2.setBackgroundDrawable(white_up);
				i3.setBackgroundDrawable(white_up);
				i4.setBackgroundDrawable(white_up);
				i5.setBackgroundDrawable(white_up);
				i6.setBackgroundDrawable(white_up);
				i7.setBackgroundDrawable(white_up);
		/*		b1.setBackgroundDrawable(black_up);
				b2.setBackgroundDrawable(black_up);
				b3.setBackgroundDrawable(black_up);
				b4.setBackgroundDrawable(black_up);
				b5.setBackgroundDrawable(black_up);
			*/
			}
			
			return true;
		}
	    ////////////////////////////////////////////////////////////////////////////////////////////
		public void playKey(Context c, final int x, boolean midiRecord)
		{
		    //here you should store your piano sound at res/raw then load it
			if(x==1){
		    sp.load(c, R.raw.c1, 1);
		    if(midiRecord){
		    	noteTrack.insertNote(0, 24, 100, 480*tick, 120);
		    }
		    	
			}
			else if(x==2){
				 sp.load(c, R.raw.d1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 26, 100, 480*tick, 120);
				    }
			}
			else if(x==3){
				 sp.load(c, R.raw.e1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 28, 100, 480*tick, 120);
				    }
			}
			else if(x==4){
				 sp.load(c, R.raw.f1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 29, 100, 480*tick, 120);
				    }
			}
			else if(x==5){
				 sp.load(c, R.raw.g1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 31, 100, 480*tick, 120);
				    }
			}
			else if(x==6){
				 sp.load(c, R.raw.a1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 33, 100, 480*tick, 120);
				    }
			}
			else if(x==7){
				 sp.load(c, R.raw.b1, 1);
				 if(midiRecord){
				    	noteTrack.insertNote(0, 35, 100, 480*tick, 120);
				    }
			}
			////////////////////////////////////////////////////////////////////////////
			else if(x==8){
			    sp.load(c, R.raw.c2, 1);
			    if(midiRecord){
			    	noteTrack.insertNote(0, 36, 100, 480*tick, 120);
			    }
			    	
				}
				else if(x==9){
					 sp.load(c, R.raw.d2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 38, 100, 480*tick, 120);
					    }
				}
				else if(x==10){
					 sp.load(c, R.raw.e2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 40, 100, 480*tick, 120);
					    }
				}
				else if(x==11){
					 sp.load(c, R.raw.f2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 41, 100, 480*tick, 120);
					    }
				}
				else if(x==12){
					 sp.load(c, R.raw.g2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 43, 100, 480*tick, 120);
					    }
				}
				else if(x==13){
					 sp.load(c, R.raw.a2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 45, 100, 480*tick, 120);
					    }
				}
				else if(x==14){
					 sp.load(c, R.raw.b2, 1);
					 if(midiRecord){
					    	noteTrack.insertNote(0, 47, 100, 480*tick, 120);
					    }
				}
			////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
				else if(x==15){
				    sp.load(c, R.raw.c3, 1);
				    if(midiRecord){
				    	noteTrack.insertNote(0, 48, 100, 480*tick, 120);
				    }
				    	
					}
					else if(x==16){
						 sp.load(c, R.raw.d3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 50, 100, 480*tick, 120);
						    }
					}
					else if(x==17){
						 sp.load(c, R.raw.e3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 52, 100, 480*tick, 120);
						    }
					}
					else if(x==18){
						 sp.load(c, R.raw.f3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 53, 100, 480*tick, 120);
						    }
					}
					else if(x==19){
						 sp.load(c, R.raw.g3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 55, 100, 480*tick, 120);
						    }
					}
					else if(x==20){
						 sp.load(c, R.raw.a3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 57, 100, 480*tick, 120);
						    }
					}
					else if(x==21){
						 sp.load(c, R.raw.b3, 1);
						 if(midiRecord){
						    	noteTrack.insertNote(0, 59, 100, 480*tick, 120);
						    }
					}
				////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
					else if(x==22){
					    sp.load(c, R.raw.c4, 1);
					    if(midiRecord){
					    	noteTrack.insertNote(0, 60, 100, 480*tick, 120);
					    }
					    	
						}
						else if(x==23){
							 sp.load(c, R.raw.d4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 62, 100, 480*tick, 120);
							    }
						}
						else if(x==24){
							 sp.load(c, R.raw.e4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 64, 100, 480*tick, 120);
							    }
						}
						else if(x==25){
							 sp.load(c, R.raw.f4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 65, 100, 480*tick, 120);
							    }
						}
						else if(x==26){
							 sp.load(c, R.raw.g4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 67, 100, 480*tick, 120);
							    }
						}
						else if(x==27){
							 sp.load(c, R.raw.a4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 69, 100, 480*tick, 120);
							    }
						}
						else if(x==28){
							 sp.load(c, R.raw.b4, 1);
							 if(midiRecord){
							    	noteTrack.insertNote(0, 71, 100, 480*tick, 120);
							    }
						}
					////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
						else if(x==29){
						    sp.load(c, R.raw.c5, 1);
						    if(midiRecord){
						    	noteTrack.insertNote(0, 72, 100, 480*tick, 120);
						    }
						    	
							}
							else if(x==30){
								 sp.load(c, R.raw.d5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 74, 100, 480*tick, 120);
								    }
							}
							else if(x==31){
								 sp.load(c, R.raw.e5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 76, 100, 480*tick, 120);
								    }
							}
							else if(x==32){
								 sp.load(c, R.raw.f5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 77, 100, 480*tick, 120);
								    }
							}
							else if(x==33){
								 sp.load(c, R.raw.g5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 79, 100, 480*tick, 120);
								    }
							}
							else if(x==34){
								 sp.load(c, R.raw.a5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 81, 100, 480*tick, 120);
								    }
							}
							else if(x==35){
								 sp.load(c, R.raw.b5, 1);
								 if(midiRecord){
								    	noteTrack.insertNote(0, 83, 100, 480*tick, 120);
								    }
							}
						////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
							else if(x==36){
							    sp.load(c, R.raw.c6, 1);
							    if(midiRecord){
							    	noteTrack.insertNote(0, 84, 100, 480*tick, 120);
							    }
							    	
								}
								else if(x==37){
									 sp.load(c, R.raw.d6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 86, 100, 480*tick, 120);
									    }
								}
								else if(x==38){
									 sp.load(c, R.raw.e6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 88, 100, 480*tick, 120);
									    }
								}
								else if(x==39){
									 sp.load(c, R.raw.f6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 89, 100, 480*tick, 120);
									    }
								}
								else if(x==40){
									 sp.load(c, R.raw.g6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 91, 100, 480*tick, 120);
									    }
								}
								else if(x==41){
									 sp.load(c, R.raw.a6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 93, 100, 480*tick, 120);
									    }
								}
								else if(x==42){
									 sp.load(c, R.raw.b6, 1);
									 if(midiRecord){
									    	noteTrack.insertNote(0, 95, 100, 480*tick, 120);
									    }
								}
							////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
								else if(x==43){
								    sp.load(c, R.raw.c7, 1);
								    if(midiRecord){
								    	noteTrack.insertNote(0, 96, 100, 480*tick, 120);
								    }
								    	
									}
									else if(x==44){
										 sp.load(c, R.raw.d7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 98, 100, 480*tick, 120);
										    }
									}
									else if(x==45){
										 sp.load(c, R.raw.e7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 100, 100, 480*tick, 120);
										    }
									}
									else if(x==46){
										 sp.load(c, R.raw.f7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 101, 100, 480*tick, 120);
										    }
									}
									else if(x==47){
										 sp.load(c, R.raw.g7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 103, 100, 480*tick, 120);
										    }
									}
									else if(x==48){
										 sp.load(c, R.raw.a7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 105, 100, 480*tick, 120);
										    }
									}
									else if(x==49){
										 sp.load(c, R.raw.b7, 1);
										 if(midiRecord){
										    	noteTrack.insertNote(0, 107, 100, 480*tick, 120);
										    }
									}
		////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////
									else if(x==50){
									    sp.load(c, R.raw.db1, 1);
									    if(midiRecord){
									    	noteTrack.insertNote(0, 25, 100, 480*tick, 120);
									    }
									    	
										}
										else if(x==51){
											 sp.load(c, R.raw.eb1, 1);
											 if(midiRecord){
											    	noteTrack.insertNote(0, 27, 100, 480*tick, 120);
											    }
										}
										else if(x==52){
											 sp.load(c, R.raw.gb1, 1);
											 if(midiRecord){
											    	noteTrack.insertNote(0, 30, 100, 480*tick, 120);
											    }
										}
										else if(x==53){
											 sp.load(c, R.raw.ab1, 1);
											 if(midiRecord){
											    	noteTrack.insertNote(0, 32, 100, 480*tick, 120);
											    }
										}
										else if(x==54){
											 sp.load(c, R.raw.bb1, 1);
											 if(midiRecord){
											    	noteTrack.insertNote(0, 34, 100, 480*tick, 120);
											    }
										}
			///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==55){
sp.load(c, R.raw.db2, 1);
if(midiRecord){
noteTrack.insertNote(0, 37, 100, 480*tick, 120);
}

}
else if(x==56){
sp.load(c, R.raw.eb2, 1);
if(midiRecord){
noteTrack.insertNote(0, 39, 100, 480*tick, 120);
}
}
else if(x==57){
sp.load(c, R.raw.gb2, 1);
if(midiRecord){
noteTrack.insertNote(0, 42, 100, 480*tick, 120);
}
}
else if(x==58){
sp.load(c, R.raw.ab2, 1);
if(midiRecord){
noteTrack.insertNote(0, 44, 100, 480*tick, 120);
}
}
else if(x==59){
sp.load(c, R.raw.bb2, 1);
if(midiRecord){
noteTrack.insertNote(0, 46, 100, 480*tick, 120);
}
}
///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==60){
sp.load(c, R.raw.db3, 1);
if(midiRecord){
noteTrack.insertNote(0, 49, 100, 480*tick, 120);
}

}
else if(x==61){
sp.load(c, R.raw.eb3, 1);
if(midiRecord){
noteTrack.insertNote(0, 51, 100, 480*tick, 120);
}
}
else if(x==62){
sp.load(c, R.raw.gb3, 1);
if(midiRecord){
noteTrack.insertNote(0, 54, 100, 480*tick, 120);
}
}
else if(x==63){
sp.load(c, R.raw.ab3, 1);
if(midiRecord){
noteTrack.insertNote(0, 56, 100, 480*tick, 120);
}
}
else if(x==64){
sp.load(c, R.raw.bb3, 1);
if(midiRecord){
noteTrack.insertNote(0, 58, 100, 480*tick, 120);
}
}
///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==65){
sp.load(c, R.raw.db4, 1);
if(midiRecord){
noteTrack.insertNote(0, 61, 100, 480*tick, 120);
}

}
else if(x==66){
sp.load(c, R.raw.eb4, 1);
if(midiRecord){
noteTrack.insertNote(0, 63, 100, 480*tick, 120);
}
}
else if(x==67){
sp.load(c, R.raw.gb4, 1);
if(midiRecord){
noteTrack.insertNote(0, 66, 100, 480*tick, 120);
}
}
else if(x==68){
sp.load(c, R.raw.ab4, 1);
if(midiRecord){
noteTrack.insertNote(0, 68, 100, 480*tick, 120);
}
}
else if(x==69){
sp.load(c, R.raw.bb4, 1);
if(midiRecord){
noteTrack.insertNote(0, 70, 100, 480*tick, 120);
}
}
///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==70){
sp.load(c, R.raw.db5, 1);
if(midiRecord){
noteTrack.insertNote(0, 73, 100, 480*tick, 120);
}

}
else if(x==71){
sp.load(c, R.raw.eb5, 1);
if(midiRecord){
noteTrack.insertNote(0, 75, 100, 480*tick, 120);
}
}
else if(x==72){
sp.load(c, R.raw.gb5, 1);
if(midiRecord){
noteTrack.insertNote(0, 78, 100, 480*tick, 120);
}
}
else if(x==73){
sp.load(c, R.raw.ab5, 1);
if(midiRecord){
noteTrack.insertNote(0, 80, 100, 480*tick, 120);
}
}
else if(x==74){
sp.load(c, R.raw.bb5, 1);
if(midiRecord){
noteTrack.insertNote(0, 82, 100, 480*tick, 120);
}
}
///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==75){
sp.load(c, R.raw.db6, 1);
if(midiRecord){
noteTrack.insertNote(0, 85, 100, 480*tick, 120);
}

}
else if(x==76){
sp.load(c, R.raw.eb6, 1);
if(midiRecord){
noteTrack.insertNote(0, 87, 100, 480*tick, 120);
}
}
else if(x==77){
sp.load(c, R.raw.gb6, 1);
if(midiRecord){
noteTrack.insertNote(0, 90, 100, 480*tick, 120);
}
}
else if(x==78){
sp.load(c, R.raw.ab6, 1);
if(midiRecord){
noteTrack.insertNote(0, 92, 100, 480*tick, 120);
}
}
else if(x==79){
sp.load(c, R.raw.bb6, 1);
if(midiRecord){
noteTrack.insertNote(0, 94, 100, 480*tick, 120);
}
}
///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////
else if(x==80){
sp.load(c, R.raw.db7, 1);
if(midiRecord){
noteTrack.insertNote(0, 97, 100, 480*tick, 120);
}

}
else if(x==81){
sp.load(c, R.raw.eb7, 1);
if(midiRecord){
noteTrack.insertNote(0, 99, 100, 480*tick, 120);
}
}
else if(x==82){
sp.load(c, R.raw.gb7, 1);
if(midiRecord){
noteTrack.insertNote(0, 102, 100, 480*tick, 120);
}
}
else if(x==83){
sp.load(c, R.raw.ab7, 1);
if(midiRecord){
noteTrack.insertNote(0, 104, 100, 480*tick, 120);
}
}
else if(x==84){
sp.load(c, R.raw.bb7, 1);
if(midiRecord){
noteTrack.insertNote(0, 106, 100, 480*tick, 120);
}
}
			
///////////////////////////////////////////
		    sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
		    {
		        @Override
		        public void onLoadComplete(SoundPool soundPool, int i, int i2)
		        {
		            soundPool.play(i, volume, volume, 1, 0, 1);
		            sound=i;
		        }
		    });
		   sp.unload(sound);
		//    
		}
		  public void startMidiRecording(){
	    	  midiRecord=true;
	    	  new Timer().scheduleAtFixedRate(new TimerTask() {
	    	      @Override
	    	      public void run() {
	    	   	   tick++;
	    	      }
	    	  }, 0, 280);
	    	}

public void keyColor(int pianoInt){
	if(pianoInt ==1){
		c.setText("C1");
		d.setText("D1");
		e.setText("E1");
		f.setText("F1");
		g.setText("G1");
		a.setText("A1");
		b.setText("B1");
		c.setBackgroundColor(Color.parseColor("#ff9aa5"));
		d.setBackgroundColor(Color.parseColor("#ff9aa5"));
		e.setBackgroundColor(Color.parseColor("#ff9aa5"));
		f.setBackgroundColor(Color.parseColor("#ff9aa5"));
		g.setBackgroundColor(Color.parseColor("#ff9aa5"));
		a.setBackgroundColor(Color.parseColor("#ff9aa5"));
		b.setBackgroundColor(Color.parseColor("#ff9aa5"));
	}
	else if(pianoInt ==2){
		c.setText("C2");
		d.setText("D2");
		e.setText("E2");
		f.setText("F2");
		g.setText("G2");
		a.setText("A2");
		b.setText("B2");
		c.setBackgroundColor(Color.parseColor("#a59aff"));
		d.setBackgroundColor(Color.parseColor("#a59aff"));
		e.setBackgroundColor(Color.parseColor("#a59aff"));
		f.setBackgroundColor(Color.parseColor("#a59aff"));
		g.setBackgroundColor(Color.parseColor("#a59aff"));
		a.setBackgroundColor(Color.parseColor("#a59aff"));
		b.setBackgroundColor(Color.parseColor("#a59aff"));
	}
else if(pianoInt ==3){
	c.setText("C3");
	d.setText("D3");
	e.setText("E3");
	f.setText("F3");
	g.setText("G3");
	a.setText("A3");
	b.setText("B3");
	c.setBackgroundColor(Color.parseColor("#a5ddff"));
	d.setBackgroundColor(Color.parseColor("#a5ddff"));
	e.setBackgroundColor(Color.parseColor("#a5ddff"));
	f.setBackgroundColor(Color.parseColor("#a5ddff"));
	g.setBackgroundColor(Color.parseColor("#a5ddff"));
	a.setBackgroundColor(Color.parseColor("#a5ddff"));
	b.setBackgroundColor(Color.parseColor("#a5ddff"));
	}
else if(pianoInt ==4){
	c.setText("C4");
	d.setText("D4");
	e.setText("E4");
	f.setText("F4");
	g.setText("G4");
	a.setText("A4");
	b.setText("B4");
	c.setBackgroundColor(Color.parseColor("#33aaff"));
	d.setBackgroundColor(Color.parseColor("#33aaff"));
	e.setBackgroundColor(Color.parseColor("#33aaff"));
	f.setBackgroundColor(Color.parseColor("#33aaff"));
	g.setBackgroundColor(Color.parseColor("#33aaff"));
	a.setBackgroundColor(Color.parseColor("#33aaff"));
	b.setBackgroundColor(Color.parseColor("#33aaff"));
}
else if(pianoInt ==5){
	c.setText("C5");
	d.setText("D5");
	e.setText("E5");
	f.setText("F5");
	g.setText("G5");
	a.setText("A5");
	b.setText("B5");
	c.setBackgroundColor(Color.parseColor("#ddffdd"));
	d.setBackgroundColor(Color.parseColor("#ddffdd"));
	e.setBackgroundColor(Color.parseColor("#ddffdd"));
	f.setBackgroundColor(Color.parseColor("#ddffdd"));
	g.setBackgroundColor(Color.parseColor("#ddffdd"));
	a.setBackgroundColor(Color.parseColor("#ddffdd"));
	b.setBackgroundColor(Color.parseColor("#ddffdd"));
}
else if(pianoInt ==6){
	c.setText("C6");
	d.setText("D6");
	e.setText("E6");
	f.setText("F6");
	g.setText("G6");
	a.setText("A6");
	b.setText("B6");
	c.setBackgroundColor(Color.parseColor("#a5ff9a"));
	d.setBackgroundColor(Color.parseColor("#a5ff9a"));
	e.setBackgroundColor(Color.parseColor("#a5ff9a"));
	f.setBackgroundColor(Color.parseColor("#a5ff9a"));
	g.setBackgroundColor(Color.parseColor("#a5ff9a"));
	a.setBackgroundColor(Color.parseColor("#a5ff9a"));
	b.setBackgroundColor(Color.parseColor("#a5ff9a"));
}
else if(pianoInt ==7){
	c.setText("C7");
	d.setText("D7");
	e.setText("E7");
	f.setText("F7");
	g.setText("G7");
	a.setText("A7");
	b.setText("B7");
	c.setBackgroundColor(Color.parseColor("#dddd60"));
	d.setBackgroundColor(Color.parseColor("#dddd60"));
	e.setBackgroundColor(Color.parseColor("#dddd60"));
	f.setBackgroundColor(Color.parseColor("#dddd60"));
	g.setBackgroundColor(Color.parseColor("#dddd60"));
	a.setBackgroundColor(Color.parseColor("#dddd60"));
	b.setBackgroundColor(Color.parseColor("#dddd60"));
}
}
public void screenSize(){
	Display display=getWindowManager().getDefaultDisplay();
	width=display.getWidth();
	height=display.getHeight();
	i1.getLayoutParams().width=(int)width/7;
	i2.getLayoutParams().width=(int)width/7;
	i3.getLayoutParams().width=(int)width/7;
	i4.getLayoutParams().width=(int)width/7;
	i5.getLayoutParams().width=(int)width/7;
	i6.getLayoutParams().width=(int)width/7;
	i7.getLayoutParams().width=(int)width/7;
	i1.getLayoutParams().height=(int)((height*2)/3);
	i2.getLayoutParams().height=(int)((height*2)/3);
	i3.getLayoutParams().height=(int)((height*2)/3);
	i4.getLayoutParams().height=(int)((height*2)/3);
	i5.getLayoutParams().height=(int)((height*2)/3);
	i6.getLayoutParams().height=(int)((height*2)/3);
	i7.getLayoutParams().height=(int)((height*2)/3);
	b1.getLayoutParams().width=(int)width/11;
	b2.getLayoutParams().width=(int)width/11;
	b3.getLayoutParams().width=(int)width/11;
	b4.getLayoutParams().width=(int)width/11;
	b5.getLayoutParams().width=(int)width/11;
	b1.getLayoutParams().height=(int)((height*7)/16);
	b2.getLayoutParams().height=(int)((height*7)/16);
	b3.getLayoutParams().height=(int)((height*7)/16);
	b4.getLayoutParams().height=(int)((height*7)/16);
	b5.getLayoutParams().height=(int)((height*7)/16);
	RelativeLayout.LayoutParams lp =
            (RelativeLayout.LayoutParams) b1.getLayoutParams();
	int dp1=(int)((13*width)/140);
	lp.leftMargin=dp1;
	b1.setLayoutParams(lp);
	RelativeLayout.LayoutParams lp2 =
            (RelativeLayout.LayoutParams) b2.getLayoutParams();
	int dp2=(int)((33*width)/140);
	lp2.leftMargin=dp2;
	b2.setLayoutParams(lp2);
	RelativeLayout.LayoutParams lp3 =
            (RelativeLayout.LayoutParams) b3.getLayoutParams();
	int dp3=(int)((73*width)/140);
	lp3.leftMargin=dp3;
	b3.setLayoutParams(lp3);
	RelativeLayout.LayoutParams lp4 =
            (RelativeLayout.LayoutParams) b4.getLayoutParams();
	int dp4=(int)((93*width)/140);
	lp4.leftMargin=dp4;
	b4.setLayoutParams(lp4);
	RelativeLayout.LayoutParams lp5 =
            (RelativeLayout.LayoutParams) b5.getLayoutParams();
	int dp5=(int)((113*width)/140);
	lp5.leftMargin=dp5;
	b5.setLayoutParams(lp5);
	RelativeLayout.LayoutParams lp6 =
            (RelativeLayout.LayoutParams) c.getLayoutParams();
	int dp6=(int)((20*width)/420);
	lp6.leftMargin=dp6;
	//lp6.leftMargin=lp.leftMargin;
	c.setLayoutParams(lp6);
	//piano.getLayoutParams().width=(int)((width*3)/7);
//	piano.getLayoutParams().height=(int)((height*1)/10);
/*	if(height >700 || width > 1000){
		forw.setImageResource(R.drawable.bigforward);
	    bak.setImageResource(R.drawable.bigbackward);
	    record.setImageResource(R.drawable.bigrecord);
	    big=true;
	    title.setTextSize(36);
	    
	}*/
}

		
}
