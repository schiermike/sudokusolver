package sudoku.simplification;

import sudoku.Candidates;
import sudoku.SudokuTreeItem;

public class HiddenTuples extends Simplifier 
{
	@Override
	protected void simplify() 
	{
		for(int row : lenIter)
		{
			for(int col : lenIter)
			{
				Candidates p = sudoku.getCandidates(row, col);
				
				hiddenRowTuples(row, col, p);
				hiddenColTuples(row, col, p);
				hiddenBoxTuples(row, col, p);
			}
		}
	}
	
	private void hiddenRowTuples(int row, int col, Candidates p) 
	{
		// we intersect row,col with row,col1
		for(int col1 : lenIter)
		{
			if(col == col1)
				continue;
			Candidates pIntersect = new Candidates(p);
			pIntersect.retainAll(sudoku.getCandidates(row, col1));
			if(pIntersect.size() <= 1)
				continue;
			int intersectCount = 2;
			
			// and then we check whether we can further intersect this set with the rest of the row
			for(int col2 : lenIter)
			{
				if(col2 == col || col2 == col1)
					continue;
				Candidates pIntersect2 = new Candidates(pIntersect);
				pIntersect2.retainAll(sudoku.getCandidates(row, col2));
				if(pIntersect2.isEmpty())
					continue;
				
				// we have an intersection - the new intersection set may be smaller than the previous one
				pIntersect = pIntersect2;
				intersectCount++;
				
				if(pIntersect.size() < intersectCount)
					break;
			}
			
			// now if the intersection set size is greater or equal the number of its occurrences, we can eliminate the other possibilities
			if(pIntersect.size() < intersectCount)
				continue;
			
			for(int col2 : lenIter)
			{
				Candidates pCell = sudoku.getCandidates(row, col2); 
				if(!pCell.equals(pIntersect) && pCell.containsAll(pIntersect))
				{
					pCell.clear();
					pCell.addAll(pIntersect);
					progress = true;
					if(node != null)
					{
						node.add(new SudokuTreeItem("HiddenTuplesRow", row, col2, "="+pCell));
						node.visualize(sudoku);
					}
				}
			}
		}
	}
	

	private void hiddenColTuples(int row, int col, Candidates p) 
	{
		// we intersect row,col with row1,col
		for(int row1 : lenIter)
		{
			if(row == row1)
				continue;
			Candidates pIntersect = new Candidates(p);
			pIntersect.retainAll(sudoku.getCandidates(row1, col));
			if(pIntersect.size() <= 1)
				continue;
			int intersectCount = 2;
			
			// and then we check whether we can further intersect this set with the rest of the col
			for(int row2 : lenIter)
			{
				if(row2 == row || row2 == row1)
					continue;
				Candidates pIntersect2 = new Candidates(pIntersect);
				pIntersect2.retainAll(sudoku.getCandidates(row2, col));
				if(pIntersect2.isEmpty())
					continue;
				
				// we have an intersection - the new intersection set may be smaller than the previous one
				pIntersect = pIntersect2;
				intersectCount++;
				
				if(pIntersect.size() < intersectCount)
					break;
			}
			
			// now if the intersection set size is greater or equal the number of its occurrences, we can eliminate the other possibilities
			if(pIntersect.size() < intersectCount)
				continue;
			
			for(int row2 : lenIter)
			{
				Candidates pCell = sudoku.getCandidates(row2, col); 
				if(!pCell.equals(pIntersect) && pCell.containsAll(pIntersect))
				{
					pCell.clear();
					pCell.addAll(pIntersect);
					progress = true;
					if(node != null)
					{
						node.add(new SudokuTreeItem("HiddenTuplesCol", row2, col, "="+pCell));
						node.visualize(sudoku);
					}
				}
			}
		}
	}
	

	private void hiddenBoxTuples(int row, int col, Candidates p) 
	{
		// we intersect row,col with row1,col1
		for(int[] coord1 : getOtherBoxCoords(row, col))
		{
			if(row == coord1[0] && col == coord1[1])
				continue;
			
			Candidates pIntersect = new Candidates(p);
			pIntersect.retainAll(sudoku.getCandidates(coord1[0], coord1[1]));
			if(pIntersect.size() <= 1)
				continue;
			int intersectCount = 2;
			
			// and then we check whether we can further intersect this set with the rest of the box
			for(int[] coord2 : getOtherBoxCoords(row, col))
			{
				if(row == coord2[0] && col == coord2[1] || coord1[0] == coord2[0] && coord1[1] == coord2[1])
					continue;
				Candidates pIntersect2 = new Candidates(pIntersect);
				pIntersect2.retainAll(sudoku.getCandidates(coord2[0], coord2[1]));
				
				if(pIntersect2.isEmpty())
					continue;
				
				// we have an intersection - the new intersection set may be smaller than the previous one
				pIntersect = pIntersect2;
				intersectCount++;
				
				if(pIntersect.size() < intersectCount)
					break;
			}
			if(pIntersect.size() < intersectCount)
				break;
			
			// now if the intersection set size is greater or equal the number of its occurrences, we can eliminate the other possibilities
			if(pIntersect.size() < intersectCount)
				continue;
			
			for(int[] coord2 : getOtherBoxCoords(row, col))
			{
				Candidates pCell = sudoku.getCandidates(coord2[0], coord2[1]);
				if(!pCell.equals(pIntersect) && pCell.containsAll(pIntersect))
				{
					pCell.clear();
					pCell.addAll(pIntersect);
					progress = true;
					if(node != null)
					{
						node.add(new SudokuTreeItem("HiddenTuplesBox", coord2[0], coord2[1], "="+pCell));
						node.visualize(sudoku);
					}
				}
			}
		}
	}

}
