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
public class PicSurfaceView extends SurfaceView implements Callback {
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

    public PicSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
        paint = new Paint();

        paint.setAntiAlias(true);
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setKeepScreenOn(true);
        setFocusable(true);
        this.getWidth();
        this.getHeight();
    }

    private void initialize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        this.setKeepScreenOn(true);
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
        new DrawVideo().start();
    }

    public void getCameraIP(String urlStr) {
        url = urlStr;
    }

    class DrawVideo extends Thread {
        public DrawVideo() {
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public void run() {
            Paint pt = new Paint();
            pt.setAntiAlias(true);
            pt.setColor(Color.GREEN);
            pt.setTextSize(20);
            pt.setStrokeWidth(1);

            // 视频图片的缓冲buffer
            int bufSize = 512 * 1024;
            byte[] jpgBuf = new byte[bufSize];

            // 每次读取的最大字节流
            int maxReadSize = 4096;
            byte[] readBuf = new byte[maxReadSize];


            while (isRun()) {
                long startTime = 0;
                long costTime = 0;
                int fps = 0;
                String fpsStr = "0 fps";

                try {
                    videoUrl = new URL(url);
                    connection = (HttpURLConnection) videoUrl.openConnection();
                    startTime = System.currentTimeMillis();

                    int actualBytesNum = 0;
                    int status = 0;
                    int jpgCount = 0;

                    while (isRun()) {
                        actualBytesNum = connection.getInputStream().read(readBuf, 0, maxReadSize);

                        if (actualBytesNum > 0) {
                            for (int i = 0; i < actualBytesNum; i++) {
                                switch (status) {
                                    case 0:
                                        if (readBuf[i] == (byte) 'C') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 1:
                                        if (readBuf[i] == (byte) 'o') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 2:
                                        if (readBuf[i] == (byte) 'n') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 3:
                                        if (readBuf[i] == (byte) 't') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 4:
                                        if (readBuf[i] == (byte) 'e') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 5:
                                        if (readBuf[i] == (byte) 'n') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 6:
                                        if (readBuf[i] == (byte) 't') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 7:
                                        if (readBuf[i] == (byte) '-') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 8:
                                        if (readBuf[i] == (byte) 'L') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 9:
                                        if (readBuf[i] == (byte) 'e') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 10:
                                        if (readBuf[i] == (byte) 'n') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 11:
                                        if (readBuf[i] == (byte) 'g') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 12:
                                        if (readBuf[i] == (byte) 't') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 13:
                                        if (readBuf[i] == (byte) 'h') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 14:
                                        if (readBuf[i] == (byte) ':') {
                                            status++;
                                        } else {
                                            status = 0;
                                        }
                                        break;
                                    case 15:
                                        if (readBuf[i] == (byte) 0xFF) {
                                            status++;
                                        }
                                        jpgCount = 0;
                                        jpgBuf[jpgCount++] = readBuf[i];
                                        break;
                                    case 16:
                                        if (readBuf[i] == (byte) 0xD8) {
                                            status++;
                                            jpgBuf[jpgCount++] = readBuf[i];
                                        } else {
                                            if (readBuf[i] != (byte) 0xFF) {
                                                status = 15;
                                            }
                                        }
                                        break;

                                    case 17:
                                        jpgBuf[jpgCount++] = readBuf[i];
                                        if (readBuf[i] == (byte) 0xFF) {
                                            status++;
                                        }
                                        if (jpgCount >= bufSize) {
                                            status = 0;
                                        }
                                        break;
                                    case 18:
                                        jpgBuf[jpgCount++] = readBuf[i];

                                        if (readBuf[i] == (byte) 0xD9) {
                                            status = 0;
                                            // jpg数据接收完成

                                            fps++;
                                            costTime = System.currentTimeMillis() - startTime;
                                            if (costTime > 1000L) {
                                                startTime = System.currentTimeMillis();
                                                fpsStr = String.valueOf(fps) + " fps";
                                                Log.e("fps: ", fpsStr);
                                                fps = 0;
                                            }

                                            // 显示图像
                                            canvas = surfaceHolder.lockCanvas();
                                            if (canvas == null) {
                                                Log.e("canvas == null:", "canvas == null...");
                                                surfaceHolder.unlockCanvasAndPost(null);
                                                Thread.sleep(500);
                                                continue;
                                            }
                                            canvas.drawColor(Color.WHITE);

                                            Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(jpgBuf));
                                            if (bmp == null) {
                                                Log.e("bmp == null", "bmp == null...");
                                                if (canvas != null) {
                                                    surfaceHolder.unlockCanvasAndPost(canvas);
                                                } else {
                                                    surfaceHolder.unlockCanvasAndPost(null);
                                                }
                                                Thread.sleep(500);
                                                continue;
                                            }

                                            int width = screenWidth;
                                            int height = screenHeight;

                                            float rateWidth = (float) screenWidth / (float) bmp.getWidth();
                                            float rateHeight = (float) screenHeight / (float) bmp.getHeight();

                                            if (isScaled) {
                                                if (rateWidth > rateHeight) {
                                                    width = (int) ((float) bmp.getWidth() * rateHeight);
                                                } else {
                                                    height = (int) ((float) bmp.getHeight() * rateWidth);
                                                }
                                            }

                                            bitmap = Bitmap.createScaledBitmap(bmp, width, height, false);
                                            canvas.drawBitmap(bitmap, 0, 0, null);
                                            canvas.drawText(fpsStr, 2, screenWidth / 2, pt);

                                            // 画完一幅图像后解锁画布
                                            surfaceHolder.unlockCanvasAndPost(canvas);

                                        } else {
                                            if (readBuf[i] != (byte) 0xFF) {
                                                status = 17;
                                            }
                                        }
                                        break;
                                    default:
                                        status = 0;
                                        break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    connection.disconnect();
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } else {
                        surfaceHolder.unlockCanvasAndPost(null);
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
