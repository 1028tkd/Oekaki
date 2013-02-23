package jp.co.oekaki.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import jp.co.oekaki.layout.BoardView;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;

public abstract class RecevierThread extends Thread {

	//ソケットに対するI/O処理
	private OutputStream out;
	protected BluetoothSocket socket;
	protected Handler handler = new Handler();

	public RecevierThread(){
	}

	public void setSocket(BluetoothSocket socket){
		this.socket = socket;
	}

	public void setHandler(Handler handler){
		this.handler = handler;
	}

	public void sendMessage(byte[] value) {
		try {
			if(socket == null) return;
			out = socket.getOutputStream();
			out.write(value);
			out.write("\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receveMessage(final BoardView view){
		Log.v("receveMessage","socketは" + socket);
		if(socket == null) return;
		String message;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((message = br.readLine()) != null) {
//				Log.v("message", message);
				final String value = message;
				new Thread(new Runnable() {
				    public void run() {
				    	handler.post(new Runnable() {
				        public void run() {
				        	// value[3]にENDが存在する場合終了とみなす
							view.drawCanvas(value);
				        }
				      });
				    }
				  }).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("WorldReadableFiles")
	public void receveImage(final BoardView view, Context context){
		Log.v("receveImage","socketは" + socket);
		if(socket == null) return;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			Log.v("receveImage","受信中");
			final String fileNm = DateFormat.format("yyyy-MM-dd_kk.mm.ss", System.currentTimeMillis()).toString() + ".jpg";
			// 一度ローカルに保存する
			out = new BufferedOutputStream(context.openFileOutput(fileNm,Context.MODE_WORLD_READABLE));
			// 入力ストリームを取得
			in = new BufferedInputStream(socket.getInputStream());
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))!=-1){
				Log.v("receveImage","len[" + len + "]");
				out.write(buf, 0, len);
			}
			Log.v("receveImage","受信完了");
			// 入出力ストリームを閉じる
			out.flush();
			Log.v("receveImage","flush中");

			new Thread(new Runnable() {
			    public void run() {
			    	handler.post(new Runnable() {
			        public void run() {
						Log.v("receveImage","描画処理開始");
						// 保存した画像を表示する
						view.readToFile(fileNm);
						Log.v("receveImage","描画処理終了");
			        }
			      });
			    }
			  }).start();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			Log.v("receveImage","処理[finally]");
			try {
				if(out != null) out.close();
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
