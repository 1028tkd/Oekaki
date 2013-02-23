package jp.co.oekaki.menu;

import java.util.ArrayList;
import java.util.List;

import jp.co.oekaki.AppConst;
import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.R;
import jp.co.oekaki.menu.OekakiPopupMenu.OnPopupSelectionListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

public class PenMenu {

	// Context
	private Context context;
	// onPauseメソッドで確実に閉じるための参照
	private OekakiPopupMenu mPopupMenu;
	// アプリケーションインスタンス
	private OekakiApplication mTboardApp;
	// このアクティビィティのルートビュー
	private View mRootContainer;

	public PenMenu(Context context){
		this.context = context;
		mTboardApp = (OekakiApplication)context.getApplicationContext();
		mRootContainer = (View)((Activity)context).findViewById(R.id.rootContainer);
	}

	/**===========================================================================================================
	 * 状況に応じてメニューを動的に生成する。
	 * 連絡先のメール, メールアプリ, 検索履歴を開く, 検索履歴削除, 住所編集, 使い方
	 *
	 * @param		value	タイトル
	 * @param		flg		0:初期設定 1:色 2:太さ 3:種類
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	private List<OekakiPopupMenu.MenuItem> createMenuItems(String[] value, int flg) {
		List<OekakiPopupMenu.MenuItem> result = new ArrayList<OekakiPopupMenu.MenuItem>();
		OekakiPopupMenu.MenuItem item = null;
		for(int i=0; i < value.length; i++){
			item = new OekakiPopupMenu.MenuItem();
			item.id = i+1;
			item.icon = OekakiApplication.POPUPMENU_ICONS[2];
			if(flg == 0 && item.id == AppConst.MENUID_CLOSE){
				item.icon = OekakiApplication.POPUPMENU_ICONS[9];
			}
			if(flg == 1){
				item.icon = OekakiApplication.POPUPMENU_ICONS_PENCOLOR[i];
			}
			item.title = value[i];
			result.add(item);
		}
		return result;
	}

	/**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー(線の種類)
	 *===========================================================================================================*/
	private OnPopupSelectionListener settingOnPopupSelectionListener = new OnPopupSelectionListener() {
		public void onSelection(int positon, long id) {
			int menuId = (int) id;
			closePopupMenu();
			switch(menuId) {
				case AppConst.MENUID_COLOR:
					// 初期設定
					mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.POPUPMENU_WIDTH,createMenuItems(mTboardApp.popupMenuPenColor, 1),null,0);
					// 選択項目コールバックリスナー
					mPopupMenu.setOnPopupSelectionListener(colorOnPopupSelectionListener);
					// 画面の右下寄せで表示する
					mPopupMenu.showAtLocation(Gravity.LEFT | Gravity.BOTTOM, mTboardApp.defaultPopupX(), mTboardApp.defaultPopupY());
					break;
				case AppConst.MENUID_STROKE:
					// 初期設定
					mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.POPUPMENU_WIDTH,createMenuItems(mTboardApp.popupMenuPenStroke, 2),null,0);
					// 選択項目コールバックリスナー
					mPopupMenu.setOnPopupSelectionListener(strokeOnPopupSelectionListener);
					// 画面の右下寄せで表示する
					mPopupMenu.showAtLocation(Gravity.LEFT | Gravity.BOTTOM, mTboardApp.defaultPopupX(), mTboardApp.defaultPopupY());
					break;
				case AppConst.MENUID_KIND:
					// 初期設定
					mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.POPUPMENU_WIDTH,createMenuItems(mTboardApp.popupMenuPenKind, 3),null,0);
					// 選択項目コールバックリスナー
					mPopupMenu.setOnPopupSelectionListener(kindOnPopupSelectionListener);
					// 画面の右下寄せで表示する
					mPopupMenu.showAtLocation(Gravity.LEFT | Gravity.BOTTOM, mTboardApp.defaultPopupX(), mTboardApp.defaultPopupY());
					break;
			}
		}
	};

	/**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー(線の種類)
	 *===========================================================================================================*/
	private OnPopupSelectionListener kindOnPopupSelectionListener = new OnPopupSelectionListener() {
		public void onSelection(int positon, long id) {
			int menuId = (int) id;
			switch(menuId) {
				case AppConst.MENUID_STRAIGHT:
					Log.v("onSelection","MENUID_STRAIGHT");
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setPaint(0);
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setIDrawKind(0);
					break;
				case AppConst.MENUID_CURVE:
					Log.v("onSelection","MENUID_CURVE");
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setPaint(0);
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setIDrawKind(1);
					break;
			}
			closePopupMenu();
		}
	};

	/**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー(線の太さ)
	 *===========================================================================================================*/
	private OnPopupSelectionListener strokeOnPopupSelectionListener = new OnPopupSelectionListener() {
		public void onSelection(int positon, long id) {
			int menuId = (int) id;
			switch(menuId) {
				case AppConst.MENUID_FAT:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawStrokeWidth(AppConst.STROKE_MAX);
					break;
				case AppConst.MENUID_LITTLEFAT:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawStrokeWidth(AppConst.STROKE_LITTLE_FAT);
					break;
				case AppConst.MENUID_PUBLIC:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawStrokeWidth(AppConst.STROKE_PUBLIC);
					break;
				case AppConst.MENUID_LITTLETHIN:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawStrokeWidth(AppConst.STROKE_LITTLE_THIN);
					break;
				case AppConst.MENUID_THIN:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawStrokeWidth(AppConst.STROKE_THIN);
					break;
			}
			closePopupMenu();
		}
	};

	/**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー(線の色)
	 *===========================================================================================================*/
	private OnPopupSelectionListener colorOnPopupSelectionListener = new OnPopupSelectionListener() {
		public void onSelection(int positon, long id) {
			int menuId = (int) id;
			switch(menuId) {
				case AppConst.MENUID_BLACK:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.BLACK);
					break;
				case AppConst.MENUID_BLUE:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.BLUE);
					break;
				case AppConst.MENUID_RED:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.RED);
					break;
				case AppConst.MENUID_YELLOW:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.YELLOW);
					break;
				case AppConst.MENUID_PINK:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.parseColor("#C85B98"));
					break;
				case AppConst.MENUID_GREEN:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.GREEN);
					break;
				case AppConst.MENUID_WHITE:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.WHITE);
					break;
				case AppConst.MENUID_GRAY:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.GRAY);
					break;
				case AppConst.MENUID_MAJENT:
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawColor(Color.MAGENTA);
					break;
			}
			closePopupMenu();
		}
	};

	/**===========================================================================================================
	 * ポップアップウインドウを開く。※動的にメニュー項目を変更可能
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	public void openPopupMenu() {
		closePopupMenu();
		// 初期設定
		mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.POPUPMENU_WIDTH,createMenuItems(mTboardApp.popupMenuPenSetting,0),null,0);
		// 選択項目コールバックリスナー
		mPopupMenu.setOnPopupSelectionListener(settingOnPopupSelectionListener);
		// 画面の右下寄せで表示する
		mPopupMenu.showAtLocation(Gravity.LEFT | Gravity.BOTTOM, mTboardApp.defaultPopupX(), mTboardApp.defaultPopupY());
	}

	/**===========================================================================================================
	 * ポップアップウインドウを閉じる
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	public void closePopupMenu(){
		if (mPopupMenu != null && mPopupMenu.isShowing()) {
			mPopupMenu.dismiss();
			mPopupMenu = null;
		}
	}
}
