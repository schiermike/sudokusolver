package test;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sudoku.Sudoku;
import sudoku.SudokuSolver;
import sudoku.simplification.FindSingles;
import sudoku.simplification.SolvedCellSimplifiesUnits;

public class TestFindSingles
{
	private SudokuSolver solver;

	@Before
	public void setUp() throws Exception 
	{
		solver = new SudokuSolver(new Sudoku());
		solver.useSimplifier(new SolvedCellSimplifiesUnits());
		solver.useSimplifier(new FindSingles());
	}
	
	@Test
	public void test1()
	{
		solver.getSudoku().set(".94...13..............76..2.8..1.....32.........2...6.....5.4.......8..7..63.4..8");
		solver.solve();
		Assert.assertEquals("79458213626{58}{49}31{579}{45789}{59}{1358}{15}{1358}{49}76{59}{4589}268{59}71{59}32443286{59}{579}{1579}{159}{159}{15}724386{159}{189}2{189}6574{19}3{359}4{359}1286{59}7{15}763942{15}8", 
				solver.getSudoku().toString(true));
	}
	
	@Test
	public void test2()
	{
		solver.getSudoku().set("4...187..8.......2.....4..............6.53..1..726.43...1.7...6.3...1.8....6.....");
		solver.solve();
		Assert.assertEquals("4{2569}{2359}{359}187{569}{359}8{1579}{359}{3579}{39}6{1359}42{13579}{15679}{359}{3579}24{135689}{1569}{3589}{2359}{24589}{234589}1{48}7{25689}{2569}{589}{29}{2489}6{48}53{289}71{15}{158}726943{58}{259}{24589}1{34589}7{25}{2359}{259}663{2459}{459}{49}1{259}87{2579}{25789}{2589}6{389}{25}{12359}{1259}4", 
				solver.getSudoku().toString(true));
	}
	
	@Test
	public void test3()
	{
		solver.getSudoku().set("35.6.......7.......9..3.18......5....6..1..9....4......18.9..6.......5.......7.32");
		solver.solve();
		Assert.assertEquals("35{124}6{2478}{12489}{2479}{247}{479}{12468}{28}7{2589}{2458}{12489}{23469}{245}{34569}{246}9{246}{257}3{24}18{4567}{124789}{2378}{12349}{23789}{2678}5{234678}{1247}{134678}{24578}6{2345}{2378}1{238}{23478}9{34578}{125789}{2378}{12359}4{2678}{23689}{23678}{1257}{135678}{257}18{235}9{234}{47}6{47}{2679}{237}{2369}{238}{2468}{23468}5{147}{14789}{569}4{569}1{568}7{89}32", 
				solver.getSudoku().toString(true));
	}
	
	@Test
	public void test4()
	{
		solver.getSudoku().set("31.6.......2.......5..9.78......5....9..1..6....4......75.6..3.......4.......7.92");
		solver.solve();
		Assert.assertEquals("31{4789}6{24578}{248}{259}{245}{459}{46789}{468}2{13578}{34578}{1348}{13569}{145}{134569}{46}5{46}{123}9{1234}78{1346}{124678}{23468}{134678}9{2378}5{1238}{1247}{13478}{24578}9{3478}{2378}1{238}{2358}6{34578}{12578}{238}{1378}4{2378}6{123589}{1257}{135789}{12489}75{128}6{12489}{18}3{18}{12689}{2368}{13689}{12358}{2358}{12389}4{157}{15678}{1468}{3468}{13468}{1358}{3458}7{1568}92", 
				solver.getSudoku().toString(true));
	}
	
	@Test
	public void test5()
	{
		solver.getSudoku().set("1...4...2.5.....9...8...3.....5.9...7...8...3...7.6.....7...5...9.....4.6...2...1");
		solver.solve();
		Assert.assertEquals("1{367}{369}{3689}4{3578}{678}{5678}2{234}5{2346}{12368}{1367}{12378}{14678}9{4678}{249}{2467}8{1269}{15679}{1257}3{1567}{4567}{2348}{123468}{12346}5{13}9{124678}{12678}{4678}7{1246}{124569}{124}8{124}{12469}{1256}3{234589}{12348}{123459}7{13}6{12489}{1258}{4589}{2348}{12348}7{134689}{1369}{1348}5{2368}{689}{2358}9{1235}{1368}{13567}{13578}{2678}4{678}6{348}{345}{3489}2{34578}{789}{378}1", 
				solver.getSudoku().toString(true));
	}
}
