package com.JAM.justaminute.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    private static final int REQUEST_MICROPHONE = 231;
    private Context mcontext;
    private Activity mactivity;
    public Permissions(Context context,Activity activity){
        mcontext = context;
        mactivity = activity;
    }

    public void checkperm(){
        if (ContextCompat.checkSelfPermission(mcontext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( mactivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_MICROPHONE);

        }
//        if (ContextCompat.checkSelfPermission(mcontext,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions( mactivity,
//                    new String[]{Manifest.permission.RECORD_AUDIO},
//                    REQUEST_MICROPHONE);
//
//        }

    }
    public boolean checkmic(){
        if (ContextCompat.checkSelfPermission(mcontext,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( mactivity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
            return false;
        }
        return true;
    }

}
