/**
 * 
 */
package jp.gr.java_conf.turner.comiket.gui.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import jp.gr.java_conf.turner.comiket.csv.Circle;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Area;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Days;

/**
 * @author TURNER
 * 
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel implements Observer {

//	private Image bg = null;
//
//	private Image image = null;
//
//	private Image fg = null;

	
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
		super.paintComponent(g);
		Image bg = doc.getBGImage(viewDay, viewArea);
		if (bg != null) {
			g.drawImage(bg, 0, 0, this);
		}
		Image image = doc.getMapImage(viewDay, viewArea);
		if (image != null) {
			g.drawImage(image, 0, 0, this);
		}
		Image fg = doc.getFGImage(viewDay, viewArea);
		if (fg != null) {
			g.drawImage(fg, 0, 0, this);
		}
		List<Circle> circles = doc.getCircles(viewDay, viewArea);
		if(circles!=null){
			int size = circles.size();
			int[] xs = new int[size];
			int[] ys = new int[size];
			for(int i=0;i<size;i++){
				Circle c = circles.get(i);
				xs[i] = c.getMapX();
				ys[i] = c.getMapY();
			}
			g.setColor(Color.RED);
			g.drawPolyline(xs, ys, size);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof MapDocument) {
//			MapDocument doc = (MapDocument) o;
//			setImage(doc.getMapImage(this.viewDay, this.viewArea));
//			setFg(doc.getFGImage(this.viewDay, this.viewArea));
//			setBg(doc.getBGImage(this.viewDay, this.viewArea));
			repaint();
		}
	}

//	public void setImage(Image image) {
//		this.image = image;
//	}
//
//	/**
//	 * @param bg
//	 *            Ý’è‚·‚é bg
//	 */
//	public void setBg(Image bg) {
//		this.bg = bg;
//	}
//
//	/**
//	 * @param fg
//	 *            Ý’è‚·‚é fg
//	 */
//	public void setFg(Image fg) {
//		this.fg = fg;
//	}

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
