import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class used to compress all symmetric 4x4 configurations into a single "minimum value" configuration.
 * @author Kerrick Staley
 *
 */
public class Canonicalize2
{
    public static void cycle4(List<Byte> board, int a, int b, int c, int d)
    {
        byte tmp = board.get(a);
        board.set(a, board.get(b));
        board.set(b, board.get(c));
        board.set(c, board.get(d));
        board.set(d, tmp);
    }
    
    public static void rotate(List<Byte> board)
    {
    	cycle4(board, 0, 3, 15, 12);
        cycle4(board, 1, 7, 14, 8);
        cycle4(board, 2, 11, 13, 4);
        cycle4(board, 5, 6, 10, 9);
    }
    
    public static void cycle2(List<Byte> board, int a, int b)
    {
        byte tmp = board.get(a);
        board.set(a,  board.get(b));
        board.set(b, tmp);
    }
    
    public static void vmirror(List<Byte> board)
    {
        cycle2(board, 0, 3);
        cycle2(board, 1, 2);
        cycle2(board, 4, 7);
        cycle2(board, 5, 6);
        cycle2(board, 8, 11);
        cycle2(board, 9, 10);
        cycle2(board, 12, 15);
        cycle2(board, 13, 14);
    }
    
    public static void shuffle1(List<Byte> board)
    {
        cycle2(board, 0, 5);
        cycle2(board, 1, 4);
        cycle2(board, 2, 7);
        cycle2(board, 3, 6);
        cycle2(board, 8, 13);
        cycle2(board, 9, 12);
        cycle2(board, 10, 15);
        cycle2(board, 11, 14);
    }
    
    public static void shuffle2(List<Byte> board)
    {
        cycle2(board, 1, 2);
        cycle2(board, 7, 11);
        cycle2(board, 14, 13);
        cycle2(board, 8, 4);
        cycle2(board, 5, 10);
        cycle2(board, 6, 9);
    }
    
    /*
     * Takes a byte[] representing the board. The mapping from
     * location on the board to position in this vector is as follows:
     * 
     *  0  1  2  3
     *  4  5  6  7
     *  8  9 10 11
     * 12 13 14 15
     * 
     * Returns a uint64_t representing the board. All boards that are
     * equivalent to one another (under the various equivalence operations
     * defined above) will yield the same value when passed to this
     * function.
     */
    public List<Byte> convertArrToList(byte[] arr)
    {
    	List<Byte> retVal = new ArrayList<Byte>();
    	for (int i = 0; i < 16; ++i)
    	{
    		retVal.add(arr[i]);
    	}
    	return retVal;
    }
        
    List<Byte> canonicalize(List<Byte> board, boolean calculate)
    {
    	if (!calculate)
    	{
	    	return new ArrayList<Byte>(board);
    	}
    	
    	List<List<Byte>> allConfigs = new ArrayList<List<Byte>>();
    	for (int i = 0; i < 4; ++i)
        {
    		rotate(board);
        	for (int j = 0; j < 2; ++j)
        	{
        		vmirror(board);
        		for (int k = 0; k < 2; ++k)
        		{
        			shuffle1(board);
        			for (int l = 0; l < 2; ++l)
        			{
        				shuffle2(board);
        				allConfigs.add(new ArrayList<Byte>(board));
        			}
        		}
        	}
        }
    	
    	List<Byte> min = allConfigs.get(0);
    	for (int i = 1; i < allConfigs.size(); ++i)
    	{
    		List<Byte> cur = allConfigs.get(i);
    		for (int j = 0; j < 16; ++j)
    		{
    			if (cur.get(j) < min.get(j))
    			{
    				min = cur;
    				break;
    			}
    			else if (cur.get(j) > min.get(j))
    			{
    				break;
    			}
    		}
    	}
    	return min;
    }
}
