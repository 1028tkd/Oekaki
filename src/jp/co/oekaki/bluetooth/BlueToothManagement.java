package jp.co.oekaki.bluetooth;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.co.oekaki.AppConst;
import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.R;
import jp.co.oekaki.menu.BluetoothMenu;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BlueToothManagement {

	//Context
	private Context context;
	//Bluetooth
	private BluetoothAdapter mBluetoothAdapter;
	//接続履歴のあるデバイス情報
//	private List<String> pairedDeviceAdapter = new  ArrayList<String>();
	private ArrayAdapter<String> pairedDeviceAdapter;
	//BluetoothDevice
	private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
	// デバイス検索終了フラグ
	private boolean isDiscvoryFlg;
	//進捗
	private ProgressDialog dialog;

	public BlueToothManagement(Context context){
		this.context = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDeviceAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
	}

	public BluetoothDevice getBluetoothDevice(int potision){
		if(deviceList != null && deviceList.size() > 0)
			return deviceList.get(potision);
		else
			return null;
	}

	public ArrayAdapter<String> getPairedDeviceAdapter() {
		return pairedDeviceAdapter;
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public void setBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}

	public boolean isDiscvoryFlg(){
		return this.isDiscvoryFlg;
	}

	/**===========================================================================================================
     * エラーメッセージを表示する
	 *===========================================================================================================*/
	private void errorMsg(String message){
    	AlertDialog.Builder alert = new AlertDialog.Builder(context);
    	//タイトルの設定
    	alert.setTitle(context.getString(R.string.titleBluetooth));
    	//メッセージの設定
    	alert.setMessage(message);
    	alert.setPositiveButton(context.getString(R.string.item1), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
    	alert.show();
	}

	/**===========================================================================================================
     * Bluetooth端末対応チェック
     * @return		true:使用可 / false:使用不可
	 *===========================================================================================================*/
	public boolean isBluetoothExit(){
		if(mBluetoothAdapter == null){
			return false;
		}
		return true;
	}

	/**===========================================================================================================
     * Bluetooth使用可否チェック
     * @return		true:使用可 / false:使用不可
	 *===========================================================================================================*/
	public boolean isBluetoothEnable(){
		if (!mBluetoothAdapter.isEnabled()) {
			return false;
		}
		return true;
	}

	/**===========================================================================================================
     * ブロードキャストの解除
	 *===========================================================================================================*/
	public void unResisterReceiver(){
		// デバイス検出が終了した場合は、BroadcastReceiver を解除
		try{
            context.unregisterReceiver(devieFoundReceiver);
            Log.v("unResisterReceiver","解除");
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

	/**===========================================================================================================
     * ペアデバイスの問い合わせ
     * @return	 	true:ペアあり / false:ペアなし or 接続履歴なし
	 *===========================================================================================================*/
	public boolean searchPaireDevice(){
		pairedDeviceAdapter.clear();
		//BluetoothAdapterから、接続履歴のあるデバイスの情報を取得
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
	    	Log.v("searchPaireDevice","デバイスあり");
		    //接続履歴のあるデバイスが存在する
		    for (BluetoothDevice device : pairedDevices) {
		        //接続履歴のあるデバイスの情報を順に取得してアダプタに詰める
		        //getName()・・・デバイス名取得メソッド
		        //getAddress()・・・デバイスのMACアドレス取得メソッド
		    	pairedDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
		    	deviceList.add(device);
		    	Log.v("searchPaireDevice",device.getName() + "\n" + device.getAddress());
		    }
		    return true;
		} else {
	    	Log.v("searchPaireDevice","デバイスなし");
		    return false;
		}
	}

	/**===========================================================================================================
     * デバイス検索処理
     * @return	 	true:ペアあり / false:ペアなし or 接続履歴なし
	 *===========================================================================================================*/
	public void doDiscvory(){
        Log.v("doDiscvory","開始");
		// 既に検索処理中は一旦停止する
		if (mBluetoothAdapter.isDiscovering()) {
	        Log.v("doDiscvory","一旦停止");
			mBluetoothAdapter.cancelDiscovery();
	    }

		setProgressDialogShow();

        //インテントフィルターとBroadcastReceiverの登録
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(devieFoundReceiver, filter);

        // 検索処理開始
		mBluetoothAdapter.startDiscovery();
        Log.v("doDiscvory","終了");
	}

	/**===========================================================================================================
     * 発見機能の有効化
     * ローカルデバイスを他のデバイスから発見可能になるよう設定する
     * @return	 	true:ペアあり / false:ペアなし or 接続履歴なし
	 *===========================================================================================================*/
	public void ensureDiscoverable() {
		isDiscvoryFlg = false;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        ((Activity)context).startActivityForResult(discoverableIntent,AppConst.REQUEST_ENABLE_BLUETOOTH_DISCOVERABLE);
	}

	/**===========================================================================================================
     * クライアントスレッドの起動（2人でお絵描き時）
     * @param	position		選択されたBluetoothMacアドレスの位置
	 *===========================================================================================================*/
	public void createClientThread(int position){
		// 選択されたBluetoothDeviceを取得
		BluetoothDevice device = getBluetoothDevice(position);
		if(device != null){
			Log.v("createClientThread", "処理フラグ[" + ((OekakiApplication) context.getApplicationContext()).isBluetoothProcessFlg() + "]");
			if(((OekakiApplication) context.getApplicationContext()).isBluetoothProcessFlg()){
				// 通信開始
				BluetoothClientThread clientThread = new BluetoothClientThread(context, device, getBluetoothAdapter());
				clientThread.start();
				((OekakiApplication) context.getApplicationContext()).setClientThread(clientThread);
			}else{
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				((OekakiApplication) context.getApplicationContext()).getLayout().getBoardView().getBitmap().compress(CompressFormat.JPEG, 100, os);
				byte[] bin = os.toByteArray();
				// 通信開始
				BluetoothSendImageClientThread clientThread = new BluetoothSendImageClientThread(context, device, getBluetoothAdapter(),bin);
				clientThread.start();
				((OekakiApplication) context.getApplicationContext()).setSendImageClientThread(clientThread);
			}
		}else{
			errorMsg(context.getString(R.string.none_device));
		}
	}

	/**===========================================================================================================
     * 検出されたデバイスからのブロードキャストの設定
	 *===========================================================================================================*/
	private final BroadcastReceiver devieFoundReceiver = new BroadcastReceiver(){
	    //検出されたデバイスからのブロードキャストを受ける
	    @Override
	    public void onReceive(Context context, Intent intent){
	        String action = intent.getAction();
	        String dName = null;
	        BluetoothDevice foundDevice;

            Log.v("onReceive","スキャンaction[" +  action + "]");
	        if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
	            Log.v("onReceive","スキャン開始");
	        }
            Log.v("onReceive[action]","action[" + action + "]");
	        if(BluetoothDevice.ACTION_FOUND.equals(action)){
	            //デバイスが検出された
	            foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("ACTION_FOUND", foundDevice.getName());
	            if((dName = foundDevice.getName()) != null){
	                if(foundDevice.getBondState() != BluetoothDevice.BOND_BONDED){
	                    //接続したことのないデバイスのみアダプタに詰める
	                	pairedDeviceAdapter.add(dName + "\n" + foundDevice.getAddress());
	                	deviceList.add(foundDevice);
	                    Log.d("ACTION_FOUND", dName);
	                }
	            }
	        }
	        if(BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
	            foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if(foundDevice.getBondState() != BluetoothDevice.BOND_BONDED){
	            	dName = foundDevice.getName();
	                //接続したことのないデバイスのみアダプタに詰める
	            	pairedDeviceAdapter.add(dName + "\n" + foundDevice.getAddress());
                	deviceList.add(foundDevice);
                    Log.d("ACTION_NAME_CHANGED", dName + "\n" + foundDevice.getAddress());
	            }
	        }

	        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
	        	closeProgressDialog();
	        	if(pairedDeviceAdapter.getCount() > 0){
					BluetoothMenu bluetoothMenu = new BluetoothMenu(context);
					bluetoothMenu.openPopup();
	        	}else{
	    			errorMsg(context.getString(R.string.none_device));
	        	}
	    		// デバイス検出が終了した場合は、BroadcastReceiver を解除
	        	unResisterReceiver();
	            Log.v("onReceive","スキャン終了");
	        }
	    }
	};

	/**===========================================================================================================
     * ProgressDialogを閉じる
	 *===========================================================================================================*/
	private void closeProgressDialog(){
		if(dialog != null){
	        Log.v("closeProgressDialog","ダイアログクローズ");
			dialog.dismiss();
		}
	}

	/**===========================================================================================================
     * ProgressDialogを表示する
	 *===========================================================================================================*/
	private void setProgressDialogShow(){
		dialog = new ProgressDialog(context);
        Log.v("setProgressDialogShow","開始");
		// 進捗状況の表示
		dialog.setTitle("Bluetooth検索");
		dialog.setMessage(context.getString(R.string.infoMsg1));
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		// ProgressDialog の Cancel ボタン
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE,context.getString(R.string.cancel),
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					getBluetoothAdapter().cancelDiscovery();
					unResisterReceiver();
					closeProgressDialog();
				}
			}
		);
		dialog.show();
        Log.v("setProgressDialogShow","終了");
	}
}
