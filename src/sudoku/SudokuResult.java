package sudoku;

public enum SudokuResult
{
	SOLVED,
	STUCK,
	IMPOSSIBLE;
	
	public String toString()
	{
		switch(this)
		{
			case SOLVED: return "SOLVED";
			case STUCK: return "STUCK";
			case IMPOSSIBLE: return "IMPOSSIBLE";
			default: return "";
		}
	};
}