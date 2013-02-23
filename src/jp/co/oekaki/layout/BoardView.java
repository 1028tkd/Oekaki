package jp.co.oekaki.layout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.image.ImageManagement;
import jp.co.oekaki.util.AndroidDisplayUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**===========================================================================================================
 * 描画に関するクラス
 *
 * @author sugahara satoshi
 * @serial 1.0
 * @create 2013-01-31
 *===========================================================================================================*/
@SuppressLint({ "ViewConstructor", "NewApi", "WorldReadableFiles" })
public class BoardView extends View {

	// 座標値(X)
    private float oldX = 0f;
    // 座標値(Y)
    private float oldY = 0f;
	private Context context = null;
	private Bitmap bitmap = null;
	private Canvas canvas = null;
    // 自分用Paint情報
    private Paint paint;
	// 通信相手のPaint情報
	private Paint partnerPaint = null;
	// 幅
	private int width;
	// 高さ
	private int height;
	private int iDrawKind = 1;
	// Bluetooth通信にて取得したパスを一時的に格納する
	private List<String[]> tempBtReceveList = new ArrayList<String[]>();
	// Bluetooth通信の最後のパスを設定する
	private List<String[]> btReceveList = new ArrayList<String[]>();
	// Modeフラグ(0:線1:消しゴム)
	private int iMode;
    // 描画色
	private int drawColor = Color.BLACK;
	// 描画太さ
	private int drawStrokeWidth = 5;

	public Bitmap getBitmap(){
		return this.bitmap;
	}

	public void setBitmap(Bitmap b){
        canvas = new Canvas(b);
        invalidate();
        Log.v("描画","終了");
	}

	public int getDrawColor() {
		return drawColor;
	}

	public void setDrawColor(int drawColor) {
		this.drawColor = drawColor;
        paint.setColor(drawColor);
		paint.setStrokeWidth(drawStrokeWidth);
	}

	public int getDrawStrokeWidth() {
		return drawStrokeWidth;
	}

	public void setDrawStrokeWidth(int drawStrokeWidth) {
		this.drawStrokeWidth = drawStrokeWidth;
		paint.setStrokeWidth(drawStrokeWidth);
        paint.setColor(drawColor);
	}

	/**===========================================================================================================
	 * 画面に描画するPaintクラスの設定を行う
	 * @param  iDrawKind		0:直線 1:曲線
	 *===========================================================================================================*/
    public void setIDrawKind(int iDrawKind) {
		this.iDrawKind = iDrawKind;
	}

