package jp.co.oekaki.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class AndroidDisplayUtil {

	private AndroidDisplayUtil() {
	}

	/**===========================================================================================================
	 * ディスプレイ幅を取得する
	 * @param		context 	Context
	 * @return		ディスプレイ幅(ピクセル)
	 *===========================================================================================================*/
	public static int getWidth(Context context){
		return getDisplayMetrics(context).widthPixels;
	}

	/**===========================================================================================================
	 * ディスプレイ高さを取得する
	 * @param		context 	Context
	 * @return		ディスプレイ高さ(ピクセル)
	 *===========================================================================================================*/
	public static int getHeight(Context context){
		return getDisplayMetrics(context).heightPixels;
	}

	/**===========================================================================================================
	 * ディスプレイ情報を管理するクラス
	 * @param		context 	Context
	 * @return		DisplayMetrics
	 *===========================================================================================================*/
	public static final DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics result = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(result);
		return result;
	}

	/**===========================================================================================================
	 * 画面密度からスケール(pxの倍率)を取得する
	 * @param		context 	Context
	 * @return		スケール(pxの倍率)
	 *===========================================================================================================*/
	public synchronized static float getScaledDensity(Context context) {
		DisplayMetrics result = getDisplayMetrics(context);
		// A scaling factor for fonts displayed on the display.
		return result.scaledDensity;
	}

	/**===========================================================================================================
	 * 画面密度からDensityを取得する
	 * @param		context 	Context
	 * @return		Density(pxの倍率)
	 *===========================================================================================================*/
	public static final float dipToPixcelFloat(Context context, float dip) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		return metrics.density * dip;
	}

	/**===========================================================================================================
	 * 画面密度からスケール(pxの倍率)を取得する
	 * @param		context 	Context
	 * @return		スケール(pxの倍率)
	 *===========================================================================================================*/
	public static final int dipToPixel(Context context, int dip) {
		return (int) Math.round(dipToPixcelFloat(context, (float) dip));
	}
}

