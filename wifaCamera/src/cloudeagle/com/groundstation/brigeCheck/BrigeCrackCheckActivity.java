package cloudeagle.com.groundstation.brigeCheck;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import cloudeagle.com.groundstation.R;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;  
import android.graphics.Bitmap.Config;  
import android.graphics.BitmapFactory;  
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;  
import android.util.Log;  
import android.view.Menu;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.speech.tts.TextToSpeech;
import cloudeagle.com.groundstation.brigeCheck.TouchImageView;
public class BrigeCrackCheckActivity extends Activity implements TextToSpeech.OnInitListener {  
  
    Button btnProcess;  
    Bitmap srcBitmap;  
    Bitmap grayBitmap;  
    TouchImageView imgHuaishi;  
    Button btnRecover;
    private static boolean flag = true;   
    private static boolean isFirst = true;   
    private static final String TAG = "BrigeCrackCheckActivity"; 
    static private int openfileDialogId = 0; 
    private String filePath = "";
    private String new_file_path = "";
    private Handler handler=null;  
    private TextToSpeech  m_tts;
    private ProgressDialog m_dialog;
    private TextView m_textView;
    private boolean m_recoverFlag;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brige_crack_check);  
        initUI();  
        m_recoverFlag = false;
        btnRecover.setEnabled(false);
        btnProcess.setOnClickListener(new ProcessClickListener());  
        btnRecover.setOnClickListener(new ProcessClickListener());
        handler = new Handler(getMainLooper());
        m_tts = new TextToSpeech(this,this);
        m_textView = (TextView)findViewById(R.id.textView1);
        m_textView.setText("");
        //更新界面  
        m_dialog = new ProgressDialog(this); 
    	//设置进度条风格，风格为圆形，旋转的 
        m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 

        m_dialog.setMessage("图像分析中..."); 

    	//设置ProgressDialog 的进度条是否不明确 
    	m_dialog.setIndeterminate(false); 
    	//设置ProgressDialog 是否可以按退回按键取消 
    	m_dialog.setCancelable(true); 

    }  
    public void onInit(int status) {
    	System.out.println("onInit status："+status);
        if (status == TextToSpeech.SUCCESS) {  
            int result = m_tts.setLanguage(Locale.US);  
            if (result == TextToSpeech.LANG_MISSING_DATA  
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {  
                //Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();  
            	System.out.println("lang missing data");
            }  
        } 
        
        //m_tts.speak("welcome use brige check tool software", TextToSpeech.QUEUE_FLUSH, null);
    }
  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  
  
    public void initUI(){  
        btnProcess = (Button)findViewById(R.id.button1);
        btnRecover = (Button)findViewById(R.id.button2);  
        imgHuaishi = (TouchImageView)findViewById(R.id.imageViewCrackCheck);  
        Log.i(TAG, "initUI sucess...");  
  
    }  
  
    public void procSrc2Gray(String file_path){  


	      File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
	      File cascadeFile = new File(cascadeDir, "hogcascade_crack.xml");
	      
	      if (!cascadeFile.exists())
	      {
	    	  try
	    	  {
		    	  //Toast.makeText(getBaseContext(), "create cascade file",Toast.LENGTH_SHORT).show();
			      InputStream is = getResources().openRawResource(R.raw.hogcascade_crack);
		
			      FileOutputStream os = new FileOutputStream(cascadeFile);
			
				  byte[] buffer = new byte[4096];
				  int bytesRead;
				  while ((bytesRead = is.read(buffer)) != -1) {
				  os.write(buffer, 0, bytesRead);
				  }
				  is.close();
				  os.close();
	    	  }
	    	  catch (IOException e) {
	              e.printStackTrace();
	              Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
	          }
	     }
	    handler.post(startAns);
        int ret = JniOpencv.CrackDetect(file_path,cascadeFile.getAbsolutePath());
        if (ret == 0)
        {

        	System.out.println("proc success");
        	int index = file_path.lastIndexOf('.');
        	new_file_path = file_path.substring(0, index) + "_detect.jpg"; 
        	System.out.println("new file path is "+ new_file_path);
        	File new_file= new File(new_file_path);
        	if (!new_file.exists())
        	{
        		Log.e(TAG, "Failed to find the detect result image: "+new_file_path);
        		
        	}
        	else
        	{

        		Log.i(TAG, "procSrc2Gray sucess...");  
	        	Intent mediaScanIntent = new Intent(
	                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	      
	        	
	            Uri contentUri = Uri.fromFile(new_file); //out is your output file
	            mediaScanIntent.setData(contentUri);
	            sendBroadcast(mediaScanIntent);
	        	handler.post(drawDest);
        	}

        }
        else
        {
        	Log.i(TAG, "procSrc2Gray failed...");  
        	System.out.println("crark detect failed result = " + ret);
        	handler.post(failAns);
        	//handler.post(drawSrc);

        }
        
    }  
  
    Runnable   drawDest = new  Runnable(){  
        @Override  
        public void run() {  
            //更新界面
        	System.out.println("udpate dest UI");
        	grayBitmap = BitmapFactory.decodeFile(new_file_path);
        	if (null == grayBitmap)
        	{
        		Log.e(TAG, "decode detect file failed");
        	}
        	else
        	{
        		System.out.println("grayBitmap :"+grayBitmap.getByteCount());
        		m_dialog.cancel();
            	m_textView.setText("分析完成");
	        	imgHuaishi.setImageBitmap(grayBitmap);
	        	btnRecover.setEnabled(true);  
        	}
        	
        	System.out.println("udpate dest UI finished");
        }  
    };
        
        Runnable   drawSrc = new  Runnable(){  
            @Override  
            public void run() {  
                //更新界面  
            	btnRecover.setEnabled(false);
            	imgHuaishi.setImageBitmap(srcBitmap);
            }  
        };
        
        Runnable   startAns = new  Runnable(){  
            @Override  
            public void run() {  

            	//显示 
            	m_dialog.show();
            }  
        };
        /*Runnable   stopAns = new  Runnable(){  
            @Override  
            public void run() {  

            	//显示 
            	m_dialog.cancel();
            }  
        };*/
        Runnable   failAns = new  Runnable(){  
            @Override  
            public void run() {  

            	//显示 
            	m_textView.setText("分析中止，未提取到有效信息");
            	m_dialog.cancel();
            	btnRecover.setEnabled(false);
            	imgHuaishi.setImageBitmap(srcBitmap);
            	
            }  
        };
    public void detectAndDisplay(String filepath )
    {
      System.out.println(filepath);
      
    }

    @Override  
    protected Dialog onCreateDialog(int id) {  
        if(id==openfileDialogId){  

            Dialog dialog = openfile.createDialog(id, this, "select crack file", new CallbackBundle() {  
                @Override  
                public void callback(Bundle bundle) { 
                    filePath = bundle.getString("path");  

                    int index = filePath.lastIndexOf('/');
                    setTitle(filePath.substring(index+1));
                	
                    new Thread() {

                        @Override
                        public void run() {

                        	File imgFile = new File(filePath);
                        	if (!imgFile.exists())
                        	{
                        		System.out.println("file not exist");
                        		return;
                        	}
                            srcBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            //imgHuaishi.setImageBitmap(srcBitmap);
                            //handler.post(drawSrc);
                            procSrc2Gray(filePath);
                        }
                    }.start();
                }
            },   
            ".jpg;");  
            return dialog;  
        }  
        return null;  
    }
    private class ProcessClickListener implements OnClickListener{  
          
        @Override  
        public void onClick(View v) { 
        	if (v.getId() == R.id.button2)
        	{
        		if(false == m_recoverFlag)
        		{
	        		imgHuaishi.setImageBitmap(srcBitmap);
	        		btnRecover.setText("分析");
	        		m_recoverFlag = true;
        		}
        		else
        		{
	        		imgHuaishi.setImageBitmap(grayBitmap);
	        		btnRecover.setText("原图");
	        		m_recoverFlag = false;
        		}
        		
        	}
        	else
        	{
	            // TODO Auto-generated method stub  

	                //procSrc2Gray();
        		btnRecover.setText("原图");
        		m_recoverFlag = false;
	            	showDialog(openfileDialogId);
	            	//btnRecover.setEnabled(true);

        	}

        }  
  
    }  
  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        //load OpenCV engine and init OpenCV library  
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);  
        Log.i(TAG, "onResume sucess load OpenCV...");  
//      new Handler().postDelayed(new Runnable(){  
//  
//          @Override  
//          public void run() {  
//              // TODO Auto-generated method stub  
//              procSrc2Gray();  
//          }  
//            
//      }, 1000);  
          
    }  
      
      
      
  
}  
