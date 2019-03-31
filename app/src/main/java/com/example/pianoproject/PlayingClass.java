package com.example.pianoproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leff.midi.MidiTrack;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlayingClass extends Activity implements OnClickListener, CvCameraViewListener2 {
    FrameLayout layout;
    TextView timerText;
    private static final String TAG = "OCVSample::Activity";
    private CustomizeCameraView mOpenCvCameraView;
    Mat frame, framegray;
    private int click = 0;
    boolean midiRecord = false;
    int tick = 0;
    float volume = 0;
    Mat fingerHeu;
    RecordingManager record_obj = new RecordingManager();
    MidiTrack noteTrack = new MidiTrack();
    private List<List<Point>> whiteKeys = new ArrayList<>();
    private List<List<Point>> blackKeys = new ArrayList<>();
    private List<Point[]> whiteKeysArr = new ArrayList<>();
    private List<Point[]> blackKeysArr = new ArrayList<>();
    private Map<Integer, Integer> tunesSoundMap = new HashMap<>();
    boolean press = true;
    Point prK = new Point(0, 0);

    List<List<Point>> returns = new ArrayList<>();
    List<List<Point>> frame1 = new ArrayList<>();
    List<List<Point>> frame2 = new ArrayList<>();
    List<List<Point>> frame3 = new ArrayList<>();

    private int frameCount = 0;
    private Mat binaryImage, mask, keyboardImage;
    boolean rec = false;
    ImageView record, front, back, piano;
    int pianoInt = 4;
    String recordType = "";
    SoundPool soundPool;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    ProgressBar simpleProgressBar;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            timerText.setText("" + mins + ":"

                    + String.format("%02d", secs));

            customHandler.postDelayed(this, 0);
        }

    };

    static {
        Log.i(TAG, "OpenCV loaded successfully");
        System.loadLibrary("opencv_java3");
        System.loadLibrary("piano_project");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        layout = (FrameLayout) findViewById(R.id.PianoLayout);
        layout.setOnClickListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mOpenCvCameraView = (CustomizeCameraView) findViewById(R.id.PianoProject);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        timerText = (TextView) findViewById(R.id.timer);
        record = (ImageView) findViewById(R.id.record);
        record.setOnClickListener(this);
        front = (ImageView) findViewById(R.id.frontt);
        front.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.backk);
        back.setOnClickListener(this);
        piano = (ImageView) findViewById(R.id.piano);
        registerForContextMenu(findViewById(R.id.record));
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (checkCameraPermissions()) {
            mOpenCvCameraView.enableView();
            simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
            LoadingManager loader = new LoadingManager(this, simpleProgressBar, soundPool, tunesSoundMap);
            loader.LoadSounds();

            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                    if(tunesSoundMap.size() == 85 && tunesSoundMap.get(85) == soundId){
                        simpleProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public AlertDialog AskOption() {

        final AlertDialog ad = new AlertDialog.Builder(PlayingClass.this).create();
        // TODO Auto-generated method stub
        ad.setTitle("Save");
        ad.setMessage("Save File?");
        ad.setButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub

                if (recordType.equals("midi")) {
                    record_obj.stopMidiRecording(noteTrack);
                    Toast.makeText(getApplicationContext(), "File Saved ", Toast.LENGTH_LONG).show();
                    tick = 0;
                    midiRecord = false;

                } else if (recordType.equals("mic"))
                    record_obj.stopRecording();
            }
        });

        ad.setButton2("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                if (recordType.equals("midi")) {
                    tick = 0;
                    midiRecord = false;
                } else if (recordType.equals("mic"))
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
                if (record_obj.checkRecordingPermissions(this)) {
                    startMidiRecording();
                }
                ;
                return true;
            case R.id.mic:
                recordType = "mic";
                if (record_obj.checkRecordingPermissions(this)) {
                    startMicRecording();
                }
                ;
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean checkCameraPermissions() {
        String TAG = "Permsission : ";
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String TAG = "Permsission : ";
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            if (recordType.equals("mic")) {
                startMicRecording();
            } else if (recordType.equals("midi")) {
                startMidiRecording();
            } else {
                mOpenCvCameraView.enableView();
            }

        }
    }

    public void startMicRecording() {
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
        if (v.getId() == R.id.PianoLayout) {
            click++;

            if (blackKeysArr.size() != 10) {
                Toast.makeText(getApplicationContext(), "White keys: " + whiteKeys.size() + " Black keys: " + blackKeys.size(), Toast.LENGTH_LONG).show();
                whiteKeys.clear();
                blackKeys.clear();
                whiteKeysArr.clear();
                blackKeysArr.clear();
            } else {

                Toast.makeText(getApplicationContext(), "Detection Success  ", Toast.LENGTH_LONG).show();
            }
        }
        if (v.getId() == R.id.record) {
            if (rec == true || midiRecord == true) {
                // stop timer

                customHandler.removeCallbacks(updateTimerThread);
                // restart timer
                startTime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedTime = 0L;
                timerText.setText("00:00");
            }
            if (rec == true) {
                rec = false;
                record.setImageResource(R.drawable.recordpressed);
                if (recordType.equals("mic")) {

                    // stop mic recording

                    record_obj.stopRecording();
                    Toast.makeText(getApplicationContext(), "Audio recorded successfully",
                            Toast.LENGTH_LONG).show();
                }
                AlertDialog diaBox = AskOption();
                diaBox.show();
            } else if (rec == false) {
                rec = true;
                record.setImageResource(R.drawable.stop);
                this.openContextMenu(v);
            }

        }
        if (v.getId() == R.id.frontt) {
            if (pianoInt != 6) {
                pianoInt++;
                changeImage();
            }


        }
        if (v.getId() == R.id.backk) {
            if (pianoInt != 1) {
                pianoInt--;
                changeImage();
            }
        }

    }

    public void changeImage() {
        int resId = getResId("piano" + pianoInt);
        piano.setImageResource(resId);
    }

    public int getResId(String resName) {

        try {

            int idField = this.getResources().getIdentifier(resName, "drawable", this.getPackageName());
            return idField;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
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
        framegray = new Mat();
        frame = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        // TODO Auto-generated method stub

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // TODO Auto-generated method stub
        if (frameCount == 3) frameCount = 1;
        else frameCount++;

        frame = inputFrame.rgba();

        if (click == 1) {
//            final Drawable myDrawable = getResources().getDrawable(R.drawable.test);
//            final Bitmap bitmapImage = ((BitmapDrawable) myDrawable).getBitmap();
//            Mat srcImage = new Mat (bitmapImage.getWidth(), bitmapImage.getHeight(), CvType.CV_8UC1);
//            Utils.bitmapToMat(bitmapImage, srcImage);
//            Imgproc.cvtColor(srcImage, srcImage, Imgproc.COLOR_RGB2GRAY);

            //framegray = inputFrame.gray();
            framegray = inputFrame.gray();
            detectKeyboard(framegray);
            if (blackKeysArr.size() != 10) {
                click = 0;
            } else {
                click = 2;


            }
        }
        if (blackKeysArr.size() == 10) {

            returns = detectFinger(frame.getNativeObjAddr(), mask.getNativeObjAddr(), whiteKeysArr, blackKeysArr);

            sortR(returns);
            if (frameCount == 1) {
                frame1.clear();
                frame1.addAll(returns);
            } else if (frameCount == 2) {

                frame2.clear();
                frame2.addAll(returns);
            } else if (frameCount == 3) {
                frame3.clear();
                frame3.addAll(returns);
            }
            if (returns.size() == 0)
                press = true;

            detectKeyStrokes(frame1, frame2, frame3);

            returns.clear();

            return frame;
        } else return frame;


    }


    public void detectKeyboard(Mat srcImage) {

        Imgproc.threshold(srcImage, srcImage, 100, 255, Imgproc.THRESH_OTSU);
        binaryImage = srcImage.clone();

//........................... White keys detection ....................................//	

        // Find contours

        MatOfPoint2f mMOP2f1 = new MatOfPoint2f(), mMOP2f2 = new MatOfPoint2f();
        List<MatOfPoint> whiteMatKeys = new ArrayList<MatOfPoint>();
        List<MatOfPoint> whiteContours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(srcImage, whiteContours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIdx = 0; contourIdx < whiteContours.size(); contourIdx++) {
            Mat con = whiteContours.get(contourIdx);
            double area = Imgproc.contourArea(con);
            // Ignore small and large contours

            if (area > 350) {
                //Convert contours(i) from MatOfPoint to MatOfPoint2f

                whiteContours.get(contourIdx).convertTo(mMOP2f1, CvType.CV_32FC2);
                //Epsilon for polygon transformation (take it from the arc length of the contour)

                double epsilon = 0.01 * Imgproc.arcLength(mMOP2f1, true);
                //Processing on mMOP2f1 which is in type MatOfPoint2f and approximate to polygon

                Imgproc.approxPolyDP(mMOP2f1, mMOP2f2, epsilon, true);
                // Take polygons which have specific number of sides

                if (mMOP2f2.size().height ==6 || mMOP2f2.size().height ==8) {
                    //Convert back to MatOfPoint and put the new values back into the contours list

                    mMOP2f2.convertTo(whiteContours.get(contourIdx), CvType.CV_32S);
                    // Put key in white keyList

                    whiteMatKeys.add(whiteContours.get(contourIdx));
                    // Draw white keys on source image

                    //     Imgproc.drawContours(srcImage, whiteContours, contourIdx, new Scalar(255,0,0,255), -1);
                }
            }
        }
        hierarchy.release();
        // convert MatOfPoint to Point

        List<Point> p = new ArrayList<Point>();

        if (whiteMatKeys.size() != 0) {
            for (int i = 0; i < whiteMatKeys.size(); i++) {
                p = whiteMatKeys.get(i).toList();
                whiteKeys.add(p);
            }
            // sort white keys according to their x position
            if (whiteKeys.size() == 14) {
                sort(whiteKeys);

                for (int i = 0; i < whiteKeys.size(); i++) {
                    Point wpoint[] = new Point[whiteKeys.get(i).size()];
                    for (int j = 0; j < whiteKeys.get(i).size(); j++) {
                        wpoint[j] = whiteKeys.get(i).get(j);
                    }
                    whiteKeysArr.add(wpoint);
                }
            }
        }
//........................... Black keys detection ..........................................//	

        keyboardImage = new Mat();
        // 	keyboard=new Mat();
        List<MatOfPoint> blackMatKeys = new ArrayList<MatOfPoint>();
        if (whiteKeys.size() == 14) {
            int maxLoc = 0, minyLoc1 = 0, miny2Loc1 = 0, minLoc = 0, minyLoc2 = 0, miny2Loc2 = 0;
            double maxVal = 0, minyVal1 = whiteKeys.get(13).get(0).y;
            double minVal = whiteKeys.get(0).get(0).x, minyVal2 = whiteKeys.get(0).get(0).y;

            for (int i = 0; i < whiteKeys.get(13).size(); i++) {
                Point pp = whiteKeys.get(13).get(i);
                if (pp.x > maxVal) {
                    maxVal = pp.x;
                    maxLoc = i;
                }
            }
            for (int i = 0; i < whiteKeys.get(13).size(); i++) {
                Point pp = whiteKeys.get(13).get(i);
                if (pp.y < minyVal1) {
                    minyVal1 = pp.y;
                    minyLoc1 = i;
                }
            }
            double miny2Val1;
            if (whiteKeys.get(13).get(0).y == minyVal1) {
                miny2Val1 = whiteKeys.get(13).get(2).y;
            } else miny2Val1 = whiteKeys.get(13).get(0).y;

            for (int i = 0; i < whiteKeys.get(13).size(); i++) {
                Point pp = whiteKeys.get(13).get(i);
                if (pp.y < miny2Val1 && i != minyLoc1) {
                    miny2Val1 = pp.y;
                    miny2Loc1 = i;
                }
            }

            int maxChoice = 0;
            if (whiteKeys.get(13).get(miny2Loc1).x > whiteKeys.get(13).get(minyLoc1).x) {
                maxChoice = miny2Loc1;
            } else
                maxChoice = minyLoc1;

            for (int i = 0; i < whiteKeys.get(0).size(); i++) {
                Point pp = whiteKeys.get(0).get(i);
                if (pp.x < minVal) {
                    minVal = pp.x;
                    minLoc = i;
                }
            }
            for (int i = 0; i < whiteKeys.get(0).size(); i++) {
                Point pp = whiteKeys.get(0).get(i);
                if (pp.y < minyVal2) {
                    minyVal2 = pp.y;
                    minyLoc2 = i;
                }
            }
            double miny2Val2;
            if (whiteKeys.get(0).get(0).y == minyVal2) {
                miny2Val2 = whiteKeys.get(0).get(2).y;
            } else miny2Val2 = whiteKeys.get(0).get(0).y;

            for (int i = 0; i < whiteKeys.get(0).size(); i++) {
                Point pp = whiteKeys.get(0).get(i);
                if (pp.y < miny2Val2 && i != minyLoc2) {
                    miny2Val2 = pp.y;
                    miny2Loc2 = i;
                }
            }
            int minChoice = 0;
            if (whiteKeys.get(0).get(minyLoc2).x < whiteKeys.get(0).get(miny2Loc2).x) {
                minChoice = minyLoc2;
            } else
                minChoice = miny2Loc2;

            // Get Corner points

            Point p1 = whiteKeys.get(13).get(maxChoice);
            p1.y = p1.y + 15;
            Point p2 = whiteKeys.get(13).get(maxLoc);
            p2.y = p2.y - 15;
            Point p3 = whiteKeys.get(0).get(minLoc);
            p3.y = p3.y - 15;
            Point p4 = whiteKeys.get(0).get(minChoice);
            p4.y = p4.y + 15;
            List<MatOfPoint> pts = new ArrayList<MatOfPoint>();
            pts.add(0, new MatOfPoint(p1, p2, p3, p4));
            // create temporary image that will hold the mask

            mask = new Mat(binaryImage.size(), CvType.CV_8UC1, new Scalar(0));
            // draw your contour (Point list) in mask

            Imgproc.drawContours(mask, pts, 0, new Scalar(255, 0, 0, 255), -1);
            // 	Imgproc.drawContours(mask2, pts, 0, new Scalar(255,0,0,255), -1);
            // copy only non-zero pixels from your image to original image

            binaryImage.copyTo(keyboardImage, mask);

            // Inverse mask only
            //	keyboard=keyboardImage.clone();
            Core.bitwise_not(keyboardImage, keyboardImage, mask);
            // Find contours for black keys

            MatOfPoint2f mMOP2f3 = new MatOfPoint2f(), mMOP2f4 = new MatOfPoint2f();
            List<MatOfPoint> blackContours = new ArrayList<MatOfPoint>();
            Mat hierarchy2 = new Mat();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ImageView image = (ImageView) findViewById(R.id.test);
//                    Bitmap bmp = Bitmap.createBitmap(keyboardImage.cols(), keyboardImage.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(keyboardImage, bmp);
//                    image.setImageBitmap(bmp);
//
//                }
//            });
            Imgproc.findContours(keyboardImage, blackContours, hierarchy2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ImageView image = (ImageView) findViewById(R.id.test2);
//                    Bitmap bmp = Bitmap.createBitmap(keyboardImage.cols(), keyboardImage.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(keyboardImage, bmp);
//                    image.setImageBitmap(bmp);
//
//                }
//            });

            for (int contourIdx = 0; contourIdx < blackContours.size(); contourIdx++) {
                Mat con = blackContours.get(contourIdx);
                double area = Imgproc.contourArea(con);
                //	 Ignore small contours

                if (area > 50) {
                    blackContours.get(contourIdx).convertTo(mMOP2f3, CvType.CV_32FC2);
                    double epsilon = 0.01 * Imgproc.arcLength(mMOP2f3, true);
                    Imgproc.approxPolyDP(mMOP2f3, mMOP2f4, epsilon, true);
                    // Take polygons which have specific sides

                    if (mMOP2f4.size().height ==8) {
                        mMOP2f4.convertTo(blackContours.get(contourIdx), CvType.CV_32S);
                        blackMatKeys.add(blackContours.get(contourIdx));
                        //       Imgproc.drawContours(keyboardImage, blackContours, contourIdx, new Scalar(255,0,0,255), -1);
                    }
                }
            }
            hierarchy2.release();
        }

        // convert MatOfPoint to Point

        List<Point> pblack = new ArrayList<Point>();

        if (blackMatKeys.size() != 0) {
            for (int i = 0; i < blackMatKeys.size(); i++) {
                pblack = blackMatKeys.get(i).toList();
                blackKeys.add(pblack);
            }
            // sort black keys according to their x position
            if (blackKeys.size() == 10) {
                sort(blackKeys);

                for (int i = 0; i < blackKeys.size(); i++) {
                    Point bpoint[] = new Point[blackKeys.get(i).size()];
                    for (int j = 0; j < blackKeys.get(i).size(); j++) {
                        bpoint[j] = blackKeys.get(i).get(j);
                    }

                    blackKeysArr.add(bpoint);
                }
            }
        }
    }

    public double maxVal(List<Point> keys) {

        double max = 0;
        for (int i = 0; i < keys.size(); i++) {
            Point p = keys.get(i);

            if (p.x > max) {
                max = p.x;

            }
        }
        return max;
    }

    public void sort(List<List<Point>> contours) {

        Collections.sort(contours, new Comparator<List<Point>>() {

            @Override
            public int compare(List<Point> arg0, List<Point> arg1) {
                // TODO Auto-generated method stub
                return (maxVal(arg0) < maxVal(arg1)) ? -1 : (maxVal(arg0) > maxVal(arg1)) ? 1 : 0;
            }

        });

    }

    public void sortR(List<List<Point>> returns) {


        Collections.sort(returns, new Comparator<List<Point>>() {

            @Override
            public int compare(List<Point> arg0, List<Point> arg1) {
                // TODO Auto-generated method stub
                return (arg0.get(0).x > arg1.get(0).x) ? -1 : (arg0.get(0).x < arg1.get(0).x) ? 1 : 0;
            }

        });

    }


    public void detectKeyStrokes(List<List<Point>> frame1, List<List<Point>> frame2, List<List<Point>> frame3) {

        int size1 = frame1.size();
        int size2 = frame2.size();
        int size3 = frame3.size();

        int min = size1;
        int i = 0, f1 = 0, f2 = 0, f3 = 0;
        double maxVal1 = 0, maxVal2 = 0, maxVal3 = 0, max = 0;

        if (size1 != 0 && size2 != 0 && size3 != 0) {

            min = size1;
            if (size2 < min) min = size2;
            if (size3 < min) min = size3;


            maxVal1 = frame1.get(0).get(0).y;
            for (int j = 0; j < min; j++) {
                if (frame1.get(j).get(0).y > maxVal1) {
                    maxVal1 = frame1.get(j).get(0).y;
                    f1 = j;
                }
            }


            maxVal2 = frame2.get(0).get(0).y;
            for (int j = 0; j < min; j++) {
                if (frame2.get(j).get(0).y > maxVal2) {
                    maxVal2 = frame2.get(j).get(0).y;
                    f2 = j;
                }
            }


            maxVal3 = frame3.get(0).get(0).y;
            for (int j = 0; j < min; j++) {
                if (frame3.get(j).get(0).y > maxVal3) {
                    maxVal3 = frame3.get(j).get(0).y;
                    f3 = j;
                }
            }
            max = maxVal1;
            i = f1;
            if (maxVal2 > max) {
                max = maxVal2;
                i = f2;
            }
            if (maxVal3 > max) {
                max = maxVal3;
                i = f3;
            }

            if (
                    (frame1.get(i).get(0).x <= frame2.get(i).get(0).x + 2 &&
                            frame1.get(i).get(0).x >= frame2.get(i).get(0).x - 2 &&
                            frame1.get(i).get(0).y <= frame2.get(i).get(0).y + 2 &&
                            frame1.get(i).get(0).y >= frame2.get(i).get(0).y - 2) ||

                            (frame2.get(i).get(0).x <= frame3.get(i).get(0).x + 2 &&
                                    frame2.get(i).get(0).x >= frame3.get(i).get(0).x - 2 &&
                                    frame2.get(i).get(0).y <= frame3.get(i).get(0).y + 2 &&
                                    frame2.get(i).get(0).y >= frame3.get(i).get(0).y - 2) ||

                            (
                                    frame1.get(i).get(1).x == frame2.get(i).get(1).x &&
                                            frame2.get(i).get(1).x == frame3.get(i).get(1).x &&
                                            frame1.get(i).get(1).y == frame2.get(i).get(1).y &&
                                            frame2.get(i).get(1).y == frame3.get(i).get(1).y &&

                                            frame1.get(i).get(0).y < frame2.get(i).get(0).y &&
                                            frame2.get(i).get(0).y > frame3.get(i).get(0).y) ||

                            (
                                    frame1.get(i).get(1).x == frame2.get(i).get(1).x &&
                                            frame2.get(i).get(1).x == frame3.get(i).get(1).x &&
                                            frame1.get(i).get(1).y == frame2.get(i).get(1).y &&
                                            frame2.get(i).get(1).y == frame3.get(i).get(1).y &&
                                            frame1.get(i).get(0).y < frame3.get(i).get(0).y &&
                                            frame3.get(i).get(0).y > frame2.get(i).get(0).y) ||

                            (
                                    frame1.get(i).get(1).x == frame2.get(i).get(1).x &&
                                            frame2.get(i).get(1).x == frame3.get(i).get(1).x &&
                                            frame1.get(i).get(1).y == frame2.get(i).get(1).y &&
                                            frame2.get(i).get(1).y == frame3.get(i).get(1).y &&
                                            frame2.get(i).get(0).y < frame1.get(i).get(0).y &&
                                            frame1.get(i).get(0).y > frame3.get(i).get(0).y)
                    ) {

                Point key = frame2.get(i).get(1);

                if (!press && prK.x == key.x && prK.y == key.y) {
                } else {
                    selectSound(key);
                    press = false;
                    prK.x = key.x;
                    prK.y = key.y;

                }
            } else if ((frame1.get(i).get(0).x <= frame3.get(i).get(0).x + 2 &&
                    frame1.get(i).get(0).x >= frame3.get(i).get(0).x - 2 &&
                    frame1.get(i).get(0).y <= frame3.get(i).get(0).y + 2 &&
                    frame1.get(i).get(0).y >= frame3.get(i).get(0).y - 2)) {

                Point key = frame1.get(i).get(1);

                if (!press && prK.x == key.x && prK.y == key.y) {
                } else {
                    selectSound(key);
                    press = false;
                    prK.x = key.x;
                    prK.y = key.y;

                }

            }


        }

    }

    public native List<List<Point>> detectFinger(long frame, long mask, List<Point[]> whiteKeys, List<Point[]> blackKeys);


    public void startMidiRecording() {
        midiRecord = true;
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
    public void playKey(final int id) {
        soundPool.play(tunesSoundMap.get(id + ((pianoInt - 1) * 12)), 1, 1, 0, 0, 1);
        if (midiRecord) {
            noteTrack.insertNote(0, id - 1 + 24 + ((pianoInt - 1) * 12), 100, 480 * tick, 120);
        }
    }
///////////////////////////////////////////

    public void selectSound(final Point key) {
        // x = 1 white key
        // x = 0 black key
        // y number of key : white 13 - 0, black 9-0

        int number = 0;
        if (key.x == 1) {
            switch ((int) key.y) {
                case 13:
                    number = 1; break;
                case 12:
                    number = 3;break;
                case 11:
                    number = 5;break;
                case 10:
                    number = 6;break;
                case 9:
                    number = 8;break;
                case 8:
                    number = 10;break;
                case 7:
                    number = 12;break;
                case 6:
                    number = 13;break;
                case 5:
                    number = 15;break;
                case 4:
                    number = 17;break;
                case 3:
                    number = 18;break;
                case 2:
                    number = 20;break;
                case 1:
                    number = 22;break;
                case 0:
                    number = 24;break;

            }

        } else if (key.x == 0) {
            switch ((int) key.y) {
                case 9:
                    number = 2;break;
                case 8:
                    number = 4;break;
                case 7:
                    number = 7;break;
                case 6:
                    number = 9;break;
                case 5:
                    number = 11;break;
                case 4:
                    number = 14;break;
                case 3:
                    number = 16;break;
                case 2:
                    number = 19;break;
                case 1:
                    number = 21;break;
                case 0:
                    number = 23;break;

            }
        }
        playKey(number);

    }
}

