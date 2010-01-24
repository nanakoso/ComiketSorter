/*
 * Created on 2005/08/28
 *
 * コミケサークル巡回ソーター
 * 
 */
package jp.gr.java_conf.turner.comiket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import jp.gr.java_conf.turner.comiket.csv.Circle;
import jp.gr.java_conf.turner.comiket.csv.Color;
import jp.gr.java_conf.turner.comiket.csv.GenericCSVLine;
import jp.gr.java_conf.turner.comiket.csv.Header;
import jp.gr.java_conf.turner.comiket.csv.UnKnown;
import jp.gr.java_conf.turner.comiket.sorter.AbstractSorter;
import jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle;
import jp.gr.java_conf.turner.comiket.sorter.elem.SortElement;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author TURNER
 * ComiketSorter
 *
 * コミケサークル順路ソーター
 * 
 */
public class ComiketSorter {

	private static final int ERRCODE_NOERR = 0;
	private static final int ERRCODE_ARGERR = -1;
	private static final int ERRCODE_INOUTEQ = -2;
	private static final int ERRCODE_FILEIN = -3;
	private static final int ERRCODE_FILEOUT = -4;

	private static CommandLine opts; //引数オプション
	private static final String DEBUG_OPTION_STR = "sorter.debug";
	private static final String WIN_CRLF = "\r\n";
	public static final String WIN_SJIS;
	static {
		String vender = System.getProperty("java.vendor");
		if (vender.startsWith("Sun Microsystems")) {
			WIN_SJIS = "Windows-31J";
		} else {
			System.setOut(new PrintStream(new YenFixOutputStream(System.out,true)));
			WIN_SJIS = "SJIS";
		}
	}

	private static String[] starts; //始点オプション
	private static String[] ends; //終点オプション

	private Header header = null;
	private List colorList = new ArrayList();
	private List circleList = new ArrayList();
	private List unKnownList = new ArrayList();

	public static String getOpt(String key) {
		return opts.getOptionValue(key);
	}

	public static boolean hasOption(String key) {
		return opts.hasOption(key);
	}

	public static String getLastWeekDay() {
		String l = getOpt("l");
		boolean found = false;
		for (int i = 0; i < weekDays.length; i++) {
			if (weekDays[i].equals(l) || weekDaysA[i].equalsIgnoreCase(l)) {
				return weekDays[i];
			}
		}
		return null;
	}

	public static void main(String[] cmdline) throws IOException {

		opts = parseOpt(cmdline);
		if (opts == null) {
			System.err.println("起動オプションが正しくありません。");
			System.exit(ERRCODE_ARGERR);
		}
		//解析

		String vendor = System.getProperty("java.vendor", "?");
		if (getDebugOption()) {
			System.out.println("Java vendor:" + vendor);
			System.out.println(
				"Java version:" + System.getProperty("java.version", "?"));
		}

		String inFile, outFile;

		String[] args = opts.getArgs();
		if (!WIN_SJIS.equals("SJIS")) {
			inFile = args[0];
			outFile = args[1];
		} else {
			inFile = CharFixWriter.fixString(args[0]);
			outFile = CharFixWriter.fixString(args[1]);
		}

		if (inFile.equalsIgnoreCase(outFile)) {
			System.err.println(
				"入力ファイル(" + inFile + ")と出力ファイル(" + outFile + ")が同じファイル名です。");
			System.exit(ERRCODE_INOUTEQ);
		}

		System.out.println("infile:" + inFile);
		System.out.println("outfile:" + outFile);

		ComiketSorter sorter = new ComiketSorter();
		try {
			sorter.readLines(inFile);
		} catch (FileNotFoundException e) {
			System.err.println("入力ファイル(" + inFile + ")が開けませんでした");
			System.exit(ERRCODE_FILEIN);
		}

		try {
			sorter.sort();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(ERRCODE_ARGERR);
		}

		try {
			sorter.writeCSV(outFile);
		} catch (IOException e) {
			System.err.println("出力ファイル(" + outFile + ")に書き込めませんでした");
			System.exit(ERRCODE_FILEOUT);
		}

		System.exit(ERRCODE_NOERR);
	}

