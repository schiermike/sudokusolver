package sudoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Candidates extends HashSet<Character> 
{
	private static final long serialVersionUID = 1L;
	private final int row;
	private final int col;
	
	public Candidates()
	{
		this.row = -1;
		this.col = -1;
	}
	
	public Candidates(int row, int col)
	{
		this.row = row;
		this.col = col;
	}

	public Candidates(int row, int col, Candidates other) 
	{
		super(other);
		this.row = row;
		this.col = col;
	}

	public Candidates(Candidates other) 
	{
		super(other);
		this.row = other.row;
		this.col = other.col;
	}

	public int getRow() 
	{
		return row;
	}

	public int getCol() 
	{
		return col;
	}
	
	@Override
	public String toString() 
	{
		return toString("");
	}
	
	public String toString(String separator) 
	{
		StringBuffer buffer = new StringBuffer();
		ArrayList<Character> p = new ArrayList<Character>(this);
		Collections.sort(p);
		for(Character c : p)
			buffer.append(c + separator);
		buffer.delete(buffer.length()-separator.length(), buffer.length());
		return buffer.toString();
	}

	// true iff this minus other != empty set
	public boolean alsoContainsOthersThanThese(Candidates other) 
	{
		for(char c : this)
			if(!other.contains(c))
				return true;
		return false;
	}

	/**
	 * extended version of .contains which also considers the coordinates
	 */
	public boolean isContainedIn(Collection<Candidates> candidatesCollection)
	{
		for(Candidates cand : candidatesCollection)
			if(isIdentical(cand))
				return true;
				
		return false;
	}

	public boolean isIdentical(Candidates candidates) 
	{
		if(this == candidates)
			return true;
		
		return candidates.row == this.row && candidates.col == this.col && candidates.equals(this);
	}
}
