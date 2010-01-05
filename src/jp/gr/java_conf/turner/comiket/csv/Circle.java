/*
 * Created on 2005/08/28
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.csv;

import java.text.DecimalFormat;

/**
 * @author TURNER
 * Circle
 *
 * @
 */
public class Circle extends GenericCSVLine {

	private static final int INDEX_ID = 1;
	private static final int INDEX_COLOR = 2; //チェックカラーNO
	private static final int INDEX_PAGE = 3; //抽選漏れは0
	private static final int INDEX_ORDER_INPAGE = 4; //抽選漏れは1

	private static final int INDEX_WEEKDAY = 5; //漏というのもある。
	private static final int INDEX_PAVILION = 6; //抽選漏れは空
	private static final int INDEX_BLOCK = 7; //抽選漏れは空
	private static final int INDEX_NO = 8; //抽選漏れは0
	private static final int INDEX_GENRE_CD = 9; //ジャンルコード
	private static final int INDEX_CIRCLE = 10;
	private static final int INDEX_CIRCLE_KANA = 11;
	private static final int INDEX_ARTIST = 12;
	private static final int INDEX_BOOK = 13;
	private static final int INDEX_URL = 14;
	private static final int INDEX_MAIL = 15;
	private static final int INDEX_COMMENT = 16;
	private static final int INDEX_MEMO = 17;

	private static final int INDEX_MAP_X = 18;
	private static final int INDEX_MAP_Y = 19;

	private static final int INDEX_FACE = 20;

	public static final int FACE_TO_BOTTOM = 1;
	public static final int FACE_TO_RIFHT = 2;
	public static final int FACE_TO_TOP = 3;
	public static final int FACE_TO_LEFT = 4;
	
	private static final int INDEX_SIDE = 21; // 0:a 1:b
	protected static final int INDEX_MAX_ = 22;
	
	public int getPage(){
		return Integer.parseInt(columns[INDEX_PAGE]);
	}

	public int getId() {
		return Integer.parseInt(columns[INDEX_ID]);
	}

	public String getWeekDay() {
		return columns[INDEX_WEEKDAY];
	}

	public String getPavilion() {
		return getPavilion(this.columns);
	}

	public static String getPavilion(String columns[]) {
		return columns[INDEX_PAVILION];
	}

	public String getBlock() {
		return getBlock(this.columns);
	}

	public static String getBlock(String columns[]) {
		return columns[INDEX_BLOCK];
	}

	public int getNo() {
		return Integer.parseInt(columns[INDEX_NO]);
	}

	public String getCircle() {
		return columns[INDEX_CIRCLE];
	}

	public String getColor(){
		return columns[INDEX_COLOR];
	}

	public String getSide() {

		if ("0".equals(columns[INDEX_SIDE])) {
			return "a";
		}
		if ("1".equals(columns[INDEX_SIDE])) {
			return "b";
		}
		return "";
	}

	public int getMapX() {
		if (columns[INDEX_MAP_X].length() == 0) {
			return 0;
		}
		return Integer.parseInt(columns[INDEX_MAP_X]);
	}

	public int getMapY() {
		if (columns[INDEX_MAP_Y].length() == 0) {
			return 0;
		}
		return Integer.parseInt(columns[INDEX_MAP_Y]);
	}

	public int getFace() {
		return Integer.parseInt(columns[INDEX_FACE]);
	}

	private int order = -1;
	public Circle(String[] cols) {
		super(cols);
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comike.GenericCSVLine#getTypeOrder()
	 */
	public int getTypeOrder() {
		return 2;
	}

	public String toCodeString() {
		StringBuffer buf = new StringBuffer();

		DecimalFormat format = new DecimalFormat("00");

		buf.append(this.getOriginOrder());
		buf.append(':');
		buf.append('(');
		buf.append(this.getWeekDay());
		buf.append(')');
		buf.append(this.getPavilion());
		buf.append(this.getBlock());
		buf.append('-');
		buf.append(format.format(this.getNo()));
		buf.append(this.getSide());

		return buf.toString();
	}

	public String toShortString() {
		return toCodeString() + "(" + this.getCircle() + ")";
	}
	
	

}
