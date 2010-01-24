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
import java.util.Map;
import java.util.Observable;

import jp.gr.java_conf.turner.comiket.ComiketSorter;
import jp.gr.java_conf.turner.comiket.def.ComiketDate;
import jp.gr.java_conf.turner.comiket.def.GenericDef;

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

	boolean validCatalogRoot = false;

	private Map<MapKey, Image> imageCache = new HashMap<MapKey, Image>();

	private ArrayList<ComiketDate> comiketDate = null;

	public void setCatalogRoot(File argRoot) {

		if (argRoot != null || !argRoot.isDirectory()) {
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
		this.comiketDate = null;

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
			while(newDates.size() < Days.values().length){
				newDates.add(0, null);
			}
			this.comiketDate = newDates;
		}
		for(ComiketDate d:this.comiketDate){
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

		final Days day;

		final Area area;

		final Layer layer;

		MapKey(Days day2, Area area, Layer layer) {
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

		String getFilename() {
			return layer.toString() + day.toString() + area.toString()
					+ MAPIMGFILE_EXT;
		}
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
	}

}
