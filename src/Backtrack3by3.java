import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Backtrack on only a 3x3 board.
 * @author Bryce Sandlund, Kerrick Staley, Michael Dixon
 *
 */
public class Backtrack3by3 {
	
	/**
	 * The two-dimensional board compressed into a single array of 16 values
	 */
	private byte[] grid = new byte[9];
	
	/**
	 * I believe we are going to store boards as Longs, according to Kerrick's canonicalize() method
	 */
	private final HashMap<Long, Boolean> CLOSED = new HashMap<Long, Boolean>();
	
	/**
	 * Player 1's possible numbers. If ply1Nums[i] is true, then the number i is available to player 1. This will allow hasWinningMove() to be more efficient.
	 */
	private final boolean[] play1NumsBit = new boolean[10];
	
	/**
	 * Player 2's possible numbers. If ply2Nums[i] is true, then number i is available to player 2.
	 */
	private final boolean[] play2NumsBit = new boolean[10];
	
	/**
	 * Duplicate representation of play1Nums, used for O(n) traversal over available values. Does not throw ConcurrentModificationException.
	 */
	private final ConcurrentLinkedList<Byte> play1Nums = new ConcurrentLinkedList<Byte>();
	
	/**
	 * Duplicate representation for play2Nums.
	 */
	private final ConcurrentLinkedList<Byte> play2Nums = new ConcurrentLinkedList<Byte>();
	
	/**
	 * Getting the next position is no longer necessary as we can efficiently keep track of what positions are available through this LinkedList.
	 */
	private final ConcurrentLinkedList<Byte> availablePositions = new ConcurrentLinkedList<Byte>();
		
	/**
	 * I used this to print random boards since we cannot print all of them. Currently unused.
	 */
	private final Random r = new Random();
	
	/**
	 * The number of depths below the current depth to keep in memory.
	 * For the 3x3 board, we can very well just keep all configurations in memory.
	 */
	public static final int storeDepth = 4;
	
	/**
	 * Tracks the current keys in each depth, so they can later be removed to preserve memory.
	 */
	private final List<Long>[] depthKeys = new ArrayList[9];
	
	/**
	 * Counts how far each possible victory is from being realized
	 */
	private final int[]rowvictory={15, 15, 15};
	private final int[]colvictory={15, 15, 15};
	private int diagvictory=15;
	private int diagvictory2=15;
	
	/**
	 * Keeps track of which spaces are occupied.
	 */
	private final int[]rowfilled={0,0,0};
	private final int[]colfilled={0,0,0};
	private int diagfilled=0;
	private int diagfilled2=0;
		
	/**
	 * Starting numbers for each player
	 */
	private byte[] start1Nums = {1, 3, 5, 7, 9};
	private byte[] start2Nums = {2, 4, 6, 8};
	
	/**
	 * Starting time from when object is created
	 */
	private long startTime = System.currentTimeMillis();
	
	public static void main(String[] args) {
		Backtrack3by3 backtrack = new Backtrack3by3();
		System.out.println(backtrack.backtrack(true, 0));
	}
	
	public Backtrack3by3(byte[] start1Nums, byte[] start2Nums)
	{
		this.start1Nums = start1Nums;
		this.start2Nums = start2Nums;
		setup();
	}
	
	private void setup()
	{
		//add player 1 nums
		for (int i = 0; i < start1Nums.length; ++i)
		{
			play1Nums.add(start1Nums[i]);
			play1NumsBit[start1Nums[i]] = true;
		}
		
		//add player 2 nums
		for (int i = 0; i < start2Nums.length; ++i)
		{
			play2Nums.add(start2Nums[i]);
			play2NumsBit[start2Nums[i]] = true;
		}
		
		//add open positions
		for (byte i = 0; i < 3; ++i)
		{
			for (byte j = 0; j < 3; ++j)
			{
				availablePositions.add((byte)(i * 3 + j));
			}
		}
		
		//initialize depth key storage
		for (int i = 0; i < depthKeys.length; ++i)
		{
			depthKeys[i] = new ArrayList<Long>();
		}
	}
	
	/**
	 * Start from an empty board
	 */
	public Backtrack3by3()
	{
		setup();
	}
	
