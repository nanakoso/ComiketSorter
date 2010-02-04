/**
 * 
 */
package jp.gr.java_conf.turner.comiket.gui.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import jp.gr.java_conf.turner.comiket.csv.Circle;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Area;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Days;
import jp.gr.java_conf.turner.comiket.sorter.elem.AbstractCircle;

/**
 * @author TURNER
 * 
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel implements Observer {

	private static final int OFFSET_X = 5;

	private static final int OFFSET_Y = 5;

	private Days viewDay = Days.DAY1;

	private Area viewArea = Area.E123;

	private MapDocument doc;

	public MapPanel(MapDocument doc) {
		super();
		setBackground(Color.WHITE);

		this.doc = doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		Image bg = doc.getBGImage(viewDay, viewArea);
		if (bg != null) {
			g2.drawImage(bg, 0, 0, this);
		}
		Image image = doc.getMapImage(viewDay, viewArea);
		if (image != null) {
			g2.drawImage(image, 0, 0, this);
		}
		Image fg = doc.getFGImage(viewDay, viewArea);
		if (fg != null) {
			g2.drawImage(fg, 0, 0, this);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.RED);

		g2.setStroke(new BasicStroke(6));
		List<Circle> circles = doc.getCircles(viewDay, viewArea);
		if (circles != null) {
			int size = circles.size();
			GeneralPath path = new GeneralPath();
			for (int i = 0; i < size; i++) {
				Circle c = circles.get(i);
				int x, y;
				if (c instanceof AbstractCircle) {
					AbstractCircle ac = (AbstractCircle) c;
					x = ac.getX();
					y = ac.getY();
				} else {
					x = c.getMapX() + OFFSET_X;
					y = c.getMapY() + OFFSET_Y;
				}
				if (i == 0) {
					path.moveTo(x, y);
				} else {
					path.lineTo(x, y);
				}
				g2.draw(new Line2D.Float(x, y, x, y));
			}
			g2.setStroke(new BasicStroke(2));
			g2.draw(path);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof MapDocument) {
			repaint();
		}
	}


	/**
	 * @param day
	 */
	public void setViewDay(Days day) {
		if (this.viewDay != day) {
			this.viewDay = day;
			this.repaint();
		}
	}

	/**
	 * @param viewArea
	 *            Ý’è‚·‚é viewArea
	 */
	public void setViewArea(Area viewArea) {
		if (this.viewArea != viewArea) {
			this.viewArea = viewArea;
			this.repaint();
		}
	}
}
