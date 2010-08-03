package sudoku.simplification;

import java.util.List;

import sudoku.Candidates;
import sudoku.Sudoku;
import sudoku.SudokuTreeItem;

public class NakedTuples extends Simplifier 
{
	@Override
	protected void simplify() 
	{
		for(int i : lenIter)
		{
			findNakedTuples(sudoku.getCandidatesForRow(i));
			findNakedTuples(sudoku.getCandidatesForCol(i));
			findNakedTuples(sudoku.getCandidatesForBox(i));
		}
	}
	
	private void findNakedTuples(List<Candidates> candidatesList) 
	{
		for(int n = 2; n <= Sudoku.LENGTH/2; n++)
			findNakedNTuples(n, candidatesList);
	}

	private List<Candidates> candidatesList;
	private void findNakedNTuples(int n, List<Candidates> candidatesList) 
	{
		this.candidatesList = candidatesList;
		tryNakedNTuples(n, new Candidates(), 0, 0);
	}
	
	private void tryNakedNTuples(int n, Candidates joinedSet, int numUsedSets, int minIndex)
	{
		if(n == numUsedSets)
		{
			removeNTupleFromOtherSets(joinedSet);
			return;
		}
		
		for(int i = minIndex; i < candidatesList.size(); i++)
		{
			Candidates candidates = candidatesList.get(i);
			if(candidates.size() == 1 || candidates.size() > n)
				continue;
			
			Candidates joinedSetCopy = new Candidates(joinedSet);
			joinedSetCopy.addAll(candidates);
			if(joinedSetCopy.size() > n)
				continue;
			
			tryNakedNTuples(n, joinedSetCopy, numUsedSets+1, i+1);
		}
	}
	
	private void removeNTupleFromOtherSets(Candidates xTuple) 
	{
		for(Candidates candidates : candidatesList)
		{
			// only remove from those candidate sets which aren't part of the tuple constellation
			if(!candidates.alsoContainsOthersThanThese(xTuple))
				continue;
			if(candidates.removeAll(xTuple))
			{
				if(node != null)
				{
					node.add(new SudokuTreeItem("Naked" + xTuple.size() + "Tuple", candidates, "!="+xTuple));
					node.visualize(sudoku);
				}
				progress = true;
			}
		}
		
	}
}
