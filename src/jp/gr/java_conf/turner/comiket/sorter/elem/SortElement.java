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
public interface SortElement {
	final static SortElement DMY = CircleDmy.getInstance();

	int distance(SortElement e);

	int getX();
	int getY();
	int getHallNo();
	boolean isWallSide();
	String toShortString();

}
