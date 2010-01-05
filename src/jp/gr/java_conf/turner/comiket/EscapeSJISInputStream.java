/*
 * 作成日: 2005/12/15
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
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

	/* (非 Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int c = super.read();

		return c;
	}

	/* (非 Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return super.read(b, off, len);
	}

	/* (非 Javadoc)
	 * @see java.io.InputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return super.skip(n);
	}

}
