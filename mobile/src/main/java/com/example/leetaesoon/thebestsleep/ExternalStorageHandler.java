package com.example.leetaesoon.thebestsleep;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ExternalStorageHandler {
    private static final String TAG = "ExternalStorageHandler";
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    public ExternalStorageHandler() {
        checkExternalMedia();
    }

    private void checkExternalMedia() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        //Log.d(TAG, "External Media: readable = " + mExternalStorageAvailable + " writable = " + mExternalStorageWriteable);
    }

    public void writeFile(String data) {

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();
        //Log.d(TAG, "External file system root: " + root);

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/TheBestSleep");
        dir.mkdirs();
        File file = new File(dir, "sensor.txt");

        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f);
            pw.println(data);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "File written to '" + file + "'");
    }

//    private void readRaw() {
//        InputStream is = this.getResources().openRawResource(R.raw.textfile);
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr, 8192);    // 2nd arg is buffer size
//
//        // More efficient (less readable) implementation of above is the composite expression
//        /*BufferedReader br = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.textfile)), 8192);*/
//
//        try {
//            String test;
//            while (true){
//                test = br.readLine();
//                // readLine() returns null if no more lines in the file
//                if(test == null) break;
//                tv.append("\n"+"    "+test);
//            }
//            isr.close();
//            is.close();
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
