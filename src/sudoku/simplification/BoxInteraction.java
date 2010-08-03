package sudoku.simplification;

import sudoku.Candidates;
import sudoku.Sudoku;
import sudoku.SudokuTreeItem;

public class BoxInteraction extends Simplifier 
{

	@Override
	protected void simplify() 
	{
		boxReducesRowCol();
		rowColReducesBox();
	}

	/**
	 * -x-|x--|--x    -x-|x--|---
	 * ---|---|x-x -> ---|---|x-x
	 * --x|-xx|-x-    --x|-xx|---
	 */
	private void rowColReducesBox() 
	{
		// iterate over rows or cols
		for(int row_col : lenIter)
		{
			Candidates[] pRow = new Candidates[Sudoku.DIMENSION];
			Candidates[] pCol = new Candidates[Sudoku.DIMENSION];
			for(int i : dimIter)
			{
				pRow[i] = new Candidates();
				pCol[i] = new Candidates();
			}
			
			for(int col_row : lenIter)
			{
				pRow[col_row/Sudoku.DIMENSION].addAll(sudoku.getCandidates(row_col, col_row));
				pCol[col_row/Sudoku.DIMENSION].addAll(sudoku.getCandidates(col_row, row_col));
			}
			
			for(int i : dimIter)
			{
				rowRemoveFromBox(pRow, row_col, i);
				colRemoveFromBox(pCol, row_col, i);
			}
		}
	}
	
	private void rowRemoveFromBox(Candidates[] pRow, int row, int boxCol) 
	{
		Candidates pOnlyThisRow = new Candidates(pRow[boxCol]);
		for(int i = 0; i < Sudoku.DIMENSION; i++)
			if(i != boxCol)
				pOnlyThisRow.removeAll(pRow[i]);
		
		if(pOnlyThisRow.isEmpty())
			return;

		for(int row1 = (row/Sudoku.DIMENSION)*Sudoku.DIMENSION; row1 < (row/Sudoku.DIMENSION)*Sudoku.DIMENSION + Sudoku.DIMENSION; row1++)
		{
			if(row == row1)
				continue;
			
			for(int col1 = boxCol*Sudoku.DIMENSION; col1 < boxCol*Sudoku.DIMENSION + Sudoku.DIMENSION; col1++)
			{
				Candidates p = new Candidates(pOnlyThisRow);
				p.retainAll(sudoku.getCandidates(row1, col1));
				if(p.size()==0)
					continue;
				
				sudoku.getCandidates(row1, col1).removeAll(p);
				if(node != null)
				{
					node.add(new SudokuTreeItem("RowReducesBox-Interaction", row1, col1, "!="+p));
					node.visualize(sudoku);
				}
				progress = true;
			}
		}
	}
	
	private void colRemoveFromBox(Candidates[] pCol, int col, int boxRow) 
	{
		Candidates pOnlyThisCol = new Candidates(pCol[boxRow]);
		for(int i : dimIter)
			if(i != boxRow)
				pOnlyThisCol.removeAll(pCol[i]);
		
		if(pOnlyThisCol.isEmpty())
			return;

		for(int col1 = (col/Sudoku.DIMENSION)*Sudoku.DIMENSION; col1 < (col/Sudoku.DIMENSION)*Sudoku.DIMENSION + Sudoku.DIMENSION; col1++)
		{
			if(col == col1)
				continue;
			
			for(int row1 = boxRow*Sudoku.DIMENSION; row1 < boxRow*Sudoku.DIMENSION + Sudoku.DIMENSION; row1++)
			{
				Candidates p = new Candidates(pOnlyThisCol);
				p.retainAll(sudoku.getCandidates(row1, col1));
				if(p.size()==0)
					continue;
				
				sudoku.getCandidates(row1, col1).removeAll(p);
				if(node != null)
				{
					node.add(new SudokuTreeItem("ColReducesBox-Interaction", row1, col1, "!="+p));
					node.visualize(sudoku);
				}
				progress = true;
			}
		}
	}

	/**
	 * -xx|---|---    -xx|---|---
	 * x--|---|x-x -> ---|---|x-x
	 * ---|x--|---    ---|x--|---
	 */
	private void boxReducesRowCol() 
	{
		// iterate over boxes
		for(int row = 0; row < Sudoku.LENGTH; row += Sudoku.DIMENSION)
		{
			for(int col = 0; col < Sudoku.LENGTH; col += Sudoku.DIMENSION)
			{
				// reduce rows and cols
				Candidates[] pRow = new Candidates[Sudoku.DIMENSION];
				Candidates[] pCol = new Candidates[Sudoku.DIMENSION];
				for(int i = 0; i < Sudoku.DIMENSION; i++)
				{
					pRow[i] = new Candidates();
					pCol[i] = new Candidates();
				}
				
				for(int boxRow : dimIter)
				{
					for(int boxCol : dimIter)
					{
						pRow[boxRow].addAll(sudoku.getCandidates(row + boxRow, col + boxCol));
						pCol[boxCol].addAll(sudoku.getCandidates(row + boxRow, col + boxCol));
					}
				}
				
				for(int i : dimIter)
				{
					boxRemoveFromRow(pRow, row+i, col, i);
					boxRemoveFromCol(pCol, row, col+i, i);
				}
			}
		}
		
	}
	
	private void boxRemoveFromRow(Candidates[] pRow, int boxRow, int boxCol, int rowInBox) 
	{
		Candidates pOnlyThisRow = new Candidates(pRow[rowInBox]);
		for(int i : dimIter)
			if(i != rowInBox)
				pOnlyThisRow.removeAll(pRow[i]);

		if(pOnlyThisRow.isEmpty())
			return;
		
		for(int i = 0; i < Sudoku.LENGTH-Sudoku.DIMENSION; i++)
		{
			int col = (boxCol + Sudoku.DIMENSION + i)%Sudoku.LENGTH;
			
			Candidates p = new Candidates(pOnlyThisRow);
			p.retainAll(sudoku.getCandidates(boxRow, col));
			if(p.size()==0)
				continue;
			
			sudoku.getCandidates(boxRow, col).removeAll(p);
			if(node != null)
			{
				node.add(new SudokuTreeItem("BoxReducesRow-Interaction", boxRow, col, "!="+p));
				node.visualize(sudoku);
			}
			progress = true;
		}
	}
	
	private void boxRemoveFromCol(Candidates[] pCol, int boxRow, int boxCol, int colInBox) 
	{
		Candidates pOnlyThisCol = new Candidates(pCol[colInBox]);
		for(int i : dimIter)
			if(i != colInBox)
				pOnlyThisCol.removeAll(pCol[i]);

		if(pOnlyThisCol.isEmpty())
			return;
		
		for(int i = 0; i < Sudoku.LENGTH-Sudoku.DIMENSION; i++)
		{
			int row = (boxRow + Sudoku.DIMENSION + i)%Sudoku.LENGTH;
			
			Candidates p = new Candidates(pOnlyThisCol);
			p.retainAll(sudoku.getCandidates(row, boxCol));
			if(p.size()==0)
				continue;
			
			sudoku.getCandidates(row, boxCol).removeAll(p);
			if(node != null)
			{
				node.add(new SudokuTreeItem("BoxReducesCol-Interaction", row, boxCol, "!="+p));
				node.visualize(sudoku);
			}
			progress = true;
		}
	}
}
