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
public abstract class AbstractWestCircle extends AbstractCircle {

	static final int WEST1_OFFSET = 540;
	static final int WEST2_OFFSET = 50;

	static final int BLOCK_COL_MAX = 28;
	static final int BLOCK_ROW_MAX = 6;

	static final String WALL_CHARS = "����";

	/**
	 * @param row
	 * @param i
	 */
	private static BSize blockMatrix[][] = null;
	protected BSize getBlockSize(int row, int col) {
		if (blockMatrix == null) {
			blockMatrix = CircleWestInside.getWestBlocks();
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
	public AbstractWestCircle(String[] cols) {
		super(cols);
	}
}
