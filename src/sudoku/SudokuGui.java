package sudoku;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


public class SudokuGui extends JApplet
{
	//private static final String exampleSudoku = ".94...13..............76..2.8..1.....32.........2...6.....5.4.......8..7..63.4..8";
	//private static final String exampleSudoku = ".4398.25.6..425...2....1.949....4.7.3..6.8...41.2.9..382.5.........4...553489.71.";
	private static final String exampleSudoku = ".....8.2......693..98.7...1...........921....7......9624..9.......3..18.........3";
	//private static final String exampleSudoku = "7.1....8...5.7.2.......67..5..1...2.....9.....2...4..5..84.......3.6.5...7....4.9";
	//private static final String exampleSudoku = "--A----C-----O-I-J--A-B-P-CGF-H---D--F-I-E----P--G-EL-H----M-J------E----C--G----I--K-GA-B---E-JD-GP--J-F----A---E---C-B--DP--O-E--F-M--D--L-K-A-C--------O-I-L-H-P-C--F-A--B------G-OD---J----HK---J----H-A-P-L--B--P--E--K--A--H--B--K--FI-C----F---C--D--H-N-";
	
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, JLabel> fields = new HashMap<Integer, JLabel>();
	
	private SudokuSolver solver;
	
	private Semaphore semaphore = new Semaphore(-1);
	private SudokuTreeItem rootNode;
	private JTree tree;
	private JScrollPane treeView;
	private SolverThread solverThread = null;
	private JComboBox maxGuessesBox;
	
	public SudokuGui() 
	{	
//		Sudoku.DIMENSION = 4;
//		Sudoku.LENGTH = 16;
//		Sudoku.SIZE = 256;
//		Sudoku.CANDIDATES.clear();
//		for(char c = 'A'; c < 'A'+16; c++)
//			Sudoku.CANDIDATES.add(c);
		
		this.setName("SudokuSolver");
		Container container = this.getContentPane();
		container.setLayout(null);
		container.add(createSudokuTable());
		container.add(createButtonPanel());
		container.add(createTreeView());
		
		Sudoku sudoku = new Sudoku(exampleSudoku);
		solver = new SudokuSolver(sudoku, this);
		
		displaySudoku(sudoku);
	}
	
	private Component createTreeView() 
	{
		rootNode = new SudokuTreeItem(this);
		tree = new JTree(rootNode);
		tree.setToggleClickCount(1);
		tree.setToggleClickCount(1);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setAutoscrolls(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() 
		{
			private JLabel selection = null;
			private Color oldColor = null;
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{
				SudokuTreeItem treeItem = (SudokuTreeItem)e.getPath().getLastPathComponent();
				if(!treeItem.pointsAtCell())
					return;
				if(selection != null)
					selection.setForeground(oldColor);
				selection = fields.get(treeItem.getIndex());
				oldColor = selection.getForeground();
				selection.setForeground(Color.ORANGE);
			}
		});
		treeView = new JScrollPane(tree);
		treeView.setBounds(910, 5, 400, 920);
		treeView.getVerticalScrollBar().setBlockIncrement(5);
		treeView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		treeView.setVerticalScrollBar(new JScrollBar(JScrollBar.VERTICAL));
		return treeView;
	}

	private Component createButtonPanel() 
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBounds(5, 910, 900, 50);
		
		JLabel label = new JLabel("max. concurrent guesses:");
		buttonPanel.add(label);
		
