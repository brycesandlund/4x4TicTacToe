import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class BacktrackSkeleton {
	
	/**
	 * The two-dimensional board compressed into a single array of 16 values
	 */
	byte[] grid;
	
	/**
	 * I believe we are going to store boards as Longs, according to Kerrick's canonicalize() method
	 */
	HashMap<Long, Boolean> CLOSED;
	
	/**
	 * Player 1's possible numbers. If ply1Nums[i] is true, then the number i is available to player 1. This will allow hasWinningMove() to be more efficient.
	 */
	Boolean[] play1NumsBit;
	
	/**
	 * Player 2's possible numbers. If ply2Nums[i] is true, then number i is available to player 2.
	 */
	Boolean[] play2NumsBit;
	
	/**
	 * Duplicate representation of play1Nums, used for O(n) traversal over available values. Does not throw ConcurrentModificationException.
	 */
	ConcurrentLinkedList<Byte> play1Nums = new ConcurrentLinkedList<Byte>();
	
	/**
	 * Duplicate representation for play2Nums.
	 */
	ConcurrentLinkedList<Byte> play2Nums = new ConcurrentLinkedList<Byte>();
	
	/**
	 * Getting the next position is no longer necessary as we can efficiently keep track of what positions are available through this LinkedList.
	 */
	ConcurrentLinkedList<Byte> availablePositions = new ConcurrentLinkedList<Byte>();
	
	Canonicalize canoc = new Canonicalize();
	
	public BacktrackSkeleton()
	{
		play1Nums.add((byte)1);
		play1Nums.add((byte)3);
		play1Nums.add((byte)5);
		play1Nums.add((byte)7);
		play1Nums.add((byte)9);
		play1Nums.add((byte)11);
		play1Nums.add((byte)13);
		play1Nums.add((byte)15);
		play2Nums.add((byte)2);
		play2Nums.add((byte)4);
		play2Nums.add((byte)6);
		play2Nums.add((byte)8);
		play2Nums.add((byte)10);
		play2Nums.add((byte)12);
		play2Nums.add((byte)14);
		play2Nums.add((byte)16);
		play1NumsBit = new Boolean[17];
		play2NumsBit = new Boolean[17];
		Iterator<Byte> play1Iter = play1Nums.iterator();
		while (play1Iter.hasNext())
		{
			play1NumsBit[play1Iter.next()] = true;
		}
		Iterator<Byte> play2Iter = play2Nums.iterator();
		while (play2Iter.hasNext())
		{
			play1NumsBit[play2Iter.next()] = true;
		}
	}

	/**
	 * Basic backtracking strategy
	 * @param play1Turn True if it is player one's turn, false if it is player 2's
	 * @return True if a winning strategy exists for player 1, false otherwise.
	 */
	public boolean backtrack(boolean play1Turn)
	{
		//if this result has been computed, return its result
		boolean[] result = getSymmetricResult(grid);
		if (result[0] == true)
		{
			return result[1];
		}
		
		Boolean[] currentNumsBit;
		ConcurrentLinkedList<Byte> currentNums;
		if (play1Turn)
		{
			//if it is my move, and I can win, this branch results in a win
			if (hasWinningMove(grid, play1NumsBit))
			{
				return true;	//Note I have not stored this. It is likely that there will be a certain depth into the 
								//tree where it is no longer worth it to store results. In this implementation, I store up
								//until there is only one tile left. We may want to consider stopping storing even sooner,
								//based on how much memory this implementation would take.
			}
			else
			{
				//change reference of available placement Bytes according to whose turn it is
				currentNumsBit = play1NumsBit;
				currentNums = play1Nums;
			}
		}
		else
		{
			//if it is not my turn, and the opposing player can win, this branch results in a loss
			if (hasWinningMove(grid, play2NumsBit))
			{
				return false;
			}
			else
			{
				//change reference of available placement Bytes according to whose turn it is
				currentNumsBit = play2NumsBit;
				currentNums = play2Nums;
			}
		}
		
		if (currentNums.size() == 0)
		{
			//if this board is filled, it is not a winning board, so return false
			return false;
		}
		
		//variable representing the result of this branch
		boolean play1Win = true, decided = false;	//variable representing if early termination is possible
		
		ListIterator<Byte> nextLocations = availablePositions.listIterator();
		while (nextLocations.hasNext())
		{
			byte nextL = nextLocations.next();
			nextLocations.remove();	//make move for position
			ListIterator<Byte> availableNums = currentNums.listIterator();
			//try all possible placement numbers at this location
			while (availableNums.hasNext())
			{
				byte nextNum = availableNums.next();
				grid[nextL] = nextNum;	//make move for number
				currentNumsBit[nextNum] = false;
				availableNums.remove();
				boolean childResult = backtrack(!play1Turn);	//enter deeper recursion
				grid[nextL] = 0; //unmake move for number
				currentNumsBit[nextNum] = true;
				availableNums.add(nextNum);
				if (childResult && play1Turn)	//if it is player 1's turn, and a winning child branch exists, this
				{								//is a win and exit.
					play1Win = true;
					decided = true;
					break;
				}
				if (!childResult && !play1Turn)	//if it isn't player 1's turn, and the other player has a winning branch, 
				{								//this branch is a loss and exit.
					play1Win = false;
					decided = true;
					break;
				}
			}
			//we can no longer break the outer loop straightaway, and instead must ensure we unmake the move for position.
			nextLocations.add(nextL);	//unmake move for position
			if (decided)	
			{	//break if decided
				break;
			}
		}
		//store this result so symmetries can reference it and other branches that arrive at it will not need to recompute.	May only want to do this up until a certain depth.
		storeResult(grid, play1Win);
		//return the result
		return play1Win;
	}
	
	/**
	 * Stores the result of this grid, used in conjunction with getSymmetricResult (global variables necessary).
	 * Note: this function does not take player turn as an input, because due to the board configuration, and given
	 * a starting player, there is only one player whose turn it can be next.
	 * @param grid The two-dimensional grid
	 * @param result whether this grid results in a winning strategy for player1 or not.
	 */
	public void storeResult(byte[] grid, boolean result)
	{
		long mapping = canoc.canonicalize(grid);
		CLOSED.put(mapping, result);
	}
	
	/**
	 * Given the respective grid, has a. this result been computed for it or any of its symmetries?
	 * and b. if so, was the result true or false?
	 * @param grid The two-dimensional grid
	 * @return An array, first value is whether it has been computed for it or any of its symmetries,
	 * second value is the result of that computation, only present if the first value was true.
	 */
	public boolean[] getSymmetricResult(byte[] grid)
	{
		long mapping = canoc.canonicalize(grid);
		if (CLOSED.containsKey(mapping))
		{
			return new boolean[]{true, CLOSED.get(mapping)};
		}
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
	public boolean hasWinningMove(byte[] grid, Boolean[] nums)
	{
		return false;
	}
}