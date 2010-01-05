/*
 * �쐬��: 2005/11/14
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;

/**
 * @author notanata
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class CircleWestWall extends AbstractWestCircle {
	/**
	 * @param cols
	 */
	public CircleWestWall(String[] cols) {
		super(cols);
	}

//	protected void initFace() {
//
//	}

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
		// �O��
		blockCol = 0;
		blockRow = 0;

		if (getMapY() >= 720 && getMapY() <= 760) {
			initWall2Block_BTSide(6, 7);
		} else if (getMapY() >= 0 && getMapY() <= 40) {
			initWall2Block_BTSide(1, 0);
		} else if (getMapY() >= 230 && getMapY() <= 270) {
			initWall2Block_BTSide(2, 3);
		} else if (getMapX() >= 0 && getMapX() <= 40) {
			initWall2Block_LRSide(0);
		} else if (getMapX() >= 220 && getMapX() <= 260) {
			initWall2Block_LRSide(7);
		} else if (getMapX() >= 730 && getMapX() <= 770) {
			initWall2Block_LRSide(22);
		} else if (getMapX() >= 940 && getMapX() <= 980) {
			initWall2Block_LRSide(BLOCK_COL_MAX + 1);
		}

	}

	/* (�� Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */

	private int hall;
	public int getHallNo() {
		if (hall == 0) {
			if (getBlock().equals("��")) {
				hall = 2;
			} else {
				hall = 1;
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

		x = offset(getMapX(), false, isReverse());
		return x;
	}

	private boolean isReverse() {
		return getHallNo() == 2;
	}

	/* (non-Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getY()
	 */
	public int getY() {
		if (y != Integer.MIN_VALUE) {
			return y;
		}

		y = offset(getMapY(), true, isReverse());
		return y;
	}

	/* (�� Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getFace()
	 */
	public int getFace() {
		int tmp_face = super.getFace();
		if (isReverse()) {
			tmp_face = uTurn(tmp_face);
		}
		return tmp_face;
	}

	protected static boolean isWall(String block) {
		if ("����".indexOf(block) >= 0) {
			return true;
		}
		return false;
	}

	/* (�� Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return true;
	}

}
