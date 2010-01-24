/**
 * 
 */
package jp.gr.java_conf.turner.comiket.gui.view;

import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Area.E123;
import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Area.E456;
import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Area.W12;
import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Days.DAY1;
import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Days.DAY2;
import static jp.gr.java_conf.turner.comiket.gui.doc.MapDocument.Days.DAY3;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import jp.gr.java_conf.turner.comiket.gui.doc.MapDocument;

/**
 * @author TURNER
 * 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	final MapPanel mapPanel;

	private MapDocument doc;

	/**
	 * @throws HeadlessException
	 */
	public MainFrame() throws HeadlessException {
		this.setTitle("�R�~�P�\�[�^");
		this.setSize(600, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.getRootPane().setJMenuBar(initMenu());

		this.doc = new MapDocument();
		this.mapPanel = new MapPanel(doc);
		this.doc.addObserver(this.mapPanel);

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(this.mapPanel, BorderLayout.CENTER);

		this.getContentPane().add(initToolbar(), BorderLayout.NORTH);
	}

	/**
	 * @return
	 */
	private JMenuBar initMenu() {
		final JMenuBar mb = new JMenuBar();
		final JMenu mFile = new JMenu("�t�@�C��");

		mFile.add(aOpen);
		mFile.addSeparator();
		mFile.add(aExit);
		mb.add(mFile);

		return mb;
	}

	private JToolBar initToolbar() {
		JToolBar tb = new JToolBar();
		tb.add(aOpen);
		tb.addSeparator();

		ButtonGroup g;
		JRadioButton b;

		g = new ButtonGroup();
		b = new JRadioButton();
		b.setAction(aDay1);
		b.setSelected(true);
		mapPanel.setViewDay(DAY1);
		g.add(b);
		tb.add(b);
		b = new JRadioButton();
		b.setAction(aDay2);
		g.add(b);
		tb.add(b);
		b = new JRadioButton();
		b.setAction(aDay3);
		g.add(b);
		tb.add(b);

		tb.addSeparator();

		g = new ButtonGroup();
		b = new JRadioButton();
		b.setAction(aW12);
		b.setSelected(true);
		mapPanel.setViewArea(W12);
		g.add(b);
		tb.add(b);
		b = new JRadioButton();
		b.setAction(aE123);
		g.add(b);
		tb.add(b);
		b = new JRadioButton();
		b.setAction(aE456);
		g.add(b);
		tb.add(b);
		return tb;
	}

	final JFileChooser jFileChooser = new JFileChooser();

	final Action aDay1 = new AbstractAction("�P����") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY1);
		}
	};

	final Action aDay2 = new AbstractAction("�Q����") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY2);
		}
	};

	final Action aDay3 = new AbstractAction("�R����") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY3);
		}
	};

	final Action aW12 = new AbstractAction("���P�Q") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(W12);
		}
	};

	final Action aE123 = new AbstractAction("���P�Q�R") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(E123);
		}
	};

	final Action aE456 = new AbstractAction("���S�T�U") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(E456);
		}
	};

	final Action aOpen = new AbstractAction("�J�^���O�J�� ...") {
		public void actionPerformed(ActionEvent e) {
			File defaultRoot = MainFrame.this.doc.getCatalogRoot();
			jFileChooser.setSelectedFile(defaultRoot);
			int selected = jFileChooser.showOpenDialog(MainFrame.this);
			switch (selected) {
			case JFileChooser.APPROVE_OPTION:
				final File f = jFileChooser.getSelectedFile();
				System.out.println(f);
				MainFrame.this.doc.setCatalogRoot(f);
				break;
			default:
			}
		}
	};

	final Action aExit = new AbstractAction("�I��") {
		public void actionPerformed(ActionEvent e) {
			MainFrame.this.dispose();
		}
	};

	public static void main(final String args[]) {
		(new MainFrame()).setVisible(true);

	}

}
