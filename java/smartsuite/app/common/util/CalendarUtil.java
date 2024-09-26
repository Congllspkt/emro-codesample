package smartsuite.app.common.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class CalendarUtil {
	/** 짧은 표시 형식을 나타내는 styleCode */
	final public static int SHORT = java.text.DateFormat.SHORT;
	/** 중간 길이 표시 형식을 나타내는 styleCode */
	final public static int MEDIUM = java.text.DateFormat.MEDIUM;
	/** 긴 표시 형식을 나타내는 styleCode */
	final public static int LONG = java.text.DateFormat.LONG;
	
	private CalendarUtil() {}
	
	/** 
	 * 지금의 날짜를 리턴 XXXX-XX-XX형태로 리턴한다
	 */
	public static Date getNow() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	
	/** 
	 * 지금의 날짜를 리턴 XXXX-XX-XX형태로 리턴한다
	 */
	public static String formatNow(String formatStr) {
		return CalendarUtil.format( CalendarUtil.getNow(), formatStr );
	}
	
	/** 
	 * 생일을 받아서 나이로  리턴한다
	 */
	public static int getAge(Date birth) {
		Calendar now = Calendar.getInstance(); 
		int nowYear =  now.get(Calendar.YEAR);
		now.setTime(birth);
		int birthYear = now.get(Calendar.YEAR);
		return nowYear - birthYear +1 ;
	}
	
	/**
	 * 현재의 year를 반환한다.
	 */
	public static int getNowYear() {
		return CalendarUtil.getYear(CalendarUtil.getNow());
	}
	
	/**
	 * 현재의 month를 반환한다.
	 */
	public static int getNowMonth() {
		return CalendarUtil.getMonth(CalendarUtil.getNow());
	}
	
	/**
	 * 현재의 date를 반환한다.
	 */
	public static int getNowDate() {
		return CalendarUtil.getDate(CalendarUtil.getNow());
	}
	
	/**
	 * 현재의 요일을 반환한다. 반환값은 "월", "화", "수", "목", "금", "토", "일" 중 하나임
	 * [주의] 요일을 얻는 함수는 getNowDay()이고 날짜를 얻는 함수는 getNowDate() 임
	 */
	public static String getNowDay() {
		return CalendarUtil.getDay(CalendarUtil.getNow());
	}

	/**
	 * 현재의 hour를 반환한다.
	 */
	public static int getNowHour() {
		return CalendarUtil.getHour(CalendarUtil.getNow());
	}
	
	/**
	 * 현재의 minute를 반환한다.
	 */
	public static int getNowMinute() {
		return CalendarUtil.getMonth(CalendarUtil.getNow());
	}
	
	/**
	 * 현재의 second를 반환한다.
	 */
	public static int getNowSecond() {
		return CalendarUtil.getSecond(CalendarUtil.getNow());
	}

	/** 
	 * date에서 month를 int로 반환한다.
	 */
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}
	
	/** 
	 * date에서 month를 int로 반환한다.
	 */
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}
	
	/** 
	 * date에서 month를 int로 반환한다.
	 */
	public static int getDate(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int ret = cal.get(Calendar.DATE);
		return ret;
	}
	
	/** 
	 * date에서 요일을 반환한다. 반환값은 "월", "화", "수", "목", "금", "토", "일" 중 하나임
	 * [주의] 요일을 얻는 함수는 getDay()이고 날짜를 얻는 함수는 getDate() 임
	 */
	public static String getDay(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int ret = cal.get(Calendar.DAY_OF_WEEK);
		switch (ret) {
			case  Calendar.MONDAY:
			return "월";
			case  Calendar.TUESDAY:
			return "화";
			case  Calendar.WEDNESDAY:
			return "수";
			case  Calendar.THURSDAY:
			return "목";
			case  Calendar.FRIDAY:
			return "금";
			case  Calendar.SATURDAY:
			return "토";
			case  Calendar.SUNDAY:
			return "일";
			default:
			return null;
		}
	}
	
	/** 
	 * date에서 hour를 int로 반환한다.
	 */
	public static int getHour(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int ret = cal.get(Calendar.HOUR_OF_DAY);
		return ret;
	}

	/** 
	 * date에서 minute를 int로 반환한다.
	 */
	public static int getMinute(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int ret = cal.get(Calendar.MINUTE);
		return ret;
	}

	/** 
	 * date에서 second를 int로 반환한다.
	 */
	public static int getSecond(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		int ret = cal.get(Calendar.SECOND);
		return ret;
	}


	/**
	 * 주어진 문자열을 java.sql.Date로 반환한다.
	 * 문자열은 yyyy-M-d 형태로서 2001-05-08 또는 2001-5-8 와 같이 주어진다.
	 * [주의] 시,분,초,밀리세컨드 단위를 문자열에 포함시키면 파싱이 제대로 이루지지 않는다.<br>
	 */
	public static java.sql.Date getSQLDate(String dateStr) {
		Date parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d");
		if (parsedDate != null) {
			return new java.sql.Date(parsedDate.getTime());
		}
		return null;
	}
	
	/**
	 * 주어진 문자열을 java.sql.Time으로 반환한다.<br>
	 * 문자열은 H:m:s.S 형태로서 22:06:29.050 또는 22:6:29.50 와 같이주어진다.<br>
	 * [주의] 시간을 24시간제로서 오후 11시는 23시로 표현된다.<br>
	 * [참고] 밀리세컨드 단위는 생략 가능하다 즉 22:6:29 와 같이 입력할 수 있다<br>
	 */
	public static java.sql.Time getSQLTime(String dateStr) {
		Date parsedDate = null;
		if (dateStr.indexOf('.') == -1) {
			parsedDate = CalendarUtil.parse(dateStr, "H:m:s");
		} else {
			parsedDate = CalendarUtil.parse(dateStr, "H:m:s.S");
		}
		if (parsedDate != null) {
			return new java.sql.Time(parsedDate.getTime());
		}
		return null;
	}

	/**
	 * 주어진 문자열을 java.sql.Timestamp으로 반환한다.<br>
	 * 문자열은 yyyy-M-d H:m:s.S 형태로서 2001-05-08 22:06:29.050 또는 2001-5-8 22:6:29.50 와 같이 주어진다.<br>
	 * [주의] 시간을 24시간제로서 오후 11시는 23시로 표현된다.<br>
	 * [참고] 시,분,초,밀리세컨드 단위는 생략 가능하다. 즉 2001-05-08 와 같이 입력할 수 있다<br>
	 */
	public static java.sql.Timestamp getSQLTimestamp(String dateStr) {
		Date parsedDate = null;
		if (dateStr.indexOf(':') == -1) {
			parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d");
		} else {
			if (dateStr.indexOf('.') == -1) {
				parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d H:m:s");
			} else {
				parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d H:m:s.S");
			}
		}
		if (parsedDate != null) {
			return new java.sql.Timestamp(parsedDate.getTime());
		}
		return null;
	}
	
	/**
	 * 주어진 문자열을 Date로 반환한다.<br>
	 * 문자열은 2001-05-08 22:06:29.050 또는 2001-5-8 22:6:29.50 형태로 주어진다. <br>
	 * [주의] 시간을 오후 10시의 경우 22시로 입력한다.<br>
	 * [참고] 시,분,초,밀리세컨드 단위는 생략 가능하다. 즉 2001-05-08 와 같이 입력할 수 있다<br>
	 */
	public static Date getDate(String dateStr) {
		Date parsedDate = null;
		if (dateStr.indexOf(':') == -1) {
			parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d");
		} else {
			if (dateStr.indexOf('.') == -1) {
				parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d H:m:s");
			} else {
				parsedDate = CalendarUtil.parse(dateStr, "yyyy-M-d H:m:s.S");
			}
		}
		return parsedDate;
	}
	
	/**
	 * 주어진 year, month, date를 이용하여 만들어진 Date를 반환한다.<br>
	 * 시분초는 모두 0으로 초기화된다.
	 */
	public static Date getDate(int year, int month, int date) {
		return CalendarUtil.parse(Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(date), "yyyy-M-d");
	}
	
	/**
	 * 주어진 year, month, date를 이용하여 만들어진 Date를 반환한다.<br>
	 * 시분초는 모두 0으로 초기화된다.
	 */
	public static Date getDate(int year, int month, int date, int hour, int minute, int second, int millisecond) {
		return CalendarUtil.parse(Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(date) + " " + Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(second) + "." + Integer.toString(millisecond), "yyyy-M-d H:m:s.S");
	}
	

	/** 
	* 생일을 받아서 isKorean이 true 이면 한국나이 false이면 만나이로 리턴한다.
	*/
	public static int getAge(Date birth, boolean isKorean) {
		if (isKorean) {
			return CalendarUtil.getAge(birth);
		}
		else {
			Date now = CalendarUtil.getNow();
			return CalendarUtil.between(birth, now) / 365;
		}
	}
	
	/**
	* 주어진 날짜로 부터 몇년, 몇월, 몇일 더한 날짜를 리턴한다
	*/
	public static Date moveFromDate( String dateStr, int y, int m, int d ) {
		Calendar c = Calendar.getInstance();
		c.setTime(CalendarUtil.parse(dateStr, "yyyy-MM-dd"));
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y );
		return c.getTime();
	}
	
	/**
	 * 주어진 날짜로 부터 몇년, 몇월, 몇일 더한 날짜를 리턴한다
	 */
	public static Date moveFromDate( Date inputDate, int y, int m, int d ) {
		Calendar c = Calendar.getInstance();
		c.setTime( inputDate );
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y );
		return c.getTime();
	}	

	/**
	* 주어진 날짜로 부터 몇년, 몇월, 몇일, 몇시간, 몇분, 몇초 더한 날짜를 리턴한다
	*/
	public static Date moveFromDate( Date inputDate, int y, int m, int d, int hour, int min, int sec) {
		Calendar c = Calendar.getInstance();
		c.setTime( inputDate );
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y );
		c.add(Calendar.HOUR, hour);
		c.add(Calendar.MINUTE, min);
		c.add(Calendar.SECOND, sec);
		return c.getTime();
	}

	/** 
	 * 지금으로부터 몇년, 몇월, 몇일 더한 날짜를 리턴한다
	 */
	public static Date moveFromNow( int y, int m, int d ) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y );
		return c.getTime();
	}
	/** 
	 * 지금으로부터 몇년, 몇월, 몇일 더한 날짜를 리턴한다
	 */
	public static String moveFromNow( int y, int m, int d ,String format ) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y ); 
		return CalendarUtil.format( c.getTime(),format);
	}
	/** 
	 * 지금으로부터 몇년, 몇월, 몇일 더한 날짜를 리턴한다
	 */
	public static Date moveFromNow( int y, int m, int d, int hour, int min, int sec ) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, d );
		c.add(Calendar.MONTH, m );
		c.add(Calendar.YEAR, y );
		c.add(Calendar.HOUR, hour);
		c.add(Calendar.MINUTE, min);
		c.add(Calendar.SECOND, sec);
		return c.getTime();
	}
	
	/**
	 * 입력된 시간을 몇가지 형태의 날짜 스트링으로 변환한다.
	 * 파라메터 스타일은 <code> CalendarUtil.SHORT, CalendarUtil.MEDIUM, CalendarUtil.LONG </code> 이다.
	 * <i><b>날짜를 표현하는 예시</b></i>
	 * <li> SHORT : 00-10-13 
	 * <li> MEDUIM : 2000-10-13
	 * <li> LONG : 2000년 10월 13일 금
	 * @param dateStyleCode	날짜를 표현하는 스타일
	 */
	public static String formatDate( Date inputdate, int dateStyleCode ) {
		java.text.DateFormat df = java.text.DateFormat.getDateInstance( dateStyleCode );
		return df.format(inputdate);
	}

	/**
	 * 입력된 시간을 몇가지 형태의 날짜 스트링으로 변환한다.
	 * 파라메터 스타일은 <code> CalendarUtil.SHORT, CalendarUtil.MEDIUM, CalendarUtil.LONG </code> 이다.
	 * 파라메터 스타일은 <code> Locale.UK, Locale.US, Locale.KOREA </code> 등 이다.
	 * <i><b>날짜를 표현하는 예시</b></i>
	 * <li> SHORT : 00-10-13 
	 * <li> MEDUIM : 2000-10-13
	 * <li> LONG : 2000년 10월 13일 금
	 * @param dateStyleCode	날짜를 표현하는 스타일
	 */
	public static String formatDate( Date inputdate, int dateStyleCode, Locale localeCode ) {
		java.text.DateFormat df = java.text.DateFormat.getDateInstance( dateStyleCode, localeCode );
		return df.format(inputdate);
	}
	

	/**
	 * 입력된 시간을 몇가지 형태의 날짜-시간스트링으로 변환한다.
	 * 파라메터 스타일은 <code> CalendarUtil.SHORT, CalendarUtil.MEDIUM, CalendarUtil.LONG </code> 이다.
	 * <i><b>날짜를 표현하는 예시</b></i>
	 * <li> SHORT : 00-10-13 
	 * <li> MEDUIM : 2000-10-13
	 * <li> LONG : 2000년 10월 13일 금
	 * <p>
	 * <i><b>시간을 표현하는 예시</b></i>
	 * <li> SHORT : 오후 1:15
	 * <li> MEDIUM : 오후 1:15:18
	 * <li> LONG : 01시15분18초 오후
	 * @param dateStyleCode	날짜를 표현하는 스타일
	 * @param timeStyleCode	시간을 표현하는 스타일 SHORT = 오후 1:15
	 */
	public static String formatDateTime( Date inputdate, int dateStyleCode, int timeStyleCode  ) {
		java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance( dateStyleCode, timeStyleCode );
		return df.format(inputdate);
	}

	/**
	 * 입력된 시간을 몇가지 형태의 날짜-시간스트링으로 변환한다.
	 * 파라메터 스타일은 <code> CalendarUtil.SHORT, CalendarUtil.MEDIUM, CalendarUtil.LONG </code> 이다.
	 * 파라메터 스타일은 <code> Locale.UK, Locale.US, Locale.KOREA </code> 등 이다.
	 * <i><b>날짜를 표현하는 예시</b></i>
	 * <li> SHORT : 00-10-13 
	 * <li> MEDUIM : 2000-10-13
	 * <li> LONG : 2000년 10월 13일 금
	 * <p>
	 * <i><b>시간을 표현하는 예시</b></i>
	 * <li> SHORT : 오후 1:15
	 * <li> MEDIUM : 오후 1:15:18
	 * <li> LONG : 01시15분18초 오후
	 * @param dateStyleCode	날짜를 표현하는 스타일
	 * @param timeStyleCode	시간을 표현하는 스타일 SHORT = 오후 1:15
	 */
	public static String formatDateTime( Date inputdate, int dateStyleCode, int timeStyleCode, Locale localeCode ) {
		java.text.DateFormat df = java.text.DateFormat.getDateTimeInstance( dateStyleCode, timeStyleCode, localeCode );
		return df.format(inputdate);
	}
	
	/**
	 * 주어진 inputdate를 formatStr에 주어진 형식으로 바꾸어 문자열로 리턴한다.
	 * [formatStr의 사용예] "yyyy.MM.dd G 'at' HH:mm:ss a zzz"
	 */
	public static String format(Date inputdate, String formatStr) {
		// Format the current time.
		SimpleDateFormat formatter = new SimpleDateFormat (formatStr, Locale.KOREA);
		return formatter.format(inputdate);
 	}
 	
	/**
	 * 주어진 inputdate를 formatStr에 주어진 형식으로 바꾸어 문자열로 리턴한다.
	 * [formatStr의 사용예] "yyyy.MM.dd G 'at' HH:mm:ss a zzz"
	 * CalendarUtil.reformat("2000-05-01", "yyyy-MM-dd", "yy/MM/dd");
	 */
	public static String reformat(String inputDateStr, String fromFormatStr, String toFormatStr) {
		return CalendarUtil.format(CalendarUtil.parse(inputDateStr, fromFormatStr), toFormatStr);
 	}

 	/**
 	 * CalendarUtil.parse("2000-01-05", "yyyy-MM-dd") 를 수행하면 Date 인스턴스를 얻는다.
 	 */
 	public static Date parse(String dateStr, String formatStr) {
		SimpleDateFormat formatter = new SimpleDateFormat (formatStr, Locale.KOREA);
		return formatter.parse(dateStr, new ParsePosition(0) );
 	}
 
 	/**
 	 * fromDate부터 toDate까지의 날짜를 센다. fromDate가 toDate보다 미래인 경우에는 음수가 리턴된다.
 	 */
 	public static int between(Date fromDate, Date toDate) {
 		double milliGap = (double)(toDate.getTime() - fromDate.getTime());
 		return (int)(milliGap/(1000.0*60.0*60.0*24.0));
 	}
 	
	/** 
	 * date에서 날짜 부분은 그대로 두고 hour, minute, second 부분을 0으로 초기화한다.
	 */
	public static Date trimTime(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/** 
	 * date에서 시간 부분은 그대로 두고 year, month, date 부분을 0으로 초기화한다.
	 */
	public static Date trimDate(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set(Calendar.YEAR, cal.getMinimum(Calendar.YEAR));
		cal.set(Calendar.MONTH, cal.getMinimum(Calendar.MONTH));
		cal.set(Calendar.DATE, cal.getMinimum(Calendar.DATE));
		return cal.getTime();
	}
	
	/**
 	 * CalendarUtil.parse("2000-01-05", "yyyy-MM-dd") 를 수행하면 Date 인스턴스를 얻는다.
 	 */
 	public static java.sql.Timestamp parseSqlDate(String dateStr, String formatStr) {
		SimpleDateFormat formatter = new SimpleDateFormat (formatStr, Locale.KOREA);
		Date tmpDate = formatter.parse(dateStr, new ParsePosition(0) );
		java.sql.Timestamp tmpDate1 =new java.sql.Timestamp(tmpDate.getTime());
		return tmpDate1;
 	}
 	
 	public static java.sql.Timestamp parseSqlDate(Date dateStr, String formatStr){

		java.sql.Timestamp tmpDate1 =new java.sql.Timestamp(dateStr.getTime());

		return tmpDate1;
 	}

	/** 
	 * year, month를 받아 마지막날의 날짜를 리턴.
	 */
	public static String getLastDate( int year, int month, String formatStr ) {
		Calendar cal = Calendar.getInstance(); 
		cal.set(  year,  month, 1);
		cal.add( Calendar.DATE, -1 );
		SimpleDateFormat formatter = new SimpleDateFormat (formatStr, Locale.KOREA);
		return formatter.format(cal.getTime());
	}

	public static boolean validDate(String dateStr) {
		try{
			if (dateStr.indexOf(':') == -1) {
				CalendarUtil.parse(dateStr, "yyyyMMdd");
			} else {
				if (dateStr.indexOf('.') == -1) {
					CalendarUtil.parse(dateStr, "yyyy-MM-dd H:m:s");
				} else {
					CalendarUtil.parse(dateStr, "yyyy-MM-dd H:m:s.S");
				}
			}
			return true;
		}catch(Exception e){
			return false;
		}
	}
}// end of class: CalendarUtil