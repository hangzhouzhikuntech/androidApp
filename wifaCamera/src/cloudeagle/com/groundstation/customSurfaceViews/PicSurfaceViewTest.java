package cloudeagle.com.groundstation.customSurfaceViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by albert on 15/11/30.
 */
public class PicSurfaceViewTest extends SurfaceView implements Callback {
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    URL videoUrl;
    private String url;
    HttpURLConnection connection;
    private Bitmap bitmap;
    private Paint paint;
    private static int screenWidth;
    private static int screenHeight;

    private boolean isRun = false;
    private boolean isScaled = false;

    public PicSurfaceViewTest(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        setRun(false);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        setRun(true);
//        new DrawVideo().start();
    }
}