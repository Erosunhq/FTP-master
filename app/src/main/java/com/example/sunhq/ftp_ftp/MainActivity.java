package com.example.sunhq.ftp_ftp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String FTP_CONNECT_SUCCESSS = "ftp 连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp 连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp 断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp 上文件不存在";


    public static final String FTP_DOWN_LOADING = "ftp 文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp 文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp 文件下载失败";


    // 定义文件的存取路径(分类存放在不同的文件夹下面)
    public static final String HOME_SHOW = "/FTP/images/";  //家装展示图片路径
    public static final String CORPORATE_HONOR = "/FTP/image/";  //企业荣誉
    public static final String ENGINEERING_CASE = "/FTP/project/";  //工程案列

    String[] serverFolder = new String[]{HOME_SHOW,CORPORATE_HONOR,ENGINEERING_CASE+"parameter1/"
            ,ENGINEERING_CASE+"parameter2/",ENGINEERING_CASE+"parameter3/",ENGINEERING_CASE+"parameter4/"};

    //本地存放的文件夹
    String localFolder = "/mnt/sdcard/Picture/";
    // **********************************************

    // 创建线程池对内存进行优化处理
    private ExecutorService executorService;

    long startTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //最大十条线程同时执行
        executorService = Executors.newFixedThreadPool(10);
    }
    private void initView() {

        //下载功能
        Button buttonDown = (Button)findViewById(R.id.button_down);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 下载
                        try {
                            //String name="白云.JPG";
                            //name = new String(name.getBytes(LOCAL_CHARSET),
                            //        SERVER_CHARSET);
                            //单文件下载 (服务器上文件的路径, 存放的本地路径, 下载到本地后要保存的文件名称(可以跟源文件名不一样), 下载监听器)
                            new FTP().downloadSingleFile("/FTP/蓝天.JPG","/mnt/sdcard/sunhq/",new FTP.DownLoadProgressListener(){
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
                            e.printStackTrace();
                        }
                    }
                }).start();*/
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        // 下载
                        try {
                            for (int j = 0; j < serverFolder.length; j++) {
                                switch (j){
                                    case 0:
                                        localFolder = "/mnt/sdcard/Picture/"+"images/";
                                        break;
                                    case 1:
                                        localFolder = "/mnt/sdcard/Picture/"+"image/";
                                        break;
                                    case 2:
                                        localFolder = "/mnt/sdcard/Picture/"+"project/parameter1/";
                                        break;
                                    case 3:
                                        localFolder = "/mnt/sdcard/Picture/"+"project/parameter2/";
                                        break;
                                    case 4:
                                        localFolder = "/mnt/sdcard/Picture/"+"project/parameter3/";
                                        break;
                                    case 5:
                                        localFolder = "/mnt/sdcard/Picture/"+"project/parameter4/";
                                        break;
                                }
                                String[] serverPath = new FTP().JudgeFile(serverFolder[j]);  // 服务端的文件路径
                                for (int i = 0; i < serverPath.length; i++) {
                                    //单文件下载 (服务器上文件的路径, 存放的本地路径, 下载到本地后要保存的文件名称(可以跟源文件名不一样), 下载监听器)
                                    new FTP().downloadSingleFile(serverFolder[j] + serverPath[i], localFolder, serverPath[i], new FTP.DownLoadProgressListener() {
                                        @Override
                                        public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                            Log.d(TAG, currentStep);
                                            if (currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)) {
                                                Log.d(TAG, "-----下载--成功");
                                            } else if (currentStep.equals(MainActivity.FTP_DOWN_LOADING)) {
                                                Log.d(TAG, "-----下载---" + downProcess + "%");

                                            }
                                            downProcess = 0;   //对不同的文件,进度重新置0
                                        }
                                    });
                                    if (i == serverPath.length) {
                                        Log.d(TAG, "该文件夹下图片全部下载完成!!!");
                                    }
                                }
                        }
                        } catch (Exception e) {
                            e.printStackTrace();
                    }
                    }
                });
            }
        });

    }
}

