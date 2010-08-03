package sudoku.simplification;

import java.util.List;

import sudoku.Candidates;
import sudoku.SudokuTreeItem;

public class FindSingles extends Simplifier 
{
	/**
	 * find unique possibilites in row, column and field
	 * example: -> deduce 3 if 3 in x
	 * x74......
	 * ....3....
	 * ........3
	 */
	@Override
	protected void simplify() 
	{
		for(int i : lenIter)
		{
			findSinglesInCells(sudoku.getCandidatesForRow(i), "Row " + i);
			findSinglesInCells(sudoku.getCandidatesForCol(i), "Col " + i);
			findSinglesInCells(sudoku.getCandidatesForBox(i), "Box " + i);
		}
	}

	private void findSinglesInCells(List<Candidates> candidatesList, String info) 
	{
		for(Candidates candidates : candidatesList)
		{
			if(candidates.size() == 1)
				continue;
			
			Candidates tempCand = new Candidates(candidates);
			for(Candidates candidates2 : candidatesList)
			{
				if(candidates == candidates2) // same object by address
					continue;
				tempCand.removeAll(candidates2);
			}
			if(tempCand.size() != 1)
				continue;
			
			candidates.clear();
			candidates.addAll(tempCand);
			if(node != null)
			{
				node.add(new SudokuTreeItem("SingleCol", candidates, "="+candidates.iterator().next()));
				node.visualize(sudoku);
			}
			progress = true;
		}
	}
}
