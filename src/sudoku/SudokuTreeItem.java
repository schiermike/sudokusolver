package sudoku;

import javax.swing.tree.DefaultMutableTreeNode;



public class SudokuTreeItem extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	private SudokuGui gui;
	private final int row;
	private final int col;
	
	public SudokuTreeItem(String text, int row, int col, String value) 
	{
		super(text + " [" + (row+1) + "," + (col+1) + "]" + value);
		this.row = row;
		this.col = col;
	}
	
	public SudokuTreeItem(String text, Candidates candidates, String value)
	{
		this(text, candidates.getRow(), candidates.getCol(), value);
	}

	public SudokuTreeItem(String string) 
	{
		super(string);
		this.row = this.col = -1;
	}
	
	public SudokuTreeItem(SudokuGui gui) 
	{
		this("");
		this.gui = gui;
	}
	
	public int getRow() 
	{
		return row;
	}

	public int getCol() 
	{
		return col;
	}
	
	public boolean pointsAtCell()
	{
		return this.row >= 0 && this.col >= 0;
	}

	public int getIndex() 
	{
		return this.row * Sudoku.LENGTH + this.col;
	}
	
	public void add(SudokuTreeItem newChild) 
	{
		super.add(newChild);
		newChild.gui = this.gui;
	}

	public void visualize(Sudoku sudoku) 
	{
		gui.visualize(sudoku);
	}
}