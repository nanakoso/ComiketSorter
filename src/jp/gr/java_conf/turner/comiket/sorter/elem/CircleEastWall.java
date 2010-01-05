/*
 * 作成日: 2005/11/14
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
public class CircleEastWall extends AbstractEastCircle {
	/**
	 * @param cols
	 */
	public CircleEastWall(String[] cols) {
		super(cols);
	}

	private void initWall2Block_BTSide(final int row, final int setRow) {
		for (int col = 1; col <= BLOCK_ROW_MAX; col++) {
			BSize bSize = getBlockSize(row, col);
			if (bSize != null && bSize.left < getX() && bSize.right > getX()) {
				this.blockRow = setRow;
				this.blockCol = col;
				break;
			}
		}
	}

	private void initWall2Block_LRSide(final int setCol) {

		for (int row = 1; row <= BLOCK_ROW_MAX; row++) {
			int[] t_b = getTopAndBottom(row);
			int top = t_b[0];
			int bottom = t_b[1];
			if (top < getY() && bottom > getY()) {
				this.blockRow = row;
				this.blockCol = setCol;
				break;
			}
		}
	}

	private int[] getTopAndBottom(final int row) {
		int top = 1000;
		int bottom = -1;
		for (int col = 1; col <= BLOCK_COL_MAX; col++) {
			BSize bSize = getBlockSize(row, col);
			if (bSize != null) {
				if (top > bSize.top) {
					top = bSize.top;
				}
				if (bottom < bSize.bottom) {
					bottom = bSize.bottom;
				}
			}
		}
		return new int[] { top, bottom };
	}

	protected void initBlock() {
		// 外周
		blockCol = 0;
		blockRow = 0;

		if (getFace() == TO_LEFT) {
			initWall2Block_LRSide(BLOCK_COL_MAX + 1);
		} else if (getFace() == TO_RIGHT) {
			initWall2Block_LRSide(0);
		} else if (getFace() == TO_BOTTOM) {
			initWall2Block_BTSide(1, 0);
		}

	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */

	private int hall;
	public int getHallNo() {
		if (hall != 0) {
			return hall;
		}

		if (hall == 0) {
			int no = getNo();
			if (getBlock().equals("Ａ")) {
				if (no < 37) {
					hall = 1;
				} else if (no < 53) {
					hall = 2;
				} else {
					hall = 3;
				}
			} else {
				if (no < 37) {
					hall = 6;
				} else if (no < 53) {
					hall = 5;
				} else {
					hall = 4;
				}
			}
		}
		return hall;
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getX()
	 */
	public int getX() {
		if (x != Integer.MIN_VALUE) {
			return x;
		}

		x = offset(getMapX(), false, true);
		return x;
	}

	private boolean isReverse() {
		return true;
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getY()
	 */
	public int getY() {
		if (y != Integer.MIN_VALUE) {
			return y;
		}

		y = offset(getMapY(), true, true);
		return y;
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getFace()
	 */
	public int getFace() {
		int tmp_face = super.getFace();
		tmp_face = uTurn(tmp_face);
		return tmp_face;
	}

	protected static boolean isWall(String block) {
		if ("Ａシ".indexOf(block) >= 0) {
			return true;
		}
		return false;
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return true;
	}

}
