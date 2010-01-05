/*
 * Created on 2005/08/28
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.turner.comiket.sorter.elem.*;


/**
 * @author TURNER
 * GenericCSVLine
 *
 * @
 */
public class GenericCSVLine implements Comparable {

	private int originOrder;
	/**
	 * @return
	 */
	public int getOriginOrder() {
		return originOrder;
	}

	/**
	 * @param i
	 */
	public void setOriginOrder(int i) {
		originOrder = i;
	}

	public int getTypeOrder() {
		return -1;
	}

	protected String[] columns;

	/**
	* @param columns
	*/
	protected GenericCSVLine(String[] columns) {
		this.columns = columns;
	}

	/**
	 * @param r
	 * @return
	 * @throws IOException
	 */
	public static GenericCSVLine readCSVLine(BufferedReader r)
		throws IOException {

		//EOFチェック
		r.mark(1);
		if (r.read() == -1) {
			return null;
		}
		r.reset();

		String[] cols = readCSV(r);

		String type = "";
		if (cols != null && cols.length > 1) {
			type = cols[0];
		}

		GenericCSVLine ret;

		if (type.equalsIgnoreCase("Header")) {
			ret = new Header(cols);
		} else if (type.equalsIgnoreCase("Color")) {
			ret = new Color(cols);
		} else if (type.equalsIgnoreCase("Circle")) {
			if( "東".equals(Circle.getPavilion(cols)) ){
				ret = AbstractCircle.getInstance(cols);
			}else if("西".equals(Circle.getPavilion(cols))){
				ret = AbstractCircle.getInstance(cols);
			}else{
				ret = new Circle(cols);	//漏れ
			}
		} else if (type.equalsIgnoreCase("UnKnown")) {
			ret = new UnKnown(cols);
		} else {
			ret = new GenericCSVLine(cols);
		}

		return ret;
	}

	private static String[] readCSV(BufferedReader r) throws IOException {
		List colsList = new ArrayList();

		StringBuffer colWk = new StringBuffer();
		boolean quotFlg = false;
		int c;

		WHILELOOP : while ((c = r.read()) != -1) {
			if (!quotFlg) {
				switch (c) {
					case '\"' :
						quotFlg = true;
						break;
					case ',' :
						colsList.add(colWk.toString());
						colWk.setLength(0);
						break;
					case '\r' :
						r.mark(1);
						int lf = r.read();
						if (lf == '\n') {
							break WHILELOOP;
						} else {
							r.reset();
							colWk.append((char)c);
						}
						break;
					case '\n' :
						break WHILELOOP;
					default :
						colWk.append((char)c);
						break;
				}
			} else {
				switch (c) {
					case '\"' :
						r.mark(1);
						int c2 = r.read();
						if (c2 == '\"') {
							colWk.append('\"');
						} else {
							r.reset();
							quotFlg = false;
						}
						break;
					default :
						colWk.append((char)c);
						break;
				}
			}
		}
		colsList.add(colWk.toString());
		String[] cols = (String[])colsList.toArray(new String[0]);
		return cols;
	}

	/**
	 * @param str
	 * @return
	 */
	private static String escapeQuot(String str) {
		StringBuffer ret = new StringBuffer(str);

		for (int i = 0; i < ret.length(); i++) {
			if (ret.charAt(i) == '\"') {
				ret.replace(i, i + 1, "\"\"");
				i++;
			}
		}
		return ret.toString();
	}

	/**
	 * @param col
	 * @return
	 */
	private static String escapeCol(String col) {

		if (col.indexOf(',') >= 0
			|| col.indexOf('\"') >= 0
			|| col.indexOf('\r') >= 0
			|| col.indexOf('\n') >= 0) {
			return "\"" + escapeQuot(col) + "\"";
		} else {
			return col;
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer w = new StringBuffer();

		boolean firstFlg = true;
		for (int i = 0; i < columns.length; i++) {
			if (firstFlg) {
				firstFlg = false;
			} else {
				w.append(',');
			}
			w.append(escapeCol(columns[i]));
		}

		return w.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		int thisVal = this.getTypeOrder();
		int anotherVal = ((GenericCSVLine)o).getTypeOrder();

		int ret = (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));

		if (ret != 0) {
			return ret;
		}

		thisVal = this.getOriginOrder();
		anotherVal = ((GenericCSVLine)o).getOriginOrder();
		ret = (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));

		return ret;
	}

}
