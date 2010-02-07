/**
 * 
 */
package jp.gr.java_conf.turner.comiket.gui.doc;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jp.gr.java_conf.turner.comiket.ComiketSorter;
import jp.gr.java_conf.turner.comiket.csv.Circle;
import jp.gr.java_conf.turner.comiket.csv.Color;
import jp.gr.java_conf.turner.comiket.csv.Header;
import jp.gr.java_conf.turner.comiket.csv.UnKnown;
import jp.gr.java_conf.turner.comiket.def.ComiketDate;
import jp.gr.java_conf.turner.comiket.def.GenericDef;
import jp.gr.java_conf.turner.comiket.sorter.AbstractSorter;
import jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * @author TURNER
 * 
 */
public class MapDocument extends Observable {
	/**
	 * マップイメージフォルダ名
	 */
	private static final String MDATA = "MDATA";

	/**
	 * 定義ファイルフォルダ名
	 */
	private static final String CDATA = "CDATA";

	/**
	 * マップ画像ファイル拡張子
	 */
	private static final String MAPIMGFILE_EXT = ".PNG";

	private File catalogRoot = null;

	private File csvFile = null;

	boolean validCatalogRoot = false;

	private Map<MapKey, Image> imageCache = new HashMap<MapKey, Image>();

	private ArrayList<ComiketDate> comiketDate = new ArrayList<ComiketDate>();

	private Header header = null;

	private ListMultimap<BlockKey, Circle> circles = ArrayListMultimap.create();

	private List<UnKnown> unKnownList = new ArrayList<UnKnown>();

	private List<Color> colorList = new ArrayList<Color>();

