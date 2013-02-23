package jp.co.oekaki.menu;

import android.content.Context;

/**===========================================================================================================
 * ポップアップウインドウアダプタクラス
 *
 * @author sugahara satoshi
 * @serial 1.0
 * @create 2013-01-31
 *===========================================================================================================*/
public class MenuFacade {

	private SettingMenu sMenu;
	private PenMenu pMenu;

	public MenuFacade(Context context){
		sMenu = new SettingMenu(context);
		pMenu = new PenMenu(context);
	}

	public SettingMenu getSettingMenu(){
		return this.sMenu;
	}

	/**===========================================================================================================
	 * ポップアップウィンドウ表示処理
	 * @param		0:設定メニュー 1:鉛筆メニュー
	 *===========================================================================================================*/
	public void openPopUpMenu(int flg){
		if(flg == 0){
			// 設定メニューを表示する
			sMenu.openPopupMenu();
		}else if(flg == 1){
			// 鉛筆ボタンメニューを表示する
			pMenu.openPopupMenu();
		}else{

		}
	}

	/**===========================================================================================================
	 * ポップアップウィンドウを閉じる
	 * @param		0:設定メニュー 1:鉛筆メニュー
	 *===========================================================================================================*/
	public void closePopUpMenu(){
		if(sMenu != null)
			sMenu.closePopupMenu();
		if(pMenu != null)
			pMenu.closePopupMenu();
	}
}
