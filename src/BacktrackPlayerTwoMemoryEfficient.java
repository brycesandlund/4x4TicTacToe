import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * Backtrack on only a 4x4 board.
 * @author Bryce Sandlund, Kerrick Staley, Michael Dixon
 *
 */
public class BacktrackPlayerTwoMemoryEfficient {
	
	/**
	 * The two-dimensional board compressed into a single array of 16 values
	 */
	private List<Byte> grid = new ArrayList<Byte>();
	
	/**
	 * I believe we are going to store boards as Longs, according to Kerrick's canonicalize() method
	 */
	private final HashMap<List<Byte>, Integer>[] CLOSED = new HashMap[16];
		
	private int closedSize = 0;
	
	/**
	 * Player 1's possible numbers. If ply1Nums[i] is true, then the number i is available to player 1. This will allow hasWinningMove() to be more efficient.
	 */
	private final boolean[] play1NumsBit = new boolean[17];
	
	/**
	 * Player 2's possible numbers. If ply2Nums[i] is true, then number i is available to player 2.
	 */
	private final boolean[] play2NumsBit = new boolean[17];
	
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
	 * Object used to map all symmetrical configurations of a board to one long.
	 */
	private final Canonicalize2 canoc = new Canonicalize2();
	
	/**
	 * I used this to print random boards since we cannot print all of them. Currently unused.
	 */
	private final Random r = new Random();
	
	/**
	 * The number of depths below the current depth to keep in memory
	 */
//	public static final int storeDepth = 17; //store everything mothafucka
//	public static final int symmetryDepth = 17;
    public static final int storeDepth = 14;//9;
	
	public static final int symmetryDepth = 5;
	
	public static boolean cutoff = true;
	
	/**
	 * Counts how far each possible victory is from being realized
	 */
	private final int[]rowvictory={34,34,34,34};
	private final int[]colvictory={34,34,34,34};
	private int diagvictory=34;
	private int diagvictory2=34;
	
	/**
	 * Keeps track of which spaces are occupied.
	 */
	private final int[]rowfilled={0,0,0,0};
	private final int[]colfilled={0,0,0,0};
	private int diagfilled=0;
	private int diagfilled2=0;
	
	/**
	 * Starting numbers for each player
	 */
	private byte[] start1Nums = {1, 3, 5, 7, 9, 11, 13, 15};
	private byte[] start2Nums = {2, 4, 6, 8, 10, 12, 14, 16};
	
	private long[] timesVisited = new long[17];
	private long[] timesPly2Won = new long[17];
	private long[] timesCached = new long[17];
	
	/**
	 * Starting time from when object is created
	 */
	private long startTime = System.currentTimeMillis();
	
	public static void main(String[] args) {
		BacktrackPlayerTwoMemoryEfficient backtrack = new BacktrackPlayerTwoMemoryEfficient();
		System.out.println(backtrack.backtrack(true, 0));
	}
	
	public BacktrackPlayerTwoMemoryEfficient(byte[] start1Nums, byte[] start2Nums)
	{
		this.start1Nums = start1Nums;
		this.start2Nums = start2Nums;
		
		initializeClosed();
		setup();
	}
		
	/**
	 * Start from a non-empty board
	 * @param grid The board to start from
	 */
	public BacktrackPlayerTwoMemoryEfficient(List<Byte> grid)
	{
		initializeClosed();
		//set grid to given grid
		this.grid = grid;
		//start with all numbers
		for (int i = 0; i < start1Nums.length; ++i)
		{
			play1NumsBit[start1Nums[i]] = true;
		}
		for (int i = 0; i < start2Nums.length; ++i)
		{
			play2NumsBit[start2Nums[i]] = true;
		}
		
		//add open positions
		for (byte i = 0; i < 4; ++i)
		{
			for (byte j = 0; j < 4; ++j)
			{
				if (grid.get(i * 4 + j) == 0)
				{
					availablePositions.add((byte)(i * 4 + j));
				}
			}
		}
		
		//adjust according to filled positions
		for (int i = 0; i < 4; ++i)
		{
			for (int j = 0; j < 4; ++j)
			{
				byte nextL = (byte) (i * 4 + j);
				byte nextNum = grid.get(nextL);
				if (nextNum != 0)
					adjustVictoryMove(nextL, nextNum);
				play1NumsBit[nextNum] = false;
				play2NumsBit[nextNum] = false;
			}
		}
		
		//initialize linked lists from bit vectors
		for (byte i = 0; i < 17; ++i)
		{
			if (play1NumsBit[i])
			{
				play1Nums.add(i);
			}
			if (play2NumsBit[i])
			{
				play2Nums.add(i);
			}
		}
	}
	
