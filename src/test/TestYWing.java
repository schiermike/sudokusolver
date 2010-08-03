package test;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sudoku.Sudoku;
import sudoku.SudokuSolver;
import sudoku.simplification.BoxInteraction;
import sudoku.simplification.FindSingles;
import sudoku.simplification.NakedTuples;
import sudoku.simplification.SolvedCellSimplifiesUnits;
import sudoku.simplification.YWing;

public class TestYWing 
{
	private SudokuSolver solver;

	@Before
	public void setUp() throws Exception 
	{
		solver = new SudokuSolver(new Sudoku());
		solver.useSimplifier(new SolvedCellSimplifiesUnits());
		solver.useSimplifier(new FindSingles());
		solver.useSimplifier(new NakedTuples());
		solver.useSimplifier(new BoxInteraction());
	}
	
	@Test
	public void test1()
	{
		solver.getSudoku().set(".51.7.9..928156347..398.5..297465..3536.1.479814739625..952.736.62.97.54..564.29.");
		solver.solve();
		Assert.assertEquals("{46}51{23}7{234}9{68}{28}928156347{467}{47}398{24}5{16}{12}297465{18}{18}3536{28}1{28}479814739625{14}{48}952{18}736{13}62{38}97{18}54{137}{78}564{138}29{18}", 
				solver.getSudoku().toString(true));
		
		solver.useSimplifier(new YWing());
		solver.solve();
		Assert.assertEquals("651374982928156347743982561297465813536218479814739625489521736362897154175643298", 
				solver.getSudoku().toString(true));
	}
	
	@Test
	public void test2()
	{
		solver.getSudoku().set(".154..67....7.1.......3.5....18.54...8.....5...26.37....3.8.......3.9....67..213.");
		solver.solve();
		Assert.assertEquals("{23}154{29}867{239}{236}{39}{469}7518{29}{2349}87{49}{29}3651{249}{367}{39}18{279}54{29}{269}{67}8{69}{29}{1279}435{1269}5426{19}378{19}423187965158369247967542138", 
				solver.getSudoku().toString(true));
		
		solver.useSimplifier(new YWing());
		solver.solve();
		Assert.assertEquals("215498673634751892879236514391875426786924351542613789423187965158369247967542138", 
				solver.getSudoku().toString(true));
	}
}
