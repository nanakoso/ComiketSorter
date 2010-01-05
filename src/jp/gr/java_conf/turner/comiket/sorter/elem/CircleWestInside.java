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
public class CircleWestInside extends AbstractWestCircle {

	/**
	 * @param cols
	 */
	public CircleWestInside(String[] cols) {
		super(cols);
	}

	/**
	 * 
	 */
//	protected void initFace() {
////		face = getBSel().face;
//	}

	protected void initBlock() {

		blockRow = getBSel().blockRow;

		String block = getBlock();
		int colWk = getBlockColNo(block);
		blockCol = colWk;
	}

	private static final String[] bColTbl =
		{ "もめむみまほへふひはのねぬに", "るりらよゆや", "なとてつちたそせすしさこけく", "○○○○○○○○きかおえうい" };
	static int getBlockColNo(String block) {

		int colWk = -1;
		int group = -1;
		for (int i = 0; i < bColTbl.length; i++) {
			colWk = bColTbl[i].indexOf(block);
			if (colWk >= 0) {
				group = i;
				break;
			}
		}
		if (group >= 2) {
			colWk += bColTbl[0].length();
		}
		return colWk + 1;
	}

	private static int block2HallNo(String block) {
		for (int i = 0; i < bColTbl.length; i++) {
			int colWk = bColTbl[i].indexOf(block);
			if (colWk >= 0) {
				if (i < 2) {
					return 2;
				} else {
					return 1;
				}
			}
		}
		return -1;
	}

	private BSel bsel = null;
	private BSel getBSel() {

		if (bsel != null) {
			return bsel;
		}
		
		this.bsel = getBTSel(getNo(), getBlock());
		return bsel;
	}

	public static BSize[][] getWestBlocks() {
		BSize[][] matrix = new BSize[BLOCK_ROW_MAX + 1][];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new BSize[BLOCK_COL_MAX + 1];
		}

		for (char c = 'あ'; c <= 'ん'; c++) {
			BSel sel;
			String strC = String.valueOf(c);
			for (int no = 1;(sel = getBTSel(no, strC)) != null; no++) {
				int row = sel.blockRow;
				int col = getBlockColNo(strC);
				if (col > 0) {
					if (matrix[row][col] == null) {
						BSize blkSize = getBSize(row, strC);

						blkSize.bottom += MAP_UNIT;
						blkSize.right += MAP_UNIT;

						blkSize.top += QUOT_MINUS;
						blkSize.left += QUOT_MINUS;
						blkSize.bottom += QUOT_UNIT;
						blkSize.right += QUOT_UNIT;

						//オフセット分右にずらす。
						int offset = (col - 1) * (MAP_UNIT * 3);
						if (block2HallNo(strC) == 2) {
							offset += WEST2_OFFSET;
						} else {
							offset
								+= (WEST1_OFFSET
									- (BLOCK_COL_MAX / 2 * (MAP_UNIT * 3)));
						}
						blkSize.right += offset;
						blkSize.left += offset;

						matrix[row][col] = blkSize;
					}
				}
			}
		}

		return matrix;
	}

	private static BSize getBSize(int blockRow, String blockChar) {
		BSize bsel = null;
		for (int i = 0; i < faceTbl.length; i++) {
			if (blkTbl[i].indexOf(blockChar) >= 0) {
				if (faceTbl[i].length < blockRow) {
					return null;
				}
				bsel = (BSize) blkSizeTbl[i][blockRow - 1].clone();
				break;
			}
		}
		return bsel;
	}

	private static BSel getBTSel(int indexNo, String blockChar) {
		BSel bsel = null;
		for (int i = 0; i < faceTbl.length; i++) {
			if (blkTbl[i].indexOf(blockChar) >= 0) {
				if (faceTbl[i].length < indexNo) {
					return null;
				}
				bsel = faceTbl[i][indexNo - 1];
				break;
			}
		}
		return bsel;
	}

	private static class BSel {
		public BSel(int face, int row) {
			this.face = face;
			blockRow = row;
		}
		public int face;
		public int blockRow;
	};

	private static final String type1Blk = "ほまみむめも,くけこさしす";

	private static final BSize[] type1BSize =
		{ new BSize(60, 110), new BSize(150, 230), new BSize(270, 350)};

	private static final BSel[] type1 =
		{
			new BSel(TO_BOTTOM, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_RIGHT, 3),
			new BSel(TO_TOP, 3),
			new BSel(TO_BOTTOM, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_TOP, 2),
			new BSel(TO_BOTTOM, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_TOP, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3),
			new BSel(TO_LEFT, 3)};

	private static final String type2Blk = "ゆよらり,うえおか";
	private static final BSize[] type2BSize =
		{
			null,
			null,
			null,
			new BSize(400, 480),
			new BSize(520, 600),
			new BSize(640, 710)};

	private static final BSel[] type2 =
		{
			new BSel(TO_BOTTOM, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_TOP, 6),
			new BSel(TO_BOTTOM, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_TOP, 5),
			new BSel(TO_BOTTOM, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_TOP, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4)};

	private static final String type3Blk = "やる,いき";
	private static final BSize[] type3BSize =
		{
			null,
			null,
			null,
			new BSize(400, 480),
			new BSize(520, 600),
			new BSize(640, 700)};
	private static final BSel[] type3 =
		{
			new BSel(TO_BOTTOM, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_RIGHT, 6),
			new BSel(TO_TOP, 6),
			new BSel(TO_BOTTOM, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_RIGHT, 5),
			new BSel(TO_TOP, 5),
			new BSel(TO_BOTTOM, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_RIGHT, 4),
			new BSel(TO_TOP, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 5),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4),
			new BSel(TO_LEFT, 4)};

	private static final String type4Blk = "ふへ,せそ";
	private static final BSize[] type4BSize =
		{ new BSize(70, 110), new BSize(150, 200)};

	private static final BSel[] type4 =
		{
			new BSel(TO_BOTTOM, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_TOP, 2),
			new BSel(TO_BOTTOM, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_TOP, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2)};

	private static final String type5Blk = "ぬねのはひ,たちつてと";

	private static final BSize[] type5BSize =
		{ new BSize(60, 110), new BSize(150, 210)};

	private static final BSel[] type5 =
		{
			new BSel(TO_BOTTOM, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_TOP, 2),
			new BSel(TO_BOTTOM, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_TOP, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2)};

	private static final String type6Blk = "に,な";
	private static final BSize[] type6BSize =
		{ new BSize(70, 110), new BSize(150, 210)};

	private static final BSel[] type6 =
		{
			new BSel(TO_BOTTOM, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_RIGHT, 2),
			new BSel(TO_TOP, 2),
			new BSel(TO_BOTTOM, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_RIGHT, 1),
			new BSel(TO_TOP, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 1),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2),
			new BSel(TO_LEFT, 2)};

	private static final BSel[][] faceTbl =
		{ type1, type2, type3, type4, type5, type6 };

	private static final String[] blkTbl =
		{ type1Blk, type2Blk, type3Blk, type4Blk, type5Blk, type6Blk };

	private static final BSize[][] blkSizeTbl =
		{
			type1BSize,
			type2BSize,
			type3BSize,
			type4BSize,
			type5BSize,
			type6BSize };

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */
	private int hall = 0;
	public int getHallNo() {
		if (hall == 0) {
			String b = getBlock();
			if (b.length() > 0) {
				int c = getBlockColNo(b);
				if (c <= (BLOCK_COL_MAX/2)) {
					hall = 1;
				} else {
					hall = 2;
				}
			}
		}
		return hall;
	}

	/* (非 Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return false;
	}

}
