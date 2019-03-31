package com.example.pianoproject;

//import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import org.opencv.imgproc.Imgproc;

import com.leff.midi.MidiTrack;

import android.content.pm.PackageManager;
import android.media.AudioManager;

import android.media.SoundPool;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;;

public class OldPlayingClass extends Activity implements OnClickListener, CvCameraViewListener2{
	FrameLayout layout;
	TextView timerText;
	 private static final String    TAG = "OCVSample::Activity";
	// private CameraBridgeViewBase   mOpenCvCameraView;
	 private CustomizeCameraView   mOpenCvCameraView;
	 Mat frame,framegray;
	 private int click=0;
	 boolean midiRecord=false;
	 int tick=0;
	 float volume=0;
	 Mat fingerHeu;
	// Point pressedKey=new Point(0,0);
	 RecordingManager record_obj=new RecordingManager();
	 MidiTrack noteTrack = new MidiTrack();
	 private  List<List<Point>> whiteKeys=new ArrayList<List<Point>>();
	 private  List<List<Point>> blackKeys=new ArrayList<List<Point>>();
	 private  List<Point[]> whiteKeysArr=new ArrayList<Point[]>();
	 private  List<Point[]> blackKeysArr=new ArrayList<Point[]>();
	 
		boolean press=true;
		Point prK=new Point(0,0);
	// private List<List<Point> > fingersLocationFrame1=new ArrayList<List<Point> >();
	// private List<Point> refPoints=new ArrayList<Point>();
	 
	
	 List<List<Point>> returns=new ArrayList<List<Point>>();
	 List<List<Point>> frame1=new ArrayList<List<Point>>();
	 List<List<Point>> frame2=new ArrayList<List<Point>>();
	 List<List<Point>> frame3=new ArrayList<List<Point>>();
	
	 private int frameCount=0;
	 private Mat  binaryImage, mask ,keyboardImage;
	boolean rec=false;
    ImageView record,front,back,piano;
	int pianoInt=3;
	String recordType="";
	SoundPool   sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
	
	int sound=0;
	//SoundPool sound;
	
	/* int cID1,dID1,eID1,fID1,gID1,aID1,bID1,cID2,dID2,eID2,fID2,gID2,aID2,bID2,cID3,dID3,eID3,fID3,gID3,aID3,bID3,cID4,dID4,
	 eID4,fID4,gID4,aID4,bID4,cID5,dID5,eID5,fID5,gID5,aID5,bID5,
	 cID6,dID6,eID6,fID6,gID6,aID6,bID6,cID7,dID7,eID7,fID7,gID7,aID7,bID7,cID_1,dID_1,fID_1,gID_1,aID_1,cID_2,dID_2,fID_2,
	 gID_2,aID_2,cID_3,dID_3,fID_3,gID_3,aID_3,cID_4,dID_4,fID_4,gID_4,aID_4
	 ,cID_5,dID_5,fID_5,gID_5,aID_5,cID_6,dID_6,fID_6,gID_6,aID_6,cID_7,dID_7,fID_7,gID_7,aID_7;
	 */
	 
	/* SoundPool c1,d1,e1,f1,g1,a1,b1,c2,d2,e2,f2,g2,a2,b2,c3,d3,e3,f3,g3,a3,b3,c4,d4,e4,f4,g4,a4,b4,c5,d5,e5,f5,g5,a5,b5,
	 c6,d6,e6,f6,g6,a6,b6,c7,d7,e7,f7,g7,a7,b7,db1,d_1,gb1,g_1,bb1,db2,d_2,gb2,g_2,bb2,db3,d_3,gb3,g_3,bb3,db4,d_4,gb4,g_4,bb4
	 ,db5,d_5,gb5,g_5,bb5,db6,d_6,gb6,g_6,bb6,db7,d_7,gb7,g_7,bb7;
	 */
	 
