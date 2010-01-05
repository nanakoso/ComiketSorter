/*
 * 作成日: 2005/11/22
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public abstract class AbstractEastCircle extends AbstractCircle {

	protected static final int BLOCK_OFFSET1 = 30;
	protected static final int BLOCK_OFFSET2 = 40;
	protected static final int BLOCK_OFFSET3 = 50;
	protected static final int BLOCK_OFFSET4 = 60;
	protected static final int BLOCK_OFFSET5 = 80;
	protected static final int BLOCK_OFFSET6 = 90;
	protected static final int BLOCK_OFFSET7 = 100;
	protected static final int BLOCK_OFFSET8 = 110;

	static final int BLOCK_COL_MAX = 36;
	static final int BLOCK_ROW_MAX = 4;

	static final String WALL_CHARS = "Ａシ";

	/**
	 * @param row
	 * @param i
	 */
	private static BSize blockMatrix[][] = null;
	protected BSize getBlockSize(int row, int col) {
		if(blockMatrix == null){
			blockMatrix = CircleEastInside.getEastBlocks();
		}
		if(row <= BLOCK_ROW_MAX && col <= BLOCK_COL_MAX){
			return blockMatrix[row][col];
		}else{
			return null;
		}
	}


	/**
	 * @param cols
	 */
	public AbstractEastCircle(String[] cols) {
		super(cols);
	}
}
