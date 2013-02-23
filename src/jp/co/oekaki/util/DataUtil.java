package jp.co.oekaki.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日付に関する共通処理
 * @author 菅原　聡
 */
public class DataUtil {

	/**
	 * 現在のシステム日付を(YYYY/MM/DD形式)で取得する
	 * @return システム日付
	 */
	public static String getSystemDate(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
		return sd.format(new Date());
	}

	/**
	 * 現在のシステム時刻を(hh:mm:ss形式)で取得する
	 * @return システム日付
	 */
	public static String getSystemTime(){
		SimpleDateFormat sd = new SimpleDateFormat("hh:mm:ss");
		return sd.format(new Date());
	}

	/**
	 * 現在のシステム日付を(YYYYMMDDhhmmss形式)で取得する
	 * @return システム日付
	 */
	public static String formatString(String str){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd/ hh:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		try {
			return sd.format(format.parse(str));
		} catch (ParseException e) {
			e.printStackTrace();
			return "123";
		}
	}

	/**
	 * 2つの日付の差を求めます。
	 * 日付文字列 strDate1 - strDate2 が何日かを返します。
	 *
	 * @param strDate1    日付文字列 yyyy/MM/dd
	 * @param strDate2    日付文字列 yyyy/MM/dd
	 * @return    2つの日付の差
	 * @throws ParseException 日付フォーマットが不正な場合
	 */
	public static int differenceDays(String strDate1,String strDate2)
	    throws ParseException {
	    Date date1 = DateFormat.getDateInstance().parse(strDate1);
	    Date date2 = DateFormat.getDateInstance().parse(strDate2);
	    return differenceDays(date1,date2);
	}

	/**
	 * 2つ日付の差を求めます。
	 * java.util.Date 型の日付 date1 - date2 が何日かを返します。
	 *
	 * 計算方法は以下となります。
	 * 1.最初に2つの日付を long 値に変換します。
	 * 　※この long 値は 1970 年 1 月 1 日 00:00:00 GMT からの
	 * 　経過ミリ秒数となります。
	 * 2.次にその差を求めます。
	 * 3.上記の計算で出た数量を 1 日の時間で割ることで
	 * 　日付の差を求めることができます。
	 * 　※1 日 ( 24 時間) は、86,400,000 ミリ秒です。
	 *
	 * @param date1    日付 java.util.Date
	 * @param date2    日付 java.util.Date
	 * @return    2つの日付の差
	 */
	public static int differenceDays(Date date1,Date date2) {
	    long datetime1 = date1.getTime();
	    long datetime2 = date2.getTime();
	    long one_date_time = 1000 * 60 * 60 * 24;
	    long diffDays = (datetime1 - datetime2) / one_date_time;
	    return (int)diffDays;
	}

	/**
	 * 2つの時刻の差を求めます。
	 * java.util.Date 型の日付 date1 - date2 が何日かを返します。
	 *
	 * 計算方法は以下となります。
	 * 1.最初に2つの日付を long 値に変換します。
	 * 　※この long 値は 1970 年 1 月 1 日 00:00:00 GMT からの
	 * 　経過ミリ秒数となります。
	 * 2.次にその差を求めます。
	 * 3.上記の計算で出た数量を 1 日の時間で割ることで
	 * 　日付の差を求めることができます。
	 *
	 * @param date1    日付 java.util.Date
	 * @param date2    日付 java.util.Date
	 * @return    2つの時間の差（秒）
	 */
	public static long differenceTime(Date date1,Date date2) {
		long datetime1 = date1.getTime();
	    long datetime2 = date2.getTime();
	    return (datetime1 - datetime2) / 1000;
	}
}
