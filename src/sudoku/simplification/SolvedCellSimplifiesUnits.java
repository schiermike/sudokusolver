package sudoku.simplification;

import java.util.List;

import sudoku.Candidates;
import sudoku.SudokuTreeItem;

public class SolvedCellSimplifiesUnits extends Simplifier 
{
	/**
	 * remove possibilities from row, column and square
	 */
	@Override
	protected void simplify() 
	{
		for(int row : lenIter)
		{
			for(int col : lenIter)
			{
				if(!sudoku.isFixed(row, col))
					continue;
				
				removeCandidateFromCells(row, col, sudoku.getCandidatesForRow(row));
				removeCandidateFromCells(row, col, sudoku.getCandidatesForCol(col));
				removeCandidateFromCells(row, col, sudoku.getCandidatesForBox(row, col));
			}
		}
	}
	
	private void removeCandidateFromCells(int row, int col, List<Candidates> candidatesList) 
	{
		char entry = sudoku.getEntry(row, col);
		for(Candidates candidates : candidatesList)
		{
			if(candidates.getRow() == row && candidates.getCol() == col)
				continue;
			
			if(!candidates.remove(entry))
				continue;
			
			if(node != null  && candidates.size() == 1)
			{
				node.add(new SudokuTreeItem("Solved", candidates, "="+candidates.iterator().next()));
				node.visualize(sudoku);
			}
			
			if(candidates.isEmpty())
				throw new ImpossibleBranchException();
				
			progress = true;
		}
		
	}
}
