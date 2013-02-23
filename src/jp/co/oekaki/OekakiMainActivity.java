package jp.co.oekaki;

import jp.co.oekaki.bluetooth.BlueToothManagement;
import jp.co.oekaki.bluetooth.BluetoothServerThread;
import jp.co.oekaki.image.ImageManagement;
import jp.co.oekaki.layout.OekakiLayout;
import jp.co.oekaki.menu.MenuFacade;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

public class OekakiMainActivity extends Activity {


	// アプリケーションインスタンス
	private OekakiApplication mOekakiApp;
	private final Handler handler = new Handler();

	/**===========================================================================================================
	 * 初期化処理
	 *
	 * @param	savedInstanceState	Bundle
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_oekaki_main);

		/**-------------------------------------------------------------------------------------------------------
		 *初期設定を行う
		 * -------------------------------------------------------------------------------------------------------*/
		mOekakiApp = (OekakiApplication)this.getApplication();
		mOekakiApp.setImgInstance(new ImageManagement(this));
		mOekakiApp.setMenu(new MenuFacade(this));
		mOekakiApp.setBlueToothMng(new BlueToothManagement(this));

		/**-------------------------------------------------------------------------------------------------------
		 *ボタンレイアウトを設定する
		 *-------------------------------------------------------------------------------------------------------*/
		mOekakiApp.setLayout(new OekakiLayout(this));
		mOekakiApp.getLayout().createLaytout();

	}

	/**===========================================================================================================
	 * onPause処理
	 *===========================================================================================================*/
	@Override
	protected void onPause() {
		super.onPause();
		//ポップアップメニューを閉じる
		mOekakiApp.getMenu().closePopUpMenu();
		if(mOekakiApp.getServerThread() != null)
			mOekakiApp.getServerThread().cancel();
		if(mOekakiApp.getClientThread() != null)
			mOekakiApp.getClientThread().cancel();
		if(mOekakiApp.getSendImageClientThread() != null)
			mOekakiApp.getSendImageClientThread().cancel();
	}

	/**===========================================================================================================
	 * onSaveInstanceState処理
	 *===========================================================================================================*/
	@Override
	// インスタンスの状態保管
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**===========================================================================================================
	 * onRetainNonConfigurationInstance処理
	 *===========================================================================================================*/
	@Override
	public Object onRetainNonConfigurationInstance() {
	return null;
	}

	/**===========================================================================================================
	 * onRestoreInstanceState処理
	 *===========================================================================================================*/
	@Override
	// インスタンスの状態を再現する
	public void onRestoreInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}

	/**===========================================================================================================
	 * onResume処理
	 *===========================================================================================================*/
	@Override
	public void onResume() {
		super.onResume();
		Log.v("onResume","onResume");
		if(mOekakiApp.getBlueToothMng().isBluetoothExit()){
			if(mOekakiApp.getBlueToothMng().isBluetoothEnable()){
			//サーバースレッド起動、クライアントのからの要求待ちを開始
				BluetoothServerThread tmpServerThread = new BluetoothServerThread(this, mOekakiApp.getBlueToothMng().getBluetoothAdapter());
				tmpServerThread.setHandler(handler);
				tmpServerThread.start();
				mOekakiApp.seServerThread(tmpServerThread);
			}
		}
	}

	/**===========================================================================================================
	 * onDestroy処理
	 *===========================================================================================================*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOekakiApp.setBlueToothMng(null);
		mOekakiApp.setClientThread(null);
		mOekakiApp.setSendImageClientThread(null);
		mOekakiApp.seServerThread(null);
		mOekakiApp = null;
		cleanupView(findViewById(R.id.rootContainer));
	}

	/**===========================================================================================================
	 * onActivityResult処理
	 *===========================================================================================================*/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mOekakiApp.getMenu().getSettingMenu().onActivityResult(requestCode, resultCode, data);
	}

	/**===========================================================================================================
	 * 指定したビュー階層内のドローワブルをクリアする。
	 * （ドローワブルをのコールバックメソッドによるアクティビティのリークを防ぐため）
	 * @param view
	 *===========================================================================================================*/
	public static final void cleanupView(View view) {
		if(view instanceof ImageButton) {
			ImageButton ib = (ImageButton)view;
			ib.setImageDrawable(null);
		} else if(view instanceof ImageView) {
			ImageView iv = (ImageView)view;
			iv.setImageDrawable(null);
		} else if(view instanceof SeekBar) {
			SeekBar sb = (SeekBar)view;
			sb.setProgressDrawable(null);
			sb.setThumb(null);
			// } else if(view instanceof( xxxx )) {-- 他にもDrawable を使用するUIコンポーネントがあれば追加
		}
		if(view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup)view;
			int size = vg.getChildCount();
			for(int i = 0; i < size; i++) {
				cleanupView(vg.getChildAt(i));
			}
		}
	}
}