	 //private Mat mask=new Mat();
	 private long startTime = 0L;
     private Handler customHandler = new Handler();
     long timeInMilliseconds = 0L;
     long timeSwapBuff = 0L;
     long updatedTime = 0L;
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

static{
	Log.i(TAG, "OpenCV loaded successfully");
	System.loadLibrary("opencv_java3");
	System.loadLibrary("piano_project");

}
//	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//	    @Override
//	    public void onManagerConnected(int status) {
//	        switch (status) {
//	            case LoaderCallbackInterface.SUCCESS:
//	            {
//	                Log.i(TAG, "OpenCV loaded successfully");
//					System.loadLibrary("opencv_java3");
//					System.loadLibrary("piano_project");
//	                mOpenCvCameraView.enableView();
//	            } break;
//	            default:
//	            {
//	                super.onManagerConnected(status);
//	            } break;
//	        }
//	    }
//	};
//
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.playing);
		layout=(FrameLayout)findViewById(R.id.PianoLayout);
		layout.setOnClickListener(this);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		 mOpenCvCameraView = ( CustomizeCameraView) findViewById(R.id.PianoProject);
	     mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	     mOpenCvCameraView.setCvCameraViewListener(this);

	     
	/*     for(int i=0;i<50;i++){
	    	 refPoints.add(new Point(0,0));
	     }*/
	 
	     timerText=(TextView) findViewById(R.id.timer);
	     record=(ImageView)findViewById(R.id.record);
	     record.setOnClickListener(this);
	     front=(ImageView)findViewById(R.id.frontt);
	     front.setOnClickListener(this);
	     back=(ImageView)findViewById(R.id.backk);
	     back.setOnClickListener(this);
	     piano=(ImageView)findViewById(R.id.piano);
	     registerForContextMenu(findViewById(R.id.record));
	     this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

	     AudioManager audio=(AudioManager) getSystemService(AUDIO_SERVICE);
			float actualVolume = (float) audio
		          .getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audio
		          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			volume = actualVolume / maxVolume;

		mOpenCvCameraView.enableView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	public AlertDialog AskOption(){

       final AlertDialog ad=new AlertDialog.Builder(OldPlayingClass.this).create();
		// TODO Auto-generated method stub
		ad.setTitle("Save");
		ad.setMessage("Save File?");
		ad.setButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				if(recordType.equals("midi")){
					record_obj.stopMidiRecording(noteTrack);
					 Toast.makeText(getApplicationContext(), "File Saved ", Toast.LENGTH_LONG).show();
					tick=0;
					midiRecord=false;
				
				}
				else if(recordType.equals("mic"))
					record_obj.stopRecording();
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
					record_obj.stopRecording();
			ad.dismiss();
			
			}
		});
        return ad;
    }
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.context_menu, menu);

	   
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.midi:
				recordType = "midi";
				if(record_obj.checkRecordingPermissions(this)){
					startMidiRecording();
				};
				return true;
			case R.id.mic:
				recordType = "mic";
				if(record_obj.checkRecordingPermissions(this)){
					startMicRecording();
				};
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		String TAG = "Permsission : ";
		if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
			Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
			if (recordType.equals("mic")) {
				startMicRecording();
			}else if (recordType.equals("midi")) {
				startMidiRecording();

			}

		}
	}
	public void startMicRecording(){
		try {

			record_obj.startRecording();
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

	}
	

@Override
	public void onClick(View v) {
		
		// TODO Auto-generated method stub
		if(v.getId()==R.id.PianoLayout){
		 click++; 
		
            if(blackKeysArr.size()!=10){
            Toast.makeText(getApplicationContext(), "White keys: "+ whiteKeys.size()+ " Black keys: "+ blackKeys.size(), Toast.LENGTH_LONG).show();
            whiteKeys.clear();
    		blackKeys.clear();
    		whiteKeysArr.clear();
    		blackKeysArr.clear();
            }
            else {
            
            	Toast.makeText(getApplicationContext(), "Detection Success  ", Toast.LENGTH_LONG).show();
        /*    for(int i=0;i<fingersLocationFrame1.size();i++){
           Toast.makeText(getApplicationContext(),"Points  "+fingersLocationFrame1.get(i).get(0),Toast.LENGTH_LONG).show();
           Toast.makeText(getApplicationContext(),"location  "+fingersLocationFrame1.get(i).get(1),Toast.LENGTH_LONG).show();
            }*/
            }
		}
		if(v.getId()==R.id.record){
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
				record.setImageResource(R.drawable.recordpressed);
				if(recordType.equals("mic")){
					
			// stop mic recording
					
				record_obj.stopRecording();
			        Toast.makeText(getApplicationContext(), "Audio recorded successfully",
			        Toast.LENGTH_LONG).show();
				}
				AlertDialog diaBox = AskOption();
				diaBox.show();
			}
			else if (rec==false){
				rec=true;
				record.setImageResource(R.drawable.stop);
				this.openContextMenu(v);
			//	 Toast.makeText(getApplicationContext(), "kasso   "+x, Toast.LENGTH_LONG).show();	
			}
			
		}
		if(v.getId()==R.id.frontt){
			
			if(pianoInt==1){
				piano.setImageResource(R.drawable.piano2);
				pianoInt=2;
			}
			else if(pianoInt==2){
				piano.setImageResource(R.drawable.piano3);
				pianoInt=3;
			}
			else if(pianoInt==3){
				piano.setImageResource(R.drawable.piano4);
				pianoInt=4;
			}
			else if(pianoInt==4){
				piano.setImageResource(R.drawable.piano5);
				pianoInt=5;
			}
			else if(pianoInt==5){
				piano.setImageResource(R.drawable.piano6);
				pianoInt=6;
			}
			
		}
        if(v.getId()==R.id.backk){
        	if(pianoInt==6){
				piano.setImageResource(R.drawable.piano5);
				pianoInt=5;
			}
			else if(pianoInt==5){
				piano.setImageResource(R.drawable.piano4);
				pianoInt=4;
			}
			else if(pianoInt==4){
				piano.setImageResource(R.drawable.piano3);
				pianoInt=3;
			}
			else if(pianoInt==3){
				piano.setImageResource(R.drawable.piano2);
				pianoInt=2;
			}
			else if(pianoInt==2){
				piano.setImageResource(R.drawable.piano1);
				pianoInt=1;
			}
		}
		
	}
	@Override
	public void onResume()
	{
	    super.onResume();
	//    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	 @Override
	 public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }
	 public void onDestroy() {
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	    
	 }
	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		framegray=new Mat();
		frame=new Mat();
