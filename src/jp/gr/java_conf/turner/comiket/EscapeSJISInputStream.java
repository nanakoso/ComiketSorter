/*
 * �쐬��: 2005/12/15
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.gr.java_conf.turner.comiket;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author notanata
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class EscapeSJISInputStream extends FilterInputStream {

	/**
	 * @param in
	 */
	protected EscapeSJISInputStream(InputStream in) {
		super(in);
	}

	private boolean isHeading(int c) {
		return (c >= 0x81 && c <= 0x9f) || (c >= 0xe0 && c <= 0xef);
	}

	private int sjisToKu(int ch, int cl) {
		if (ch <= 0x9f) {
			if (cl < 0x9f)
				ch = (ch << 1) - 0xe1;
			else
				ch = (ch << 1) - 0xe0;
		} else {
			if (cl < 0x9f)
				ch = (ch << 1) - 0x161;
			else
				ch = (ch << 1) - 0x160;
		}
		return ch;
	}

	/* (�� Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int c = super.read();

		return c;
	}

	/* (�� Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return super.read(b, off, len);
	}

	/* (�� Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return super.skip(n);
	}

}
