/*
 * Created on 2005/11/20
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jp.gr.java_conf.turner.comiket.sorter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.gr.java_conf.turner.comiket.sorter.elem.AbstractWestCircle;
import jp.gr.java_conf.turner.comiket.sorter.elem.CircleDmy;
import jp.gr.java_conf.turner.comiket.sorter.elem.CircleEastDmy;
import jp.gr.java_conf.turner.comiket.sorter.elem.SortElement;

/**
 * @author TURNER
 * WestSorter
 *
 * @
 */
public abstract class AbstractSorter {
	private static final int EAST_ENTER_X = 1100;
	private static final int EAST_ENTER_Y = 500;

	private static final int EAST_EXIT_X = 160;
	private static final int EAST_EXIT_Y = 500;

	public static final int ANNEALING_SORT = 1;
	public static final int TWO_OPT_SORT = 2;

	public static AbstractSorter getInstance(int sort_type) {
		AbstractSorter ret;
		switch (sort_type) {
			case ANNEALING_SORT :
				ret = new AnnealSorter();
				break;
			case TWO_OPT_SORT :
				ret = new TwoOptSorter();
				break;
			default :
				throw new IllegalArgumentException();
		}

		return ret;
	}

	public List doSort(List circleList, SortElement st, SortElement ed) {
		if (circleList.size() == 0) {
			return circleList;
		}

		SortElement start, end;

		//始点終点のデフォルト設定
		SortElement smpl = (SortElement)circleList.get(0);
		if (smpl instanceof AbstractWestCircle) {
			start = SortElement.DMY;
			end = SortElement.DMY;
		} else {
			start = CircleEastDmy.getInstance(EAST_ENTER_X, EAST_ENTER_Y);
			end = CircleEastDmy.getInstance(EAST_EXIT_X, EAST_EXIT_Y);
		}

		//始点終点の座標を設定
		if (st != null) {
			start = CircleEastDmy.getInstance(st.getX(), st.getY());
		}
		if (ed != null) {
			end = CircleEastDmy.getInstance(ed.getX(), ed.getY());
		}
		circleList.add(0, start);
		circleList.add(end);

		circleList = doSortSub(circleList);

		Iterator i = circleList.iterator();
		while (i.hasNext()) {
			SortElement e = (SortElement)i.next();
			if (e instanceof CircleDmy) {
				i.remove();
			}
		}
		return circleList;
	}

	abstract protected List doSortSub(List circleList);

	protected int totalDidstance(List circleList) {
		int sum0 = 0;

		for (int i = 0; i < (circleList.size() - 1); i++) {
			SortElement a = (SortElement)circleList.get(i);
			SortElement b = (SortElement)circleList.get(i + 1);
			sum0 += a.distance(b);
		}

		return sum0;
	}

	protected void distanceTest(ArrayList circleList) {

		for (int i = 0; i < (circleList.size() - 1); i++) {
			for (int j = i + 1; j < circleList.size(); j++) {
				SortElement a, b;
				a = (SortElement)circleList.get(i);
				b = (SortElement)circleList.get(j);
			}
		}

	}

	/**
	 * @param list
	 */
	protected void reverse(List list) {
		Object tmp;
		for (int i = 0, j = (list.size() - 1); i < j; i++, j--) {
			tmp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, tmp);
		}
	}

	protected int calcNextSum(
		List circleList,
		int sum,
		int swpHead,
		int swpTail) {
		int nextSum = sum;
		{
			SortElement a0, a1, b0, b1;
			a0 = (SortElement)circleList.get(swpHead);
			a1 = (SortElement)circleList.get(swpHead + 1);
			b0 = (SortElement)circleList.get(swpTail);
			b1 = (SortElement)circleList.get(swpTail + 1);

			nextSum -= a0.distance(a1);
			nextSum -= b0.distance(b1);
			nextSum += a0.distance(b0);
			nextSum += a1.distance(b1);
		}
		return nextSum;
	}

}
