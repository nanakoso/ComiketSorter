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
public class CircleEastInside extends AbstractEastCircle {

	/**
	 * @param cols
	 */
	public CircleEastInside(String[] cols) {
		super(cols);
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle#initBlock()
	 */
	protected void initBlock() {

		String block = getBlock();
		int colWk = getBlockColNo(block);
		this.blockCol = colWk;

		int y = getMapY();
		for (int row = 1; row <= BLOCK_ROW_MAX; row++) {
			BSize size = getBlockSize(row, colWk);
			if (y >= size.top && y <= size.bottom) {
				this.blockRow = row;
				break;
			}
		}
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */
	public int getHallNo() {
		if ("ＢＣＤＥＦＧＨＩＪＫＬ".indexOf(getBlock()) >= 0) {
			return 1;
		} else if ("ＭＮＯＰＱＲＳＴＵＶＷＸＹＺ".indexOf(getBlock()) >= 0) {
			return 2;
		} else if ("アイウエオカキクケコサ".indexOf(getBlock()) >= 0) {
			return 3;
		} else if ("スセソタチツテトナニヌ".indexOf(getBlock()) >= 0) {
			return 4;
		} else if ("ネノハパヒピフプヘペホポマミ".indexOf(getBlock()) >= 0) {
			return 5;
		} else if ("ムメモヤユヨラリルレロ".indexOf(getBlock()) >= 0) {
			return 6;
		}
		return 0;
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return false;
	}

	private static final String[] bColTbl =
		{
			"サコケクキカオエウイアＺＹＸＷＶＵＴＳＲＱＰＯＮＭＬＫＪＩＨＧＦＥＤＣＢ",
			"ロレルリラヨユヤモメムミマポホペヘプフピヒパハノネヌニナトテツチタソセス" };
	private static int getBlockColNo(String block) {

		int wkI = -1;
		for (int i = 0; i < bColTbl.length; i++) {
			wkI = bColTbl[i].indexOf(block);
			if (wkI >= 0)
				break;
		}
		if (wkI >= 0) {
			return (wkI + 1);
		}
		return 0;
	}

	/**
	 * @return
	 */
	public static BSize[][] getEastBlocks() {
		BSize[][] matrix = new BSize[BLOCK_ROW_MAX + 1][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new BSize[BLOCK_COL_MAX + 1];
		}

		for (int row = 1; row <= BLOCK_ROW_MAX; row++) {
			for (int col = 1; col <= BLOCK_COL_MAX; col++) {
				String b = bColTbl[0].substring(col - 1, col);
				int shift;
				if (type1Blk.indexOf(b) >= 0) {
					matrix[row][col] = (BSize)type1[row].clone();
				} else if (type2Blk.indexOf(b) >= 0) {
					matrix[row][col] = (BSize)type2[row].clone();
				} else if (type3Blk.indexOf(b) >= 0) {
					matrix[row][col] = (BSize)type3[row].clone();
				}
				if (col <= 4) {
					shift = BLOCK_OFFSET1 + col * 30;
				} else if (col <= 11) {
					shift = BLOCK_OFFSET2 + col * 30;
				} else if (col == 12) {
					shift = BLOCK_OFFSET3 + col * 30;
				} else if (col <= 17) {
					shift = BLOCK_OFFSET4 + col * 30;
				} else if (col <= 24) {
					shift = BLOCK_OFFSET5 + col * 30;
				} else if (col == 25) {
					shift = BLOCK_OFFSET6 + col * 30;
				} else if (col <= 29) {
					shift = BLOCK_OFFSET7 + col * 30;
				} else {
					shift = BLOCK_OFFSET8 + col * 30;
				}
				matrix[row][col].left += shift;
				matrix[row][col].right += shift;
			}
		}

		return matrix;
	}

	private static final String type1Blk = "Ｂ" + "サ" + "ス" + "ロ";
	private static final BSize[] type1 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET5, 140),
			new BSize(170, 220),
			new BSize(300, 350),
			new BSize(380, 440)};

	private static final String type2Blk =
		"ＣＤＥＦＧＨＩＪＫＬ"
			+ "ＯＰＱＲＳＴＵＶＷＸ"
			+ "アイウエオカキクケコ"
			+ "セソタチツテトナニヌ"
			+ "ハパヒピフプヘペホポ"
			+ "ムメモヤユヨラリルレ";

	private static final BSize[] type2 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET5, 140),
			new BSize(170, 240),
			new BSize(280, 350),
			new BSize(380, 440)};

	private static final String type3Blk = "ＭＮ" + "ＹＺ" + "ネノ" + "マミ";
	private static BSize[] type3 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET7, 140),
			new BSize(170, 220),
			new BSize(300, 350),
			new BSize(380, 420)};

}
