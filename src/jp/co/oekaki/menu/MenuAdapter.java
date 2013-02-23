package jp.co.oekaki.menu;

import java.util.List;

import jp.co.oekaki.R;
import jp.co.oekaki.menu.OekakiPopupMenu.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**===========================================================================================================
 * メニュー項目を表示するアダプタークラス
 * @param dipWidth 画面幅
 *===========================================================================================================*/
class MenuAdapter extends BaseAdapter {

	// ポップアップに表示されるメニュー項目のリスト(リストアダプターにセット)
	private final List<MenuItem> mMenuItems;
	// メニューを動的に生成するインフレーター
	private final LayoutInflater mInflater;

	public MenuAdapter(List<MenuItem> mMenuItems,LayoutInflater mInflater) {
		this.mMenuItems = mMenuItems;
		this.mInflater = mInflater;
	}

	public int getCount() {
		return mMenuItems.size();
	}

	public Object getItem(int position) {
		return mMenuItems.get(position);
	}

	public long getItemId(int position) {
		return mMenuItems.get(position).id;
	}

	/**===========================================================================================================
	 * Popupウィンドウにて設定されたアイコン・タイトルを表示する
	 * @param position 		ポジション
	 * @param convertView	View
	 * @param parent		ViewGroup
	 *===========================================================================================================*/
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.popup_list_item_withimg, null);
			ImageView icon = (ImageView) convertView.findViewById(R.id.popup_icon);
			TextView title = (TextView) convertView.findViewById(R.id.popup_title);
			holder.icon = icon;
			holder.title = title;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MenuItem item = mMenuItems.get(position);
		holder.icon.setBackgroundResource(item.icon);
		holder.title.setText(item.title);

		return convertView;
	}

	private class ViewHolder {
		public ImageView icon;
		public TextView title;
	}
}