	/**===========================================================================================================
	 * 初期描画に必要な情報を設定する
	 * @param		context		Context
	 * @param		width		画面幅
	 * @param		height		画面高さ
	 *===========================================================================================================*/
    public BoardView(Context context, int width, int height) {
        super(context);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(drawColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(drawStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.width = width;
        this.height = height;
    }

	/**===========================================================================================================
	 * 画面に描画するPaintクラスの設定を行う
	 * @param  flg		0:線 1:消しゴム
	 *===========================================================================================================*/
    public void setPaint(int flg){
    	this.iMode = flg;
    	if(flg == 0){
    		paint.setXfermode(new PorterDuffXfermode(Mode.ADD));
            paint.setStrokeWidth(drawStrokeWidth);
    	}else{
    		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    		paint.setStrokeWidth(drawStrokeWidth*2);
    	}
    }
	/**===========================================================================================================
	 * 起動時に呼ばれ、キャンパスを設定する
	 * @param		w		画面幅
	 * @param		h		画面高さ
	 * @param		oldw	以前の画面幅
	 * @param		oldh	以前の画面高さ
	 *===========================================================================================================*/
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    	canvas.drawColor(Color.argb(0, 250, 250, 250));
    }

    /**===========================================================================================================
	 * タッチイベント
	 * -----------------------------------------------------------------------------------------------------------
	 * タッチからタッチアップまでの線を描画する
	 * @param	e	MotionEvent
	 *===========================================================================================================*/
    public boolean onTouchEvent(MotionEvent e) {
    	//Path情報
		String path;
    	// 0:場合/直線 1:曲線
    	if(iDrawKind == 0){
            switch (e.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            oldX = e.getX();
		            oldY = e.getY();
		            invalidate();
		            path = "PAINT:" + paint.getStrokeWidth() + "," + paint.getColor() + "," + iDrawKind + "," + iMode;
		            path += "/" + oldX + "," + oldY;
		            if(((OekakiApplication) context.getApplicationContext()).getClientThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getClientThread().sendMessage(path.getBytes());
		            }
		            if(((OekakiApplication) context.getApplicationContext()).getServerThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getServerThread().sendMessage(path.getBytes());
		            }
		            break;
				case MotionEvent.ACTION_UP: //最後のポイント
		            canvas.drawLine(oldX, oldY, e.getX(), e.getY(), paint);
					invalidate();
		            path = oldX + "," + oldY + ",END";
		            if(((OekakiApplication) context.getApplicationContext()).getClientThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getClientThread().sendMessage(path.getBytes());
		            }
		            if(((OekakiApplication) context.getApplicationContext()).getServerThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getServerThread().sendMessage(path.getBytes());
		            }
					break;
		        default:
		            break;
	        }
    	}else{
            switch (e.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            oldX = e.getX();
		            oldY = e.getY();
		            // ペイント情報を設定する(太さ,色,線の種類,線(=0)or消しゴム(=1))
		            path = "PAINT:" + paint.getStrokeWidth() + "," + paint.getColor() + "," + iDrawKind + "," + iMode;
		            path += "/" + oldX + "," + oldY;
		            if(((OekakiApplication) context.getApplicationContext()).getClientThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getClientThread().sendMessage(path.getBytes());
		            }
		            if(((OekakiApplication) context.getApplicationContext()).getServerThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getServerThread().sendMessage(path.getBytes());
		            }
		            break;
		        case MotionEvent.ACTION_MOVE:
		            canvas.drawLine(oldX, oldY, e.getX(), e.getY(), paint);
					invalidate();
		            oldX = e.getX();
		            oldY = e.getY();
		            path = oldX + "," + oldY;
		            if(((OekakiApplication) context.getApplicationContext()).getClientThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getClientThread().sendMessage(path.getBytes());
		            }
		            if(((OekakiApplication) context.getApplicationContext()).getServerThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getServerThread().sendMessage(path.getBytes());
		            }
		            break;
				case MotionEvent.ACTION_UP: //最後のポイント
		            canvas.drawLine(oldX, oldY, e.getX(), e.getY(), paint);
					invalidate();
		            path = oldX + "," + oldY + ",END";
		            if(((OekakiApplication) context.getApplicationContext()).getClientThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getClientThread().sendMessage(path.getBytes());
		            }
		            if(((OekakiApplication) context.getApplicationContext()).getServerThread() != null){
			            ((OekakiApplication) context.getApplicationContext()).getServerThread().sendMessage(path.getBytes());
		            }
					break;
		        default:
		            break;
	        }
    	}
        return true;
    }

	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

	/**===========================================================================================================
     * 画面の初期化
	 *===========================================================================================================*/
    public void clearDrawList(){
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    	canvas.drawColor(Color.argb(0, 250, 250, 250));
	    invalidate();
    }

	/**===========================================================================================================
     * 画面をギャラリーへ保存する
     * @param  fileName	: ファイル名
     * @return true:保存成功 / false:保存失敗
	 *===========================================================================================================*/
    public boolean saveToFile(String fileName){
    	try{
    		//ローカルファイルへ保存
			FileOutputStream out = context.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
			bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
			out.flush();
			out.close();
			return true;
    	}catch(IOException e){
			e.printStackTrace();
			return false;
		}
    }

	/**===========================================================================================================
	 * 画面を読み込む
	 * @param  fileName	: ファイル名
	 * @return true:保存成功 / false:保存失敗
	 *===========================================================================================================*/
	public boolean readToFile(String fileName){
		InputStream in;
		try {
			in = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	        canvas = new Canvas(bitmap);
			invalidate();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**===========================================================================================================
	 * カメラ・ギャラリーから取得した画像をリサイズ後、画面に表示する
	 * @param  activity		Activity
	 * @param  uri			Uri
	 * @return true:成功 / false:失敗
	 *===========================================================================================================*/
	public boolean readFromUri(Uri uri){
		try{
			ImageManagement imgMng = ((OekakiApplication)context.getApplicationContext()).getImgInstance();
			Bitmap bitmapImage = imgMng.createCameraOrGallryBitmap(context,uri);
	        canvas = new Canvas(bitmap);
	        // 取得した画像を中央に表示する
	        int x = 0;
	        if(bitmapImage.getWidth() < AndroidDisplayUtil.getWidth(context)){
		        int width = AndroidDisplayUtil.getWidth(context);
		        int imgWidth = bitmapImage.getWidth();
		        x = width - (imgWidth/2 + width/2);
		        Log.v("幅","x[" + x + "]imgWidth/2[" + imgWidth/2 + "]width/2[" + width/2 + "]");
	        }
	        canvas.drawBitmap(bitmapImage, x, 0, null);
			invalidate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    /**===========================================================================================================
	 * Bluetooth通信時の描画処理
	 * -----------------------------------------------------------------------------------------------------------
	 * タッチからタッチアップまでの線を描画する
	 * @param	result	0:X座標位置 1:Y座標位置 2:END
	 *===========================================================================================================*/
	public void drawCanvas(String result){
        try{
			// 最初の通信時Paint情報を取得する
			if(result.indexOf("PAINT:") >= 0){
		        String[] tempPaint = result.substring(result.indexOf(":")+1, result.indexOf("/")).split(",");
		        // Paint情報の生成
		        partnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        partnerPaint.setAntiAlias(true);
		        partnerPaint.setColor((int)Float.parseFloat(tempPaint[1]));
		        partnerPaint.setStyle(Paint.Style.STROKE);
		        partnerPaint.setStrokeCap(Paint.Cap.ROUND);
		        partnerPaint.setStrokeJoin(Paint.Join.ROUND);
		        if(tempPaint[3].equals("0")){
		        	partnerPaint.setXfermode(new PorterDuffXfermode(Mode.ADD));
		        	partnerPaint.setStrokeWidth(Float.parseFloat(tempPaint[0]));
		    	}else{
		    		partnerPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		    		partnerPaint.setStrokeWidth(Float.parseFloat(tempPaint[0])*2);
		    	}

		        Log.v("Paint情報","result[" + result + "]");
		        Log.v("Paint情報","太さ[" + tempPaint[0] + "]色[" + tempPaint[1] + "]種類[" + tempPaint[2] + "]Mode[" + tempPaint[3] + "]");

		        // Path情報を追加する
				tempBtReceveList.add(result.substring(result.indexOf("/")+1).split(","));
			}else{
		        // Path情報を追加する
				tempBtReceveList.add(result.split(","));
			}
			String[] value;
			// 一番最初に座標位置(X,Y)を設定する
			if(btReceveList.size() == 0 && tempBtReceveList.size() == 1){
				value =  tempBtReceveList.get(0);
				oldX = Float.parseFloat(value[0]);
				oldY = Float.parseFloat(value[1]);
			}
			// ENDが送信されてから描画を行う
			if(result.indexOf("END") >= 0){
				// PATHリスト分描画処理を行う
				for(int i = 0; i < tempBtReceveList.size(); i++){
					value = tempBtReceveList.get(i);
					if(i == 0){
						 canvas.drawLine(oldX, oldY, Float.parseFloat(value[0]), Float.parseFloat(value[1]), partnerPaint);
					}else{
						// Path情報一時保持用
						String[] temp = tempBtReceveList.get((i-1));
						// 次の座標位置(X,Y)を設定する
						oldX = Float.parseFloat(temp[0]);
						oldY = Float.parseFloat(temp[1]);
						canvas.drawLine(oldX, oldY, Float.parseFloat(value[0]), Float.parseFloat(value[1]), partnerPaint);
					}
					invalidate();
				}
				// PATHリストを初期化する
				tempBtReceveList.clear();
			}
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
}