	/**
	 * @param args
	 */
	private static CommandLine parseOpt(String[] args) {

		//Optionインスタンスを生成する。
		Options options = new Options();

		//定義を追加する。
		OptionBuilder.hasArg(true);
		OptionBuilder.isRequired(true);
		OptionBuilder.withArgName("最終日の曜日");
		OptionBuilder.withDescription("例: -l日 または -lsun");
		OptionBuilder.withLongOpt("lastweekday");
		Option lastWday = OptionBuilder.create("l");

		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("ルート開始位置");
		OptionBuilder.withDescription("例: -s 金Ａ11a,土B05b");
		OptionBuilder.withLongOpt("start");
		Option start = OptionBuilder.create("s");

		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("ルート終了位置");
		OptionBuilder.withDescription("例: -e 日ケ60b,fry[KO]12a ");
		OptionBuilder.withLongOpt("end");
		Option end = OptionBuilder.create("e");

		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("簡易ソート(高速)");
		OptionBuilder.withLongOpt("rapid");
		Option rapid = OptionBuilder.create("r");

		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("マーク色番号");
		OptionBuilder.withLongOpt("markcolor");
		Option color = OptionBuilder.create("c");

		options.addOption(lastWday);
		options.addOption(start);
		options.addOption(end);
		options.addOption(rapid);
		options.addOption(color);

		final String USAGE_STRING =
			"cmsort -l<最終日の曜日> [options] infile.csv outfile.csv\n"
				+ "最終日オプションは必須です。\n"
				+ "ブロックコードにはローマ字が使えます。\n"
				+ "([大文字]=カタカナ [小文字]=ひらがな)\n"
				+ "開始位置と終了位置はカンマ区切りで複数指定できます。\n"
				+ "(曜日、フロア毎に１つまで)\n";

		//パーサーを生成する。
		CommandLineParser parser = new PosixParser();

		//解析
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			//必須オプションが設定されていないので、HELPを出して終了。
			HelpFormatter help = new HelpFormatter();
			help.printHelp(USAGE_STRING, options, false);
			return null;
		}

		if (cmd.getArgs().length < 2) {
			//引数が足りないので、HELPを出して終了。
			HelpFormatter help = new HelpFormatter();
			help.printHelp(USAGE_STRING, options, false);
			return null;
		}
		opts = cmd;

		String lwd = getLastWeekDay();
		if (lwd == null) {
			//曜日がおかしいので、HELPを出して終了。
			HelpFormatter help = new HelpFormatter();
			help.printHelp(USAGE_STRING, options, false);
			return null;
		}

		String st = cmd.getOptionValue('s');
		String ed = cmd.getOptionValue('e');
		try {
			ComiketSorter.starts = makeCircle(st);
			ComiketSorter.ends = makeCircle(ed);
		} catch (Exception e2) {
			System.out.println(e2.getMessage());
			HelpFormatter help = new HelpFormatter();
			help.printHelp(USAGE_STRING, options, false);
			return null;
		}

		for (int i = 0; i < starts.length; i++) {
			System.out.println(starts[i]);
		}
		for (int i = 0; i < ends.length; i++) {
			System.out.println(ends[i]);
		}

