/*
 * Created on 2005/11/30
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;

/**
 * @author TURNER
 * EastCircleDmy
 *
 * @
 */
public class CircleEastDmy extends CircleDmy {

	private int x = 0;
	private int y = 0;

	/**
	 * @param cols
	 */
	private CircleEastDmy(String[] cols, int x, int y) {
		super(cols);
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getX()
	 */
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getY()
	 */
	public int getY() {
		return y;
	}

	public static CircleEastDmy getInstance(int x, int y) {
		String[] cols = new String[INDEX_MAX_];
		for (int i = 0; i < cols.length; i++) {
			cols[i] = "";
		}
		return new CircleEastDmy(cols, x, y);
	}

}
