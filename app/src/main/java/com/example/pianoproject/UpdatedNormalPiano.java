package com.example.pianoproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leff.midi.MidiTrack;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UpdatedNormalPiano extends Activity implements View.OnClickListener {

    private Map<Integer, Integer> tunesSoundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private long lastBackTimestamp = 0;
    private TextView timerText, e1_text, f1_text, g1_text, a1_text, b1_text, c1_text, d1_text, c2_text, d2_text, e2_text;
    ImageView forward, back, record;
    int part = 4;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    private long startTime = 0L;
    long updatedTime = 0L;
    String recordType = "";
    boolean midiRecord = false, rec = false;
    RecordingManager recordClass = new RecordingManager();
    MidiTrack noteTrack = new MidiTrack();
    int tick = 0;
    ProgressBar simpleProgressBar;
    ImageView pianoImage;
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            timerText.setText("" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };
    private ImageView b6_btn, b7_btn, w9_btn, w10_btn;

    private void IntializeButtons() {

        ImageView w1_btn = (ImageView) this.findViewById(R.id.w1_btn); // first white
        ImageView b1_btn = (ImageView) this.findViewById(R.id.b1_btn); // first black
        ImageView w2_btn = (ImageView) this.findViewById(R.id.w2_btn);// 2 w
        ImageView b2_btn = (ImageView) this.findViewById(R.id.b2_btn); // 2 b
        ImageView w3_btn = (ImageView) this.findViewById(R.id.w3_btn); // 3w
        ImageView w4_btn = (ImageView) this.findViewById(R.id.w4_btn); // 4w
        ImageView b3_btn = (ImageView) this.findViewById(R.id.b3_btn);// 3b
        ImageView w5_btn = (ImageView) this.findViewById(R.id.w5_btn); // 5w
        ImageView b4_btn = (ImageView) this.findViewById(R.id.b4_btn); // 4b

        ImageView w6_btn = (ImageView) this.findViewById(R.id.w6_btn); // 6w
        ImageView b5_btn = (ImageView) this.findViewById(R.id.b5_btn);// 5b
        ImageView w7_btn = (ImageView) this.findViewById(R.id.w7_btn); // 7w
        ImageView w8_btn = (ImageView) this.findViewById(R.id.w8_btn); // 8w
        b6_btn = (ImageView) this.findViewById(R.id.b6_btn); // 6b

        w9_btn = (ImageView) this.findViewById(R.id.w9_btn); // 9w
        b7_btn = (ImageView) this.findViewById(R.id.b7_btn); // 7b
        w10_btn = (ImageView) this.findViewById(R.id.w10_btn); //10w

        SetOnTouchListener(w1_btn, 1);
        SetOnTouchListener(b1_btn, 2);
        SetOnTouchListener(w2_btn, 3);
        SetOnTouchListener(b2_btn, 4);
        SetOnTouchListener(w3_btn, 5);
        SetOnTouchListener(w4_btn, 6);
        SetOnTouchListener(b3_btn, 7);
        SetOnTouchListener(w5_btn, 8);
        SetOnTouchListener(b4_btn, 9);
        SetOnTouchListener(w6_btn, 10);
        SetOnTouchListener(b5_btn, 11);
        SetOnTouchListener(w7_btn, 12);
        SetOnTouchListener(w8_btn, 13);
        SetOnTouchListener(b6_btn, 14);
        SetOnTouchListener(w9_btn, 15);
        SetOnTouchListener(b7_btn, 16);
        SetOnTouchListener(w10_btn, 17);
    }

    private void SetOnTouchListener(ImageView btn, final int keyNumber) {
        btn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return playSound(keyNumber, motionEvent, view);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updated_normal_piano);
        SysApplication.getInstance().addActivity(this);
        powerManager = (PowerManager) this.getSystemService(Activity.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        c1_text = (TextView) this.findViewById(R.id.c1);
        d1_text = (TextView) this.findViewById(R.id.d1);
        e1_text = (TextView) this.findViewById(R.id.e1);
        f1_text = (TextView) this.findViewById(R.id.f1);
        g1_text = (TextView) this.findViewById(R.id.g1);
        a1_text = (TextView) this.findViewById(R.id.a1);
        b1_text = (TextView) this.findViewById(R.id.b1);
        c2_text = (TextView) this.findViewById(R.id.c2);
        d2_text = (TextView) this.findViewById(R.id.d2);
        e2_text = (TextView) this.findViewById(R.id.e2);
        pianoImage=(ImageView) findViewById(R.id.npiano);
        forward = (ImageView) findViewById(R.id.forw);
        forward.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (part != 7) {
                    part++;
                    ChangeKeys();
                }
                return false;
            }
        });
        back = (ImageView) findViewById(R.id.bak);
        back.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (part != 1) {
                    part--;
                    ChangeKeys();
                }
                return false;
            }
        });

        record = (ImageView) findViewById(R.id.nrec);
        registerForContextMenu(findViewById(R.id.nrec));
        record.setOnClickListener(this);

        timerText = (TextView) findViewById(R.id.timer);
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

        final Context context = UpdatedNormalPiano.this;
        simpleProgressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        LoadingManager loader = new LoadingManager(context, simpleProgressBar, soundPool, tunesSoundMap);
        loader.LoadSounds();
        IntializeButtons();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                if(tunesSoundMap.size() == 85 && tunesSoundMap.get(85) == soundId){
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private boolean playSound(int id, MotionEvent motionEvent, View view) {
        if (motionEvent == null || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            soundPool.play(tunesSoundMap.get(id + ((part - 1) * 12)), 1, 1, 0, 0, 1);
            if (midiRecord) {
                noteTrack.insertNote(0, id - 1 + 24 + ((part - 1) * 12), 100, 480 * tick, 120);
            }
        }
        return false;
    }

    public void ChangeKeys() {
        int color = getKeyColor(part);
        c1_text.setText("C" + part);
        d1_text.setText("D" + part);
        e1_text.setText("E" + part);
        f1_text.setText("F" + part);
        g1_text.setText("G" + part);
        a1_text.setText("A" + part);
        b1_text.setText("B" + part);
        c2_text.setText("C" + (part + 1));
        d2_text.setText("D" + (part + 1));
        e2_text.setText("E" + (part + 1));
        c1_text.setBackgroundColor(color);
        d1_text.setBackgroundColor(color);
        e1_text.setBackgroundColor(color);
        f1_text.setBackgroundColor(color);
        g1_text.setBackgroundColor(color);
        a1_text.setBackgroundColor(color);
        b1_text.setBackgroundColor(color);
        color = getKeyColor(part + 1);
        c2_text.setBackgroundColor(color);
        d2_text.setBackgroundColor(color);
        e2_text.setBackgroundColor(color);

        changeImage();

        if (part == 7) {
            ChangeKeyVisiblity(View.INVISIBLE);

        } else {
            ChangeKeyVisiblity(View.VISIBLE);
        }
    }

    private void ChangeKeyVisiblity(int visibilty) {
        b6_btn.setVisibility(visibilty);
        b7_btn.setVisibility(visibilty);
        w9_btn.setVisibility(visibilty);
        w10_btn.setVisibility(visibilty);
    }

    public void changeImage() {
        int resId = getResId("npiano"+part);
        pianoImage.setImageResource(resId);
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

    public int getKeyColor(int part) {
        int color = 0;
        switch (part) {
            case 1:
                color = Color.parseColor("#ff9aa5");
                break;
            case 2:
                color = Color.parseColor("#a59aff");
                break;
            case 3:
                color = Color.parseColor("#a5ddff");
                break;
            case 4:
                color = Color.parseColor("#33aaff");
                break;
            case 5:
                color = Color.parseColor("#ddffdd");
                break;
            case 6:
                color = Color.parseColor("#a5ff9a");
                break;
            case 7:
                color = Color.parseColor("#dddd60");
                break;
        }
        return color;
    }

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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.nrec) {
            if (rec || midiRecord ) {

                customHandler.removeCallbacks(updateTimerThread);
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
                    recordClass.stopRecording();
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.midi:
                recordType = "midi";
                if(recordClass.checkRecordingPermissions(this)){
                    startMidiRecording();
                };
                return true;
            case R.id.mic:
                recordType = "mic";
               if(recordClass.checkRecordingPermissions(this)){
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

}
    public AlertDialog AskOption() {

        final AlertDialog ad = new AlertDialog.Builder(UpdatedNormalPiano.this).create();
        // TODO Auto-generated method stub
        ad.setTitle("Save");
        ad.setMessage("Save File?");
        ad.setButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (recordType.equals("midi")) {
                    recordClass.stopMidiRecording(noteTrack);
                    Toast.makeText(getApplicationContext(), "File Saved ", Toast.LENGTH_LONG).show();
                    tick = 0;
                    midiRecord = false;

                } else if (recordType.equals("mic"))
                    recordClass.stopRecording();
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
                    recordClass.stopRecording();
                ad.dismiss();

            }
        });
        return ad;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long nowTimeStamp = System.currentTimeMillis();
            if (lastBackTimestamp == 0 || nowTimeStamp - lastBackTimestamp > 2000) {
                lastBackTimestamp = nowTimeStamp;
                return true;
            } else {
                SysApplication.getInstance().exit();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }
}