	/**
	 * Basic backtracking strategy
	 * @param play1Turn True if it is player one's turn, false if it is player 2's
	 * @return True if a winning strategy exists for player 1, false otherwise.
	 */
	public boolean backtrack(boolean play1Turn, int depth)
	{
		//if this result has been computed, return its result
		boolean[] result = getSymmetricResult(grid);
		if (result[0] == true)
		{
			return result[1];
		}
		
		//set object references to make code more readable and avoid repeated logic
		boolean[] currentNumsBit;
		boolean[] oppNumsBit;
		ConcurrentLinkedList<Byte> currentNums;
		if (play1Turn)
		{
			//change reference of available placement Bytes according to whose turn it is
			currentNumsBit = play1NumsBit;
			currentNums = play1Nums;
			oppNumsBit = play2NumsBit;
		}
		else
		{
			//change reference of available placement Bytes according to whose turn it is
			currentNumsBit = play2NumsBit;
			currentNums = play2Nums;
			oppNumsBit = play1NumsBit;
		}
		
		if (currentNums.size() == 0)
		{
			//if this board is filled, it is not a winning board, so return false
			return false;
		}
		
		//variable representing the result of this branch
		boolean play1Win = !play1Turn, decided = false;	//variable representing if early termination is possible
		
		ConcurrentLinkedList<Byte>.SpecialIterator nextLocations = availablePositions.specialIterator();
		while (nextLocations.hasNext())
		{
			byte nextL = nextLocations.next();
			//need to store the Node object reference in order for this technique to work
			ConcurrentLinkedList<Byte>.Node nextLNode = nextLocations.remove();	//make move for position
			ConcurrentLinkedList<Byte>.SpecialIterator availableNums = currentNums.specialIterator();
			//try all possible placement numbers at this location
			while (availableNums.hasNext())
			{
				byte nextNum = availableNums.next();
				grid[nextL] = nextNum;	//make move for number
				currentNumsBit[nextNum] = false;
				ConcurrentLinkedList<Byte>.Node nextNumNode = availableNums.remove();
				
				adjustVictoryMove(nextL, nextNum);
				
				boolean childDecided = false, childResult = false;
	
				// Performs actual victory check. Should halt further recursion if last played move allows opponent a winning move.
				// Uses nextL to determine where the last play was, then checks all rows and columns.
				// never caches fully completed boards
				childDecided = oppHasWin(grid, oppNumsBit);
				
				if (childDecided)
				{
					childResult = !play1Turn;
				}
				
				if(!childDecided)
				{
					childResult = backtrack(!play1Turn, depth + 1);	//enter deeper recursion
				}
								
				adjustVictoryUnmove(nextL, nextNum);
				
				grid[nextL] = 0; //unmake move for number
				currentNumsBit[nextNum] = true;
				availableNums.add(nextNumNode);
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
			nextLocations.add(nextLNode);	//unmake move for position
			if (decided)	
			{	//break if decided
				break;
			}
		}
		
		//store this result so symmetries can reference it and other branches that arrive at it will not need to recompute.	May only want to do this up until a certain depth.
		if (CLOSED.size() < 55000000)	//should be able to put 80 (55 with depth keys) million elements into the hashmap with 7GB of memory
		{
			//the number of results stored at a depth of n is ((16 choose n) * (8 choose ceil(n/2)) * (8 choose floor(n/2)) * n!) / 64
			//assuming only one of the 64 symmetries is stored. However, not all symmetries are possible at each depth, so this is optimistic.
			storeResult(grid, play1Win, depth);
		}
		
		//print grid based on depth
		if (depth <= -1/* || play1Win && depth <= 5*/)
		{
			printGrid(grid);
			System.out.printf("%b at depth %d with %d elements stored, %.2f seconds elapsed\n", play1Win, depth, CLOSED.size(), (System.currentTimeMillis() - startTime) / 1000.0);
		}
		
		//return the result
		return play1Win;
	}
	
	/**
	 * Make backtrack code cleaner by putting this in a method
	 * @param nextL - position placed
	 * @param nextNum - number placed
	 */
	private void adjustVictoryMove(byte nextL, byte nextNum)
	{
		rowvictory[nextL/3]-=nextNum;
		rowfilled[nextL/3]++;
		colvictory[nextL%3]-=nextNum;
		colfilled[nextL%3]++;
		if(nextL%4==0){
			diagvictory-=nextNum;
			diagfilled++;
		}
		if(nextL != 0 && nextL != 8 && nextL%2==0){
			diagvictory2-=nextNum;
			diagfilled2++;
		}
	}
	
	/**
	 * Unmake move
	 * @param nextL - position placed
	 * @param nextNum - number placed
	 */
	private void adjustVictoryUnmove(byte nextL, byte nextNum)
	{
		rowvictory[nextL/3]+=nextNum;
		rowfilled[nextL/3]--;
		colvictory[nextL%3]+=nextNum;
		colfilled[nextL%3]--;
		if(nextL%4==0){
			diagvictory+=nextNum;
			diagfilled--;
		}
		if(nextL != 0 && nextL != 8 && nextL%2==0){
			diagvictory2+=nextNum;
			diagfilled2--;
		}
	}
	
	/**
	 * Shrink the CLOSED list
	 * @param <T> - Key
	 * @param <E> - Value
	 * @param keys - keys to remove
	 * @param CLOSED - closed list
	 */
	public static <T, E> void shrink(List<T> keys, HashMap<T, E> CLOSED)
	{
		for (int i = 0; i < keys.size(); ++i)
		{
			CLOSED.remove(keys.get(i));
		}
		keys.clear();
	}
	
	/**
	 * Print a grid
	 * @param grid - the grid
	 */
	public static void printGrid(byte[] grid)
	{
		System.out.println("Board:");
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 3; ++j)
			{
				System.out.printf("%2d ", grid[i * 3 + j]);
			}
			System.out.println();
		}
	}
	
	/**
	 * Stores the result of this grid, used in conjunction with getSymmetricResult (global variables necessary).
	 * Note: this function does not take player turn as an input, because due to the board configuration, and given
	 * a starting player, there is only one player whose turn it can be next.
	 * @param grid The two-dimensional grid
	 * @param result whether this grid results in a winning strategy for player1 or not.
	 */
	public void storeResult(byte[] grid, boolean result, int depth)
	{
		long mapping = Backtrack3by3.canoc(grid);
		depthKeys[depth].add(mapping);
		CLOSED.put(mapping, result);
	}
	
	/**
	 * Naively "canonicalizes" the board into a long so that no two boards have the same long (ignores symmetries)
	 * @param grid
	 * @return
	 */
	public static long canoc(byte[] grid)
	{
		long board = 0;
		for (int i = 0; i < 9; ++i)
		{
			board += ((long)grid[i] << (4 * i));
		}
		return board;
	}
	
	/**
	 * Given the respective grid, has a. this result been computed for it or any of its symmetries?
	 * and b. if so, was the result true or false?
	 * @param grid The two-dimensional grid
	 * @return An array, first value is whether it has been computed for it or any of its symmetries,
	 * second value is the result of that computation, only present if the first value was true.
	 */
	private boolean[] getSymmetricResult(byte[] grid)
	{
		long mapping = canoc(grid);
		if (CLOSED.containsKey(mapping))
		{
			return new boolean[]{true, CLOSED.get(mapping)};
		}
		return new boolean[]{false};
	}
	
	/**
	 * Checks all rows and diagonals to see if the opponent can win
	 * @param grid
	 * @param oppNumsBit The opponent's numbers as a bit vector
	 * @return "true" if opponent has a winning move, "false" if not
	 */
	private boolean oppHasWin(byte[] grid, boolean[] oppNumsBit)
	{
		for (int i = 0; i < 3; ++i)
		{
			if (rowfilled[i] == 2 && rowvictory[i] > 0 && rowvictory[i] < 10 && oppNumsBit[rowvictory[i]])
			{
				return true;
			}
		}
		for (int i = 0; i < 3; ++i)
		{
			if (colfilled[i] == 2 && colvictory[i] > 0 && colvictory[i] < 10 && oppNumsBit[colvictory[i]])
			{
				return true;
			}
		}
		if (diagfilled == 2 && diagvictory > 0 && diagvictory < 10 && oppNumsBit[diagvictory])
		{
			return true;
		}
		if (diagfilled2 == 2 && diagvictory2 > 0 && diagvictory2 < 10 && oppNumsBit[diagvictory2])
		{
			return true;
		}
		return false;
	}
}