package jp.co.oekaki.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class ReadWriteModel extends Thread {

	//ソケットに対するI/O処理
	public static InputStream in;
	public static OutputStream out;
	private Context mContext;
	private float[] x;
	private float[] y;

	//コンストラクタの定義
	public ReadWriteModel(Context context, BluetoothSocket socket, float[] x, float[] y){
		mContext = context;
		this.x = x;
		this.y = y;

		try {
			//接続済みソケットからI/Oストリームをそれぞれ取得
			in = socket.getInputStream();
			out = socket.getOutputStream();
			Log.v("ReadWriteModel","ReadWriteModel開始");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void write(byte[] buf){
		//Outputストリームへのデータ書き込み
		try {
			out.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		byte[] buf = new byte[1024];
		String rcvNum = null;
		int tmpBuf = 0;

		try {
			for(int i = 0; i < x.length; i++){
				Log.v("送信中","[送信中x][" + x[i] + "]");
				write(Float.toString(x[i]).getBytes("UTF-8"));
			}

			for(int i = 0; i < y.length; i++){
				Log.v("送信中","[送信中y][" + y[i] + "]");
				write(Float.toString(y[i]).getBytes("UTF-8"));
			}
			while(true){
				tmpBuf = in.read(buf);
				if(tmpBuf!=0){
					rcvNum = new String(buf, "UTF-8");
					Log.v("受信中","[" + rcvNum + "]");
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			try {
				if(in != null){
					in.close();
				}
				if(out != null){
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}