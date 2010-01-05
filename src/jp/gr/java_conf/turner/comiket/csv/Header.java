/*
 * Created on 2005/08/28
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.csv;


/**
 * @author TURNER
 * Header
 *
 * @
 */
public class Header extends GenericCSVLine {

	/**
	 * @param columns
	 */
	protected Header(String[] columns) {
		super(columns);
	}
	
	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comike.GenericCSVLine#getTypeOrder()
	 */
	public int getTypeOrder() {
		return 0;
	}


}
