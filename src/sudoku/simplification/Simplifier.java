package sudoku.simplification;

import sudoku.Sudoku;
import sudoku.SudokuTreeItem;

public abstract class Simplifier 
{
	protected boolean progress;
	protected Sudoku sudoku;
	protected SudokuTreeItem node;
	
	protected final int[] lenIter;
	protected final int[] dimIter;
	
	public Simplifier()
	{
		lenIter = new int[Sudoku.LENGTH];
		for(int i = 0; i < Sudoku.LENGTH; i++)
			lenIter[i] = i;
		
		dimIter = new int[Sudoku.DIMENSION];
		for(int i = 0; i < Sudoku.DIMENSION; i++)
			dimIter[i] = i;
	}
	
	/**
	 * returns an array containing all other coordinates of cells from the same box
	 */
	protected final int[][] getOtherBoxCoords(int row, int col) 
	{
		int[][] coords = new int[Sudoku.LENGTH-1][2];
		int rowS = (row/Sudoku.DIMENSION)*Sudoku.DIMENSION;
		int colS = (col/Sudoku.DIMENSION)*Sudoku.DIMENSION;
		int i = 0;
		for(int row1 = rowS; row1 < rowS + Sudoku.DIMENSION; row1++)
			for(int col1 = colS; col1 < colS + Sudoku.DIMENSION; col1++)
			{
				if(row1 == row && col1 == col)
					continue;
				coords[i][0] = row1;
				coords[i][1] = col1;
				i++;
			}
		return coords;
	}
	
	/**
	 * Tries to simplify the sudoku and adds new nodes to the tree-node when simplification steps were performed
	 * @param sudoku
	 * @param node
	 * @return true on success
	 */
	public final boolean simplify(Sudoku sudoku, SudokuTreeItem node)
	{
		this.sudoku = sudoku;
		this.node = node;
		progress = false;
		
		simplify();
		
		return progress;
	}
	
	/**
	 * this is where all the logic goes
	 */
	protected abstract void simplify();
}
