package sudoku;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class SudokuPermutator 
{
	private static Random r = new Random();

	public static void main(String[] args) throws IOException 
	{
		String sudokuString = "963174258178325649254689731821437596496852317735961824589713462317246985642598173";
		Sudoku s = new Sudoku(sudokuString);
		
		FileWriter writer = new FileWriter("genPermSudokus.txt");
		int cases = 50;
		
		writer.write(cases + "\n");
		for(int i = 0; i < cases; i++)
		{
			writer.write(s.toBlockString());
			shuffle(s);
			writer.write(s.toBlockString());
			writer.write("\n");
		}
		
		writer.close();
	}

	private static void shuffle(Sudoku s) 
	{
		while(r.nextDouble() <= 0.95)
		{
			switchTwoRows(s);
			switchTwoRowSegments(s);
			if(r.nextBoolean())
				rotate(s);
		}
		permutate(s);
	}

	private static void permutate(Sudoku s) 
	{
		ArrayList<Character> candidates = new ArrayList<Character>(Sudoku.ALL_CANDIDATES);
		Collections.shuffle(candidates);
		
		HashMap<Character, Character> mapping = new HashMap<Character, Character>();
		Iterator<Character> i = candidates.iterator();
		for(char c : Sudoku.ALL_CANDIDATES)
			mapping.put(c, i.next());
		
		for(int row = 0; row < Sudoku.LENGTH; row++)
			for(int col = 0; col < Sudoku.LENGTH; col++)
			{
				char newEntry = mapping.get(s.getEntry(row, col));
				s.setEntry(row, col, newEntry);
			}
	}

	private static void rotate(Sudoku s) 
	{
		Sudoku s2 = new Sudoku(s);
		for(int row = 0; row < Sudoku.LENGTH; row++)
			for(int col = 0; col < Sudoku.LENGTH; col++)
				s.setEntry(col, Sudoku.LENGTH-1-row, s2.getEntry(row, col));
	}

	private static void switchTwoRowSegments(Sudoku s) 
	{
		int rs1 = r.nextInt(Sudoku.DIMENSION);
		int rs2 = r.nextInt(Sudoku.DIMENSION);
		if(rs1 == rs2)
			return;
		
		char temp;
		for(int row = 0; row < Sudoku.DIMENSION; row++)
			for(int col = 0; col < Sudoku.LENGTH; col++)
			{
				temp = s.getEntry(Sudoku.DIMENSION*rs1 + row, col);
				s.setEntry(Sudoku.DIMENSION*rs1 + row, col, s.getEntry(Sudoku.DIMENSION*rs2 + row, col));
				s.setEntry(Sudoku.DIMENSION*rs2 + row, col, temp);
			}
	}

	private static void switchTwoRows(Sudoku s) 
	{
		int rs = r.nextInt(Sudoku.DIMENSION);
		int r1 = r.nextInt(Sudoku.DIMENSION);
		int r2 = r.nextInt(Sudoku.DIMENSION);
		if(r1 == r2)
			return;
		
		r1 += rs*Sudoku.DIMENSION;
		r2 += rs*Sudoku.DIMENSION;
		char temp;
		for(int col = 0; col < Sudoku.LENGTH; col++)
		{
			temp = s.getEntry(r1, col);
			s.setEntry(r1, col, s.getEntry(r2, col));
			s.setEntry(r2, col, temp);
		}
	}
}