//		 Size s=  mOpenCvCameraView.getResolution();
//		int h = s.height;
//	        int w = s.width;
//		   double x=(h * w)/1024000;
//		   List<Size> mResolutionList;
//		   mResolutionList = mOpenCvCameraView.getResolutionList();
//
////
//		   if( x > 8){
//			   for(int i=0;i<mResolutionList.size();i++){
//				   double r=(mResolutionList.get(i).height *mResolutionList.get(i).width)/1024000;
//				//   Toast.makeText(getApplicationContext(), "hight: "+ h+"width: "+w, Toast.LENGTH_LONG).show();
//				   if(r <=8){
//					   h=mResolutionList.get(i).height;
//					   w=mResolutionList.get(i).width;
//					   break;
//				   }
//			   }
//
//		        mOpenCvCameraView.setResolution(h, w);
//		   }

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		if(frameCount==3) frameCount=1;
	  	else frameCount++;
	
		frame=inputFrame.rgba();
  	
    if(click==1){
    	    
	framegray=inputFrame.gray(); 
	detectKeyboard(framegray);
	if(blackKeysArr.size()!=10) {
		click=0;
	}
	else{
	click=2;
  
  
	}
}
if(blackKeysArr.size()==10){	
	
returns=detectFinger(frame.getNativeObjAddr(),mask.getNativeObjAddr(),whiteKeysArr,blackKeysArr);

//skinColor(frame);
sortR(returns);
//for(int i=0;i<returns.size();i++){
//	fingersLocationFrame1.add(returns.get(i));
	//fingersLocationFrame1.addAll(returns);
//}
if(frameCount==1){
	frame1.clear();
	frame1.addAll(returns);	
	}

else if(frameCount==2){
	
	frame2.clear();
	frame2.addAll(returns);	
}
else if(frameCount==3){
	frame3.clear();
	frame3.addAll(returns);	
}
if(returns.size()==0)
	press=true;

detectKeyStrokes(frame1,frame2,frame3);
	
returns.clear();
	
	 return frame;
} 
else return frame;
	
		
	}
	
	
	public void detectKeyboard(Mat srcImage){
     	
    	 // convert to binary by thresholding
       // MinMaxLocResult mmc=Core.minMaxLoc(srcImage);
     //   double max=mmc.maxVal;
       // double min=mmc.minVal;
       // double thresh=(max+min)/2;
        Imgproc.threshold(srcImage, srcImage, 100, 255, Imgproc.THRESH_OTSU);
        binaryImage=srcImage.clone();
//      Mat b=srcImage.clone();

//........................... White keys detection ....................................//	
	       
	      // Find contours 
	        
	         MatOfPoint2f mMOP2f1=new MatOfPoint2f(), mMOP2f2=new MatOfPoint2f();
	         List<MatOfPoint> whiteMatKeys=new ArrayList<MatOfPoint>();
	       	 List<MatOfPoint> whiteContours = new ArrayList<MatOfPoint>();
	      	 Mat hierarchy = new Mat();
	      	 
	      	Imgproc.findContours(srcImage, whiteContours, hierarchy, Imgproc.RETR_LIST , Imgproc.CHAIN_APPROX_SIMPLE);
	       
	       	for (int contourIdx = 0; contourIdx < whiteContours.size(); contourIdx++) {
	        		Mat con= whiteContours.get(contourIdx);
	        		double area=Imgproc.contourArea(con);
	            // Ignore small and large contours
	        		
	        		if(area>300 && area < 5000){			
	           //Convert contours(i) from MatOfPoint to MatOfPoint2f
	        
	                whiteContours.get(contourIdx).convertTo(mMOP2f1, CvType.CV_32FC2);
	           //Epsilon for polygon transformation (take it from the arc length of the contour)
	                
	                double epsilon= 0.01 * Imgproc.arcLength(mMOP2f1, true);
	           //Processing on mMOP2f1 which is in type MatOfPoint2f and approximate to polygon
	                
	                Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, epsilon, true); 
	          // Take polygons which have specific number of sides
	                
	                if(mMOP2f2.size().height>5 && mMOP2f2.size().height<10){
	            //Convert back to MatOfPoint and put the new values back into the contours list
	                	
	        	   mMOP2f2.convertTo(whiteContours.get(contourIdx), CvType.CV_32S);	
	          // Put key in white keyList
	        	   
	        	   whiteMatKeys.add(whiteContours.get(contourIdx));
	          // Draw white keys on source image
	        	  
	    //     Imgproc.drawContours(srcImage, whiteContours, contourIdx, new Scalar(255,0,0,255), -1);
	           }
	        		}}
	    	hierarchy.release();
	    // convert MatOfPoint to Point
	       	
	     	List<Point> p=new ArrayList<Point>();
	       	
	     if(whiteMatKeys.size()!=0){
	       	for(int i=0;i<whiteMatKeys.size();i++){
	       		p=whiteMatKeys.get(i).toList();
	       		whiteKeys.add(p);
	       	} 
	   // sort white keys according to their x position
	        if(whiteKeys.size()==14){ 	
	       	sort(whiteKeys);
	      
		     for(int i=0;i<whiteKeys.size();i++){
		    	 Point wpoint[]=new Point[whiteKeys.get(i).size()];
		    	 for(int j=0;j< whiteKeys.get(i).size();j++){
		    		 wpoint[j]=whiteKeys.get(i).get(j);
		    	 }
		    	 whiteKeysArr.add(wpoint);
		     }
	     }
	     }
