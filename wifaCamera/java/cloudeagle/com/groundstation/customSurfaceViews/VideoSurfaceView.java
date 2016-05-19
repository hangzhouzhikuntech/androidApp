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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by albert on 15/12/3.
 */
public class VideoSurfaceView extends SurfaceView implements Callback {
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    URL videoUrl;
    private String url;
    HttpURLConnection conn;
    Bitmap bmp;
    private Paint paint;
    InputStream inputstream = null;
    private Bitmap mBitmap;
    private static int mScreenWidth;
    private static int mScreenHeight;
    Paint myPaint = new Paint();
    private boolean isRunning = false;

    public VideoSurfaceView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initialize();
        paint = new Paint();
        paint.setAntiAlias(true);
        myPaint.setColor(Color.GREEN);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(3);

        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setKeepScreenOn(true);
        setFocusable(true);
        this.getWidth();
        this.getHeight();
    }

    private void initialize() {
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean isRunning) { this.isRunning = isRunning; }

    class DrawVideo extends Thread {
        public DrawVideo() {
        }

        public void run() {
            while (isRunning) {
                try {
                    Log.e("test", url);
                    videoUrl = new URL(url);
                    conn = (HttpURLConnection) videoUrl.openConnection();
                    Log.e("test", conn.toString());
                    conn.connect();
                    inputstream = conn.getInputStream(); //获取流
                    Log.e("test", "-----------------------------");
                    Log.e("test", inputstream.toString());
                    BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
                    BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
                    bmp = BitmapFactory.decodeStream(inputstream, null, BitmapFactoryOptionsbfo);//从获取的流中构建出BMP图像
                    if (null == bmp) {
                        Thread.sleep(500);
                        continue;
                    }
                    mBitmap = Bitmap.createScaledBitmap(bmp, mScreenWidth, mScreenHeight, true);
                    canvas = surfaceHolder.lockCanvas();
                    if (null != canvas) {
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(mBitmap, 0, 0, null);//把BMP图像画在画布上
                        surfaceHolder.unlockCanvasAndPost(canvas);//画完一副图像，解锁画布
                    } else {
                        surfaceHolder.unlockCanvasAndPost(null);
                        Thread.sleep(500);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        setRunning(false);
    }

    public void getCameraIP(String ip) {
        url = ip;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        setRunning(true);
        new DrawVideo().start();
    }
}
