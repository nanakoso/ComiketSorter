/*
 * 作成日: 2005/12/15
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class YenFixOutputStream extends FilterOutputStream {
	private static final int EMPTY = Integer.MIN_VALUE;
	private int b0 = EMPTY;
	private final boolean isFilename;
	/**
	 * @param out
	 */
	public YenFixOutputStream(OutputStream out) {
		this(out, false);
	}
	public YenFixOutputStream(OutputStream out, boolean isFilename) {
		super(out);
		this.isFilename = isFilename;
	}

	/* (非 Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		if ((b0 == '0' && b == 0x1f)
			|| (isFilename && b0 == 0x81 && b == 0x5f)) {
			b = '\\';
			b0 = EMPTY;
		}
		if (b0 != EMPTY) {
			super.write(b0);
		}

		b0 = b;
	}

	private boolean isHeading(int c) {
		return (c >= 0x81 && c <= 0x9f) || (c >= 0xe0 && c <= 0xef);
	}

	/* (非 Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		this.flush();
		super.close();
	}

	/* (非 Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		if (b0 != EMPTY) {
			super.write(b0);
			b0 = EMPTY;
		}
		super.flush();
	}

	public static void main(String[] args) throws IOException {
		InputStream is = new FileInputStream(args[0]);
		OutputStream os =
			new YenFixOutputStream(new FileOutputStream(args[1]), true);
		int c;
		while ((c = is.read()) != -1) {
			os.write(c);
		}
		is.close();
		os.close();
	}
}
