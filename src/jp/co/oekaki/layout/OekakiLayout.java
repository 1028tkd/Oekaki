package jp.co.oekaki.layout;

import jp.co.oekaki.AppConst;
import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.R;
import jp.co.oekaki.image.ImageManagement;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public  class OekakiLayout extends LayoutTemplete implements OnClickListener, OnTouchListener {

	// Context
	private Context context;
	// 画面サイズ
	private static final DisplayMetrics dispMet = new DisplayMetrics();
	// 描画領域
	private static BoardView boardView;
	// Viewの左辺座標：X軸
	private int currentX;
	// Viewの上辺座標：Y軸
	private int currentY;
	// 画面タッチ位置の座標：X軸
	private int offsetX;
	// 画面タッチ位置の座標：Y軸
	private int offsetY;
	// 画像読み込むファイルパス
	private String readFilePath;

	public OekakiLayout(){
	}

	public OekakiLayout(Context context){
		/**===========================================================================================================
		 * 画面サイズを取得後、レイアウトを設定する
	     * ===========================================================================================================*/
	    WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    display.getMetrics(dispMet);
		boardView = new BoardView(context, this.getWidthPixels(), this.getHeightPixels());
		this.context = context;
	}

	/**===========================================================================================================
	 * 画面の幅を取得する
	 *===========================================================================================================*/
	protected int getWidthPixels(){
		return dispMet.widthPixels;
	}

	/**===========================================================================================================
	 * 画面の高さを取得する
	 *===========================================================================================================*/
	protected int getHeightPixels(){
		return dispMet.heightPixels;
	}

	/**===========================================================================================================
	 * 描画領域を取得する
	 *===========================================================================================================*/
	public BoardView getBoardView(){
		return boardView;
	}

	/**===========================================================================================================
	 * 画面に表示するレイアウトを作成する
	 *===========================================================================================================*/
	public void createLaytout(){
		Log.v("createLaytout","開始");
		readFilePath = null;
		ImageManagement imgMng = ((OekakiApplication)context.getApplicationContext()).getImgInstance();

    	/**-------------------------------------------------------------------------------------------------------
		 *  レイアウトに描画領域を設定する
         * -------------------------------------------------------------------------------------------------------*/
		LinearLayout fLayoutTs = (LinearLayout)((Activity)context).findViewById(R.id.paintLayout);
		FrameLayout.LayoutParams fParam = new FrameLayout.LayoutParams(getWidthPixels(), getHeightPixels());
		fLayoutTs.setLayoutParams(fParam);
		fLayoutTs.removeView(getBoardView());
		fLayoutTs.addView(getBoardView());

        /**-------------------------------------------------------------------------------------------------------
         * 設定の設定
         * -------------------------------------------------------------------------------------------------------*/
		ImageView imgBtnSetting = (ImageView)((Activity)context).findViewById(R.id.imgBtnSetting);
        imgBtnSetting.setImageDrawable(new BitmapDrawable(context.getResources(), imgMng.getBmpSetting()));
        imgBtnSetting.setOnClickListener(this);

        /**-------------------------------------------------------------------------------------------------------
		 *  鉛筆
	     * -------------------------------------------------------------------------------------------------------*/
		ImageView imgBtnBkPen = (ImageView)((Activity)context).findViewById(R.id.imgBtnPencileBk);
		imgBtnBkPen.setImageDrawable(new BitmapDrawable(context.getResources(), imgMng.getBmpPencilBk()));
		imgBtnBkPen.setOnClickListener(this);

        /**-------------------------------------------------------------------------------------------------------
         * 消しゴムの設定
         * -------------------------------------------------------------------------------------------------------*/
        ImageView imgBtnEraser = (ImageView)((Activity)context).findViewById(R.id.imgBtnEraser);
        imgBtnEraser.setImageDrawable(new BitmapDrawable(context.getResources(), imgMng.getBmpEraser()));
        imgBtnEraser.setOnClickListener(this);

        /**-------------------------------------------------------------------------------------------------------
         * ギャラリーの設定（初期表示は非表示)
         * -------------------------------------------------------------------------------------------------------*/
        HorizontalScrollView hsView = (HorizontalScrollView)((Activity)context).findViewById(R.id.galleryHorizontalScrollView);
        hsView.setVisibility(View.INVISIBLE);
	}

	/**===========================================================================================================
	 * 画面に表示するレイアウトを作成する
	 * @para	fileList	端末に保存しているファイルリスト
	 *===========================================================================================================*/
	@SuppressLint("NewApi")
	public void createImageViewScrollLaytout(String[] fileList){
		if(fileList == null) return;
        /**-------------------------------------------------------------------------------------------------------
         * ギャラリーの設定
         * -------------------------------------------------------------------------------------------------------*/
        HorizontalScrollView hsView = (HorizontalScrollView)((Activity)context).findViewById(R.id.galleryHorizontalScrollView);
        hsView.setVisibility(View.VISIBLE);
		LinearLayout galleryLinearLayout = (LinearLayout)((Activity)context).findViewById(R.id.galleryLrLayout);
		FrameLayout.LayoutParams fParam = new FrameLayout.LayoutParams((int)(getWidthPixels()*0.75), (int)(getHeightPixels()*0.72));
		LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams((int)(getWidthPixels()*0.7), (int)(getHeightPixels()*0.7));
		// 一度子Viewをすべて
		galleryLinearLayout.removeAllViews();
		galleryLinearLayout.setLayoutParams(fParam);
		galleryLinearLayout.setBackgroundColor(Color.rgb(140, 140, 140));

        /**-------------------------------------------------------------------------------------------------------
         * 取得したファイルリストを元にImageViewに設定する
         * -------------------------------------------------------------------------------------------------------*/
		ImageManagement imgMng = ((OekakiApplication)context.getApplicationContext()).getImgInstance();
		for(String filePath:fileList){
			// Bitmapの作成
			Bitmap bitmap = imgMng.createBitmap(context.getApplicationInfo().dataDir + "/files/" + filePath);
			ImageView imgView = new ImageView(context);
			// 幅・高さの設定
			imgView.setLayoutParams(lParam);
			// Bitmapを設定する
			// ファイル名を設定する
			imgView.setTag(filePath);
			imgView.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
			imgView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Log.v("FileOpen","v.getTag()[" + v.getTag() + "]");
					// ファイル名を取得する
					readFilePath = (String)v.getTag();
					getBoardView().readToFile(readFilePath);
			        HorizontalScrollView hsView = (HorizontalScrollView)((Activity)context).findViewById(R.id.galleryHorizontalScrollView);
					hsView.setVisibility(View.INVISIBLE);
				}
			});
			galleryLinearLayout.addView(imgView);
		}
	}

	/**===========================================================================================================
	 * 各ボタン押下処理
	 * @param	v	View
	 *===========================================================================================================*/
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.imgBtnSetting:
				/**-------------------------------------------------------------------------------------------------------
				 *設定メニューを設定する
				 * -------------------------------------------------------------------------------------------------------*/
				((OekakiApplication)context.getApplicationContext()).getMenu().openPopUpMenu(0);
				break;
			case R.id.imgBtnPencileBk:
				/**-------------------------------------------------------------------------------------------------------
				 *鉛筆用メニューを設定する
				 * -------------------------------------------------------------------------------------------------------*/
				getBoardView().setPaint(0);
				((OekakiApplication)context.getApplicationContext()).getMenu().openPopUpMenu(1);
				break;
			case R.id.imgBtnEraser:
				getBoardView().setPaint(1);
				getBoardView().setIDrawKind(1);
				getBoardView().setDrawStrokeWidth(AppConst.STROKE_MAX);
				break;
			default:
				break;
		}
	}

	/**===========================================================================================================
	 * 各ボタンタッチ処理
	 * @param	v	View
	 *===========================================================================================================*/
    public boolean onTouch(View view, MotionEvent event) {

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch(event.getAction()) {
	        case MotionEvent.ACTION_MOVE:
	            int diffX = offsetX - x;
	            int diffY = offsetY - y;

	            currentX -= diffX;
	            currentY -= diffY;
	            //画像の移動
	            view.layout(currentX, currentY, currentX + view.getWidth(),currentY + view.getHeight());
	            offsetX = x;
	            offsetY = y;
	            break;

	        case MotionEvent.ACTION_DOWN:
	            //x,yセット
	            currentX = view.getLeft();
	            currentY = view.getTop();
	            offsetX = x;
	            offsetY = y;
	            break;

	        case MotionEvent.ACTION_UP:
	            break;
        }
        return true;
    }

	/**===========================================================================================================
     * 保存ボタン処理
     * 現在表示しているBitmapのファイル名を保存する
     * アニメーションを作成する
	 *===========================================================================================================*/
    public void onSaveBitmap(){
    	try{
	        /**
	         * 読み込んだファイルの場合上書きを行うか確認する
	         */
	        if(readFilePath != null){
	        	AlertDialog.Builder alert = new AlertDialog.Builder(context);
	        	//タイトルの設定
	        	alert.setTitle(context.getString(R.string.titleSave));
	        	//メッセージの設定
	        	alert.setMessage(context.getString(R.string.titleSaveMessage1));
	        	/**
	        	 * ボタン押下処理の設定
	        	 * 「はい」上書き保存 / 「いいえ」新規保存
	        	 */
	        	alert.setPositiveButton("はい", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/**-------------------------------------------------------------------------------------------------------
				         * 画像をローカルに保存する
						 *-------------------------------------------------------------------------------------------------------*/
				        if(!getBoardView().saveToFile(readFilePath)){
				        	Toast.makeText(context, "保存に失敗しました。", Toast.LENGTH_SHORT).show();
				        }
				        // ファイルパスの初期化
				        readFilePath = null;
				        // 画面を初期化する
				        getBoardView().clearDrawList();

					}
				});
	        	alert.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						readFilePath = DateFormat.format("yyyy-MM-dd_kk.mm.ss", System.currentTimeMillis()).toString() + ".jpg";
						/**-------------------------------------------------------------------------------------------------------
				         * 画像をローカルに保存する
						 *-------------------------------------------------------------------------------------------------------*/
				        if(!getBoardView().saveToFile(readFilePath)){
				        	Toast.makeText(context, "保存に失敗しました。", Toast.LENGTH_SHORT).show();
				        }
				        // ファイルパスの初期化
				        readFilePath = null;
				        // 画面を初期化する
				        getBoardView().clearDrawList();
				        createLaytout();
					}
				});
	        	alert.create();
	        	alert.show();
	        }else{
		        //保存ファイル名の取得
	        	readFilePath = DateFormat.format("yyyy-MM-dd_kk.mm.ss", System.currentTimeMillis()).toString() + ".jpg";
				/**-------------------------------------------------------------------------------------------------------
		         * 画像をローカルに保存する
				 *-------------------------------------------------------------------------------------------------------*/
		        if(!getBoardView().saveToFile(readFilePath)){
		        	Toast.makeText(context, "保存に失敗しました。", Toast.LENGTH_SHORT).show();
		        }

		        // 画面を初期化する
		        getBoardView().clearDrawList();
		        createLaytout();
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

	/**===========================================================================================================
	 * ファイルを削除する
	 *===========================================================================================================*/
	public boolean deleteToFile(){
		if(readFilePath != null){
			context.deleteFile(readFilePath);
			readFilePath = null;
	        getBoardView().clearDrawList();
		}
		return true;
	}

	/**===========================================================================================================
	 * ファイル名を取得する
	 *===========================================================================================================*/
	public String getReadFilePath() {
		return readFilePath;
	}
}
