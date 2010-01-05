/*
 * 作成日: 2005/11/22
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.gr.java_conf.turner.comiket.sorter.elem;

/**
 * @author notanata
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public interface SortElement {
	final static SortElement DMY = CircleDmy.getInstance();

	int distance(SortElement e);

	int getX();
	int getY();
	int getHallNo();
	boolean isWallSide();
	String toShortString();

}
