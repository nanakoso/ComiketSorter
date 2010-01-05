/*
 * �쐬��: 2005/11/22
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
public class CircleEastInside extends AbstractEastCircle {

	/**
	 * @param cols
	 */
	public CircleEastInside(String[] cols) {
		super(cols);
	}

	/* (�� Javadoc)
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

	/* (�� Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#getHallNo()
	 */
	public int getHallNo() {
		if ("�a�b�c�d�e�f�g�h�i�j�k".indexOf(getBlock()) >= 0) {
			return 1;
		} else if ("�l�m�n�o�p�q�r�s�t�u�v�w�x�y".indexOf(getBlock()) >= 0) {
			return 2;
		} else if ("�A�C�E�G�I�J�L�N�P�R�T".indexOf(getBlock()) >= 0) {
			return 3;
		} else if ("�X�Z�\�^�`�c�e�g�i�j�k".indexOf(getBlock()) >= 0) {
			return 4;
		} else if ("�l�m�n�p�q�s�t�v�w�y�z�|�}�~".indexOf(getBlock()) >= 0) {
			return 5;
		} else if ("����������������������".indexOf(getBlock()) >= 0) {
			return 6;
		}
		return 0;
	}

	/* (�� Javadoc)
	 * @see jp.gr.java_conf.turner.comiket.csv.Circle#isWallSide()
	 */
	public boolean isWallSide() {
		return false;
	}

	private static final String[] bColTbl =
		{
			"�T�R�P�N�L�J�I�G�E�C�A�y�x�w�v�u�t�s�r�q�p�o�n�m�l�k�j�i�h�g�f�e�d�c�b�a",
			"�����������������������~�}�|�z�y�w�v�t�s�q�p�n�m�l�k�j�i�g�e�c�`�^�\�Z�X" };
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

	private static final String type1Blk = "�a" + "�T" + "�X" + "��";
	private static final BSize[] type1 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET5, 140),
			new BSize(170, 220),
			new BSize(300, 350),
			new BSize(380, 440)};

	private static final String type2Blk =
		"�b�c�d�e�f�g�h�i�j�k"
			+ "�n�o�p�q�r�s�t�u�v�w"
			+ "�A�C�E�G�I�J�L�N�P�R"
			+ "�Z�\�^�`�c�e�g�i�j�k"
			+ "�n�p�q�s�t�v�w�y�z�|"
			+ "��������������������";

	private static final BSize[] type2 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET5, 140),
			new BSize(170, 240),
			new BSize(280, 350),
			new BSize(380, 440)};

	private static final String type3Blk = "�l�m" + "�x�y" + "�l�m" + "�}�~";
	private static BSize[] type3 =
		new BSize[] {
			null,
			new BSize(BLOCK_OFFSET7, 140),
			new BSize(170, 220),
			new BSize(300, 350),
			new BSize(380, 420)};

}
