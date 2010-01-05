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

	static final String WALL_CHARS = "�`�V";

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