		return cmd;
	}

	/**
	 * @param starts
	 */
	private static String[] makeCircle(String s) throws Exception {
		String[] ret = new String[0];
		if (s == null) {
			return ret;
		}
		StringTokenizer t = new StringTokenizer(s, ",");
		ArrayList a = new ArrayList();
		while (t.hasMoreElements()) {
			String org = t.nextToken().trim();
			String wk = org;
			wk = replaceWeekDays(wk);
			wk = replaceBlockCharRoma(wk);
			int num;
			try {
				if (wk.length() == 4) {
					num = Integer.parseInt(wk.substring(2, 3));
					wk =
						wk.substring(0, 2)
							+ "0"
							+ num
							+ wk.substring(3).toLowerCase();
				} else if (wk.length() == 5) {
					num = Integer.parseInt(wk.substring(2, 4));
					wk =
						wk.substring(0, 2)
							+ num
							+ wk.substring(4).toLowerCase();
				} else {
					throw new Exception();
				}
				if ("ab".indexOf(wk.substring(4, 5)) == -1) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception("サークルオプションフォーマットエラー:" + org);
			}
			a.add(wk);
		}
		ret = (String[]) a.toArray(new String[0]);
		return ret;
	}

	/**
	 * @param string
	 */
	private static String replaceWeekDays(String s) {
		for (int i = 0; i < weekDaysA.length; i++) {
			if (s.toUpperCase().startsWith(weekDaysA[i])) {
				s = s.substring(weekDaysA[i].length());
				s = weekDays[i] + s;
				break;
			}
		}
		return s;
	}

	/**
	 * @param string
	 */
	private static String replaceBlockCharRoma(String s) {
		int i0, i1;
		i0 = s.indexOf('[');
		if (i0 >= 0) {
			i1 = s.indexOf(']', i0);
			if (i1 >= 0) {
				String roma = s.substring(i0 + 1, i1);
				String replace = null;
				replace = romaToChar(roma);
				if (replace != null) {
					s = s.substring(0, i0) + replace + s.substring(i1 + 1);
				}

			}
		} else if (s.length() >= 2) {
			i0 = 1;
			i1 = 2;
			String replace = hanToZen(s.substring(i0, i1));
			if (replace != null) {
				s = s.substring(0, i0) + replace + s.substring(i1);
			}
		}
		return s;
	}
	private static String romaToChar(String roma) {
		String replace = null;
		for (int i = 0; i < blockCharRoma.length; i++) {
			if (roma.equals(blockCharRoma[i])) {
				replace = blockChars[i];
				break;
			}
		}
		return replace;
	}

	private static String hanToZen(String roma) {
		String replace = null;
		for (int i = 0; i < hanAlpha.length; i++) {
			if (roma.equals(hanAlpha[i])) {
				replace = zenAlpha[i];
				break;
			}
		}
		return replace;
	}

	/**
	 * @param string
	 * @return
	 */
	//	private static String fixFilename(String arg) {
	//		String path = CharFixWriter.fixString(arg);
	//		String p = "\\";
	//		StringTokenizer tokenizer = new StringTokenizer(path, p, true);
	//
	//		StringBuffer buf = new StringBuffer();
	//		while (tokenizer.hasMoreTokens()) {
	//			String token = tokenizer.nextToken();
	//			if (token.startsWith(p)) {
	//				buf.append(p);
	//				token = token.substring(1);
	//				buf.append(fixDir(token));
	//			} else {
	//				buf.append(fixDir(token));
	//			}
	//		}
	//		return buf.toString();
	//	}

	/**
	 * @param string
	 * @return
	 */
	//	private static String fixDir(String string) {
	//		try {
	//			byte[] bytes = string.getBytes(WIN_SJIS);
	//			StringBuffer buf = new StringBuffer();
	//			for (int i = 0; i < bytes.length; i++) {
	//				buf.append((char)bytes[i]);
	//			}
	//			return buf.toString();
	//		} catch (UnsupportedEncodingException e) {
	//			return string;
	//		}
	//
	//	}

	public static boolean getDebugOption() {
		String debugOpt = System.getProperty(DEBUG_OPTION_STR, "off");
		boolean dbg =
			debugOpt.equalsIgnoreCase("true")
				|| debugOpt.equalsIgnoreCase("on");
		return dbg;
	}

	public interface SelectItem {
		boolean isSelected(Circle c);
	}

	public class SelectWeekDay implements SelectItem {
		String wday;
		public SelectWeekDay(String wday) {
			this.wday = wday;
		}
		public boolean isSelected(Circle c) {
			return c.getWeekDay().equals(wday);
		}
	}

	public class SelectCodes implements SelectItem {
		final String[] codes;
		public SelectCodes(String[] codes) {
			this.codes = codes;
		}
		/* (non-Javadoc)
		 * @see jp.gr.java_conf.turner.comiket.ComiketSorter.SelectItem#isSelected(jp.gr.java_conf.turner.comiket.csv.Circle)
		 */
		public boolean isSelected(Circle c) {
			if (codes == null || codes.length == 0) {
				return false;
			}
			for (int i = 0; i < codes.length; i++) {
				String wk = codes[i];

				String day = wk.substring(0, 1);
				String blk = wk.substring(1, 2);
				int num = Integer.parseInt(wk.substring(2, 4));
				String side = wk.substring(4, 5);

				if (!c.getWeekDay().equals(day))
					continue;
				if (!c.getBlock().equals(blk))
					continue;
				if (c.getNo() != num)
					continue;
				if (!c.getSide().equals(side))
					continue;

				return true;
			}
			return false;
		}

	}

	public class SelectColor implements SelectItem {
		String color;
		public SelectColor(String color) {
			this.color = color;
			if (this.color != null) {
				this.color = this.color.trim();
			}
		}
		public boolean isSelected(Circle c) {
			if (color == null && color.length() == 0) {
				return false;
			}
			return (color.indexOf(c.getColor()) >= 0);
		}
	}

	static final String[] weekDays = { "月", "火", "水", "木", "金", "土", "日" };
	static final String[] weekDaysA =
		{ "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };

	static final String[] blockChars;
	static final String[] blockCharRoma;

	static final String[] hanAlpha;
	static final String[] zenAlpha;

	static {
		ArrayList zen = new ArrayList();
		ArrayList han = new ArrayList();
		for (char c = 'Ａ', a = 'A'; c <= 'Ｚ'; c++, a++) {
			zen.add(Character.toString(c));
			han.add(Character.toString(a));
		}
		for (char c = 'Ａ', a = 'a'; c <= 'Ｚ'; c++, a++) {
			zen.add(Character.toString(c));
			han.add(Character.toString(a));
		}
		zenAlpha = (String[]) zen.toArray(new String[0]);
		hanAlpha = (String[]) han.toArray(new String[0]);

		ArrayList chars = new ArrayList();
		ArrayList romas = new ArrayList();
		String roma[];
		char kana[];
		roma = new String[] { "A", "I", "U", "E", "O" };
		kana = new char[] { 'ア', 'イ', 'ウ', 'エ', 'オ' };
		addRomaChar(chars, romas, kana, roma);
		roma = new String[] { "KA", "KI", "KU", "KE", "KO" };
		kana = new char[] { 'カ', 'キ', 'ク', 'ケ', 'コ' };
		addRomaChar(chars, romas, kana, roma);
		roma = new String[] { "CA", "CI", "CU", "CE", "CO" };
		kana = new char[] { 'カ', 'シ', 'ク', 'セ', 'コ' };
		addRomaChar(chars, romas, kana, roma);
		roma = new String[] { "SA", "SHI", "SI", "SU", "SE", "SO" };
		kana = new char[] { 'サ', 'シ', 'シ', 'ス', 'セ', 'ソ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "TA", "TI", "TU", "TE", "TO" };
		kana = new char[] { 'タ', 'チ', 'ツ', 'テ', 'ト' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "THA", "THI", "THU", "THE", "THO" };
		kana = new char[] { 'タ', 'チ', 'ツ', 'テ', 'ト' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "NA", "NI", "NU", "NE", "NO" };
		kana = new char[] { 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "HA", "HI", "HU", "HE", "HO" };
		kana = new char[] { 'ハ', 'ヒ', 'フ', 'ヘ', 'ホ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "BA", "BI", "BU", "BE", "BO" };
		kana = new char[] { 'バ', 'ビ', 'ブ', 'ベ', 'ボ' };

		addRomaChar(chars, romas, kana, roma);
		roma = new String[] { "PA", "PI", "PU", "PE", "PO" };
		kana = new char[] { 'パ', 'ピ', 'プ', 'ペ', 'ポ' };

		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "MA", "MI", "MU", "ME", "MO" };
		kana = new char[] { 'マ', 'ミ', 'ム', 'メ', 'モ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "YA", "YU", "YO" };
		kana = new char[] { 'ヤ', 'ユ', 'ヨ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "WA", "WO", "N", "NN", "MM" };
		kana = new char[] { 'ワ', 'ヲ', 'ン', 'ン', 'ン' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "a", "i", "u", "e", "o" };
		kana = new char[] { 'あ', 'い', 'う', 'え', 'お' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ka", "ki", "ku", "ke", "ko" };
		kana = new char[] { 'か', 'き', 'く', 'け', 'こ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ca", "ci", "cu", "ce", "co" };
		kana = new char[] { 'か', 'し', 'く', 'せ', 'こ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "sa", "shi", "si", "su", "se", "so" };
		kana = new char[] { 'さ', 'し', 'し', 'す', 'せ', 'そ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ta", "ti", "tu", "te", "to" };
		kana = new char[] { 'た', 'ち', 'つ', 'て', 'と' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "na", "ni", "nu", "ne", "no" };
		kana = new char[] { 'な', 'に', 'ぬ', 'ね', 'の' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ha", "hi", "hu", "he", "ho" };
		kana = new char[] { 'は', 'ひ', 'ふ', 'へ', 'ほ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ba", "bi", "bu", "be", "bo" };
		kana = new char[] { 'ば', 'び', 'ぶ', 'べ', 'ぼ' };

		addRomaChar(chars, romas, kana, roma);
		roma = new String[] { "pa", "pi", "pu", "pe", "po" };
		kana = new char[] { 'ぱ', 'ぴ', 'ぷ', 'ぺ', 'ぽ' };

		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ma", "mi", "mu", "me", "mo" };
		kana = new char[] { 'ま', 'み', 'む', 'め', 'も' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "ya", "yu", "yo" };
		kana = new char[] { 'や', 'ゆ', 'よ' };
		addRomaChar(chars, romas, kana, roma);

		roma = new String[] { "wa", "wo", "n", "nn", "mm" };
		kana = new char[] { 'わ', 'を', 'ん', 'ん', 'ん' };
		addRomaChar(chars, romas, kana, roma);

		blockChars = (String[]) chars.toArray(new String[0]);
		blockCharRoma = (String[]) romas.toArray(new String[0]);

	}

	static final void addRomaChar(
		ArrayList chars,
		ArrayList romas,
		char[] kana,
		String[] roma) {
		for (int i = 0; i < roma.length; i++) {
			chars.add(Character.toString(kana[i]));
			romas.add(roma[i]);
		}
	}

	//	SelectItem selFri = new SelectItem() {
	//		public boolean isSelected(Circle c) {
	//			return c.getWeekDay().equals("金");
	//		}
	//	};
	//	SelectItem selSat = new SelectItem() {
	//		public boolean isSelected(Circle c) {
	//			return c.getWeekDay().equals("土");
	//		}
	//	};
	//	SelectItem selSun = new SelectItem() {
	//		public boolean isSelected(Circle c) {
	//			return c.getWeekDay().equals("日");
	//		}
	//	};

	SelectItem selEast123 = new SelectItem() {
		public boolean isSelected(Circle c_arg) {
			if(c_arg.getPage()==0){
				return false;
			}
			if (c_arg instanceof AbstractCircle) {
				AbstractCircle c = (AbstractCircle) c_arg;
				if (c.getPavilion().equals("東")) {
					int hole = c.getHallNo();
					if (hole >= 1 && hole <= 3) {
						return true;
					}
				}
			}
			return false;
		}
	};

	SelectItem selEast456 = new SelectItem() {
		public boolean isSelected(Circle c_arg) {
			if(c_arg.getPage()==0){
				return false;
			}
			if (c_arg instanceof AbstractCircle) {
				AbstractCircle c = (AbstractCircle) c_arg;
				if (c.getPavilion().equals("東")) {
					int hole = c.getHallNo();
					if (hole >= 4 && hole <= 6) {
						return true;
					}
				}
			}
			return false;
		}
	};

	SelectItem selWest1 = new SelectItem() {
		public boolean isSelected(Circle c) {
			if(c.getPage()==0){
				return false;
			}
			return c.getPavilion().equals("西")
				&& ((AbstractCircle) c).getHallNo() == 1;
		}
	};

	SelectItem selWest2 = new SelectItem() {
		public boolean isSelected(Circle c) {
			if(c.getPage()==0){
				return false;
			}
			return c.getPavilion().equals("西")
				&& ((AbstractCircle) c).getHallNo() == 2;
		}
	};

	private void sort() throws Exception {

		//		List day1, day2, day3;
		//		day1 = new ArrayList();
		//		day2 = new ArrayList();
		//		day3 = new ArrayList();
		//
		//		split(circleList, day1, selFri);
		//		split(circleList, day2, selSat);
		//		split(circleList, day3, selSun);

		//		day1 = sort1Day(day1);
		//		day2 = sort1Day(day2);
		//		day3 = sort1Day(day3);

		List tmp = new ArrayList();

		for (int i = 0; i < weekDays.length; i++) {
			List day = new ArrayList();
			SelectWeekDay selWeekDay = new SelectWeekDay(weekDays[i]);
			split(circleList, day, selWeekDay);
			day = sort1Day(day);
			tmp.addAll(day);
		}
		//		tmp.addAll(day1);
		//		tmp.addAll(day2);
		//		tmp.addAll(day3);

		tmp.addAll(circleList); //残りは抽選漏れ

		circleList = tmp;
	}

	private List sort1Day(List circles) throws Exception {

		List selColor = new ArrayList();

		List east123 = new ArrayList();
		List east456 = new ArrayList();
		List west1 = new ArrayList();
		List west2 = new ArrayList();

		if (getOpt("c") != null) {
			split(circles, selColor, new SelectColor(getOpt("c")));
		} else {
			selColor = circles; //すべてのチェックマークはソート対象。
			circles = new ArrayList(); //ソート対象にしない残りは０
		}

		split(selColor, east123, selEast123);
		split(selColor, east456, selEast456);
		split(selColor, west1, selWest1);
		split(selColor, west2, selWest2);

		east123 = doSort(east123);
		east456 = doSort(east456);
		west1 = doSort(west1);
		west2 = doSort(west2);

		List ret = new ArrayList();
		ret.addAll(east123);
		ret.addAll(east456);
		ret.addAll(west2); //西２のほうが西１よりコードは若い[あ～な]
		ret.addAll(west1);

		ret.addAll(circles); //マークチェックの残り。
		ret.addAll(selColor); //残っていないはずだがねんのため。
		return ret;
	}

	/**
	 * @param west2
	 * @return
	 */
	private List doSort(List circles) throws Exception {
		AbstractSorter sorter;
		if (hasOption("r")) {
			sorter = AbstractSorter.getInstance(AbstractSorter.TWO_OPT_SORT);
		} else {
			sorter = AbstractSorter.getInstance(AbstractSorter.ANNEALING_SORT);
		}

		List s = new ArrayList();
		List e = new ArrayList();
		pickup(circles, s, new SelectCodes(ComiketSorter.starts));
		pickup(circles, e, new SelectCodes(ComiketSorter.ends));
		if (s.size() >= 2) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < s.size(); i++) {
				buf.append(" " + ((SortElement) s.get(i)).toShortString());
			}
			throw new Exception("同じ日の同じホール内に始点が複数指定されました。" + buf);
		}
		if (e.size() >= 2) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < e.size(); i++) {
				buf.append(" " + ((SortElement) e.get(i)).toShortString());
			}
			throw new Exception("同じ日の同じホール内に終点が複数指定されました。" + buf);
		}

		SortElement st = null, ed = null;
		if (s.size() >= 1) {
			st = (SortElement) s.get(0);
		}
		if (e.size() >= 1) {
			ed = (SortElement) e.get(0);
		}
		List ret = sorter.doSort(circles, st, ed);
		return ret;
	}

	/**
	 * @param west1
	 */

	private static List split(List src, List dest, SelectItem f) {
		return splitSub(src, dest, f, true);
	}

	private static List pickup(List src, List dest, SelectItem f) {
		return splitSub(src, dest, f, false);
	}

	private static List splitSub(
		List src,
		List dest,
		SelectItem f,
		boolean delflg) {
		Iterator i = src.iterator();
		while (i.hasNext()) {
			Circle c = (Circle) i.next();
			if (f.isSelected(c)) {
				dest.add(c);
				if (delflg)
					i.remove();
			}
		}
		return src;
	}

	private void writeCSV(String outFile) throws IOException {
		OutputStream os0 = new FileOutputStream(outFile);
		OutputStream os;
		if (WIN_SJIS.equals("SJIS")) {
			os = new YenFixOutputStream(os0);
		} else {
			os = os0;
		}

		BufferedWriter w0 =
			new BufferedWriter(new OutputStreamWriter(os, WIN_SJIS));

		Writer w;
		if (WIN_SJIS.equals("SJIS")) {
			w = new CharFixWriter(w0);
		} else {
			w = w0;
		}

		writeLine(w, header);
		writeLines(w, colorList);
		writeLines(w, circleList);
		writeLines(w, unKnownList);

		w.close();
	}

	private static void writeLines(Writer w, List list) throws IOException {
		int ln = 0;
		DecimalFormat fmt = new DecimalFormat("0000");
		Iterator i = list.iterator();
		while (i.hasNext()) {
			GenericCSVLine out = (GenericCSVLine) i.next();
			writeLine(w, out);
		}

	}

	private static void writeLine(Writer w, GenericCSVLine out)
		throws IOException {
		if (out == null) {
			return;
		}
		w.write(out.toString());
		w.write(WIN_CRLF);
	}

	private void readLines(String inFile) throws IOException {
		InputStream is = new FileInputStream(inFile);

		BufferedReader r =
			new BufferedReader(new InputStreamReader(is, WIN_SJIS));

		int line = 0;
		GenericCSVLine in;
		while ((in = GenericCSVLine.readCSVLine(r)) != null) {

			in.setOriginOrder(line);

			if (in instanceof Header) {
				header = (Header) in;
			} else if (in instanceof Color) {
				colorList.add(in);
			} else if (in instanceof Circle) {
				circleList.add(in);
			} else if (in instanceof UnKnown) {
				unKnownList.add(in);
			}
			line++;
		}
		r.close();
	}
}