		maxGuessesBox = new JComboBox(new Object[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
		maxGuessesBox.setSelectedIndex(0);
		maxGuessesBox.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int maxGuesses = maxGuessesBox.getSelectedIndex() >= 0 ? maxGuessesBox.getSelectedIndex() : 0;
				solver.setMaxConcurrentGuesses(maxGuesses);
			}
		});
		buttonPanel.add(maxGuessesBox);
		
		JButton loadButton = new JButton();
		loadButton.setText("load Sudoku");
		loadButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				load();
			}
		});
		buttonPanel.add(loadButton);
		
		JButton stepButton = new JButton();
		stepButton.setText("solve step-by-step");
		stepButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				step(1);
			}
		});
		buttonPanel.add(stepButton);
		
		JButton runButton = new JButton();
		runButton.setText("just solve");
		runButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				step(1000000);
			}
		});
		buttonPanel.add(runButton);
		
		JButton stopButton = new JButton();
		stopButton.setText("stop");
		stopButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				stopThread();
			}
		});
		buttonPanel.add(stopButton);
		
		return buttonPanel;
	}

	private Component createSudokuTable()
	{
		JPanel sudokuPanel = new JPanel();
		sudokuPanel.setLayout(new GridLayout(0,Sudoku.DIMENSION,5,5));
		sudokuPanel.setBounds(5, 5, 900, 900);
		HashMap<Integer, Container> containerMap = new HashMap<Integer, Container>();
		for(int row=0; row < Sudoku.DIMENSION; row++)
			for(int col=0; col < Sudoku.DIMENSION; col++)
			{
				JPanel subContainer = new JPanel();
				subContainer.setPreferredSize(new Dimension(220, 220));
				subContainer.setLayout(new GridLayout(0,Sudoku.DIMENSION,1,1));
				containerMap.put(Sudoku.DIMENSION*row + col, subContainer);
				sudokuPanel.add(subContainer);
			}
		
		for(int row=0; row < Sudoku.LENGTH; row++)
			for(int col=0; col < Sudoku.LENGTH; col++)
			{
				JLabel label = new JLabel();
				label.setBorder(new LineBorder(Color.GRAY, 1));
				label.setHorizontalAlignment(JLabel.CENTER);
				fields.put(row*Sudoku.LENGTH+col, label);
				containerMap.get( (row/Sudoku.DIMENSION) * Sudoku.DIMENSION + (col/Sudoku.DIMENSION) ).add(label);
			}
		return sudokuPanel;
	}
	
	private void load()
	{
		stopThread();
		
		String sudokuText = new InputDialog(solver.getSudoku().toBlockString()).getInputText();
		solver = new SudokuSolver(new Sudoku(sudokuText), this);
		displaySudoku(solver.getSudoku());
	}
	
	private void stopThread()
	{
		// ensure that the last thread stopped
		if(solverThread != null)
		{
			solverThread.halt();
			semaphore.release(10000);
			try 
			{
				solverThread.join();
			} 
			catch (InterruptedException e) {/*not needed*/}
		}
		semaphore.drainPermits();
		getRootNode().removeAllChildren();
	}
		
	private void startThread()
	{
		solverThread = new SolverThread(this);
		solverThread.start();
	}
	
	private void step(int numSteps)
	{
		if(!semaphore.hasQueuedThreads())
		{
			stopThread();
			startThread();
		}
		
		if(semaphore.availablePermits() == 0)
			semaphore.release(numSteps);
	}
	
	private void displaySudoku(Sudoku sudoku)
	{
		for(int row = 0; row < Sudoku.LENGTH; row++)
			for(int col = 0; col < Sudoku.LENGTH; col++)
			{
				JLabel label = fields.get(Sudoku.LENGTH*row + col);
				label.setText("<html><p>" + sudoku.getCandidates(row, col).toString(" ") + "</p></html>");
				if(sudoku.isFixed(row, col))
				{
						label.setForeground(Color.black);
						label.setFont(label.getFont().deriveFont(40f));
				}
				else
				{
					label.setForeground(Color.red);
					label.setFont(label.getFont().deriveFont(10f));
				}
			}
		
		TreePath path = tree.getSelectionPath();
		((DefaultTreeModel)tree.getModel()).reload();
		treeView.getVerticalScrollBar().setValue(treeView.getVerticalScrollBar().getMaximum()+10);
		
		if(path != null)
		{
			tree.expandPath(path);
			tree.setSelectionPath(path);
		}
	}
	
	public void visualize(Sudoku sudoku) 
	{
		displaySudoku(sudoku);
		
		if(Thread.currentThread() instanceof SolverThread)
		{
			try 
			{
				semaphore.acquire();
				if(((SolverThread)Thread.currentThread()).shouldHalt())
					throw new IllegalStateException("Thread stopped");
			} 
			catch (InterruptedException e) {/*not needed*/} 
		}
	}

	public SudokuTreeItem getRootNode() 
	{
		return rootNode;
	}
	
	private static class InputDialog extends JDialog
	{
		private static final long serialVersionUID = 1L;
		private JTextArea textArea;

		public InputDialog(String initialText)
		{
			textArea = new JTextArea(initialText);
			this.getContentPane().add(textArea);
			
			this.setName("edit Sudoku");
			this.setSize(140, 280);
			this.setResizable(false);
			this.setModal(true);
			this.setVisible(true);
		}
		
		public String getInputText()
		{
			return textArea.getText();
		}
	}
	
	private class SolverThread extends Thread
	{
		public static final String NAME = "SolverThread";
		private boolean stop = false;

		public SolverThread(SudokuGui sudokuGui)
		{
			this.setName(NAME);
		}
		
		@Override
		public void run() 
		{
			try 
			{
				SudokuResult result = solver.solve();
				rootNode.add(new SudokuTreeItem(result.toString()));
			}
			catch(IllegalStateException e) 
			{
				rootNode.add(new SudokuTreeItem("STOPPED"));	
			}

			
			displaySudoku(solver.getSudoku());
			semaphore.drainPermits();
		}

		public void halt() 
		{
			stop = true;
		}
		
		public boolean shouldHalt()
		{
			return stop;
		}
	}
}
