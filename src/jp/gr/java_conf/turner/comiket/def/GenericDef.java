/**
 * 
 */
package jp.gr.java_conf.turner.comiket.def;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author TURNER
 * 
 */
public abstract class GenericDef {

	private static final ArrayList<ComiketDate> comiketDateList = new ArrayList<ComiketDate>();

	public enum DefEnum {

		/**
		 * �R�~�P�b�g�񐔏��
		 */
		Comiket,

		/**
		 * �T�[�N���J�b�g�\�����
		 */
		cutInfo,

		/**
		 * �}�b�v���\�����
		 */
		mapTableInfo,

		/**
		 * �J�Ó��� + �W�����v���
		 */
		ComiketDate {
			@Override
			GenericDef fromCols(String[] cols) {
				return new ComiketDate(cols);
			}

			@Override
			void toCollection(GenericDef o) {
				comiketDateList.add((ComiketDate) o);
			}

			@Override
			void clearCollection() {
				comiketDateList.clear();
			}
		},

		/**
		 * �n�}���
		 */
		ComiketMap,

		/**
		 * �n�� + �u���b�N���
		 */
		ComiketArea,

		/**
		 * �W���������
		 */
		ComiketGenre, ;

		GenericDef fromCols(String[] cols) {
			return null;
		}

		void toCollection(GenericDef o) {
		}

		void clearCollection() {
		}
	}

	public static void readTSVDef(BufferedReader r) throws IOException {

		String line = null;
		DefEnum nowLoadingDefEnum = null;

		while ((line = r.readLine()) != null) {

			line = line.trim();

			if (line.length() == 0 || line.startsWith("#")) {
				continue;
			}

			if (line.startsWith("*")) {
				try {
					nowLoadingDefEnum = DefEnum.valueOf(line.substring(1));
					nowLoadingDefEnum.clearCollection();
					System.out.println("loading:" + nowLoadingDefEnum);
				} catch (IllegalArgumentException e) {
					nowLoadingDefEnum = null;
					System.out.println(e);
				}
				continue;
			}

			if (nowLoadingDefEnum != null) {
				String[] cols = line.split("\\t", -1);
				GenericDef def = nowLoadingDefEnum.fromCols(cols);
				if (def != null) {
					nowLoadingDefEnum.toCollection(def);
				}
			}
		}

	}

	/**
	 * @return comiketDateList
	 */
	public static ArrayList<ComiketDate> getComiketDateList() {
		return comiketDateList;
	}

}
