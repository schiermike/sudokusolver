package sudoku;
import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import sudoku.simplification.BoxInteraction;
import sudoku.simplification.FindSingles;
import sudoku.simplification.HiddenTuples;
import sudoku.simplification.ImpossibleBranchException;
import sudoku.simplification.NakedTuples;
import sudoku.simplification.Simplifier;
import sudoku.simplification.SolvedCellSimplifiesUnits;
import sudoku.simplification.YWing;


public class SudokuSolver 
{
	private final Sudoku sudoku;
	private final SudokuGui gui;
	private ArrayList<Simplifier> simplifiers = new ArrayList<Simplifier>();
	
	private int maxConcurrentGuesses = 0;
	
	public SudokuSolver(Sudoku sudoku)
	{
		this.sudoku = sudoku;
		this.gui = null;
	}
	
	public SudokuSolver(Sudoku sudoku, SudokuGui sudokuGui)
	{
		this.sudoku = sudoku;
		this.gui = sudokuGui;
		
		useSimplifier(new SolvedCellSimplifiesUnits());
		useSimplifier(new FindSingles());
		useSimplifier(new BoxInteraction());
		useSimplifier(new NakedTuples());
		useSimplifier(new HiddenTuples());
		useSimplifier(new YWing());
	}
	
	public void useSimplifier(Simplifier simplifier)
	{
		simplifiers.add(simplifier);
	}
	
	public void clearSimplifiers()
	{
		simplifiers.clear();
	}
	
	public SudokuResult solve()
	{
		return solve(sudoku, maxConcurrentGuesses, gui == null ? null : gui.getRootNode());
	}
	
	/**
	 * recursive solving function, cascades with tryAndError
	 * recursionDepth is equal to the number of concurrently made guesses
	 * @param sudoku
	 * @param recursionDepth
	 * @param node
	 * @return
	 */
	private SudokuResult solve(Sudoku sudoku, int maxGuesses, SudokuTreeItem node)
	{
		while(true)
		{
			try 
			{
				simplify(sudoku, node);
			} 
			catch (ImpossibleBranchException e) 
			{
				return SudokuResult.IMPOSSIBLE;
			}
			
			if(sudoku.isSolved())
				return SudokuResult.SOLVED;
	
			/**
			 *  if we are already guessing - bad luck
			 *  
			 *  if we are not guessing, so we can start with it now
			 *  if we have no luck with that, we got stuck in this branch
			 *  else we go on to another loop
			 */
			if(maxGuesses <= 0)
			{
				if(gui != null)
					node.add(new DefaultMutableTreeNode("MAX_DEPTH reached"));
				return SudokuResult.STUCK;
			}
			SudokuTreeItem subNode = null;
			if(gui != null)
			{
				subNode = new SudokuTreeItem("tryAndError(" + maxGuesses + " guesses allowed)");
				node.add(subNode);
				gui.visualize(sudoku);
			}
			if(!tryAndError(sudoku, maxGuesses, subNode))
				return SudokuResult.STUCK;
				
		}
	}
	
	/**
	 * try to simplify the sudoku as far as possible by removing impossible entries
	 * @param sudoku
	 * @param node
	 */
	private void simplify(Sudoku sudoku, SudokuTreeItem node) 
	{
		while(true)
		{
			int failCount = 0;
			for(Simplifier simplifier : simplifiers)
			{
				if(simplifier.simplify(sudoku, node))
					break;
				failCount++;
			}
			if(failCount == simplifiers.size())
				break;
			
//			if(yWing(sudoku, node))
//				continue;
		}
	}
	
//=================================================================================================================
	
	private boolean tryAndError(Sudoku sudoku, int maxGuesses, SudokuTreeItem node)
	{
		// step 3: try to guess entry starting with the smallest possibilities sets
		for(int setSize = 2; setSize <= Sudoku.LENGTH; setSize++)
		{
			for(int row = 0; row < Sudoku.LENGTH; row++)
			{
				for(int col = 0; col < Sudoku.LENGTH; col++)
				{
					// ignore filled fields
					if(sudoku.isFixed(row, col))
						continue;
					// ignore unfilled fields with an inappropriate set size
					if(sudoku.getCandidates(row, col).size() != setSize)
						continue;
					
					// try all possible candidates
					Candidates candidates = sudoku.getCandidates(row, col);
					for(char candidate : candidates)
					{
						Sudoku sudokuClone = new Sudoku(sudoku);
						sudokuClone.setEntry(row, col, candidate);
						// now try to solve this simplified sudoku
						
						// IMPORTANT TUNING PART: the more possibilities are left, the fewer recursions are allowed
						//int newMaxGuesses = maxGuesses + 1 - setSize;
						int newMaxGuesses = maxGuesses - 1;
						
						SudokuTreeItem subNode = null;
						if(gui != null)
						{
							node.add(new SudokuTreeItem("TRY ", candidates, "="+candidate));				
							subNode = new SudokuTreeItem("solve(" + newMaxGuesses + " guesses allowed)");
							node.add(subNode);
							gui.visualize(sudoku);
						}
						SudokuResult result = solve(sudokuClone, newMaxGuesses, subNode);
						switch(result)
						{
							case SOLVED:
								// we were lucky and could solve the entire sudoku!
								sudokuClone.copyTo(sudoku);
								return true;
							case IMPOSSIBLE:
								// the choice turned out to be impossible, so we can exclude this possibility
								sudoku.getCandidates(row, col).remove(candidate);
								if(gui != null)
								{
									node.add(new SudokuTreeItem("IMPOSSIBLE ", candidates, "="+candidate));
									gui.visualize(sudoku);
								}
								return true;
							case STUCK:
								// we had no luck and just try the next candidate
								break;
						}
					}
				}
			}
		}
		
		return false;
	}
	
//=================================================================================================================

	public void setMaxConcurrentGuesses(int maxConcurrentGuesses) 
	{
		this.maxConcurrentGuesses = maxConcurrentGuesses;
	}
	
	public Sudoku getSudoku() 
	{
		return sudoku;
	}
}
