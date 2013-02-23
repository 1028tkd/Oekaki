package jp.co.oekaki.image;

import java.io.IOException;
import java.io.InputStream;

import jp.co.oekaki.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**===========================================================================================================
 * 画像を管理するクラス
 * @author 		sugahara
 * @since		2012.11.10
 *===========================================================================================================*/
public class ImageManagement {

	// 画面サイズ
	private DisplayMetrics dispMet = new DisplayMetrics();
	// 鉛筆
	private final Bitmap bmpPencilBk;
	// 消しゴム
	private final Bitmap bmpEraser;
	// 設定
	private final Bitmap bmpSetting;
	// カメラ・ギャラリーより取得した画像
	private Bitmap bmpCameraOrGallery;

	public ImageManagement(Context context){
	    /**-------------------------------------------------------------------------------------------------------
		 * 画面サイズを取得後、レイアウトを設定する
	     * -------------------------------------------------------------------------------------------------------*/
	    WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    display.getMetrics(dispMet);

	    /**-------------------------------------------------------------------------------------------------------
		 *  鉛筆
	     * -------------------------------------------------------------------------------------------------------*/
	    Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.enpitu_black_menu);
	    // 最終的なサイズにするための縮小率を求める
	    // 画像変形用のオブジェクトに拡大・縮小率をセットし
		bmpPencilBk = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), setScaleImage(bmp.getWidth(), bmp.getHeight(),2,0.1), true);
        /**-------------------------------------------------------------------------------------------------------
         * 消しゴムの設定
         * -------------------------------------------------------------------------------------------------------*/
		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.kesigomu);
		bmpEraser = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), setScaleImage(bmp.getWidth(), bmp.getHeight(),2,0.1), true);
        /**-------------------------------------------------------------------------------------------------------
         * 設定の設定
         * -------------------------------------------------------------------------------------------------------*/
		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.setting);
		bmpSetting = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), setScaleImage(bmp.getWidth(), bmp.getHeight(),2,0.1), true);
    }

	/**===========================================================================================================
	 * 画像の縮小・拡大率を設定する
	 * @param		w		画像の幅
	 * @param		h		画像の高さ
	 * @return		縮小・拡大率(Matrix)
     * ===========================================================================================================*/
	public Matrix setScaleImage(int w, int h, float divisionW, double divisionH){
		Matrix matrix = new Matrix();
		float scale = Math.min((float)dispMet.widthPixels/divisionW/w, (float)(dispMet.heightPixels * divisionH)/h);
		matrix.postScale(scale, scale);
		return matrix;
	}

	/**===========================================================================================================
	 * カメラ・ギャラリーより取得した画像の縮小・拡大率を設定する
	 * @param		w		画像の幅
	 * @param		h		画像の高さ
	 * @return		縮小・拡大率(Matrix)
     * ===========================================================================================================*/
	public Matrix setScaleCameraOrGalleryImage(int w, int h){
		Matrix matrix = new Matrix();
		float scalex = (float)dispMet.widthPixels / w;
		float scaley = (float)dispMet.heightPixels / h;
		float scale = Math.min((float)dispMet.widthPixels / w, (float)dispMet.heightPixels / h);
		// TODO
		//カメラによって解像度・幅が違うので検討が必要！！
		matrix.postScale(scale, scale);
		Log.v("setScaleCameraOrGalleryImage","scale[" + scalex + "][" + scaley + "]");
		return matrix;
	}

	/**===========================================================================================================
	 * 引数でしていされたファイルパスよりBitmapを作成する
	 *
	 * @param	filePath	ファイルパス
     * ===========================================================================================================*/
	public Bitmap createBitmap(String filePath){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inSampleSize=4;
		return BitmapFactory.decodeFile(filePath,opt);
	}

	/**===========================================================================================================
     * カメラ・ギャラリーより取得した画像の設定
	 *
	 * @param	context		Context
	 * @param  uri			Uri
	 * @exception	IOException
     * ===========================================================================================================
	 * @throws IOException */
	public Bitmap createCameraOrGallryBitmap(Context context, Uri uri) throws IOException{
		InputStream is = null;
		Bitmap bitmap = null;
		try{
			is = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is);
			Log.v("setScaleCameraOrGalleryImage","幅1[" + bitmap.getWidth() + "]高さ1[" + bitmap.getHeight() + "]");
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), setScaleCameraOrGalleryImage(bitmap.getWidth(),bitmap.getHeight()), true);
			Log.v("setScaleCameraOrGalleryImage","幅[" + bitmap.getWidth() + "]高さ[" + bitmap.getHeight() + "]");
			return bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(is != null) is.close();
			if(bitmap != null) bitmap.recycle();
		}
	}

	public Bitmap getBmpPencilBk() {
		return bmpPencilBk;
	}

	public Bitmap getBmpEraser() {
		return bmpEraser;
	}

	public Bitmap getBmpSetting() {
		return bmpSetting;
	}

	public Bitmap getBmpCameraOrGallery() {
		return bmpCameraOrGallery;
	}
}
