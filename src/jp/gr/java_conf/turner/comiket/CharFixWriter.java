/*
 * �쐬��: 2005/11/08
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.gr.java_conf.turner.comiket;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author notanata
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class CharFixWriter extends FilterWriter {

	/**
	 * @param out
	 */
	public CharFixWriter(Writer out) {
		super(out);
	}

	private char[] cbufWk = new char[1024];
	public void write(char[] cbuf, int off, int len) throws IOException {
		while (cbufWk.length < len) {
			cbufWk = new char[cbufWk.length * 2];
		}
		for (int i = 0; i < len; i++) {
			cbufWk[i] = (char)fixChar((char)cbuf[off + i]);
		}
		super.write(cbufWk, 0, len);
	}
	public static String fixString(String arg) {
		if (arg == null)
			return null;
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < arg.length(); i++){
			buf.append((char)fixChar(arg.charAt(i),true));
		}
		return buf.toString();
	}

	public void write(int c) throws IOException {
		super.write(fixChar(c));
	}

	public void write(String str, int off, int len) throws IOException {
		this.write(str.toCharArray(), off, len);
	}

	/**
	 * Windows-31J(Shift_JIS)�Ƃ̕ϊ��̍ۖ��ɂȂ镶���R�[�h��u��������B
	 */
	private static int fixChar(int c) {
		return fixChar(c,false);
	}
	private static int fixChar(int c,boolean yenSign) {
		//TODO'\uFFA5'�͂Ȃ��� 30,1f�̂Ȃ�тɂȂ��Ă��܂��炵���B
		//���Ƃł���ɑΉ��B
		if (yenSign && c == '\uFFA5') {
			c = '\\'; //�~�}�[�N(\)�̕��������΍�H
		}
		if (c == '\uFF5E') {
			c = '\u301C'; //WaveDash(�`)�̕��������΍�B
		}
		if (c == '\u2225') {
			c = '\u2016'; //DOUBLE VERTICAL LINE(�a)�̕��������΍�B
		}
		if (c == '\uFF0D') {
			c = '\u2212'; //FULLWIDTH HYPHEN-MINUS(�|)�̕��������΍�B
		}
		if (c == '\u007E') {
			c = '\u203E'; //OVERLINE(TILDE)(~)�̕��������΍�B
		}
		if (c == '\uFFFD') {
			c = '?'; //��̕����͂Ƃ肠�����H�ɂ���B
		}
		return c;
	}

}
