package jp.co.oekaki;

import jp.co.oekaki.bluetooth.BlueToothManagement;
import jp.co.oekaki.bluetooth.BluetoothClientThread;
import jp.co.oekaki.bluetooth.BluetoothSendImageClientThread;
import jp.co.oekaki.bluetooth.BluetoothServerThread;
import jp.co.oekaki.image.ImageManagement;
import jp.co.oekaki.layout.OekakiLayout;
import jp.co.oekaki.menu.MenuFacade;
import android.app.Application;
import android.content.res.Configuration;
import android.net.Uri;

public class OekakiApplication extends Application {

	// メニュータイトル
	public String[] popupMenuTitles;
	// 線の種類
	public String[] popupMenuPenKind;
	// 線の太さ
	public String[] popupMenuPenStroke;
	// 線の設定
	public String[] popupMenuPenSetting;
	// 線の色
	public String[] popupMenuPenColor;
	private int iPopupX;
	private int iPopupY;
	private static OekakiApplication instance;
	// 画像管理クラスインスタンス
	private ImageManagement imgInstance;
	// レイアウト
	private OekakiLayout layout;
	// メニュー用
	private MenuFacade menu;
	// カメラより取得したUri
	private Uri mImageUri;
	// Bluetooth管理クラス
	private BlueToothManagement blueToothMng;
	// Bluetoothサーバーソケット
	private BluetoothServerThread serverThread;
	// Bluetoothクライアントソケット
	private BluetoothClientThread clientThread;
	// Bluetoothクライアントソケット(画像送信用）
	private BluetoothSendImageClientThread sendImageClientThread;

	// Bluetooth Bluetooth処理判別フラグ(true:2人でお絵描き用/ false:画像送信用)
	private boolean bluetoothProcessFlg;

	/**
	* シングルトンインスタンスを取得する。
	* @return シングルトンインスタンス
	*/
	public static OekakiApplication getInstance() {
		return instance;
	}

	/**===========================================================================================================
	 * 設定メニュー表示用画像配列
	 *===========================================================================================================*/
	public static final int[] POPUPMENU_ICONS = {
		R.drawable.ic_menu_save,
		R.drawable.ic_menu_delete,
		R.drawable.ic_menu_edit,
		R.drawable.ic_menu_archive,
		R.drawable.ic_menu_camera,
		R.drawable.ic_menu_gallery,
		R.drawable.ic_menu_compose,
		R.drawable.ic_menu_bluetooth,
		R.drawable.ic_menu_bluetooth,
//		R.drawable.ic_menu_help,
		R.drawable.ic_menu_close_clear_cancel
	};

	/**===========================================================================================================
	 * 設定メニュー表示用画像配列(ペンの種類)
	 *===========================================================================================================*/
	public static final int[] POPUPMENU_ICONS_PENCOLOR = {
		R.drawable.enpitu_black,
		R.drawable.enpitu_blue,
		R.drawable.enpitu_yellow,
		R.drawable.enpitu_red,
		R.drawable.enpitu_perple,
		R.drawable.enpitu_black,
		R.drawable.enpitu_green,
		R.drawable.enpitu_black,
		R.drawable.enpitu_perple
	};

	/**===========================================================================================================
	 * 初期処理
	 *===========================================================================================================*/
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		popupMenuTitles = getResources().getStringArray(R.array.popupmenu_mainTitles);
		// 線の種類
		popupMenuPenKind = getResources().getStringArray(R.array.popupmenu_penKind);
		// 線の太さ
		popupMenuPenStroke = getResources().getStringArray(R.array.popupmenu_penStroke);
		// 線の設定
		popupMenuPenSetting = getResources().getStringArray(R.array.popupmenu_penSetting);
		// 線の色
		popupMenuPenColor = getResources().getStringArray(R.array.popupmenu_penColor);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public void setLayout(OekakiLayout layout){
		this.layout = layout;
	}

	public OekakiLayout getLayout(){
		return layout;
	}


	public void setDefaultPopupX(int pixelX) {
		iPopupX = pixelX;
	}

	public void setDefaultPopupY(int pixelY) {
		iPopupY = pixelY;
	}

	public int defaultPopupX() {
		return iPopupX;
	}

	public int defaultPopupY() {
		return iPopupY;
	}

	public ImageManagement getImgInstance() {
		return imgInstance;
	}

	public void setImgInstance(ImageManagement imgInstance) {
		this.imgInstance = imgInstance;
	}

	public MenuFacade getMenu() {
		return menu;
	}

	public void setMenu(MenuFacade menu) {
		this.menu = menu;
	}

	public Uri getImageUri() {
		return mImageUri;
	}

	public void setImageUri(Uri mImageUri) {
		this.mImageUri = mImageUri;
	}

	public BlueToothManagement getBlueToothMng() {
		return blueToothMng;
	}

	public void setBlueToothMng(BlueToothManagement blueToothMng) {
		this.blueToothMng = blueToothMng;
	}

	public BluetoothServerThread getServerThread() {
		return serverThread;
	}

	public void seServerThread(BluetoothServerThread serverThread) {
		this.serverThread = serverThread;
	}

	public BluetoothClientThread getClientThread() {
		return clientThread;
	}

	public void setClientThread(BluetoothClientThread clientThread) {
		this.clientThread = clientThread;
	}
	public boolean isBluetoothProcessFlg() {
		return bluetoothProcessFlg;
	}

	public void setBluetoothProcessFlg(boolean bluetoothProcessFlg) {
		this.bluetoothProcessFlg = bluetoothProcessFlg;
	}

	public BluetoothSendImageClientThread getSendImageClientThread() {
		return sendImageClientThread;
	}

	public void setSendImageClientThread(
			BluetoothSendImageClientThread sendImageClientThread) {
		this.sendImageClientThread = sendImageClientThread;
	}
}
