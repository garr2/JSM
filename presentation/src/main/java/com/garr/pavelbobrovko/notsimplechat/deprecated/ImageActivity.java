package com.garr.pavelbobrovko.notsimplechat.deprecated;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.garr.pavelbobrovko.notsimplechat.R;
import com.pavelbobrovko.garr.domain.utils.ConstantInterface;

public class ImageActivity extends Activity {

    private ImageView ivFullImage;
    private ImageButton iBtnExit;
    private FrameLayout flImageContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ivFullImage=(ImageView)findViewById(R.id.ivFullImage);
        iBtnExit=(ImageButton)findViewById(R.id.iBtnExit);
        flImageContainer=(FrameLayout)findViewById(R.id.flImageContainer);
        WindowManager w = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        int mWindowWidth;
        int mWindowHeight;


        Display d = w.getDefaultDisplay();
        mWindowWidth = d.getWidth();
        mWindowHeight = d.getHeight();
        Runtime.getRuntime().gc();

        ivFullImage.setImageBitmap(decodeSampledBitmapFromResource("",mWindowWidth,mWindowHeight));
        logMemory();


        iBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == iBtnExit)finish();
            }
        });

    }

    private void logMemory() {
        Log.i(ConstantInterface.LOG_TAG, String.format("Total memory = %s",
                (int) (Runtime.getRuntime().totalMemory() / 1024)));
    }

    private Bitmap decodeSampledBitmapFromResource(String path,
                                                   int reqWidth, int reqHeight) {

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),R.drawable.photo,options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(),R.drawable.photo,options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Вычисляем наибольший inSampleSize, который будет кратным двум
            // и оставит полученные размеры больше, чем требуемые
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onDestroy(){
        flImageContainer.removeAllViews();
        super.onDestroy();
    }
}
