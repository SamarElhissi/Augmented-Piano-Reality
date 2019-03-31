package com.example.pianoproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

public class RecordingManager {
    Calendar c = Calendar.getInstance();
    int mseconds = c.get(Calendar.MILLISECOND);
    int seconds = c.get(Calendar.SECOND);
    MediaRecorder myAudioRecorder = new MediaRecorder();
    String outputFile = null;
    File audiofile = null;
    int tick;
    MidiTrack tempoTrack = new MidiTrack();

    public void startRecording() throws IOException {
        try {
            String name = "sound" + seconds + "" + mseconds + ".mp3";
            audiofile = createFile(name);
            audiofile.createNewFile();
        } catch (IOException e) {
            return;
        }
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format  
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myAudioRecorder.setOutputFile(audiofile.getAbsolutePath());
        myAudioRecorder.prepare();
        myAudioRecorder.start();
    }

    public void stopRecording() {
        myAudioRecorder.release();
    }

    public boolean checkRecordingPermissions(Activity activity){
        String TAG = "Permsission : ";
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void stopMidiRecording(MidiTrack noteTrack) {
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        Tempo tempo = new Tempo();
        tempo.setBpm(228);
        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);
        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);
        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
        String name = "sound" + seconds + "" + mseconds + ".mid";
        File output4 = createFile(name);

        try {

            output4.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {

            midi.writeToFile(output4);
        } catch (IOException e) {
        }
    }

public File createFile(String name){
    File mFolder = new File(Environment.getExternalStorageDirectory(), "MagicPiano");
    if (!mFolder.exists()) {
        mFolder.mkdirs();

    }
    File audiofile = new File(Environment.getExternalStorageDirectory()
            + File.separator +"MagicPiano"+ File.separator+ name);
    return audiofile;
}
}
