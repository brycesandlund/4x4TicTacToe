import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Sacrifices time for memory. Generates subsets randomly and stores in a closed list. O(n) next() time, where
 * n is the total number of possible subsets.
 * @author Bryce Sandlund
 *
 */
public class RandomSubsetIterator implements Iterator<int[]>{
	private Random rand = new Random();
	private HashSet<List<Integer>> CLOSED = new HashSet<List<Integer>>();
	private int n, r;
	
	public RandomSubsetIterator(int totalElements, int subsetSize)
	{
		n = totalElements;
		r = subsetSize;
	}
	
	@Override
	public boolean hasNext() {
		return true;	//assume we are never trying to do all subsets
	}

	@Override
	public int[] next() {
		List<Integer> list = new ArrayList<Integer>();
		List<Integer> chosenList = new ArrayList<Integer>();
		do
		{
			chosenList.clear();
			list.clear();
			for (int i = 0; i < n; ++i)
			{
				list.add(i);
			}
			for (int i = 0; i < r; ++i)
			{
				chosenList.add(list.remove(rand.nextInt(list.size())));
			}
			Collections.sort(chosenList);
		}
		while (CLOSED.contains(chosenList));
		CLOSED.add(chosenList);
		return toIntArray(chosenList);
	}
	
	int[] toIntArray(List<Integer> list)
	{
		  int[] ret = new int[list.size()];
		  for(int i = 0;i < ret.length;i++)
		    ret[i] = list.get(i);
		  return ret;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