//........................... Black keys detection ..........................................//	
	  
	        keyboardImage=new Mat();
 	      // 	keyboard=new Mat();
	        List<MatOfPoint> blackMatKeys=new ArrayList<MatOfPoint>();
	       	if(whiteKeys.size()==14){
	       	int maxLoc=0, minyLoc1=0,miny2Loc1=0,minLoc=0, minyLoc2=0, miny2Loc2=0;
	       	double maxVal=0, minyVal1=whiteKeys.get(13).get(0).y;
	       	double minVal= whiteKeys.get(0).get(0).x,minyVal2=whiteKeys.get(0).get(0).y ;
	       	
	       	for(int i=0;i<whiteKeys.get(13).size();i++){
	       		Point pp=whiteKeys.get(13).get(i);
	       		if(pp.x>maxVal){
	       			maxVal=pp.x;
	       			maxLoc=i;
	       		} 
	       	}
	    	for(int i=0;i<whiteKeys.get(13).size();i++){
	       		Point pp=whiteKeys.get(13).get(i);
	       		if(pp.y<minyVal1 ){
	       			minyVal1=pp.y;
	       			minyLoc1=i;
	       		} 
	       	}
	    	double miny2Val1;
	    	if(whiteKeys.get(13).get(0).y==minyVal1){
	    		miny2Val1=whiteKeys.get(13).get(2).y;
	    	}
	    	else miny2Val1=whiteKeys.get(13).get(0).y;
	    	
	    	for(int i=0;i<whiteKeys.get(13).size();i++){
	       		Point pp=whiteKeys.get(13).get(i);
	       		if(pp.y<miny2Val1 && i != minyLoc1){
	       			miny2Val1=pp.y;
	       			miny2Loc1=i;
	       		} 
	       	}
	    	
	    	int maxChoice=0;
	       if(whiteKeys.get(13).get(miny2Loc1).x > whiteKeys.get(13).get(minyLoc1).x ){
	    	   maxChoice=miny2Loc1;
	       } 
	       else
	    	   maxChoice=minyLoc1;

	       for(int i=0;i<whiteKeys.get(0).size();i++){
	       		Point pp=whiteKeys.get(0).get(i);
	       		if(pp.x<minVal){
	       			minVal=pp.x;
	       			minLoc=i;
	       		} 
	       	}
	       for(int i=0;i<whiteKeys.get(0).size();i++){
	       		Point pp=whiteKeys.get(0).get(i);
	       		if(pp.y<minyVal2 ){
	       			minyVal2=pp.y;
	       			minyLoc2=i;
	       		} 
	       	}
	       double miny2Val2;
	    	if(whiteKeys.get(0).get(0).y==minyVal2){
	    		miny2Val2=whiteKeys.get(0).get(2).y;
	    	}
	    	else miny2Val2=whiteKeys.get(0).get(0).y;
	    	
	    	for(int i=0;i<whiteKeys.get(0).size();i++){
	       		Point pp=whiteKeys.get(0).get(i);
	       		if(pp.y<miny2Val2 && i != minyLoc2){
	       			miny2Val2=pp.y;
	       			miny2Loc2=i;
	       		} 
	       	}
	    	int minChoice=0;
	       if(whiteKeys.get(0).get(minyLoc2).x < whiteKeys.get(0).get(miny2Loc2).x ){
	    	   minChoice=minyLoc2;
	       } 
	       else
	    	   minChoice=miny2Loc2;
	   
	        // Get Corner points
	       	
	       	Point p1= whiteKeys.get(13).get(maxChoice); 
	       	p1.y=p1.y+3;
	    	Point p2= whiteKeys.get(13).get(maxLoc); 
	    	p2.y=p2.y-3;
	    	Point p3= whiteKeys.get(0).get(minLoc);
	    	p3.y=p3.y-3;
	    	Point p4= whiteKeys.get(0).get(minChoice);
	    	p4.y=p4.y+3;
	    	List<MatOfPoint>pts=new ArrayList<MatOfPoint>();
	    	pts.add(0, new MatOfPoint(p1,p2,p3,p4));
	    // create temporary image that will hold the mask
	    
	        mask=new Mat(binaryImage.size(), CvType.CV_8UC1, new Scalar(0));
	    	// draw your contour (Point list) in mask
	    	
	    	Imgproc.drawContours(mask, pts, 0, new Scalar(255,0,0,255), -1);
	   // 	Imgproc.drawContours(mask2, pts, 0, new Scalar(255,0,0,255), -1);
	    	// copy only non-zero pixels from your image to original image
	    	
	    	binaryImage.copyTo(keyboardImage, mask);
	    	
	        // Inverse mask only
	    //	keyboard=keyboardImage.clone();
	    	Core.bitwise_not(keyboardImage, keyboardImage, mask);
	    	// Find contours for black keys

	    	 MatOfPoint2f mMOP2f3=new MatOfPoint2f(), mMOP2f4=new MatOfPoint2f();
        	List<MatOfPoint> blackContours = new ArrayList<MatOfPoint>();
        	 Mat hierarchy2 = new Mat(); 
       	     Imgproc.findContours(keyboardImage, blackContours, hierarchy2, Imgproc.RETR_LIST , Imgproc.CHAIN_APPROX_SIMPLE);
    	     for (int contourIdx = 0; contourIdx < blackContours.size(); contourIdx++) {
  		 Mat con= blackContours.get(contourIdx);
  		 double area=Imgproc.contourArea(con);
  	//	 Ignore small contours
  		 
  		 if(area>350){	
  			blackContours.get(contourIdx).convertTo(mMOP2f3, CvType.CV_32FC2);
              double epsilon= 0.01 * Imgproc.arcLength(mMOP2f3, true);
              Imgproc.approxPolyDP(mMOP2f3, mMOP2f4, epsilon, true); 
      // Take polygons which have specific sides

           if(mMOP2f4.size().height>5 ){
  	     mMOP2f4.convertTo(blackContours.get(contourIdx), CvType.CV_32S);	
  	     blackMatKeys.add(blackContours.get(contourIdx));
    //       Imgproc.drawContours(keyboardImage, blackContours, contourIdx, new Scalar(255,0,0,255), -1);
     }
  		}
  		 }
    	     hierarchy2.release();
	       	}
	       	
          // convert MatOfPoint to Point
	       	
	     	List<Point> pblack=new ArrayList<Point>();
	      
	       	if(blackMatKeys.size()!=0){
	       	for(int i=0;i<blackMatKeys.size();i++){
	       		pblack=blackMatKeys.get(i).toList();
	       		blackKeys.add(pblack);
	       	} 
	        // sort black keys according to their x position
	        if(blackKeys.size()==10){
	       	sort(blackKeys);
	       
		     for(int i=0;i<blackKeys.size();i++){
		    	 Point bpoint[]=new Point[blackKeys.get(i).size()];
		    	 for(int j=0;j< blackKeys.get(i).size();j++){
		    		 bpoint[j]=blackKeys.get(i).get(j);
		    	 }
		   
		    	 blackKeysArr.add(bpoint);
		     }
	        }
	       	}
}
	public double maxVal(List<Point> keys){
        	
		
		double max=0;
	  	for(int i=0;i<keys.size();i++){
       		Point p=keys.get(i);
       		
       		if(p.x>max){
       			max=p.x;
       			
       		} 
       	}
	  	return  max;
	}
	
	public void sort(List<List<Point>> contours){
		

		Collections.sort(contours, new Comparator<List<Point>>(){

			@Override
			public int compare(List<Point> arg0, List<Point> arg1) {
				// TODO Auto-generated method stub
				return (maxVal(arg0) < maxVal(arg1)) ? -1 : (maxVal(arg0) > maxVal(arg1)) ? 1 : 0;
			}

		});
			
		}
	
