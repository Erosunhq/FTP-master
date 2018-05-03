package com.example.sunhq.ftp_ftp;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTP {


	FTPFile[] files;
	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 端口号
	 */
	private int serverPort;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;

	public FTP() {
		this.hostName = "192.168.1.100";
		this.serverPort = 2121;
		this.userName = "sunhq";
		this.password = "1234";
		this.ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(10000);  //设置十秒钟超时,未连接上就表示超时,给出提示
	}

	// -------------------------------------------------------文件下载方法------------------------------------------------

	// 打开FTP服务以及判断服务器文件是否存在
	public String[] JudgeFile(String serverPathLists){
		List<String> list = new ArrayList<String>();

    	// 打开FTP服务
		try {

			this.openConnect();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 先判断服务器文件是否存在
		try {
			files = ftpClient.listFiles(serverPathLists);
			for(int i = 0; i < files.length;i++){
				list.add(files[i].getName());
				//System.out.println(serverPath[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String serverPath[] = new String[list.size()];
		serverPath = list.toArray(serverPath);
		return serverPath;
	}


	/**
	 * 下载单个文件，可实现断点下载.
	 * 
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param localPath
	 *            本地目录
	 * @param listener
	 *            监听器
	 * @throws IOException
	 */
    //下面的 serverPath   如果路径是文件，只会返回一个长度为1的数组。
    //如果该路径为文件夹，则会返回该文件夹下对应的所有文件。
	public void downloadSingleFile(String serverPath, String localPath, String name , DownLoadProgressListener listener)
			throws Exception {

		// 打开FTP服务
		try {

			this.openConnect();
			listener.onDownLoadProgress(MainActivity.FTP_CONNECT_SUCCESSS, 0, null);
		} catch (IOException e1) {
			e1.printStackTrace();
			listener.onDownLoadProgress(MainActivity.FTP_CONNECT_FAIL, 0, null);
			return;
		}


		//创建本地文件夹
		File mkFile = new File(localPath);
		if (!mkFile.exists()) {
			mkFile.mkdirs();
		}



		//下载后存储到本地的绝对路径以及文件名,文件名是服务器上图片的原文件名
		localPath = localPath + name;
		// 接着判断下载的文件是否能断点下载
		files = ftpClient.listFiles(serverPath);
		long serverSize = files[0].getSize(); // 获取远程文件的长度

		File localFile = new File(localPath);
		long localSize = 0;
		if (localFile.exists()) {
			localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
			if (localSize >= serverSize) {
				File file = new File(localPath);
				file.delete();
			}
		}
		
		// 进度
		long step = serverSize / 100;
		long process = 0;
		long currentSize = 0;
		// 开始准备下载文件
		OutputStream out = new FileOutputStream(localFile, true);
		ftpClient.setRestartOffset(localSize);
		ftpClient.setBufferSize(1024*1024);
		InputStream input = ftpClient.retrieveFileStream(serverPath);
		byte[] b = new byte[1024];
		int length = 0;
		while ((length = input.read(b)) != -1) {
			out.write(b, 0, length);
			currentSize = currentSize + length;
			if (currentSize / step != process) {
				process = currentSize / step;
				if (process % 20 == 0) {  //每隔%5的进度返回一次
					listener.onDownLoadProgress(MainActivity.FTP_DOWN_LOADING, process, null);
				}
			}
		}
		out.flush();
		out.close();
		input.close();
		
		// 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
		if (ftpClient.completePendingCommand()) {
			listener.onDownLoadProgress(MainActivity.FTP_DOWN_SUCCESS, 0, new File(localPath));
		} else {
			listener.onDownLoadProgress(MainActivity.FTP_DOWN_FAIL, 0, null);
		}

		// 下载完成之后关闭连接
		this.closeConnect();
		listener.onDownLoadProgress(MainActivity.FTP_DISCONNECT_SUCCESS, 0, null);

		return;
	}


	// -------------------------------------------------------打开关闭连接------------------------------------------------

	/**
	 * 打开FTP服务.
	 * 
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
		// 中文转码
		ftpClient.setControlEncoding("GBK");
		int reply; // 服务器响应值
		// 连接至服务器
		ftpClient.connect(hostName, serverPort);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		// 登录到服务器
		ftpClient.login(userName, password);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient
					.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// 二进制文件支持
			ftpClient
					.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	/**
	 * 关闭FTP服务.
	 * 
	 * @throws IOException
	 */
	public void closeConnect() throws IOException {
		if (ftpClient != null) {
			// 退出FTP
			ftpClient.logout();
			// 断开连接
			ftpClient.disconnect();
		}
	}

	// ---------------------------------------------------下载监听---------------------------------------------


	/*
	 * 下载进度监听
	 */
	public interface DownLoadProgressListener {
		public void onDownLoadProgress(String currentStep, long downProcess, File file);

		//待修改
	}

}