	public void setCatalogRoot(File argRoot) {

		if (argRoot != null && !argRoot.isDirectory()) {
			File parent = argRoot.getParentFile();
			if (parent != null) {
				argRoot = parent;
			}
		}

		if (this.catalogRoot != null) {
			if (this.catalogRoot.equals(argRoot)) {
				return;
			}
		} else {
			if (argRoot == null) {
				return;
			}
		}

		this.catalogRoot = argRoot;

		this.imageCache.clear();
		this.comiketDate.clear();

		this.validCatalogRoot = isValidCatalogRoot();
		if (this.validCatalogRoot) {
			File cDir = new File(catalogRoot, CDATA);
			File[] files = cDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String upperName = name.toUpperCase();
					return upperName.startsWith("C")
							&& upperName.endsWith("DEF.TXT");
				}
			});
			File defFile = null;
			if (files.length > 0) {
				defFile = files[0];
			}

			if (defFile != null) {
				readDefFile(defFile);

			}

		}

		this.circles = splitCircles(circles.values(), comiketDate);
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * @param f
	 */
	public void setCSVRoot(final File f) {

		ex.execute(new Runnable() {

			public void run() {
				MapDocument.this.csvFile = null;
				if (f != null) {
					MapDocument.this.readCSVFile(f);
				}
				MapDocument.this.csvFile = f;
				MapDocument.this.setChanged();
				MapDocument.this.notifyObservers();
			}
		});
	}

	private void readCSVFile(File inFile) {
		Header header = null;
		List<Color> colorList = new ArrayList<Color>();
		List<UnKnown> unKnownList = new ArrayList<UnKnown>();

		List<Circle> circleList = new ArrayList<Circle>();
		try {
			header = ComiketSorter.readLines(inFile.getPath(), colorList,
					circleList, unKnownList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (header == null) {
			return;
		}

		this.circles = splitCircles(circleList, this.comiketDate);
		this.header = header;
		this.colorList = colorList;
		this.unKnownList = unKnownList;
	}

	/**
	 * @param circleList
	 * @param comiketDate
	 * @return
	 */
	private static ListMultimap<BlockKey, Circle> splitCircles(
			Collection<Circle> circleList, List<ComiketDate> comiketDate) {
		ListMultimap<BlockKey, Circle> circles = ArrayListMultimap.create();
		for (Circle c : circleList) {
			Days day = Days.fromCircle(c, comiketDate);
			SortBlock block = SortBlock.fromCircle(c);
			if (day != null && block != null) {
				BlockKey key = new BlockKey(day, block);
				if (day == Days.DAY3
						&& (block == SortBlock.E123 || block == SortBlock.E456)) {
					((AbstractCircle) c).setLastEast(true);
				} else {
					((AbstractCircle) c).setLastEast(false);
				}
				circles.put(key, c);
			} else {
				circles.put(BlockKey.NULL_KEY, c);
			}
		}
		return circles;
	}

	/**
	 * @param defFile
	 */
	private void readDefFile(File defFile) {
		try {
			FileInputStream is = new FileInputStream(defFile);
			InputStreamReader ir = new InputStreamReader(is,
					ComiketSorter.WIN_SJIS);
			GenericDef.readTSVDef(new BufferedReader(ir));
			this.comiketDate = GenericDef.getComiketDateList();
		} catch (IOException e) {

		}

		if (this.comiketDate != null
				&& this.comiketDate.size() < Days.values().length) {
			ArrayList<ComiketDate> newDates = new ArrayList<ComiketDate>();
			newDates.addAll(this.comiketDate);
			while (newDates.size() < Days.values().length) {
				newDates.add(0, null);
			}
			this.comiketDate = newDates;
		}
		for (ComiketDate d : this.comiketDate) {
			System.out.println(d.getWeekday());
		}
	}

	/**
	 * 現在のファイルに上書きセーブ （既存のファイルはBAKへ）
	 * 
	 * @param parent
	 */
	public void saveCSV(JFrame parent) {
		this.saveCSV(this.csvFile, parent);
	}

	/**
	 * 既存ファイルをBAKに置き換えてセーブ
	 * 
	 * @param writeFile
	 * @param parent
	 */
	private void saveCSV(File writeFile, JFrame parent) {
		if (writeFile == null) {
			return;
		}
		File dir = writeFile.getParentFile();

		String nameBody = writeFile.getName();
		String ext = "";
		int lastDot = nameBody.lastIndexOf('.');
		if (lastDot >= 0) {
			ext = nameBody.substring(lastDot);// ".CSV"
			nameBody = nameBody.substring(0, lastDot);
		}

		File tmpFile;
		try {
			tmpFile = File.createTempFile(nameBody, ext, dir);
			this.writeCSV(tmpFile);
			File bakFile = new File(dir, nameBody + ".BAK");
			if (bakFile.exists()) {
				bakFile.delete();
			}
			writeFile.renameTo(bakFile);
			tmpFile.renameTo(this.csvFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, this.csvFile.getName()
					+ "の書き込みに失敗しました。\n" + e.getMessage(), "エラー",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 名前を指定してセーブ
	 * 
	 * @param file
	 * @param parent
	 */
	public void saveCSVTo(File file, JFrame parent) {
		if (file == null) {
			return;
		}
		if (file.equals(this.csvFile) || file.exists()) {

			int dlg = JOptionPane.showConfirmDialog(parent.getContentPane(),
					file.getName() + "を上書きしますか？", "ファイル上書き確認",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (dlg == JOptionPane.YES_OPTION) {
				this.saveCSV(file, parent);
			}
			return;
		}
		try {
			this.writeCSV(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parent, file.getName()
					+ "の書き込みに失敗しました。\n" + e.getMessage(), "エラー",
					JOptionPane.ERROR_MESSAGE);
		}
		return;
	}

	/**
	 * ファイル書き込み処理本体
	 * 
	 * @param outFile
	 * @throws IOException
	 */
	private void writeCSV(File outFile) throws IOException {
		OutputStream os = new FileOutputStream(outFile);
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os,
				ComiketSorter.WIN_SJIS));
		ComiketSorter.writeLine(w, header);
		ComiketSorter.writeLines(w, colorList);
		for (Days d : Days.values()) {
			for (SortBlock b : SortBlock.values()) {
				List<Circle> cblock = circles.get(new BlockKey(d, b));
				ComiketSorter.writeLines(w, cblock);
			}
		}
		ComiketSorter.writeLines(w, circles.get(BlockKey.NULL_KEY));
		ComiketSorter.writeLines(w, unKnownList);

		w.close();
	}

	public File getCatalogRoot() {
		return this.catalogRoot;
	}

	private boolean isValidCatalogRoot() {
		if (this.catalogRoot == null) {
			return false;
		}
		File fMdata = new File(this.catalogRoot, MDATA);
		if (!fMdata.isDirectory()) {
			return false;
		}
		return true;
	}

	public Image getMapImage(Days day, Area area) {
		return getImage(day, area, Layer.MAP);
	}

	public Image getFGImage(Days day, Area area) {
		return getImage(day, area, Layer.GFG);
	}

	public Image getBGImage(Days day, Area area) {
		return getImage(day, area, Layer.GBG);
	}

	public List<Circle> getCircles(Days day, Area area) {
		List<Circle> ret = new ArrayList<Circle>();
		switch (area) {
		case E123:
			BlockKey keyE123 = new BlockKey(day, SortBlock.E123);
			if (circles.containsKey(keyE123)) {
				ret.addAll(circles.get(keyE123));
			}
			break;
		case E456:
			BlockKey keyE456 = new BlockKey(day, SortBlock.E456);
			if (circles.containsKey(keyE456)) {
				ret.addAll(circles.get(keyE456));
			}
			break;
		case W12:
			BlockKey keyW1 = new BlockKey(day, SortBlock.W1);
			if (circles.containsKey(keyW1)) {
				ret.addAll(circles.get(keyW1));
			}
			BlockKey keyW2 = new BlockKey(day, SortBlock.W2);
			if (circles.containsKey(keyW2)) {
				ret.addAll(circles.get(keyW2));
			}
			break;
		}
		return ret;
	}

	/**
	 * @param day
	 * @param area
	 * @param layer
	 * @return
	 */
	private Image getImage(Days day, Area area, Layer layer) {
		final MapKey mapKey = new MapKey(day, area, layer);
		Image image = imageCache.get(mapKey);
		if (image == null && this.validCatalogRoot) {

			File fMdata = new File(catalogRoot, MDATA);
			File fMap = new File(fMdata, mapKey.getFilename());
			// try {
			// System.out.println(fMap);
			// image = ImageIO.read(fMap);
			// } catch (IOException e) {
			// // TODO エラー処理
			// e.printStackTrace();
			// }
			image = Toolkit.getDefaultToolkit().getImage(fMap.getPath());
			imageCache.put(mapKey, image);
		}
		return image;
	}

	private class MapKey {

		private final Days day;

		private final Area area;

		private final Layer layer;

		private MapKey(Days day2, Area area, Layer layer) {
			this.day = day2;
			this.area = area;
			this.layer = layer;
		}

		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof MapKey) {
				MapKey o2 = (MapKey) o;
				if (day == o2.day && area == o2.area && layer == o2.layer) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return day.hashCode() + area.hashCode() + layer.hashCode();
		}

		private String getFilename() {
			return layer.toString() + day.toString() + area.toString()
					+ MAPIMGFILE_EXT;
		}
	}

	private static class BlockKey {
		private static final BlockKey NULL_KEY = new BlockKey(null, null);

		final Days day;

		final SortBlock sortBlock;

		private BlockKey(Days day, SortBlock sortBlock) {
			this.day = day;
			this.sortBlock = sortBlock;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof BlockKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			if (this == NULL_KEY || obj == NULL_KEY) {
				return false;
			}
			return this.day == ((BlockKey) obj).day
					&& this.sortBlock == ((BlockKey) obj).sortBlock;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if (this == NULL_KEY) {
				return super.hashCode();
			}
			return day.hashCode() + sortBlock.hashCode();
		}

	}

	public static enum SortBlock {
		W1, W2, E123, E456, ;

		static private SortBlock fromCircle(Circle c) {
			if (c.getPage() <= 0) {
				return null;
			}
			if (c.getPavilion().equals("西")) {
				switch (((AbstractCircle) c).getHallNo()) {
				case 1:
					return W1;
				case 2:
					return W2;
				}

			} else if (c.getPavilion().equals("東")) {
				switch (((AbstractCircle) c).getHallNo()) {
				case 1:
				case 2:
				case 3:
					return E123;
				case 4:
				case 5:
				case 6:
					return E456;
				}
			}
			return null;
		};
	}

	public static enum Area {
		E123, E456, W12,
	}

	public static enum Layer {
		GFG, MAP, GBG,
	}

	public static enum Days {
		DAY1, DAY2, DAY3, ;

		@Override
		public String toString() {
			return Integer.toString(this.ordinal() + 1);
		}

		private static Days fromCircle(Circle c, List<ComiketDate> comiketDate) {
			if (c.getPage() <= 0) {
				return null;
			}
			String weekday = c.getWeekDay();
			for (Days d : Days.values()) {
				if (d.ordinal() < comiketDate.size()) {
					String wDay = comiketDate.get(d.ordinal()).getWeekday();
					if (wDay.equals(weekday)) {
						return d;
					}
				}
			}
			return null;

		}
	}

	/**
	 * @return
	 */
	public File getCSVFile() {
		return this.csvFile;
	}

	/**
	 * ソート実行
	 */
	public void sort() {
		ex.execute(new Runnable() {
			ListMultimap<BlockKey, Circle> wk = ArrayListMultimap.create();

			public void run() {
				for (Days d : Days.values()) {
					for (SortBlock b : SortBlock.values()) {
						BlockKey blockKey = new BlockKey(d, b);
						List<Circle> cs = circles.get(blockKey);
						List<Circle> cs2 = sort(cs);
						wk.putAll(blockKey, cs2);
					}
				}
				wk.putAll(BlockKey.NULL_KEY, circles.get(BlockKey.NULL_KEY));

				MapDocument.this.circles = wk;
				MapDocument.this.setChanged();
				MapDocument.this.notifyObservers();
			}

		});
	}

	/**
	 * @param cs
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static List<Circle> sort(List<Circle> cs) {
		AbstractSorter sorter = AbstractSorter
				.getInstance(AbstractSorter.TWO_OPT_SORT);
		return sorter.doSort(cs, null, null);
	}

	public static void shutdownNow() {
		ex.shutdownNow();
	}

	private static final ExecutorService ex = Executors.newCachedThreadPool();
}