public void sortR(List<List<Point>> returns){
		

		Collections.sort(returns, new Comparator<List<Point>>(){

			@Override
			public int compare(List<Point> arg0, List<Point> arg1) {
				// TODO Auto-generated method stub
				return (arg0.get(0).x > arg1.get(0).x) ? -1 : (arg0.get(0).x < arg1.get(0).x) ? 1 : 0;
			}

		});
			
		}
	

    public void detectKeyStrokes(List<List<Point>> frame1,List<List<Point>>frame2, List<List<Point>> frame3 ){
	
     int size1=frame1.size();
	 int size2=frame2.size();
	 int size3=frame3.size();
	 
	 int min=size1; int i=0, f1=0,f2=0,f3=0;
	 double maxVal1=0,maxVal2=0,maxVal3=0, max=0;
	 
	 if(size1 != 0 && size2 !=0 && size3 !=0){
		
	 min=size1;	
	 if(size2<min ) min=size2;
	 if(size3<min) min=size3;
	 
	 
		 maxVal1=frame1.get(0).get(0).y;
		 for(int j=0;j<min;j++){
		 if(frame1.get(j).get(0).y> maxVal1){
			 maxVal1=frame1.get(j).get(0).y;
			 f1=j;
		 }
		 }
	 
	 
		 maxVal2=frame2.get(0).get(0).y;
		 for(int j=0;j<min;j++){
		 if(frame2.get(j).get(0).y> maxVal2){
			 maxVal2=frame2.get(j).get(0).y;
			f2=j;
		 }
		 }
	 
	 
		 maxVal3=frame3.get(0).get(0).y;
		 for(int j=0;j<min;j++){
		 if(frame3.get(j).get(0).y> maxVal3){
			 maxVal3=frame3.get(j).get(0).y;
			 f3=j;
		 }
		 }
	 max=maxVal1;i=f1;
	if(maxVal2>max) {
		max=maxVal2;
		i=f2;
	}
	if(maxVal3>max){
		max=maxVal3;
		i=f3;
	}
	
		 if( 
			(frame1.get(i).get(0).x<=frame2.get(i).get(0).x+2 &&
			frame1.get(i).get(0).x>=frame2.get(i).get(0).x-2 &&
			frame1.get(i).get(0).y<=frame2.get(i).get(0).y+2 &&
			frame1.get(i).get(0).y>=frame2.get(i).get(0).y-2)||
		
			(frame2.get(i).get(0).x<=frame3.get(i).get(0).x+2 &&
			frame2.get(i).get(0).x>=frame3.get(i).get(0).x-2 &&
			frame2.get(i).get(0).y<=frame3.get(i).get(0).y+2 &&
			frame2.get(i).get(0).y>=frame3.get(i).get(0).y-2)||
		
			(/*frame1.get(i).get(0).x>=frame2.get(i).get(0).x-1 &&
			frame1.get(i).get(0).x<=frame2.get(i).get(0).x+1 &&
			frame3.get(i).get(0).x>=frame2.get(i).get(0).x-1 &&
			frame3.get(i).get(0).x<=frame2.get(i).get(0).x+1 &&*/
			frame1.get(i).get(1).x==frame2.get(i).get(1).x &&
			frame2.get(i).get(1).x==frame3.get(i).get(1).x &&
			frame1.get(i).get(1).y==frame2.get(i).get(1).y &&
			frame2.get(i).get(1).y==frame3.get(i).get(1).y &&
					
		    frame1.get(i).get(0).y<frame2.get(i).get(0).y &&
		    frame2.get(i).get(0).y>frame3.get(i).get(0).y)  ||
		    
		    (/*frame1.get(i).get(0).x>=frame3.get(i).get(0).x-1 &&
			frame1.get(i).get(0).x<=frame3.get(i).get(0).x+1 &&
			frame2.get(i).get(0).x>=frame3.get(i).get(0).x-1 &&
			frame2.get(i).get(0).x<=frame3.get(i).get(0).x+1 &&*/
		    		frame1.get(i).get(1).x==frame2.get(i).get(1).x &&
					frame2.get(i).get(1).x==frame3.get(i).get(1).x &&
					frame1.get(i).get(1).y==frame2.get(i).get(1).y &&
					frame2.get(i).get(1).y==frame3.get(i).get(1).y &&
		    frame1.get(i).get(0).y<frame3.get(i).get(0).y &&
		    frame3.get(i).get(0).y>frame2.get(i).get(0).y)  ||
		    
		    (/*frame2.get(i).get(0).x>=frame1.get(i).get(0).x-1 &&
			frame2.get(i).get(0).x<=frame1.get(i).get(0).x+1 &&
			frame3.get(i).get(0).x>=frame1.get(i).get(0).x-1 &&
			frame3.get(i).get(0).x<=frame1.get(i).get(0).x+1 &&*/
		    		frame1.get(i).get(1).x==frame2.get(i).get(1).x &&
					frame2.get(i).get(1).x==frame3.get(i).get(1).x &&
					frame1.get(i).get(1).y==frame2.get(i).get(1).y &&
					frame2.get(i).get(1).y==frame3.get(i).get(1).y &&
		    frame2.get(i).get(0).y<frame1.get(i).get(0).y &&
		    frame1.get(i).get(0).y>frame3.get(i).get(0).y)  
		    ){
			 
			 Point key=frame2.get(i).get(1);
			
			 if(!press &&  prK.x==key.x &&prK.y==key.y){}
			 else{
             selectSound(key);
             press=false;
             prK.x=key.x;
	         prK.y=key.y;
            		 
			 }
            /* frame1.clear();
             frame2.clear();
             frame3.clear();
             frame1.add(def);
             frame2.add(def);
             frame3.add(def);
             */
            
  //           break;
		 
	 }
		 else if((frame1.get(i).get(0).x<=frame3.get(i).get(0).x+2 &&
			 frame1.get(i).get(0).x>=frame3.get(i).get(0).x-2 &&
			 frame1.get(i).get(0).y<=frame3.get(i).get(0).y+2 &&
			 frame1.get(i).get(0).y>=frame3.get(i).get(0).y-2)){
		 
		 Point key=frame1.get(i).get(1);
			
		 if(!press &&  prK.x==key.x &&prK.y==key.y){}
		 else{
         selectSound(key);
         press=false;
         prK.x=key.x;
         prK.y=key.y;
        		 
		 }
		 
		 /* frame1.clear();
          frame2.clear();
          frame3.clear();
          frame1.add(def);
          frame2.add(def);
          frame3.add(def);
        */
    //     break;
	// }
	 }
	 
	
	 }
	 /*
	if(frameCount==3){
	 if(size2 !=0 && size1==0 && size3==0) {
		 k+="E";
		 Point key=frame2.get(i).get(1);
			
		 if(!press &&  prK.x==key.x &&prK.y==key.y){}
		 else{
         selectSound(key);
         press=false;
         prK.x=key.x;
         prK.y=key.y;
        		 
		 }
	       return;
		 }
	}
	 
		 else if(size1!=0 && size2==0 && size3==0) {
			 k+="F";
			  Point key=frame1.get(0).get(1);		
	      selectSound(key);
	     // frame1.clear();
	      return;
		 }
		 else if(size3!=0 && size1==0 && size2==0) {
			 k+="G";
			  Point key=frame3.get(0).get(1);		
	      selectSound(key);
	   //   frame3.clear();
	      return;
		 }
	}*/
	 }
 /*
    public void detectKeyStrokes(List<List<Point> > returns){
/*  int size= returns.size();  Point keys=new Point(0,0);
  if(size==0){
	  if(press==0){
		  press=1;
	  } else if(press==2){
		  refPoints.set(0, new Point(0,0));
		  selectSound(keys);
		  press=0;
	  }
	
  }
  else if(size!=0) {
	  if(press==1){
		  press=2;
		   keys=returns.get(0).get(1);
	  } else {
		  press=0;
	  }
  }
    	for(int i=0;i<returns.size();i++){
    		fingersLocationFrame1.add(returns.get(i));
    		
    /*		 if(returns.get(i).get(0).x <= refPoints.get(i).x+2 &&
    			returns.get(i).get(0).x >= refPoints.get(i).x-2 &&
    			returns.get(i).get(0).y <= refPoints.get(i).y+2 &&
    			returns.get(i).get(0).y >= refPoints.get(i).y-2
    			){
    			 refPoints.set(i, new Point(0,0));
    			  Point key=returns.get(i).get(1);
    			  selectSound(key);
    			  break;
    		  }
    		else if(returns.get(i).get(0).y >= refPoints.get(i).y){
		  refPoints.set(i, returns.get(i).get(0));
	  } 
	 
	  else {
		  refPoints.set(i, new Point(0,0));
		  Point key=returns.get(i).get(1);
		  selectSound(key);
		  break;
	  }
  }
  
    }*/
    public native List<List<Point>>  detectFinger(long frame, long mask, List<Point[] > whiteKeys ,List<Point[] > blackKeys);
    
   
    public void startMidiRecording(){
    	  midiRecord=true;
    	  new Timer().scheduleAtFixedRate(new TimerTask() {
    	      @Override
    	      public void run() {
    	   	   tick++;
    	      }
    	  }, 0, 280);
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
    	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
}
public void selectSound(Point key){
	 if(key.x==1){
     	if(key.y==13){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 1, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 8, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 15, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 22, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 29, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 36, midiRecord);
     		}
     	}
			
     	else if(key.y==12){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 2, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 9, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 16, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 23, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 30, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 37, midiRecord);
     		}
     	}
     	
     	else if(key.y==11){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 3, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 10, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 17, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 24, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 31, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 38, midiRecord);
     		}
     	}
     	else if(key.y==10){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 4, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 11, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 18, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 25, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 32, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 39, midiRecord);
     		}
     	}
     	else if(key.y==9){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 5, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 12, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 19, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 26, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 33, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 40, midiRecord);
     		}
     	}
     	else if(key.y==8){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 6, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 13, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 20, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 27, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 34, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 41, midiRecord);
     		}
     	}
     	else if(key.y==7){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 7, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 14, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 21, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 28, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 35, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 42, midiRecord);
     		}
     	}
     	else if(key.y==6){
     		 if(pianoInt==1){
     			 playKey(getApplicationContext(), 8, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 15, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 22, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 29, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 36, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 43, midiRecord);
     		}
     	}
     	else if(key.y==5)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 9, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 16, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 23, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 30, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 37, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 44, midiRecord);
     		}
         	}
     	else if(key.y==4)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 10, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 17, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 24, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 31, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 38, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 45, midiRecord);
     		}
         	}
     	else if(key.y==3)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 11, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 18, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 25, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 32, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),39 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 46, midiRecord);
     		}
         	}
     	else if(key.y==2)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(),12 , midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),19 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 26, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 33, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),40 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 47, midiRecord);
     		}
         	}
     	else if(key.y==1)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 13, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),20 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 27, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(),34 , midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 41, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 48, midiRecord);
     		}
         	}
     	else if(key.y==0){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 14, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 21, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),28 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 35, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),42 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 49, midiRecord);
     		}
     }
     }
     else if(key.x==0){
     	if(key.y==9){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 50, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 55, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 60, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 65, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 70, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 75, midiRecord);
     		}
     	}
			
     	else if(key.y==8){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 51, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 56, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 61, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 66, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),71 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 76, midiRecord);
     		}
     	}
     	
     	else if(key.y==7){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 52, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),57 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 62, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 67, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 72, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 77, midiRecord);
     		}
     	}
     	else if(key.y==6){
     		if(pianoInt==1){
     			playKey(getApplicationContext(),53 , midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),58 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),63 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 68, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),73 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 78, midiRecord);
     		}
     	}
     	else if(key.y==5){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 54, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 59, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),64 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 69, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),74 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 79, midiRecord);
     		}
     	}
     	else if(key.y==4){
     		if(pianoInt==1){
     			playKey(getApplicationContext(),55 , midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 60, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 65, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(),70 , midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 75, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(),80 , midiRecord);
     		}
     	}
     	else if(key.y==3){
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 56, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),61 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),66 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(),71 , midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),76 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(), 81, midiRecord);
         			noteTrack.insertNote(0, 99, 100, 480*tick, 120);
     		}
     	}
     	else if(key.y==2){
     		if(pianoInt==1){
     			playKey(getApplicationContext(),57 , midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(),62 , midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),67 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 72, midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 77, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(),82 , midiRecord);
     		}
     	}
     	else if(key.y==1)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 58, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 63, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(), 68, midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(),73 , midiRecord);
     		}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(),78 , midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(),83 , midiRecord);
     		}
         	}
     	else if(key.y==0)
         	{
     		if(pianoInt==1){
     			playKey(getApplicationContext(), 59, midiRecord);
     		}
     		else if(pianoInt==2){
     			playKey(getApplicationContext(), 64, midiRecord);
     		}
     		else if(pianoInt==3){
     			playKey(getApplicationContext(),69 , midiRecord);
     		}
     		else if(pianoInt==4){
     			playKey(getApplicationContext(), 74, midiRecord);              
     			}
     		else if(pianoInt==5){
     			playKey(getApplicationContext(), 79, midiRecord);
     		}
     		else if(pianoInt==6){
     			playKey(getApplicationContext(),84 , midiRecord);
     		}
         	}     					
     	}
}
}

