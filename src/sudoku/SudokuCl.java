package sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class SudokuCl 
{

	public static void main(String[] args) throws IOException 
	{
		args = new String[]{"multitestcases/msk_009.sdk"};
		if(args.length != 1)
			throw new IllegalArgumentException("You have to provide a test-case file (*.sdk)!");
		
		int solved = 0;
		int count = 0;
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		long time = System.currentTimeMillis();
		while(reader.ready())
		{
			Sudoku sudoku = new Sudoku(reader.readLine());
			
			SudokuSolver solver = new SudokuSolver(sudoku);
			solver.setMaxConcurrentGuesses(0);
			SudokuResult result = solver.solve();
			
			count++;
			if(result == SudokuResult.SOLVED)
				solved++;
			else if(result == SudokuResult.IMPOSSIBLE)
				throw new IllegalStateException("Detected impossible sudoku:\n" + sudoku);
			System.out.print(result==SudokuResult.SOLVED ? "S" : "_");
			System.out.flush();
		}

		time = System.currentTimeMillis() - time;
		System.out.println("\n\nTime used: " + time/1000.0 + "s");
		System.out.println("Solved " + (100*solved)/count + "%");
	}

}
