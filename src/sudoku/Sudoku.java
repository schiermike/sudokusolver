package sudoku;

import java.util.ArrayList;
import java.util.List;


public class Sudoku
{
	/**
	 * these values have to be initially set
	 */
	public static int DIMENSION = 3;
	public static int LENGTH = 9;
	public static int SIZE = 81;
	public static final Candidates ALL_CANDIDATES = new Candidates();
	
	/**
	 * cell items, can be replaces with letters
	 */
	static
	{
		for(char entry = '1'; entry <= '9'; entry++)
			ALL_CANDIDATES.add(entry);
	}
	
	private Candidates[][] candidates = new Candidates[LENGTH][LENGTH];
	
	public static int index(int row, int col)
	{
		return LENGTH * row + col;
	}
	
	public Candidates getCandidates(int row, int col)
	{
		return candidates[row][col];
	}
	
	public List<Candidates> getCandidatesForRow(int row)
	{
		List<Candidates> list = new ArrayList<Candidates>();
		for(int col = 0; col < LENGTH; col++)
			list.add(getCandidates(row, col));
		return list;
	}
	
	public List<Candidates> getCandidatesForCol(int col)
	{
		List<Candidates> list = new ArrayList<Candidates>();
		for(int row = 0; row < LENGTH; row++)
			list.add(getCandidates(row, col));
		return list;
	}
	
	/**
	 * box numbering:
	 * 012
	 * 345
	 * 678 
	 */
	public List<Candidates> getCandidatesForBox(int box)
	{
		List<Candidates> list = new ArrayList<Candidates>();
		int r = (box/DIMENSION)*DIMENSION;
		int c = (box%DIMENSION)*DIMENSION;
		for(int row = r; row < r + DIMENSION; row++)
			for(int col = c; col < c + DIMENSION; col++)
			list.add(getCandidates(row, col));
		return list;
	}
	
	public List<Candidates> getCandidatesForBox(int row, int col) 
	{
		return getCandidatesForBox((row/DIMENSION)*DIMENSION + col/DIMENSION);
	}
	
	public boolean isSolved()
	{
		return countFixed() == SIZE;
	}
	
	public char getEntry(int row, int col)
	{
		return getCandidates(row, col).iterator().next();
	}
	
	public void setEntry(int row, int col, char entry)
	{
		Candidates p = getCandidates(row, col);
		p.clear();
		p.add(entry);
	}
	
	public boolean isFixed(int row, int col)
	{
		return getCandidates(row, col).size() == 1;
	}
	
	public Sudoku(Sudoku other)
	{
		other.copyTo(this);
	}
	
	public Sudoku() 
	{
		clear();
	}
	
	public Sudoku(String sudokuString)
	{
		set(sudokuString);
	}
	
	public void clear()
	{
		for(int row = 0; row < LENGTH; row++)
			for(int col = 0; col < LENGTH; col++)
				candidates[row][col] = new Candidates(row, col, ALL_CANDIDATES);
	}
	
	public void set(String sudokuString)
	{
		clear();
		
		sudokuString = sudokuString.replaceAll("\\n", "").trim();
		if(sudokuString.length() != SIZE)
			throw new IllegalArgumentException("Sudoku-String has to have a length of " + SIZE + "!");
		
		int stringIndex = 0;
		for(int row = 0; row < LENGTH; row++)
		{
			for(int col = 0; col < LENGTH; col++)
			{
				char c = sudokuString.charAt(stringIndex++);
				if(c == '.' || c == '-')
					continue;
				
				if(c == '{')
				{
					while(c != '}')
					{
						c = sudokuString.charAt(stringIndex++);
						if(ALL_CANDIDATES.contains(c))
							getCandidates(row, col).add(c);
					}
				}
				else if(ALL_CANDIDATES.contains(c))
					setEntry(row, col, c);
			}
		}
	}
	
	@Override
	public String toString() 
	{
		return toString(false);
	}
	
	public String toString(boolean includeNonfixedEntries) 
	{
		StringBuffer buffer = new StringBuffer();
		for(int row = 0; row < LENGTH; row++)
			for(int col = 0; col < LENGTH; col++)
			{
				if(isFixed(row, col))
					buffer.append(getEntry(row, col));
				else if(includeNonfixedEntries)
					buffer.append("{" + getCandidates(row,col).toString() + "}");
				else
					buffer.append('.');
						
			}
		System.out.println(buffer);
		return buffer.toString();
	}
	
	public void copyTo(Sudoku target) 
	{
		for(int row = 0; row < LENGTH; row++)
			for(int col = 0; col < LENGTH; col++)
				target.candidates[row][col] = new Candidates(this.candidates[row][col]);
	}

	public int countFixed() 
	{
		int numFixed = 0;
		for(int row = 0; row < LENGTH; row++)
			for(int col = 0; col < LENGTH; col++)
				if(isFixed(row, col))
					numFixed++;
		return numFixed;
	}


	public String toBlockString() 
	{
		StringBuffer buffer = new StringBuffer();
		for(int row = 0; row < LENGTH; row++)
		{
			for(int col = 0; col < LENGTH; col++)
				buffer.append(isFixed(row, col) ? getEntry(row, col) : "-");
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object other) 
	{
		if(!(other instanceof Sudoku))
			return false;
		Sudoku s2 = (Sudoku)other;
		
		return this.candidates.equals(s2.candidates);
	}
}
