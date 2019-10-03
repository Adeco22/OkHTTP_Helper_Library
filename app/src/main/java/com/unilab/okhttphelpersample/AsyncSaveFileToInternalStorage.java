package com.unilab.okhttphelpersample;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author c_aldeco
 * Created by Anthony Deco
 * at 8:52 PM 9/26/2019
 */
public class AsyncSaveFileToInternalStorage extends AsyncTask<Void, Void, Boolean> {

    private Bitmap bitmap;
    private FileOutputStream out;
    private OnInternalStorageFileSaveListener onInternalStorageFileSaveListener;

    public AsyncSaveFileToInternalStorage(Bitmap bitmap, FileOutputStream out, OnInternalStorageFileSaveListener onInternalStorageFileSaveListener) {
        this.bitmap = bitmap;
        this.out = out;
        this.onInternalStorageFileSaveListener = onInternalStorageFileSaveListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            onInternalStorageFileSaveListener.onInternalFileSaved();
        } else {
            onInternalStorageFileSaveListener.onInternalFileSaveFail();
        }
    }

    public interface OnInternalStorageFileSaveListener{
        void onInternalFileSaved();
        void onInternalFileSaveFail();
    }
}
