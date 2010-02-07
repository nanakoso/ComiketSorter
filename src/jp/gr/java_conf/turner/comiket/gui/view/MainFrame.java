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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

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
		this.setTitle("コミケソータ");
		this.setSize(600, 500);

		this.getRootPane().setJMenuBar(initMenu());

		this.doc = new MapDocument();
		this.mapPanel = new MapPanel(doc);
		this.doc.addObserver(this.mapPanel);

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(this.mapPanel, BorderLayout.CENTER);

		this.getContentPane().add(initToolbar(), BorderLayout.NORTH);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// キャンセル等未実装
				MainFrame.this.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				MapDocument.shutdownNow();
				System.exit(0);
			}
		});
	}

	/**
	 * @return
	 */
	private JMenuBar initMenu() {
		final JMenuBar mb = new JMenuBar();
		final JMenu mFile = new JMenu("ファイル");

		mFile.add(aCatalogOpen);
		mFile.add(aCSVOpen);
		mFile.addSeparator();
		mFile.add(aSaveCSV);
		mFile.add(aSaveAsCSV);
		mFile.addSeparator();
		mFile.add(aExit);
		mb.add(mFile);

		return mb;
	}

	private JToolBar initToolbar() {
		JToolBar tb = new JToolBar();
		tb.add(aCatalogOpen);
		tb.add(aSort);
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

	final Action aSort = new AbstractAction("SORT!") {
		public void actionPerformed(ActionEvent e) {
			doc.sort();
		}
	};

	final Action aDay1 = new AbstractAction("１日目") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY1);
		}
	};

	final Action aDay2 = new AbstractAction("２日目") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY2);
		}
	};

	final Action aDay3 = new AbstractAction("３日目") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewDay(DAY3);
		}
	};

	final Action aW12 = new AbstractAction("西１２") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(W12);
		}
	};

	final Action aE123 = new AbstractAction("東１２３") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(E123);
		}
	};

	final Action aE456 = new AbstractAction("東４５６") {
		public void actionPerformed(ActionEvent e) {
			mapPanel.setViewArea(E456);
		}
	};

	final Action aCatalogOpen = new AbstractAction("カタログ開く ...") {

		private final JFileChooser jFileChooser = new JFileChooser();

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
			}
		}

		{
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
	};

	final Action aCSVOpen = new AbstractAction("CSVを開く ...") {

		private final JFileChooser jCSVFileChooser = new JFileChooser();

		public void actionPerformed(ActionEvent e) {
			File csvFile = MainFrame.this.doc.getCSVFile();
			jCSVFileChooser.setSelectedFile(csvFile);
			int selected = jCSVFileChooser.showOpenDialog(MainFrame.this);
			switch (selected) {
			case JFileChooser.APPROVE_OPTION:
				final File f = jCSVFileChooser.getSelectedFile();
				System.out.println(f);
				MainFrame.this.doc.setCSVRoot(f);
				break;
			}
		}

		{
			jCSVFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jCSVFileChooser.setFileFilter(CSV_FILE_FILTER);

		}

	};

	final Action aSaveAsCSV = new AbstractAction("ファイルを指定してセーブ") {
		private final JFileChooser jCSVFileSaveChooser = new JFileChooser();

		public void actionPerformed(ActionEvent e) {
			File csvFile = MainFrame.this.doc.getCSVFile();
			this.jCSVFileSaveChooser.setSelectedFile(csvFile);
			int selected = jCSVFileSaveChooser.showOpenDialog(MainFrame.this);
			switch (selected) {
			case JFileChooser.APPROVE_OPTION:
				final File f = jCSVFileSaveChooser.getSelectedFile();
				System.out.println(f);
				MainFrame.this.doc.saveCSVTo(f, MainFrame.this);
				break;
			}
		}

		{
			jCSVFileSaveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jCSVFileSaveChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			jCSVFileSaveChooser.setDialogTitle("保存");
			jCSVFileSaveChooser.setApproveButtonText("保存");
			jCSVFileSaveChooser.setApproveButtonToolTipText("保存するファイル名を指定する");

			jCSVFileSaveChooser.setFileFilter(CSV_FILE_FILTER);

		}
	};

	final static FileFilter CSV_FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			String n = f.getName();
			if (n != null && n.toUpperCase().endsWith("CSV")) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "CD-ROMカタログチェックセーブファイル";
		}
	};

	final Action aSaveCSV = new AbstractAction("セーブ") {
		public void actionPerformed(ActionEvent e) {
			MainFrame.this.doc.saveCSV(MainFrame.this);
		}
	};

	final Action aExit = new AbstractAction("終了") {
		public void actionPerformed(ActionEvent e) {
			MainFrame.this.dispose();
		}
	};

	public static void main(final String args[]) {
		(new MainFrame()).setVisible(true);

	}

	DropTargetListener dtl = new DropTargetAdapter() {

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
				return;
			}
			dtde.rejectDrag();
		}

		public void drop(DropTargetDropEvent dtde) {
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable t = dtde.getTransferable();
				try {
					List list = (List) t
							.getTransferData(DataFlavor.javaFileListFlavor);
					boolean csvDone = false;
					boolean rootDone = false;
					for (Object o : list) {
						if (o instanceof File) {
							File f = (File) o;
							if (f.isFile() && csvDone == false) {
								MainFrame.this.doc.setCSVRoot(f);
								csvDone = true;
							} else if (f.isDirectory() && rootDone == false) {
								MainFrame.this.doc.setCatalogRoot(f);
								rootDone = true;
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dtde.dropComplete(true);
				return;
			}
		}
	};
	{
		new DropTarget(this, DnDConstants.ACTION_COPY, dtl, true);
	}
}
