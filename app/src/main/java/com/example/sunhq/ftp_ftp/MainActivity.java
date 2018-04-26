package com.example.sunhq.ftp_ftp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String FTP_CONNECT_SUCCESSS = "ftp 连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp 连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp 断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp 上文件不存在";


    public static final String FTP_DOWN_LOADING = "ftp 文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp 文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp 文件下载失败";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }
    private void initView() {


        //下载功能
        Button buttonDown = (Button)findViewById(R.id.button_down);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 下载
                        try {
                            String name="白云.JPG";
                            //name = new String(name.getBytes(LOCAL_CHARSET),
                            //        SERVER_CHARSET);
                            //单文件下载 (服务器上文件的路径, 存放的本地路径, 下载到本地后要保存的文件名称(可以跟源文件名不一样), 下载监听器)
                            new FTP().downloadSingleFile("/FTP/"+name,"/mnt/sdcard/sunhq/",name,new FTP.DownLoadProgressListener(){
                            //new FTP().downloadSingleFile("/FTP/a2.JPG","/mnt/sdcard/sunhq/","back11.JPG",new FTP.DownLoadProgressListener(){
                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                    Log.d(TAG, currentStep);
                                    if(currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)){
                                        Log.d(TAG, "-----下载--成功");
                                    } else if(currentStep.equals(MainActivity.FTP_DOWN_LOADING)){
                                        Log.d(TAG, "-----下载---"+downProcess + "%");
                                    }
                                }

                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

    }
}

