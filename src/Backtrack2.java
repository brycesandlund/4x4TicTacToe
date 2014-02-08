import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


public class Backtrack2 {
	
	/**
	 * The two-dimensional board.
	 */
	int[][] grid;
	
	/**
	 * I believe we are going to store boards as Longs, according to Kerrick's canonicalize() method
	 */
	HashMap<Long, Boolean> CLOSED;
	
	/**
	 * Player 1's possible numbers. Data structure TBD. I think a BST might be better.
	 */
	ConcurrentLinkedList<Integer> ply1Nums;
	
	Boolean[] ply1NumsBit;
	
	/**
	 * Player 2's possible numbers. Data structure TBD. I think a BST might be better.
	 */
	ConcurrentLinkedList<Integer> ply2Nums;
	
	Boolean[] ply2NumsBit;
	
	public static void main(String[] args) {
		Backtrack blah = new Backtrack();
		blah.grid = new int[4][4];
		blah.ply1Nums = new LinkedList<Integer>();
		blah.ply1Nums.add(1);
		blah.ply1Nums.add(3);
		blah.ply1Nums.add(5);
		blah.ply1Nums.add(7);
		blah.ply1Nums.add(9);
		blah.ply1Nums.add(11);
		blah.ply1Nums.add(13);
		blah.ply1Nums.add(15);
		blah.ply2Nums = new LinkedList<Integer>();
		blah.ply2Nums.add(2);
		blah.ply2Nums.add(4);
		blah.ply2Nums.add(6);
		blah.ply2Nums.add(8);
		blah.ply2Nums.add(10);
		blah.ply2Nums.add(12);
		blah.ply2Nums.add(14);
		blah.ply2Nums.add(16);
		blah.backtrack(true);
	}

	/**
	 * Basic backtracking strategy
	 * @param ply1Turn True if it is player one's turn, false if it is player 2's
	 * @return True if a winning strategy exists for player 1, false otherwise.
	 */
	public boolean backtrack(boolean ply1Turn)
	{
		//if this result has been computed, return its result
		boolean[] result = getSymmetricResult(grid);
		if (result[0] == true)
		{
			return result[1];
		}
		
		ConcurrentLinkedList<Integer> currentNumbers;
		Boolean[] currentNumsBit;
		if (ply1Turn)
		{
			//if it is my move, and I can win, this branch results in a win
			if (hasWinningMove(grid, ply1Nums))
			{
				return true;
			}
			else
			{
				//change reference of available placement integers according to whose turn it is
				currentNumbers = ply1Nums;
				currentNumsBit = ply1NumsBit;
			}
		}
		else
		{
			//if it is not my turn, and the opposing player can win, this branch results in a loss
			if (hasWinningMove(grid, ply2Nums))
			{
				return false;
			}
			else
			{
				//change reference of available placement integers according to whose turn it is
				currentNumbers = ply2Nums;
				currentNumsBit = ply2NumsBit;
			}
		}
		//get the next possible locations as an iterator
		Iterator<Point> nextLocations = getNextMoves(grid, currentNumbers);
		//variable representing the result of this branch
		boolean ply1Win = true;
		
		//label the loop for easy exit when this branch can be decided early
		ChildSearch:
		while (nextLocations.hasNext())
		{
			//the next point to attempt
			Point nextL = nextLocations.next();


			ListIterator<Integer> numIter = currentNumbers.listIterator();
			
			//try all possible placement numbers at this location
			while (numIter.hasNext())
			{
				int nextNum = numIter.next();
				numIter.remove();
				currentNumsBit[nextNum] = false;
				grid[nextL.x][nextL.y] = nextNum;	//make move
				boolean childResult = backtrack(!ply1Turn);	//enter deeper recursion
				grid[nextL.x][nextL.y] = 0; //unmake move
				currentNumsBit[nextNum] = true;
				numIter.add(arg0)
				if (childResult && ply1Turn)	//if it is player 1's turn, and a winning child branch exists, this
				{								//is a win and exit.
					ply1Win = true;
					break ChildSearch;
				}
				if (!childResult && !ply1Turn)	//if it isn't player 1's turn, and the other player has a winning branch, 
				{								//this branch is a loss and exit.
					ply1Win = false;
					break ChildSearch;
				}
			}
		}
		//store this result so symmetries can reference it and other branches that arrive at it will not need to recompute.
		storeResult(grid, ply1Win);
		//return the result
		return ply1Win;
	}
	
	/**
	 * Stores the result of this grid, used in conjunction with getSymmetricResult (global variables necessary).
	 * Note: this function does not take player turn as an input, because due to the board configuration, and given
	 * a starting player, there is only one player whose turn it can be next.
	 * @param grid The two-dimensional grid
	 * @param result whether this grid results in a winning strategy for player1 or not.
	 */
	public void storeResult(int[][] grid, boolean result)
	{
		
	}
	
	/**
	 * Given the respective grid, has a. this result been computed for it or any of its symmetries?
	 * and b. if so, was the result true or false?
	 * @param grid The two-dimensional grid
	 * @return An array, first value is whether it has been computed for it or any of its symmetries,
	 * second value is the result of that computation, only present if the first value was true.
	 */
	public boolean[] getSymmetricResult(int[][] grid)
	{
		return new boolean[]{false};
	}
	
	/**
	 * If there exists a winning single move from the set of possible numbers and the grid, return true. Return
	 * false otherwise.
	 * @param grid The two-dimensional grid
	 * @param nums The list of possible values that can fill the grid. Might want to consider using a BST instead of
	 * a linked list, if this makes hasWinningMove more efficient.
	 * @return True if a winning move exists, false otherwise
	 */
	public boolean hasWinningMove(int[][] grid, ConcurrentLinkedList<Integer> nums)
	{
		return false;
	}
	
	/**
	 * Returns an iterator of Point's, representing the next possible locations where values can be placed.
	 * @param grid
	 * @param nums
	 * @return Iterator of Point's for next possible move locations
	 */
	public Iterator<Point> getNextMoves(int[][] grid, ConcurrentLinkedList<Integer> nums)
	{
		return new ArrayList<Point>().iterator();
	}
}
