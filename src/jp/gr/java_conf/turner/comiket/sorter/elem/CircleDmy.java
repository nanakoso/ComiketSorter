/*
 * Created on 2005/11/20
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;


/**
 * @author TURNER
 * CircleWestDmy
 *
 * @
 */
public class CircleDmy extends AbstractCircle {

	/**
	 * @param cols
	 */
	protected CircleDmy(String[] cols) {
		super(cols);
	}


	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.CircleWest#initBlock()
	 */
	protected void initBlock() {
	}


	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */
	public int getHallNo() {
		return 0;
	}

	/**
	 * @return
	 */
	public synchronized static SortElement getInstance() {
		if (instance != null){
			return instance;
		}
		String[] cols = new String[INDEX_MAX_];
		for (int i = 0; i < cols.length; i++) {
			cols[i] = "";
		}
		instance = new CircleDmy(cols);
		return instance;
	}
	

	private static CircleDmy instance;

	/* (”ñ Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return false;
	}

	public String toString(){
		StringBuffer buf = new StringBuffer();
		return buf.toString();		
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle#getBlockSize(int, int)
	 */
	protected BSize getBlockSize(int row, int col) {
		return null;
	}

}
