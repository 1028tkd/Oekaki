package jp.co.oekaki.bluetooth;

import java.io.IOException;
import java.util.UUID;

import jp.co.oekaki.OekakiApplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class BluetoothClientThread extends RecevierThread {
	//クライアント側の処理
	private final BluetoothSocket clientSocket;
	private final BluetoothDevice mDevice;
	private Context context;
	//UUIDの生成
	public static final UUID OEKAKI_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myClientAdapter;

	//コンストラクタ定義
	public BluetoothClientThread(Context context,  BluetoothDevice device, BluetoothAdapter btAdapter){
		//各種初期化
		this.context = context;
		BluetoothSocket tmpSock = null;
		mDevice = device;
		myClientAdapter = btAdapter;

		try{
			//自デバイスのBluetoothクライアントソケットの取得
			tmpSock = device.createRfcommSocketToServiceRecord(OEKAKI_UUID);
		}catch(IOException e){
			e.printStackTrace();
		}
		clientSocket = tmpSock;
		super.socket = clientSocket;
		Log.v("BluetoothClientThread","クライアント始動開始");
	}

	public void run(){
		//接続要求を出す前に、検索処理を中断する。
		if(myClientAdapter.isDiscovering()){
			myClientAdapter.cancelDiscovery();
		}

		try{
			//サーバー側に接続要求
			Log.v("BluetoothClientThread","接続要求開始");
			clientSocket.connect();
			Log.v("BluetoothClientThread","接続要求終了");
		}catch(IOException e){
			 e.printStackTrace();
			 try {
				 clientSocket.close();
			 } catch (IOException closeException) {
				 e.printStackTrace();
			 }
			 return;
		}

		receveMessage(((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView());
	}

	public void cancel() {
		try {
			Log.v("cancel","クライアントclose");
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}