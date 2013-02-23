package jp.co.oekaki.bluetooth;

import java.io.IOException;
import java.util.UUID;

import jp.co.oekaki.OekakiApplication;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class BluetoothServerThread extends RecevierThread {
	//サーバー側の処理
	//UUID：Bluetoothプロファイル毎に決められた値
	private final BluetoothServerSocket servSock;
	static BluetoothAdapter myServerAdapter;
	private Context context;
	//UUIDの生成
	public static final UUID OEKAKI_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	//コンストラクタの定義
	public BluetoothServerThread(Context context, BluetoothAdapter btAdapter){
		//各種初期化
		this.context = context;
		BluetoothServerSocket tmpServSock = null;
		myServerAdapter = btAdapter;
		try{
			//自デバイスのBluetoothサーバーソケットの取得
			 tmpServSock = myServerAdapter.listenUsingRfcommWithServiceRecord("OekakiBlueTooth", OEKAKI_UUID);
		}catch(IOException e){
			e.printStackTrace();
		}
		servSock = tmpServSock;
		Log.v("BluetoothServerThread","サーバー始動開始[" + servSock + "]");
	}

	public void run(){
		BluetoothSocket receivedSocket = null;
		while(true){
			try{
				//クライアント側からの接続要求待ち。ソケットが返される。
				receivedSocket = servSock.accept();
			}catch(IOException e){
				break;
			}

			if (receivedSocket != null) {
				// 追加の接続を受け付けない為に先にサーバーソケットを閉じる
				synchronized (BluetoothServerThread.this) {
//					cancel();
				}
			}

			if(receivedSocket != null){
				//ソケットを受け取れていた(接続完了時)の処理
				setSocket(receivedSocket);
//				receveMessage(((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView());
				receveImage(((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView(), context);
				break;
			}
		}
	}

	public void cancel() {
		try {
			Log.v("cancel","サーバーclose");
			servSock.close();
		} catch (IOException e) { }
	}
}