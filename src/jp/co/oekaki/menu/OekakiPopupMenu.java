package jp.co.oekaki.menu;

import java.util.List;

import jp.co.oekaki.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**===========================================================================================================
 * ポップアップウインドウに関するクラス
 *
 * @author sugahara satoshi
 * @serial 1.0
 * @create 2013-01-31
 *===========================================================================================================*/
@SuppressLint("ViewConstructor")
public class OekakiPopupMenu extends PopupWindow {

	// このクラスを呼び出すアクティビィティのルートビュー
	private final View mParent;
	// このクラスを呼び出すアクティビィティのコンテキスト
	private final Context mContext;
	// メニューを動的に生成するインフレーター
	private final LayoutInflater mInflater;
	// ポップアップに表示されるメニュー項目のリスト(リストアダプターにセット)
	private final MenuAdapter menuAdapter;
	// ポップアップに表示されるメニュー項目のリスト(リストアダプターにセット)
	private final BluetoothMenuAdapter bluetoothAdapter;
	// マーキー表示するヒント文字列
	private final String mHitText;
	// ポップアップウインドウで選択された項目を受取るためのコールバックリスナー
	private OnPopupSelectionListener mOnPopupSelectionListener;

	/**===========================================================================================================
	 * コールバックリスナーインターフェース定義
	 *===========================================================================================================*/
	public interface OnPopupSelectionListener {
		// 選択された位置とメニューIDを呼び出し側に返却する
		void onSelection(int positon, long id);
	}

	/**===========================================================================================================
	 * コールバックリスナーセッター
	 *===========================================================================================================*/
	public void setOnPopupSelectionListener(OnPopupSelectionListener listener) {
		mOnPopupSelectionListener = listener;
	}

	/**===========================================================================================================
	 * ポップアップメニュー項目クラス定義
	 *===========================================================================================================*/
	public static class MenuItem {
		// 選択されたメニュー項目を特定するID
		public int id;
		// 選択されたメニュー項目を特定するアイコン
		public int icon;
		// 選択されたメニュー項目を特定するタイトル
		public String title;
	}

	/**===========================================================================================================
	 * コンストラクター メニュー表示用
	 * @param context 呼び出すアクティビィティのコンテキスト
	 * @param parent 画面のルートビュー
	 * @param dipWidth ウインドーの幅 (単位)dip
	 * @param menuItems メニュー項目リスト
	 * @param hintText ヒント文字列 (null ならヒント文字列を表示しない)
	 * @param flg	0:MenuAdapter 1:BluetoothAdapter
	 *===========================================================================================================*/
	public OekakiPopupMenu(Context context, View parent,int dipWidth, List<MenuItem> menuItems, String hintText, int flg) {
		super(parent);
		mParent = parent;
		mContext = parent.getContext();
		mInflater = LayoutInflater.from(mContext);
		mHitText = hintText;
		menuAdapter = new MenuAdapter(menuItems, mInflater);
		bluetoothAdapter = new BluetoothMenuAdapter(menuItems, mInflater);
		setupView(dipWidth, flg);
	}

	/**===========================================================================================================
	 * 呼び出し側の画面に表示する。
	 * @param gravity 表示方向
	 * @param x ウインドウのX軸の開始位置オフセット
	 * @param y ウインドウのY軸の開始位置オフセット
	 *===========================================================================================================*/
	public void showAtLocation(int gravity, int x, int y) {
		// PopupWindowクラスのメソッドを呼び出す
		super.showAtLocation(mParent, gravity, x, y);
	}

	/**===========================================================================================================
	 * ポップアップウインドウに表示するビューの初期化
	 * @param dipWidth 画面幅
	 * @param flg	0:MenuAdapter 1:BluetoothAdapter
	 *===========================================================================================================*/
	private void setupView(int dipWidth, int flg) {
		// アニメーション: スローフェードイン・ファーストフェードアウト
		setAnimationStyle(R.style.AnimationFade2d);
		// ポップアップウインドウのルートビューをリソースから動的に生成
		final View popUpContainer = mInflater.inflate(R.layout.popup_list, null);
		// 生成したルートビューをこのクラスにセット
		setContentView(popUpContainer);
		// ウインドウ幅の計算 ※ Dip サイズを Pixel単位に変換
		DisplayMetrics result = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(result);
		int widthPixel = (int) Math.round(result.density * dipWidth);
	    // スーパークラスのメソッドに設定
		setWidth(widthPixel);
		// ポップアップウインドウの高さを、表示するビューの高さにあわせる
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		// ヒント文字列
		if (!TextUtils.isEmpty(mHitText)) {
			// ヒント文字列が有効なら、メニューリストの上段に表示
			final TextView hintView = (TextView) popUpContainer.findViewById(R.id.hintText);
			hintView.setVisibility(View.VISIBLE);
			hintView.setText(mHitText);
			// フォーカスを当てることによりマーキー機能が働きます
			hintView.requestFocus();
			// キーリスナーをテキストビューに設定
			hintView.setOnKeyListener(mKeyListener);
		}
		// メニューを表示するリストビューを生成
		final ListView listView = (ListView) popUpContainer.findViewById(R.id.list_menu);
		// リストアダプターをリストに設定
		if(flg == 0){
			listView.setAdapter(menuAdapter);
		}else if(flg == 1){
			listView.setAdapter(bluetoothAdapter);
		}
		// 項目クリックリスナー設定
		listView.setOnItemClickListener(new  OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// リスナーに、選択された位置と項目IDを通知する
				if (mOnPopupSelectionListener != null) {
					// この通知より、呼び出したアクティビィティ側で項目IDに応じたアクションを実行できる。
					mOnPopupSelectionListener.onSelection(position, id);
				}
				// 選択されたら自分自身を閉じる
				dismiss();
			}
		});
		listView.setOnKeyListener(mKeyListener);
		// ポップアップウインドウのフォーカス設定 ※これを設定しないとリストビューを選択できない
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(false);
	}

	/**===========================================================================================================
	 * バックキーとホームキーをトラップするキーリスナー
	 * @param dipWidth 画面幅
	 *===========================================================================================================*/
	protected View.OnKeyListener mKeyListener = new View.OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (	event.getKeyCode() == KeyEvent.KEYCODE_BACK
				||  event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
				// 自分自身を閉じる
				dismiss();
				return true;
			}
			return false;
		}
	};
}
