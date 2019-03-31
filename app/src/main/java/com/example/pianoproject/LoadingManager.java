package com.example.pianoproject;

import android.content.Context;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import java.util.Map;

public class LoadingManager {
    ProgressBar progressBar;
    SoundPool soundPool;
    private Map<Integer, Integer> tunesSoundMap;
    Context context;

    public LoadingManager(Context context,
                          ProgressBar progressBar,
                          SoundPool soundPool,
                          Map<Integer, Integer> tunesSoundMap) {
        this.progressBar = progressBar;
        this.soundPool = soundPool;
        this.tunesSoundMap = tunesSoundMap;
        this.context = context;

    }

    public void LoadSounds() {
        new SoundsLoader().execute();
    }

    private class SoundsLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public int getResId(String resName) {

            try {

                int idField = context.getResources().getIdentifier(resName, "raw", context.getPackageName());
                return idField;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected Void doInBackground(Void... urls) {

            for (int i = 1; i <= 7; i++) {

                int ai = load(getResId("a" + i));
                int bi = load(getResId("b" + i));
                int ci = load(getResId("c" + i));
                int di = load(getResId("d" + i));
                int ei = load(getResId("e" + i));
                int fi = load(getResId("f" + i));
                int gi = load(getResId("g" + i));

                int abi = load(getResId("ab" + i));
                int bbi = load(getResId("bb" + i));
                int dbi = load(getResId("db" + i));
                int ebi = load(getResId("eb" + i));
                int gbi = load(getResId("gb" + i));

                int x = ((i - 1) * 12);

                tunesSoundMap.put(1 + x, ci);
                tunesSoundMap.put(2 + x, dbi);
                tunesSoundMap.put(3 + x, di);
                tunesSoundMap.put(4 + x, ebi);
                tunesSoundMap.put(5 + x, ei);
                tunesSoundMap.put(6 + x, fi);
                tunesSoundMap.put(7 + x, gbi);
                tunesSoundMap.put(8 + x, gi);
                tunesSoundMap.put(9 + x, abi);
                tunesSoundMap.put(10 + x, ai);
                tunesSoundMap.put(11 + x, bbi);
                tunesSoundMap.put(12 + x, bi);
            }
            int c8 = load(getResId("c8"));
            tunesSoundMap.put(85, c8);

            return null;
        }

        private int load(int resId) {

            return soundPool.load(context, resId, 1);
        }

        @Override
        protected void onPostExecute(Void result) {
           // progressBar.setVisibility(View.INVISIBLE);
        }
    }
}

