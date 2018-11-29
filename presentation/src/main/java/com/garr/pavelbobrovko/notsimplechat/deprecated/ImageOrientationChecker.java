package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by garr on 12.07.2017.
 */

public class ImageOrientationChecker {

    private Context mCtx;
    private Bitmap tempBitmap;
    private File tempImage;
    private Uri tempUri;
    private boolean isRotate=false;

    public void checkImage(Uri image, Context _mCtx){
        mCtx=_mCtx;
        tempUri=image;
        checkAndChangeOrientation();
    }

    private void checkAndChangeOrientation(){
        /*try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream imageStream = mCtx.getContentResolver().openInputStream(tempUri);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();

            options.inJustDecodeBounds = false;
            imageStream = mCtx.getContentResolver().openInputStream(tempUri);
            tempBitmap = BitmapFactory.decodeStream(imageStream, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        tempBitmap=BitmapFactory.decodeFile(tempUri.getPath());

        if (tempBitmap == null) Log.d("log","Bitmap null");
        if (tempImage==null)Log.d("log","File null");
        if (tempUri==null){
            Log.d("log","Uri null");
        }else Log.d("log","Uri: " + tempUri.toString());

        ExifInterface ei;
        try {
            ei = new ExifInterface(tempUri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("log", "ei = " + ei.toString());
            Log.d("log", "orientation = " + orientation);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    tempBitmap = rotateImage(tempBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    tempBitmap = rotateImage(tempBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    tempBitmap = rotateImage(tempBitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    Log.d("log","ORIENTATION_NORMAL");
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    Log.d("log","ORIENTATION_FLIP_HORIZONTAL");
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    Log.d("log","ORIENTATION_FLIP_VERTICAL");
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    Log.d("log","ORIENTATION_TRANSPOSE");
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    Log.d("log","ORIENTATION_TRANSVERSE");
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    //tempBitmap=rotateImage(tempBitmap,90);
                    Log.d("log","ORIENTATION_UNDEFINED");
                    break;

            }

            if (isRotate)saveBitmap();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveBitmap() {
        try {

            FileOutputStream fos=new FileOutputStream(tempUri.getPath());
            tempBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.close();
            Log.d("log","File saved " + tempUri);
        } catch (IOException e) {
            Log.d("log","File save failed " + e);
            e.printStackTrace();
        }
    }


    private Bitmap rotateImage(Bitmap bitmap, int degrees){
        if (bitmap==null);
        Log.d("log","Image rotated");
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        isRotate=true;
        return bitmap;
    }
}