	private void initializeClosed()
	{
		for (int i = 0; i < CLOSED.length; ++i)
		{
			CLOSED[i] = new HashMap<List<Byte>, Integer>(/*1000000*/);
		}
	}
	
	private void setup()
	{
		for (int i = 0; i < 16; ++i)
		{
			grid.add((byte)0);
		}
		
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
		for (byte i = 0; i < 4; ++i)
		{
			for (byte j = 0; j < 4; ++j)
			{
				availablePositions.add((byte)(i * 4 + j));
			}
		}
	}
	
	/**
	 * Start from an empty board
	 */
	public BacktrackPlayerTwoMemoryEfficient()
	{
		initializeClosed();
		setup();
	}

	/**
	 * Basic backtracking strategy
	 * @param play1Turn True if it is player one's turn, false if it is player 2's
	 * @return True if a winning strategy exists for player 1, false otherwise.
	 */
	public boolean backtrack(boolean play1Turn, int depth)
	{
		++timesVisited[depth];
		//if depth is 16, board is full and no winning strategy exists for player 1
		if (depth == 16)
		{
			return false;
		}
		
		List<Byte> symmetry = null;
		if (!cutoff || depth <= storeDepth)
		{
			//if this result has been computed, return its result
			symmetry = canoc.canonicalize(grid, depth <= symmetryDepth);
			//if this result has been computed, return its result
			boolean[] result = getSymmetricResult(symmetry, depth);
			if (result[0] == true)
			{
				++timesCached[depth];
				int full = CLOSED[depth].get(symmetry);
				int times = full >> 1;
//				if (times != 0)
//				{
//					System.out.println("heree");
//				}
				++times;
				int newR = times << 1 + (result[1] ? 1 : 0); 
				CLOSED[depth].put(symmetry, newR);
				if (result[1])
				{
					++timesPly2Won[depth];
				}
				return result[1];
			}
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
		
//		if (currentNums.size() == 0)
//		{
//			//if this board is filled, it is not a winning board, so return false
//			return false;
//		}
		
		//variable representing the result of this branch
		boolean play2Win = play1Turn, decided = false;	//variable representing if early termination is possible
		
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
				grid.set(nextL, nextNum); //make move for number
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
					childResult = play1Turn;
				}
				
				if(!childDecided)
				{
					childResult = backtrack(!play1Turn, depth + 1);	//enter deeper recursion
				}
								
				adjustVictoryUnmove(nextL, nextNum);
				
				grid.set(nextL, (byte)0); //unmake move for number
				currentNumsBit[nextNum] = true;
				availableNums.add(nextNumNode);
				if (!childResult && play1Turn)
				{
					play2Win = false;
					decided = true;
					break;
				}
				if (childResult && !play1Turn)
				{
					play2Win = true;
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
		
		if (!cutoff && depth + storeDepth + 1 < 16)
		{
			shrink(depth + storeDepth + 1, CLOSED);
		}
		
		//store this result so symmetries can reference it and other branches that arrive at it will not need to recompute.	May only want to do this up until a certain depth.
	//	if (CLOSED.size() < 55000000)	//should be able to put 80 (55 with depth keys) million elements into the hashmap with 7GB of memory
		
		if (closedSize < 3000000)
		{
			if (!cutoff || depth <= storeDepth)
			{
				storeResult(symmetry, play2Win, depth);
			}
		}
		else
		{
			clean();
		}
		
		//print grid based on depth
		if (depth <= 4)
		{
			printGrid(grid);
			System.out.printf("%b at depth %d with %d elements stored, %.2f seconds elapsed\n", play2Win, depth, closedSize, (System.currentTimeMillis() - startTime) / 1000.0);
			printStats();
		}
		
		//return the result
		if (play2Win)
		{
			++timesPly2Won[depth];
			if (timesPly2Won[depth+1-depth%2] != timesPly2Won[depth])
			{
				//System.out.println("here");
			}
		}
		return play2Win;
	}
	
	private void printStats()
	{
		for (int i = 0; i < 17; ++i)
		{
			System.out.printf("Depth %2d: Times Visited: %8d, Times Cached: %8d, Times Player2 Won: %8d\n", i, timesVisited[i], timesCached[i], timesPly2Won[i]);
		}
	}
	
	private void clean()
	{
		System.out.println("Cleaning");
		long preSize = closedSize;
		for (int i = 0; i < CLOSED.length; ++i)
		{
			HashMap<List<Byte>, Integer> newMap = new HashMap<List<Byte>, Integer>();
			
			Set<Entry<List<Byte>, Integer> > set = CLOSED[i].entrySet();
			Iterator<Entry<List<Byte>, Integer>> iter = set.iterator();
			int entries = 0;
			while (iter.hasNext())
			{
				Entry<List<Byte>, Integer> cur = iter.next();
				int val = cur.getValue() >> 1;
				if (val >= 1)
				{
					newMap.put(cur.getKey(), cur.getValue());
					++entries;
				}
			}
			int size = newMap.size();
			int preV = CLOSED[i].size();
			closedSize -= CLOSED[i].size() - newMap.size();
			CLOSED[i].clear();
			CLOSED[i] = newMap;
		}
		long deleted = preSize - closedSize;
		System.out.println("Cleaning Finished. Deleted " + deleted + " (" + 100.0*deleted/preSize + "%)");
	}
	
	/**
	 * Make backtrack code cleaner by putting this in a method
	 * @param nextL - position placed
	 * @param nextNum - number placed
	 */
	private void adjustVictoryMove(byte nextL, byte nextNum)
	{
		rowvictory[nextL/4]-=nextNum;
		rowfilled[nextL/4]++;
		colvictory[nextL%4]-=nextNum;
		colfilled[nextL%4]++;
		if(nextL%5==0){
			diagvictory-=nextNum;
			diagfilled++;
		}
		if(nextL != 0 && nextL != 15 && nextL%3==0){
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
		rowvictory[nextL/4]+=nextNum;
		rowfilled[nextL/4]--;
		colvictory[nextL%4]+=nextNum;
		colfilled[nextL%4]--;
		if(nextL%5==0){
			diagvictory+=nextNum;
			diagfilled--;
		}
		if(nextL != 0 && nextL != 15 && nextL%3==0){
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
	public <T, E> void shrink(int depth, HashMap<T, E>[] CLOSED)
	{
		closedSize -= CLOSED[depth].size();
		CLOSED[depth].clear();
	}
	
	/**
	 * Print a grid
	 * @param grid - the grid
	 */
	public static void printGrid(List<Byte> grid)
	{
		System.out.println("Board:");
		for (int i = 0; i < 4; ++i)
		{
			for (int j = 0; j < 4; ++j)
			{
				System.out.printf("%2d ", grid.get(i * 4 + j));
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
	public void storeResult(List<Byte> symmetry, boolean result, int depth)
	{
		++closedSize;
		CLOSED[depth].put(symmetry, result == true ? 1 : 0);
	}
	
	/**
	 * Given the respective grid, has a. this result been computed for it or any of its symmetries?
	 * and b. if so, was the result true or false?
	 * @param grid The two-dimensional grid
	 * @return An array, first value is whether it has been computed for it or any of its symmetries,
	 * second value is the result of that computation, only present if the first value was true.
	 */
	private boolean[] getSymmetricResult(List<Byte> symmetry, int depth)
	{
		if (CLOSED[depth].containsKey(symmetry))
		{
			long result = CLOSED[depth].get(symmetry);
			return new boolean[]{true, result % 2 == 1};
		}
		return new boolean[]{false};
	}
	
	/**
	 * Checks all rows and diagonals to see if the opponent can win
	 * @param grid
	 * @param oppNumsBit The opponent's numbers as a bit vector
	 * @return "true" if opponent has a winning move, "false" if not
	 */
	private boolean oppHasWin(List<Byte> grid, boolean[] oppNumsBit)
	{
		for (int i = 0; i < 4; ++i)
		{
			if (rowfilled[i] == 3 && rowvictory[i] > 0 && rowvictory[i] < 17 && oppNumsBit[rowvictory[i]])
			{
				return true;
			}
		}
		for (int i = 0; i < 4; ++i)
		{
			if (colfilled[i] == 3 && colvictory[i] > 0 && colvictory[i] < 17 && oppNumsBit[colvictory[i]])
			{
				return true;
			}
		}
		if (diagfilled == 3 && diagvictory > 0 && diagvictory < 17 && oppNumsBit[diagvictory])
		{
			return true;
		}
		if (diagfilled2 == 3 && diagvictory2 > 0 && diagvictory2 < 17 && oppNumsBit[diagvictory2])
		{
			return true;
		}
		return false;
	}
}