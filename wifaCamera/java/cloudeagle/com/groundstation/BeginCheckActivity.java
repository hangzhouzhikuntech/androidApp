package cloudeagle.com.groundstation;

import cloudeagle.com.groundstation.customSurfaceViews.PicSurfaceView;
import cloudeagle.com.groundstation.customSurfaceViews.VideoSurfaceView;
import cloudeagle.com.groundstation.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BeginCheckActivity extends Activity {

    private String controlIP;
    private String videoIP;
    private String IP;
    private int port;
//     PicSurfaceView picSurfaceView;
    VideoSurfaceView picSurfaceView;

    private boolean isConnecting = false;
    private Socket mSocketClient = null;
    private static PrintWriter mPrintWriterClient = null;
    private static BufferedReader mBufferedReaderClient = null;
    private Thread mThreadClient = null;

    private String recvMessageClient = "";
    private String TAG = "groundStation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_begin_check);

//            videoIP = "http://192.168.8.1:8083?action=stream";
//            controlIP = "192.168.8.1:2001";
//            IP = "192.168.8.1";
//            videoIP = "http://192.168.1.1:8083?action=stream";
            videoIP = "http://192.168.1.1:8080?action=snapshot";
            controlIP = "192.168.1.1:2001";
            IP = "192.168.1.1";
            port = Integer.valueOf("2001");
            Log.e("aaaaaaa", videoIP);
            Log.e(TAG, videoIP);
            Log.e(TAG, controlIP);

            picSurfaceView = (VideoSurfaceView) findViewById(R.id.videoView);
            picSurfaceView.getCameraIP(videoIP);
//            picSurfaceView = (PicSurfaceView) findViewById(R.id.videoView);

            // 添加右侧button的点击事件
            ImageButton takePicBtn = (ImageButton) findViewById(R.id.takePicBtn);
            takePicBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BeginCheckActivity.this, "拍照，功能开发中~", Toast.LENGTH_SHORT).show();
                }
            });

            ImageButton takeVideoBtn = (ImageButton) findViewById(R.id.takeVideoBtn);
            takeVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BeginCheckActivity.this, "摄像，功能开发中~", Toast.LENGTH_SHORT).show();
                }
            });

            ImageButton backHomeBtn = (ImageButton) findViewById(R.id.backHomeBtn);
            backHomeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 切换到主页Activity
                    Intent intent = new Intent();
                    intent.setClass(BeginCheckActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            // 开启视频流线程
            //initData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initData() {
        Log.e(TAG, videoIP);
        picSurfaceView.getCameraIP(videoIP);
        // 视频监听
        // newSurfaceView = (MySurfaceView) findViewById(R.id.videoview);
        // newSurfaceView.GetCameraIP(GlobalData.CameraIp);

        if (isConnecting) {
            isConnecting = false;
            try {
                if (mSocketClient != null) {
                    mSocketClient.close();
                    mSocketClient = null;

                    mPrintWriterClient.close();
                    mPrintWriterClient = null;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mThreadClient.interrupt();
        } else {
            isConnecting = true;
            mThreadClient = new Thread(mRunvideo);
            mThreadClient.start();
        }
    }

    // 线程:监听服务器发来的消息
    private Runnable mRunnable = new Runnable() {
        public void run() {
            try {
                // 连接服务器
                mSocketClient = new Socket(IP, port); // portnum
                // 取得输入、输出流
                mBufferedReaderClient = new BufferedReader(
                        new InputStreamReader(mSocketClient.getInputStream()));
                mPrintWriterClient = new PrintWriter(
                        mSocketClient.getOutputStream(), true);
                recvMessageClient = "已经连接server!\n";// 消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                recvMessageClient = e.toString() + e.getMessage() + "\n";// 消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
            char[] buffer = new char[1024];
            int count = 0;
            String temp = "";
            while (isConnecting) {
                try {
                    if ((count = mBufferedReaderClient.read(buffer)) > 0) {
                        temp += getInfoBuff(buffer, count);// 消息换行
                        Log.e("isConnecting:", temp);
                    }
                    temp = temp.replaceAll("\r|\n", "");
                    Log.e("isConnecting finish:", recvMessageClient);
                    if (temp.length() == "FF12345678FF".length()) {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = temp;
                        mHandler.sendMessage(msg);
                        temp = "";
                    }
                } catch (Exception e) {
                    recvMessageClient = e.getMessage() + "\n";// 消息换行
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        }

        private String getInfoBuff(char[] buffer, int count) {
            // TODO Auto-generated method stub
            char[] temp = new char[count];
            for (int i = 0; i < count; i++) {
                temp[i] = buffer[i];
            }
            return new String(temp);

        }
    };

    private Runnable mRunvideo = new Runnable() {
        public void run() {
            try {
                picSurfaceView.getCameraIP(videoIP);
                recvMessageClient = videoIP;
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                recvMessageClient = "视频连接错误！";//消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
        }
    };

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
            } else if (msg.what == 1) {
                Toast.makeText(BeginCheckActivity.this, recvMessageClient, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                // 处理参数显示
                // FF1000XXXXFF-里程,FF1100XXXXFF-速度,FF1200XXXXFF-温度,FF1300XXXXFF-湿度,FF140000XXFF-火焰　亮与灰
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
