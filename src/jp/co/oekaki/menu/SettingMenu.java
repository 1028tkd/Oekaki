package jp.co.oekaki.menu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.oekaki.AppConst;
import jp.co.oekaki.OekakiApplication;
import jp.co.oekaki.R;
import jp.co.oekaki.bluetooth.BlueToothManagement;
import jp.co.oekaki.menu.OekakiPopupMenu.OnPopupSelectionListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**===========================================================================================================
 * 設定ボタン押下時メニューを表示するクラス
 *
 * @author sugahara satoshi
 * @serial 1.0
 * @create 2013-01-31
 *===========================================================================================================*/
public class SettingMenu {

	// Context
	private Context context;
	// アプリケーションインスタンス
	private OekakiApplication mOekakiApp;
	// onPauseメソッドで確実に閉じるための参照
	private OekakiPopupMenu mPopupMenu;
	// このアクティビィティのルートビュー
	private View mRootContainer;

	public SettingMenu(Context context){
		this.context = context;
		mOekakiApp = (OekakiApplication) context.getApplicationContext();
		mRootContainer = (View)((Activity)context).findViewById(R.id.rootContainer);
	}

	/**===========================================================================================================
	 * 状況に応じてメニューを動的に生成する。
	 * メールアプリ, カメラ, 使い方等・・。
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	private List<OekakiPopupMenu.MenuItem> createMenuItems() {
		List<OekakiPopupMenu.MenuItem> result = new ArrayList<OekakiPopupMenu.MenuItem>();
		OekakiPopupMenu.MenuItem item = null;
		for(int i=0; i < OekakiApplication.POPUPMENU_ICONS.length; i++){
			item = new OekakiPopupMenu.MenuItem();
			item.id = i+1;
			item.icon = OekakiApplication.POPUPMENU_ICONS[i];
			item.title = mOekakiApp.popupMenuTitles[i];
			result.add(item);
		}
		return result;
	}

	/**===========================================================================================================
	 * ポップアップウインドウを開く。※動的にメニュー項目を変更可能
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	public void openPopupMenu() {
		closePopupMenu();
		mPopupMenu = new OekakiPopupMenu(context,mRootContainer,AppConst.SETTING_POPUPMENU_WIDTH,createMenuItems(),((Activity)context).getString(R.string.menuHint),0);
		// 選択項目コールバックリスナー
		mPopupMenu.setOnPopupSelectionListener(mOnPopupSelectionListener);
		// 画面の右下寄せで表示する
		mPopupMenu.showAtLocation(Gravity.CENTER, mOekakiApp.defaultPopupX(), mOekakiApp.defaultPopupY());
	}

    /**===========================================================================================================
	 * ポップアップメニューで選択された項目IDを受取るためのリスナー。
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	private OnPopupSelectionListener mOnPopupSelectionListener = new OnPopupSelectionListener() {
		public void onSelection(int positon, long id) {
			int menuId = (int) id;
			Intent intent = new Intent();
			switch(menuId) {
				case AppConst.MENUID_SAVE:
					mOekakiApp.getLayout().onSaveBitmap();
					break;
				case AppConst.MENUID_DELETE:
					mOekakiApp.getLayout().deleteToFile();
					mOekakiApp.getLayout().createLaytout();
					break;
				case AppConst.MENUID_INITIALIZION:
					mOekakiApp.getLayout().createLaytout();
					((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().clearDrawList();
					break;
				case AppConst.MENUID_OPENFILE:
					openAppFile();
					break;
				case AppConst.MENUID_BLUETOOTH:
				case AppConst.MENUID_SEND_PAINT_BLUETOOTH:
					Log.v("ID","MENUID[" + menuId + "][" + AppConst.MENUID_BLUETOOTH + "][" + AppConst.MENUID_SEND_PAINT_BLUETOOTH + "]");
					// 処理フラグ(true:2人でお絵描き/false:画像送信)
					if(menuId == AppConst.MENUID_BLUETOOTH){
						mOekakiApp.setBluetoothProcessFlg(true);
					}else{
						mOekakiApp.setBluetoothProcessFlg(false);
					}
					//BluetoothAdapter取得
					BlueToothManagement mBluetoothMng = ((OekakiApplication)context.getApplicationContext()).getBlueToothMng();
					// BlueTooth対応端末かチェック
					if (!mBluetoothMng.isBluetoothExit()) {
				        Toast.makeText(context, context.getString(R.string.errorMsg3), Toast.LENGTH_LONG).show();
				        return;
				    }
					// Bluetootheが使用可能かチェック
					if (!mBluetoothMng.isBluetoothEnable()) {
						Log.v("Bluetooth","使用不可なので使用できるようにする");
					    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    	((Activity)context).startActivityForResult(enableIntent, AppConst.REQUEST_ENABLE_BLUETOOTH);
					} else {
						Log.v("Bluetooth","使用可");
						// 自端末発見機能有効化
						if(mOekakiApp.getBlueToothMng().searchPaireDevice()){
							Log.v("Bluetooth","発見[接続履歴あり]");
				        	if(mOekakiApp.getBlueToothMng().getPairedDeviceAdapter().getCount() > 0){
								BluetoothMenu bluetoothMenu = new BluetoothMenu(context);
								bluetoothMenu.openPopup();
				        	}
						}else{
							Log.v("Bluetooth","[接続履歴なし]");
							//接続履歴がない場合デバイスの検索を行う
							mOekakiApp.getBlueToothMng().doDiscvory();
						}
				    }
					break;
				case AppConst.MENUID_MAIL:
		        	AlertDialog.Builder alert = new AlertDialog.Builder(context);
		        	//タイトルの設定
		        	alert.setTitle(context.getString(R.string.titleMail));
		        	//メッセージの設定
		        	alert.setMessage(context.getString(R.string.titleSaveMessage2));
		        	/**
		        	 * ボタン押下処理の設定
		        	 * 「はい」現在の画像を添付 / 「いいえ」ファイル選択画面の表示
		        	 */
		        	alert.setPositiveButton(context.getString(R.string.item1), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							long dataTaken = System.currentTimeMillis();
							String fileNm = DateFormat.format("yyyy-MM-dd_kkmmss", dataTaken).toString() + ".jpg";
							Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//							Bitmap b = ((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().getBitmap();

							((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawingCacheEnabled(true);
							Bitmap b = Bitmap.createBitmap(((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().getDrawingCache());
							Log.v("b = ", "[" + b + "]");
							((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setBitmap(b);
							((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().setDrawingCacheEnabled(false);
							/**-------------------------------------------------------------------------------------------------------
					         * 画像をローカルに保存する
							 *-------------------------------------------------------------------------------------------------------*/
//					        if(!((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().saveToFile(fileNm)){
//					        	Toast.makeText(context, "保存に失敗しました。", Toast.LENGTH_SHORT).show();
//					        }
//					        String filePath = Environment.getExternalStorageDirectory() + "/memo.txt";
//					        File file = new File(filePath);
//					        file.getParentFile().mkdir();
//
//					        FileOutputStream fos;
//					        try {
//					            fos = new FileOutputStream(file, true);
//					            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
//					            BufferedWriter bw = new BufferedWriter(osw);
//					            String str = ((EditText) findViewById(R.id.EditTextInput)).getText().toString();
//					            bw.write(str);
//					            bw.flush();
//					            bw.close();
//					        } catch (Exception e) {
//					        }
							String applicationName = "PATOM";
						    // ファイル保存先 (SDカード)
							String fileFullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
								+ File.separator + applicationName
								+ File.separator + fileNm;
		                    Log.v("fileFullPathだよ", "[" + fileFullPath + "]");

					        File dir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + applicationName);
					        if (!dir.exists()) {
					            dir.mkdirs();
			                    Log.v("フォルダ作成", "[" + Environment.getExternalStorageDirectory().toString() + File.separator + applicationName + "]");
					        }
					        FileOutputStream out = null;
							try {
						        ContentValues values = new ContentValues();
						        values.put(MediaStore.Images.Media.TITLE, fileFullPath);
						        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

						        mOekakiApp.setImageUri(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
//							    ContentValues values = new ContentValues(7);
//							    values.put(Images.Media.TITLE, fileNm);
//							    values.put(Images.Media.DISPLAY_NAME, fileNm);
//							    values.put(Images.Media.DATE_TAKEN, dataTaken);
//							    values.put(Images.Media.MIME_TYPE, "image/jpeg");
//							    values.put(Images.Media.DATA, fileFullPath);
//							    context.getContentResolver().insert(IMAGE_URI, values);

								// 画像をバイト配列に変換する
								ByteArrayOutputStream os = new ByteArrayOutputStream();
								b.compress(CompressFormat.JPEG, 75, os); // PNGの場合は CompressFormat.PNG にする
								os.flush();
								byte[] w = os.toByteArray();
								os.close();

								// バイト配列をファイルとしてSDカードに書き出す
								out = new FileOutputStream(fileFullPath);
								out.write(w, 0, w.length);
								out.flush();
								out.close();
								out = null;

							    // ファイルを示すインテントを作成する
//								Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileFullPath));
//								Uri uri = Uri.fromFile(new File(fileFullPath));
//								Uri uri = Uri.parse("file://" + fileFullPath);
//								File externalSD = new File(System.getenv("EXTERNAL_STORAGE"));
//								Uri uri = Uri.parse("file://" + externalSD.getPath() + File.pathSeparator + applicationName +"/" + fileNm);
//			                    Log.v("ファイル", "[" + uri.getPath() + "]");

								Intent intent = new Intent(Intent.ACTION_SEND);
			                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
							    intent.putExtra(Intent.EXTRA_SUBJECT, "件名");
						        intent.putExtra(Intent.EXTRA_TEXT, "本文");
								intent.setType("image/jpg"); // PNGの場合は "image/png" にする
								intent.putExtra(Intent.EXTRA_STREAM, mOekakiApp.getImageUri());

								// 外部アプリを起動する
								try {
									context.startActivity(Intent.createChooser(intent, "Choose Email Client"));// 常に送信先を選択させる
								} catch (ActivityNotFoundException e) {
									//
								}

							} catch (FileNotFoundException ex) {
								//
							} catch (IOException ex) {
								//
							} finally {
								try {
									if (out != null) {
										out.close();
									}
								} catch (IOException ex) {
									//
								}
							}
					        // 画面を初期化する
					        ((OekakiApplication)context.getApplicationContext()).getLayout().getBoardView().clearDrawList();
//							Intent intent = new Intent();
//		                    intent.setAction(Intent.ACTION_SEND);
//		                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//		                    // 件名
//		                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.title_activity_oekaki_main));
//		                    intent.setType("image/jpg");
//		                    Log.v("ファイルパス","file://" + context.getApplicationInfo().dataDir + "/files/" + fileNm);
//		                    URI path = URI.create("file://" + context.getApplicationInfo().dataDir + "/files/" + fileNm);
//		                    ParcelFileDescriptor parcel = null;
//							try {
//								parcel = ParcelFileDescriptor.open(new File(path),ParcelFileDescriptor.MODE_READ_ONLY);
//							} catch (FileNotFoundException e1) {
//								// TODO 自動生成された catch ブロック
//								e1.printStackTrace();
//							}
//		                    Log.v("Uriだよ", "[" + parcel + "]");
//		                    intent.putExtra(Intent.EXTRA_STREAM, parcel);
//		                    // 本文
//		                    String value = DataUtil.getSystemDate() + " " + DataUtil.getSystemTime() + ((Activity)context).getString(R.string.mailText);
//		                    intent.putExtra(Intent.EXTRA_TEXT, value);
//		                    try {
//		                        // システムにインストールされているメーラーが表示される。
//		                    	((Activity)context).startActivityForResult(intent,AppConst.REQUEST_MAIL);
//		                     } catch (ActivityNotFoundException e ) {
//		                        // ※このエラーは通常はインテントの設定ミスがほとんどのはずだ。
//		                        Log.e("MailSend", e.getMessage());
//		                     }
						}
					});
		        	alert.setNegativeButton(context.getString(R.string.item3), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							openAppFile();
						}
					});
		        	alert.create();
		        	alert.show();
					break;
				case AppConst.MENUID_CAMERA:
			    	String filename = System.currentTimeMillis() + ".jpg";

			        ContentValues values = new ContentValues();
			        values.put(MediaStore.Images.Media.TITLE, filename);
			        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			        mOekakiApp.setImageUri(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));

			        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOekakiApp.getImageUri());
			        ((Activity)context).startActivityForResult(intent,AppConst.REQUEST_CAPTURE_IMAGE);
					break;
				case AppConst.MENUID_GALLERY:
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					((Activity)context).startActivityForResult(intent,AppConst.REQUEST_GALLERY);
					break;
				case AppConst.MENUID_CLOSE_SETTING:
					break;
			}
			closePopupMenu();
		}
	};

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

	/**===========================================================================================================
	 * ファイルを開く
	 * @author sugahara satoshi
	 * @serial 1.0
	 * @create 2013-01-31
	 *===========================================================================================================*/
	private void openAppFile(){
		if(((Activity)context).fileList() != null && ((Activity)context).fileList().length > 0){
			mOekakiApp.getLayout().createImageViewScrollLaytout(((Activity)context).fileList());
			for(int i = 0; i < ((Activity)context).fileList().length; i++){
				Log.v("fileList","[" + ((Activity)context).fileList()[i] + "]");
			}
		}else{
			Toast.makeText(context, context.getString(R.string.errorMsg1), Toast.LENGTH_LONG).show();
		}
	}

	/**===========================================================================================================
	 * バックキーとホームキーをトラップするキーリスナー
	 * @param dipWidth 画面幅
	 *===========================================================================================================*/
	protected View.OnKeyListener mKeyListener = new View.OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (	event.getKeyCode() == KeyEvent.KEYCODE_BACK
				||  event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
				if(mOekakiApp.getBlueToothMng().getBluetoothAdapter() != null){
					mOekakiApp.getBlueToothMng().getBluetoothAdapter().cancelDiscovery();
				}
				return true;
			}
			return false;
		}
	};

	/**===========================================================================================================
	 * Intent発行後の返却処理
	 * @param dipWidth 画面幅
	 *===========================================================================================================*/
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("onActivityResult","requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data + " Activity.RESULT_OK:" + Activity.RESULT_OK + " Activity.RESULT_CANCELED:" + Activity.RESULT_CANCELED);
		if(AppConst.REQUEST_CAPTURE_IMAGE == requestCode && resultCode == Activity.RESULT_OK ){
			mOekakiApp.getLayout().getBoardView().readFromUri(mOekakiApp.getImageUri());
		}else if (resultCode==Activity.RESULT_OK && requestCode==AppConst.REQUEST_GALLERY) {
			mOekakiApp.getLayout().getBoardView().readFromUri(data.getData());
		}else if (resultCode==Activity.RESULT_OK && requestCode==AppConst.REQUEST_ENABLE_BLUETOOTH) {
			Log.v("Bluetooth","[自端末発見機能有効化]");
			// 自端末発見機能有効化
			mOekakiApp.getBlueToothMng().ensureDiscoverable();
		}else if (resultCode==300 && requestCode==AppConst.REQUEST_ENABLE_BLUETOOTH_DISCOVERABLE) {
			// 接続履歴の検索
			if(mOekakiApp.getBlueToothMng().searchPaireDevice()){
				Log.v("Bluetooth","発見[接続履歴あり]");
	        	if(mOekakiApp.getBlueToothMng().getPairedDeviceAdapter().getCount() > 0){
					BluetoothMenu bluetoothMenu = new BluetoothMenu(context);
					bluetoothMenu.openPopup();
	        	}
			}else{
				Log.v("Bluetooth","[接続履歴なし]");
				//接続履歴がない場合デバイスの検索を行う
				mOekakiApp.getBlueToothMng().doDiscvory();
			}
		}else if (resultCode==Activity.RESULT_CANCELED) {
			Log.v("Bluetooth","[なし]");
		}
	}

}
