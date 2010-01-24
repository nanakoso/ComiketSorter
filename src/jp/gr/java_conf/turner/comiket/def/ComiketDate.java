/**
 * 
 */
package jp.gr.java_conf.turner.comiket.def;


/**
 * @author TURNER
 * 
 */
public class ComiketDate extends GenericDef{

	private enum Cols {
		YEAR {
			@Override
			void setColToProperty(ComiketDate comiketDate, String[] cols) {
				comiketDate.year = Integer.parseInt(cols[ordinal()]);
			}
		},
		MONTH {
			@Override
			void setColToProperty(ComiketDate comiketDate, String[] cols) {
				comiketDate.month = Integer.parseInt(cols[ordinal()]);
			}
		},
		DAY {
			@Override
			void setColToProperty(ComiketDate comiketDate, String[] cols) {
				comiketDate.day = Integer.parseInt(cols[ordinal()]);
			}
		},
		WEEKDAY {
			@Override
			void setColToProperty(ComiketDate comiketDate, String[] cols) {
				comiketDate.weekday = cols[ordinal()];
			}
		},
		START_PAGE {
			@Override
			void setColToProperty(ComiketDate comiketDate, String[] cols) {
				comiketDate.startPage = Integer.parseInt(cols[ordinal()]);
			}
		};

		abstract void setColToProperty(ComiketDate comiketDate, String[] cols);

	};

	private int year;

	private int month;

	private int day;

	private String weekday;

	private int startPage;

	/**
	 * @param columns
	 */
	protected ComiketDate(String[] columns) {
		for (Cols col : Cols.values()) {
			col.setColToProperty(this, columns);
		}
	}

	/**
	 * @return day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @return startPage
	 */
	public int getStartPage() {
		return startPage;
	}

	/**
	 * @return weekday
	 */
	public String getWeekday() {
		return weekday;
	}

	/**
	 * @return year
	 */
	public int getYear() {
		return year;
	}

}
