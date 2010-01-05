/*
 * 作成日: 2005/11/08
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
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
	 * Windows-31J(Shift_JIS)との変換の際問題になる文字コードを置き換える。
	 */
	private static int fixChar(int c) {
		return fixChar(c,false);
	}
	private static int fixChar(int c,boolean yenSign) {
		//TODO'\uFFA5'はなぜか 30,1fのならびになってしまうらしい。
		//あとでこれに対応。
		if (yenSign && c == '\uFFA5') {
			c = '\\'; //円マーク(\)の文字化け対策？
		}
		if (c == '\uFF5E') {
			c = '\u301C'; //WaveDash(～)の文字化け対策。
		}
		if (c == '\u2225') {
			c = '\u2016'; //DOUBLE VERTICAL LINE(∥)の文字化け対策。
		}
		if (c == '\uFF0D') {
			c = '\u2212'; //FULLWIDTH HYPHEN-MINUS(－)の文字化け対策。
		}
		if (c == '\u007E') {
			c = '\u203E'; //OVERLINE(TILDE)(~)の文字化け対策。
		}
		if (c == '\uFFFD') {
			c = '?'; //謎の文字はとりあえず？にする。
		}
		return c;
	}

}
