/*
 * 作成日: 2005/11/14
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;

import jp.gr.java_conf.turner.comiket.ComiketSorter;
import jp.gr.java_conf.turner.comiket.csv.Circle;

//import jp.gr.java_conf.turner.comiket.csv.CircleWestInside.BSize;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public abstract class AbstractCircle extends Circle implements SortElement {

	protected static class BSize implements Cloneable {
		public BSize(int t, int b) {
			top = t;
			bottom = b;
		}
		int top;
		int bottom;
		int left = 0;
		int right = 10;
		public Object clone() {
			BSize ret = new BSize(top, bottom);
			ret.left = left;
			ret.right = right;
			return ret;
		}
		public String toString() {
			return "[t:"
				+ top
				+ ",b:"
				+ bottom
				+ ",l:"
				+ left
				+ ",r:"
				+ right
				+ "]";
		}
	}

	public String toString() {
		String ret = super.toString();
		boolean dbg = ComiketSorter.getDebugOption();
		if (dbg) {
			ret
				+= ("," + getX() + "," + getY() + "," + blockRow + "," + blockCol);
		}
		return ret;
	}
	protected static final int X_MAGNI = 6;
	protected static final int H_MAGNI = 10;
//	protected static final int X_MAGNI = 1;
//	protected static final int H_MAGNI = 1;
	

	protected static final int TO_UNFIXED = -1;

	protected static final int TO_BOTTOM = 1;
	protected static final int TO_RIGHT = 2;
	protected static final int TO_TOP = 3;
	protected static final int TO_LEFT = 4;

	protected static final int MAP_UNIT = 10;
	protected static final int HALF_UNIT = MAP_UNIT / 2;
	protected static final int QUOT_UNIT = HALF_UNIT / 2;
	protected static final int QUOT_MINUS = QUOT_UNIT - HALF_UNIT;

	private int face = TO_UNFIXED;

	protected int blockRow = 0; //外周
	protected int blockCol = 0; //外周

	protected int x = Integer.MIN_VALUE;
	protected int y = Integer.MIN_VALUE;

	//	protected abstract void initFace();
	protected abstract void initBlock();
	protected abstract BSize getBlockSize(int row, int col);

	//	public abstract boolean isWallSide();

	/**
	 * @param cols
	 */
	public AbstractCircle(String[] cols) {
		super(cols);
		//		initFace();
		initBlock();
	}

	public static AbstractCircle getInstance(String[] cols) {
		AbstractCircle ret;
		String b = Circle.getBlock(cols);

		if (AbstractWestCircle.WALL_CHARS.indexOf(b) >= 0) {

			ret = new CircleWestWall(cols);
		} else if (AbstractEastCircle.WALL_CHARS.indexOf(b) >= 0) {

			ret = new CircleEastWall(cols);
		} else if (CircleWestInside.getBlockColNo(b) >= 1) {

			ret = new CircleWestInside(cols);
		} else {

			ret = new CircleEastInside(cols);
		}
		return ret;
	}

	public int getX() {
		if (x != Integer.MIN_VALUE) {
			return x;
		}

		x = offset(getMapX(), false, false);
		return x;
	}

	public int getY() {
		if (y != Integer.MIN_VALUE) {
			return y;
		}

		y = offset(getMapY(), true, false);
		return y;
	}

	public int getFace() {
		if (face == TO_UNFIXED) {
			face = super.getFace();
		}
		return face;
	}

	protected int offset(int arg_x, boolean yFlg, boolean reverse) {

		arg_x += HALF_UNIT;

		//90度回転
		int tmp_face = getFace();
		if (yFlg) {
			tmp_face = leftTurn(tmp_face); //1->2 , 2->3, 3->4, 4->1
		}
		String plusSide;
		if (!reverse) {
			plusSide = "a";
		} else {
			plusSide = "b";
		}

		switch (tmp_face) {
			case TO_RIGHT :
				arg_x += HALF_UNIT + QUOT_UNIT;
				break;
			case TO_LEFT :
				arg_x += -HALF_UNIT + QUOT_MINUS;
				break;
			case TO_TOP :
				if (getSide().equals(plusSide)) {
					arg_x += QUOT_UNIT;
				} else {
					arg_x += QUOT_MINUS;
				}
				break;
			case TO_BOTTOM :
				if (!getSide().equals(plusSide)) {
					arg_x += QUOT_UNIT;
				} else {
					arg_x += QUOT_MINUS;
				}
				break;
		}

		return arg_x;
	}

	protected int uTurn(int tmp_face) {
		return ((tmp_face - 1) ^ 0x02) + 1;
	}

	protected int leftTurn(int tmp_face) {
		return (tmp_face & 0x03) + 1;
	}

	/**
	 * @return お誕生席だったらtrue
	 */
	public boolean isTopOrBottom() {
		return (getFace() == TO_TOP || getFace() == TO_BOTTOM);
	}

	public int distance(SortElement destarg) {
		
		
		int ret = 0;

		AbstractCircle dest = (AbstractCircle)destarg;
		if (this == dest)
			return 0;

		//ダミーエントリーのときは位置に関わらず常に距離０
		if (this == DMY || dest == DMY) {
			return 0;
		}

		//基本距離を計算する。
		ret =
			Math.abs(this.getX() - dest.getX()) * X_MAGNI
				+ Math.abs(this.getY() - dest.getY());

		//始点、終点用ダミーエントリーのときは迂回路を計算しない。
		if (this instanceof CircleDmy || dest instanceof CircleDmy) {
			return ret;
		}

		//一方が島と面していない外周とは迂回の必要がない。
		if ((blockRow == 0 && blockCol == 0)
			|| (dest.getBlockRow() == 0 && dest.getBlockCol() == 0)) {
			return ret;
		}

		//島の行と列両方が違うときは迂回の必要がない。
		if (blockRow != dest.getBlockRow() && blockCol != dest.getBlockCol()) {
			return ret;
		}

		//島中同士でブロック行が違うときは迂回の必要がない。
		if (!isTopOrBottom()
			&& !dest.isTopOrBottom()
			&& blockRow != dest.getBlockRow()) {
			return ret;
		}

		//同じ島かどうか
		if (blockRow == dest.getBlockRow() && blockCol == dest.getBlockCol()) {
			if (isTopOrBottom() != dest.isTopOrBottom()) {
				//同じ島で島中とお誕生席だったら迂回の必要がない。
				return ret;
			} else {
				if (isTopOrBottom()) {
					//縦方向に対する迂回路距離を追加
					ret += calcVerticalDetour(dest);
				} else {
					//横方向に対する迂回路距離を追加。
					ret += calcHorizontalDetour(dest) * H_MAGNI;
				}
			}
		} else {
			if (blockRow == dest.getBlockRow()) {
				//横方向に対する迂回路距離を追加。
				ret += calcHorizontalDetour(dest) * H_MAGNI;
			} else {
				//縦方向に対する迂回路距離を追加
				ret += calcVerticalDetour(dest);

			}
		}
		return ret;
	}

	/**
	 * @param dest
	 * @return
	 */
	//	private static boolean isTopOrBottom(SortElement dest) {
	//		return (dest.getFace() == TO_TOP || dest.getFace() == TO_BOTTOM);
	//	}

	private int calcHorizontalDetour(SortElement destarg) {
		AbstractCircle dest = (AbstractCircle)destarg;

		AbstractCircle st, ed;
		if (this.blockCol < dest.getBlockCol()) {
			st = this;
			ed = dest;
		} else if (this.blockCol > dest.getBlockCol()) {

			ed = this;
			st = dest;
		} else {
			if (this.getFace() == dest.getFace()) {
				return 0;
			}
			if (this.getFace() == TO_LEFT) {
				st = this;
				ed = dest;
			} else {
				ed = this;
				st = dest;
			}
		}

		int row = st.blockRow;
		int stCol = st.blockCol;
		int edCol = ed.blockCol;
		int stY = st.getY();
		int edY = ed.getY();

		if (st.getFace() != TO_LEFT)
			stCol++;
		if (ed.getFace() == TO_RIGHT)
			edCol++;

		int top0, bottom0;
		int top = top0 = Math.min(stY, edY);
		int bottom = bottom0 = Math.max(stY, edY);

		for (int col = stCol; col < edCol; col++) {
			if (isLastEast()) {
				if (col == 1 || col == AbstractEastCircle.BLOCK_COL_MAX) {
					continue;
				}
			}
			BSize bl = getBlockSize(row, col);
			if (bl != null) {
				if (bl.top < top)
					top = bl.top;
				if (bl.bottom > bottom)
					bottom = bl.bottom;
			}
		}

		int ret = Math.min(top0 - top, bottom - bottom0) * 2;

		return ret;
	}

	/**
	 * @return
	 */
	private int isLastEastFlg = -1;
	private synchronized boolean isLastEast() {
		if (isLastEastFlg == -1) {
			String lwd = ComiketSorter.getLastWeekDay();
			if (getWeekDay().equals(lwd)
				&& this instanceof AbstractEastCircle) {
				isLastEastFlg = 1;
			} else {
				isLastEastFlg = 0;
			}
		}
		return (isLastEastFlg == 1);
	}

	private int calcVerticalDetour(SortElement destarg) {
		AbstractCircle dest = (AbstractCircle)destarg;

		AbstractCircle st, ed;
		if (this.blockRow < dest.blockRow) {
			st = this;
			ed = dest;
		} else if (this.blockRow > dest.blockRow) {
			ed = this;
			st = dest;
		} else {
			if (this.getFace() == dest.getFace()) {
				return 0;
			}
			if (this.getFace() == TO_TOP) {
				st = this;
				ed = dest;
			} else {
				ed = this;
				st = dest;
			}
		}

		int col = st.blockCol;

		int stRow = st.blockRow;
		int edRow = ed.blockRow;
		int stX = st.getX();
		int edX = ed.getX();

		if (st.getFace() != TO_TOP)
			stRow++;
		if (ed.getFace() == TO_BOTTOM)
			edRow++;

		int left0, right0;
		int left = left0 = Math.min(stX, edX);
		int right = right0 = Math.max(stX, edX);

		for (int row = stRow; row < edRow; row++) {
			BSize bl = getBlockSize(row, col);
			if (bl != null) {
				if (bl.left < left)
					left = bl.left;
				if (bl.right > right)
					right = bl.right;
			}
		}

		int ret = Math.min(left0 - left, right - right0) * 2;

		return ret;
	}

	/**
	 * @return
	 */
	public int getBlockCol() {
		return blockCol;
	}

	/**
	 * @return
	 */
	public int getBlockRow() {
		return blockRow;
	}

}
