package jp.co.oekaki;

public class AppConst {

	/**===========================================================================================================
	 * メニュー用
	 *===========================================================================================================*/
	// 直線
	public static final int MENUID_STRAIGHT = 1;
	// 曲線
	public static final int MENUID_CURVE = 2;
	// 黒
	public static final int MENUID_BLACK = 1;
	// 青
	public static final int MENUID_BLUE = 2;
	// 黄
	public static final int MENUID_YELLOW = 3;
	// 赤
	public static final int MENUID_RED = 4;
	// 紫
	public static final int MENUID_PINK = 5;
	// 白
	public static final int MENUID_WHITE = 6;
	// 緑
	public static final int MENUID_GREEN = 7;
	// グレー
	public static final int MENUID_GRAY = 8;
	// マジェンタ
	public static final int MENUID_MAJENT = 9;
	// 太い
	public static final int MENUID_FAT = 1;
	// やや太い
	public static final int MENUID_LITTLEFAT = 2;
	// 普通
	public static final int MENUID_PUBLIC = 3;
	// やや細い
	public static final int MENUID_LITTLETHIN = 4;
	// 細い
	public static final int MENUID_THIN = 5;
	// 色
	public static final int MENUID_COLOR = 1;
	// 太さ
	public static final int MENUID_STROKE = 2;
	// 種類
	public static final int MENUID_KIND = 3;
	// 閉じる
	public static final int MENUID_CLOSE = 4;
	// 表示幅
	public static final int POPUPMENU_WIDTH = 200;//dip
	// 表示位置X
	public static final int POPUPMENU_X = 10; //dip
	// 表示位置Y
	public static final int POPUPMENU_Y = 10; //dip

	// 保存
	public static final int MENUID_SAVE = 1;
	// 削除
	public static final int MENUID_DELETE = 2;
	// 初期化
	public static final int MENUID_INITIALIZION = 3;
	// ファイルオープン
	public static final int MENUID_OPENFILE = 4;
	// カメラ
	public static final int MENUID_CAMERA = 5;
	// ギャラリーから取得
	public static final int MENUID_GALLERY = 6;
	// メール
	public static final int MENUID_MAIL = 7;
	// 2人でお絵描き(Bluetooth通信)を実行
	public static final int MENUID_BLUETOOTH = 8;
	// ヘルプ
//	public static final int MENUID_HELP = 9;
	// 絵を友達にあげる
	public static final int MENUID_SEND_PAINT_BLUETOOTH = 9;
	// 閉じる
	public static final int MENUID_CLOSE_SETTING = 10;
	// メニュー画面幅
	public static final int SETTING_POPUPMENU_WIDTH = 400;//dip
	// メニュー表示位置X
	public static final int SETTING_POPUPMENU_X = 10; //dip
	// メニュー表示位置Y
	public static final int SETTING_POPUPMENU_Y = 10; //dip

	/**===========================================================================================================
	 * Paintに関する情報
	 *===========================================================================================================*/
	// 線の太さ（太い）
	public static final int STROKE_MAX = 18;
	// 線の太さ（やや太い）
	public static final int STROKE_LITTLE_FAT = 12;
	// 線の太さ（普通）
	public static final int STROKE_PUBLIC = 8;
	// 線の太さ（やや細い）
	public static final int STROKE_LITTLE_THIN = 5;
	// 線の太さ（細い）
	public static final int STROKE_THIN = 2;
	// カメラ起動
	public static final int REQUEST_CAPTURE_IMAGE = 100;
	// ギャラリーからデータ取得
	public static final int REQUEST_GALLERY = 101;
	// メール
	public static final int REQUEST_MAIL = 102;
	// Bluetooth使用可否
	public static final int REQUEST_ENABLE_BLUETOOTH = 103;
	// Bluetooth発見機能可否
	public static final int REQUEST_ENABLE_BLUETOOTH_DISCOVERABLE = 104;
}
