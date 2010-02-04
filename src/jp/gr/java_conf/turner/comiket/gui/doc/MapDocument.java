/**
 * 
 */
package jp.gr.java_conf.turner.comiket.gui.doc;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.gr.java_conf.turner.comiket.ComiketSorter;
import jp.gr.java_conf.turner.comiket.csv.Circle;
import jp.gr.java_conf.turner.comiket.csv.Color;
import jp.gr.java_conf.turner.comiket.csv.Header;
import jp.gr.java_conf.turner.comiket.csv.UnKnown;
import jp.gr.java_conf.turner.comiket.def.ComiketDate;
import jp.gr.java_conf.turner.comiket.def.GenericDef;
import jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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

	private File csvRoot = null;

	boolean validCatalogRoot = false;

	private Map<MapKey, Image> imageCache = new HashMap<MapKey, Image>();

	private ArrayList<ComiketDate> comiketDate = new ArrayList<ComiketDate>();

	private Header header = null;

	private Multimap<BlockKey, Circle> circles = ArrayListMultimap.create();

	private List<Circle> dropCircles = new ArrayList<Circle>();

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

		this.setChanged();
		this.notifyObservers();
	}

	public void readCSVFile(File inFile) {
		Header header = null;
		List<Color> colorList = new ArrayList<Color>();
		List<UnKnown> unKnownList = new ArrayList<UnKnown>();
		Multimap<BlockKey, Circle> circles = ArrayListMultimap.create();
		List<Circle> dropCircles = new ArrayList<Circle>();

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
	
		for (Circle c : circleList) {
			Days day = Days.fromCircle(c, comiketDate);
			SortBlock block = SortBlock.fromCircle(c);
			if (day != null && block != null) {
				BlockKey key = new BlockKey(day, block);
				circles.put(key, c);
			} else {
				dropCircles.add(c);
			}
		}

		this.header = header;
		this.colorList = colorList;
		this.unKnownList = unKnownList;
		this.circles = circles;
		this.dropCircles = dropCircles;
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

	private class BlockKey {
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
			return day.hashCode() + sortBlock.hashCode();
		}

	}

	public static enum SortBlock {
		E123, E456, W1, W2, ;

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
					if (comiketDate.get(d.ordinal()).getWeekday().equals(weekday)) {
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
	public File getCSVRoot() {
		return this.csvRoot;
	}

	/**
	 * @param f
	 */
	public void setCSVRoot(final File f) {

		ex.execute(new Runnable() {

			public void run() {
				MapDocument.this.csvRoot = null;
				if (f != null) {
					MapDocument.this.readCSVFile(f);
				}
				MapDocument.this.csvRoot = f;
				MapDocument.this.setChanged();
				MapDocument.this.notifyObservers();
			}
		});
	}

	public static void shutdownNow() {
		ex.shutdownNow();
	}

	private static final ExecutorService ex = Executors.newCachedThreadPool();
}
