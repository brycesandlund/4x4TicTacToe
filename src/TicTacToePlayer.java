import java.util.ArrayList;
import java.util.List;


public class TicTacToePlayer {

	public static void main(String[] args) {
		int[][] grid = new int[4][4];
		for (int i = 0; i < grid.length; ++i)
		{
			for (int j = 0; j < grid[i].length; ++j)
			{
				grid[i][j] = 0;
			}
		}
		TicTacToePlayer tttp = new TicTacToePlayer(grid, new int[]{1, 3, 5, 7, 9}, new int[]{2, 4, 6, 8});
		System.out.println(tttp.isUniqueSymmetry(grid));
	}
	
	private List<Integer> p1Numbers = new ArrayList<Integer>(), p2Numbers = new ArrayList<Integer>();
	
	public TicTacToePlayer(int[][] grid, int[] p1Numbers, int[] p2Numbers)
	{
		for (int i = 0; i < p1Numbers.length; ++i)
		{
			this.p1Numbers.add(p1Numbers[i]);
		}
		for (int i = 0; i < p2Numbers.length; ++i)
		{
			this.p2Numbers.add(p2Numbers[i]);
		}
	}
	
	public void solve()
	{
		
	}
	
//	public int[] nextMove(int[][] grid, boolean turn, )
//	{
//		
//	}
	
	public static void printGrid(int[][] grid)
	{
		for (int i = 0; i < grid.length; ++i)
		{
			for (int j = 0; j < grid[i].length; ++j)
			{
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
	
	/**
	 * Define an ordering on grids. A grid1 is considered of less value than grid2 if it has an odd number that appears
	 * in traditional grid traversal order before grid2. If odds occur in the same place, grid1 is considered of less
	 * value than grid2 if it has an even number that appears in traditional grid traversal order before grid2.
	 */
	public int compare(int[][] grid1, int[][] grid2)
	{
		for (int i = 0; i < grid1.length; ++i)
		{
			for (int j = 0; j < grid2.length; ++j)
			{
				if (grid1[i][j] % 2 == 1 && grid2[i][j] % 2 != 1)
				{
					return -1;
				}
				else if (grid2[i][j] % 2 == 1 && grid1[i][j] % 2 != 1)
				{
					return 1;
				}
			}
		}
		for (int i = 0; i < grid1.length; ++i)
		{
			for (int j = 0; j < grid2.length; ++j)
			{
				if (grid1[i][j] != 0 && grid1[i][j] % 2 == 0 && (grid2[i][j] == 0 || grid2[i][j] % 2 != 0))
				{
					return -1;
				}
				else if (grid2[i][j] != 0 && grid2[i][j] % 2 == 0 && (grid1[i][j] == 0 || grid1[i][j] % 2 != 0))
				{
					return 1;
				}
			}
		}
		return 0;
	}
	
	public int[][] getUniqueGrid(int[][] grid)
	{
		int[][] maxGrid = grid;
		int[][] newGrid = grid;
		for (int i = 0; i < 4; ++i)
		{
			if (compare(maxGrid, newGrid) > 0)
			{
				
			}
			if (compare(maxGrid))
		}
	}
	
	public boolean isUniqueSymmetry(int[][] grid)
	{
		boolean isUnique = true;
		int[][] gridNew;
		gridNew = grid;
		for (int i = 0; i < 4; ++i)
		{
			if (compare(grid, gridNew) > 0)
			{
				isUnique = false;
				break;
			}
			if (compare(grid, flip(gridNew)) > 0)
			{
				isUnique = false;
				break;
			}
			gridNew = rotate(gridNew);
		}
		return isUnique;
	}
	
	public int[][] rotate(int[][] grid)
	{
		int[][] newGrid = new int[grid.length][grid[0].length];
		for (int i = 0; i < grid.length; ++i)
		{
			for (int j = 0; j < grid[i].length; ++j)
			{
				newGrid[i][j] = grid[grid.length - 1 - j][i];
//				newGrid[i][j] = grid[j][grid.length - 1 - i];
			}
		}
		return newGrid;
	}
	
	public int[][] flip(int[][] grid)
	{
		int[][] newGrid = new int[grid.length][grid[0].length];
		for (int i = 0; i < grid.length; ++i)
		{
			for (int j = 0; j < grid[i].length; ++j)
			{
				newGrid[grid.length - 1 - i][j] = grid[i][j];
			}
		}
		return newGrid;
	}
}
