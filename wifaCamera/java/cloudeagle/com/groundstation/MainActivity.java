package cloudeagle.com.groundstation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sony.cameraremote.DeviceDiscoveryActivity;

import cloudeagle.com.groundstation.customBtn.ImageTextButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化按钮及添加点击事件
        ImageTextButton beginCheckBtn = (ImageTextButton)findViewById(R.id.beginCheck);
        beginCheckBtn.initialCustomSetting(R.drawable.camera_000000_128, "开始桥梁检测", 14);
        beginCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(MainActivity.this, "开始桥梁检测", Toast.LENGTH_LONG).show();

                //
//                try {
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this, BeginCheckActivity.class);
//                    startActivity(intent);
//                }catch (Exception e){
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
                try {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DeviceDiscoveryActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        ImageTextButton dataAnalyseBtn = (ImageTextButton)findViewById(R.id.dataAnalyse);
        dataAnalyseBtn.initialCustomSetting(R.drawable.tasks_000000_128, "桥梁数据分析", 14);
        dataAnalyseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "桥梁数据分析，功能开发中~", Toast.LENGTH_LONG).show();
            }
        });

        ImageTextButton cloudDataBtn = (ImageTextButton)findViewById(R.id.cloudData);
        cloudDataBtn.initialCustomSetting(R.drawable.cloud_000000_128, "云端桥梁数据", 14);
        cloudDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "云端桥梁数据，功能开发中~", Toast.LENGTH_LONG).show();
            }
        });

        ImageTextButton planSettingBtn = (ImageTextButton)findViewById(R.id.planSetting);
        planSettingBtn.initialCustomSetting(R.drawable.plane_000000_128, "无人机设置", 14);
        planSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "无人机设置，功能开发中~", Toast.LENGTH_LONG).show();
            }
        });

        ImageTextButton userSettingBtn = (ImageTextButton)findViewById(R.id.userSetting);
        userSettingBtn.initialCustomSetting(R.drawable.user_000000_128, "用户设置", 14);
        userSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "用户设置，功能开发中~", Toast.LENGTH_LONG).show();
            }
        });

        ImageTextButton appSettingBtn = (ImageTextButton)findViewById(R.id.appSetting);
        appSettingBtn.initialCustomSetting(R.drawable.cog_000000_128, "应用设置", 14);
        appSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "应用设置，功能开发中~", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
