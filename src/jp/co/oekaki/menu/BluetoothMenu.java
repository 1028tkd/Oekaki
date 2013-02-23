package jp.co.oekaki.menu;

import java.util.ArrayList;
import java.util.List;

import jp.co.oekaki.AppConst;
import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.R;
import jp.co.oekaki.menu.OekakiPopupMenu.OnPopupSelectionListener;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;

/**===========================================================================================================
 * Bluetoothメニュー管理クラス
 *
 * @author sugahara satoshi
 * @serial 1.0
 * @create 2013-01-31
 *===========================================================================================================*/
public class BluetoothMenu {

	// Context
	private Context context;
	// onPauseメソッドで確実に閉じるための参照
	private OekakiPopupMenu mPopupMenu;
	// このアクティビィティのルートビュー
	private View mRootContainer;
	// アプリケーションインスタンス
	private OekakiApplication mOekakiApp;

	public BluetoothMenu(Context context){
		this.context = context;
		mOekakiApp = (OekakiApplication) context.getApplicationContext();
		mRootContainer = (View)((Activity)context).findViewById(R.id.rootContainer);
	}

	/**===========================================================================================================
	 * Bluetoothメニューを生成する。
	 *===========================================================================================================*/
	private List<OekakiPopupMenu.MenuItem> createBluetoothItems() {
		List<OekakiPopupMenu.MenuItem> result = new ArrayList<OekakiPopupMenu.MenuItem>();
		OekakiPopupMenu.MenuItem item = null;
		ArrayAdapter<String> bluetoothPairedDeviceList = mOekakiApp.getBlueToothMng().getPairedDeviceAdapter();
		for(int i=0; i < bluetoothPairedDeviceList.getCount(); i++){
			item = new OekakiPopupMenu.MenuItem();
			item.id = i+1;
			item.title = bluetoothPairedDeviceList.getItem(i);
			result.add(item);
		}
		return result;
	}

	/**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー。
	 *===========================================================================================================*/
	private OnPopupSelectionListener mOnPopupSelectionListenerBt = new OnPopupSelectionListener() {
		public void onSelection(int position, long id) {
			Log.v("mOnPopupSelectionListenerBt","positon[" + position + "]id[" + id + "]");
			mOekakiApp.getBlueToothMng().createClientThread(position);
		}
	};

	/**===========================================================================================================
	 * ポップアップウインドウを閉じる
	 *===========================================================================================================*/
	public void closePopupMenu(){
		if (mPopupMenu != null && mPopupMenu.isShowing()) {
			mPopupMenu.dismiss();
			mPopupMenu = null;
		}
	}

	/**===========================================================================================================
	 * ポップアップウインドウを開く
	 *===========================================================================================================*/
	public void openPopup(){
		mRootContainer = (View)((Activity)context).findViewById(R.id.rootContainer);
		mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.SETTING_POPUPMENU_WIDTH,createBluetoothItems(),((Activity)context).getString(R.string.menuHint),1);
		// 選択項目コールバックリスナー
		mPopupMenu.setOnPopupSelectionListener(mOnPopupSelectionListenerBt);
		// 画面の右下寄せで表示する
		mPopupMenu.showAtLocation(Gravity.CENTER, mOekakiApp.defaultPopupX(), mOekakiApp.defaultPopupY());
	}
}
