package sudoku.simplification;

import java.util.Iterator;
import java.util.List;

import sudoku.Candidates;
import sudoku.SudokuTreeItem;

public class YWing extends Simplifier 
{
	private Candidates abCandidates;

	/**
	 * Find pairs with the following property:
	 * AB ... AC
	 * .      .
	 * .      .
	 * BC ... CDE
	 * we can deduce that C is not possible in the bottom-right cell
	 */
	@Override
	protected void simplify() 
	{
		for(int row : lenIter)
		{
			for(int col : lenIter)
			{
				// candidate for set AB
				abCandidates = sudoku.getCandidates(row, col);
				if(abCandidates.size() != 2)
					continue;
				
				tryCellAB();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void tryCellAB() 
	{
		Iterator<Character> iter = abCandidates.iterator();
		char a = iter.next();
		char b = iter.next();
		
		List<Candidates>[] rowColBoxCandidatesLists = new List[3];
		rowColBoxCandidatesLists[0] = sudoku.getCandidatesForRow(abCandidates.getRow());
		rowColBoxCandidatesLists[1] = sudoku.getCandidatesForCol(abCandidates.getCol());
		rowColBoxCandidatesLists[2] = sudoku.getCandidatesForBox(abCandidates.getRow(), abCandidates.getCol());
		
		for(List<Candidates> candidatesListA : rowColBoxCandidatesLists)
		{
			for(Candidates acCandidates : candidatesListA)
			{
				if(acCandidates.size() != 2)
					continue;
				if(!acCandidates.contains(a))
					continue;
				
				iter = acCandidates.iterator();
				char c = iter.next();
				c = a != c ? c : iter.next();
				
				for(List<Candidates> candidatesListB : rowColBoxCandidatesLists)
				{
					// AC and BC must not be in the same unit
					if(candidatesListA == candidatesListB)
						continue;
					
					for(Candidates bcCandidates : candidatesListB)
					{
						if(bcCandidates.size() != 2)
							continue;
						if(!bcCandidates.contains(b))
							continue;
						if(!bcCandidates.contains(c))
							continue;
						// special case: b = c -> all three sets must be equal
						if(b == c && !bcCandidates.equals(acCandidates))
							continue;
						if(bcCandidates.isContainedIn(candidatesListA))
							continue;
						if(acCandidates.isContainedIn(candidatesListB))
							continue;
						
						// we have a Y-Wing constellation
						reduceCandidates(c, acCandidates, bcCandidates);
					}
				}
				
			}
			
		}
	}

	private void reduceCandidates(char c, Candidates acCandidates, Candidates bcCandidates) 
	{
		// remove c from all row/col crossover areas
		removeFromCell(sudoku.getCandidates(acCandidates.getRow(), bcCandidates.getCol()), c);
		removeFromCell(sudoku.getCandidates(bcCandidates.getRow(), acCandidates.getCol()), c);
		
		// remove c from all row/col box crossover areas
		for(Candidates acBoxCandidates : sudoku.getCandidatesForBox(acCandidates.getRow(), acCandidates.getCol()))
		{
			if(acBoxCandidates.getRow() == bcCandidates.getRow() || acBoxCandidates.getCol() == bcCandidates.getCol())
				removeFromCell(acBoxCandidates, c);
		}
		
		for(Candidates bcBoxCandidates : sudoku.getCandidatesForBox(bcCandidates.getRow(), bcCandidates.getCol()))
		{
			if(bcBoxCandidates.getRow() == acCandidates.getRow() || bcBoxCandidates.getCol() == acCandidates.getCol())
				removeFromCell(bcBoxCandidates, c);
		}
	}

	private void removeFromCell(Candidates candidates, char value)
	{
		if(abCandidates.isIdentical(candidates))
			return;
		
		if(!candidates.remove(value))
			return;
		
		if(node != null)
		{
			node.add(new SudokuTreeItem("Y-Wing", candidates, "!=" + value));
			node.visualize(sudoku);
		}
		progress = true;
	}
}
