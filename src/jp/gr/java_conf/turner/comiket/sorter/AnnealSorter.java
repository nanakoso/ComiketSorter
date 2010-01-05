/*
 * 作成日: 2005/11/22
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket.sorter;

import java.util.List;
import java.util.Random;

import jp.gr.java_conf.turner.comiket.ComiketSorter;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
class AnnealSorter extends AbstractSorter {
	private Random rand = new Random(0);

	private long loopCount = 0;

	private long swapCount = 0;

	private double dt = 0.9;
	private double T;
	private double T_end;

	private int sum;

	protected List doSortSub(List circleList) {
		if (circleList.size() <= 3) {
			return circleList;
		}

		//		distanceTest(circleList);

		int sum0 = totalDidstance(circleList);
		int noSwapCount = 0;
		sum = sum0;
		boolean isSwapped;
		T = circleList.size() * 2f;
		T_end = 0.01;
		do {
			isSwapped = false;
			for (int i = 0; i < (circleList.size() - 1); i++) {
				for (int j = 0; j < (circleList.size() - 1); j++) {
					if (Math.abs(i - j) >= 2) {
						boolean swapOK = trySwap(circleList, i, j);
						if (swapOK) {
							isSwapped = true;
							swapCount++;
						}
					}
				}
			}
			T *= dt;
			loopCount++;
			if (!isSwapped) {
				noSwapCount++;
				if (noSwapCount > 5) {
					break;
				}
			} else {
				noSwapCount = 0;
			}
			loopLog(sum);
		} while (/*isSwapped == true*/
			T > T_end && sum > 0);

		return circleList;
	}

	private boolean trySwap(List circleList, int i, int j) {
		boolean swapOK = false;
		{
			int swpHead, swpTail;
			if (i < j) {
				swpHead = i;
				swpTail = j;
			} else {
				swpHead = j;
				swpTail = i;
			}
			int nextSum = calcNextSum(circleList, sum, swpHead, swpTail);

			if (nextSum < sum) {
				swapOK = true;
			} else if (nextSum > sum) {
				double p = Math.exp(- (nextSum - sum) / T);
				double x = rand.nextDouble();
				if (x < p) {
					swapOK = true;
					swapLog(sum, nextSum);
				}
			}
			if (swapOK) {
				reverse(circleList.subList(swpHead + 1, swpTail + 1));
				sum = nextSum;
			}
		}
		return swapOK;
	}

	private void loopLog(int sum) {
		if (ComiketSorter.getDebugOption()) {
			System.out.println(
				"T:"
					+ T
					+ " tend:"
					+ T_end
					+ " loop:"
					+ loopCount
					+ "swap:"
					+ swapCount
					+ " distance:"
					+ sum);
		} else {
			System.out.print("*");
			System.out.flush();
		}
	}

	private void swapLog(int sum, int nextSum) {
		if (ComiketSorter.getDebugOption()) {
			System.out.println(
				"hop! T:"
					+ T
					+ " swap:"
					+ swapCount
					+ " distance:"
					+ sum
					+ " +:"
					+ (nextSum - sum));
		}
	}

}
