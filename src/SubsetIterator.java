import java.util.Arrays;
import java.util.Iterator;


public class SubsetIterator implements Iterator<int[]> {
	private int n, r;
	private int[] cur;
	private boolean hasNext = true;
	
	public static boolean[] setToBitmap(int[] set, int size)
	{
		boolean[] bitmap = new boolean[size];
		for (int i = 0; i < set.length; ++i)
		{
			bitmap[set[i]] = true;
		}
		return bitmap;
	}
	
	public static int[] complement(int[] set, int totalElements)
	{
		boolean[] elements = setToBitmap(set, totalElements);
		int[] complement = new int[totalElements - set.length];
		int index = 0;
		for (int i = 0; i < elements.length; ++i)
		{
			if (!elements[i])
			{
				complement[index++] = i;
			}
		}
		return complement;
	}
	
	public SubsetIterator(int totalElements, int subsetSize)
	{
		this.n = totalElements;
		this.r = subsetSize;
		cur = new int[r];
		for (int i = 0; i < r; ++i)
		{
			cur[i] = i;
		}
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public int[] next() {
		int[] ret = Arrays.copyOf(cur, r);
		boolean broke = false;
		for (int i = r - 1; i >= 0; --i)
		{
			if (cur[i] < n - (r - i))
			{
				++cur[i];
				for (int j = i + 1; j < r; ++j)
				{
					cur[j] = cur[i] + (j - i);
				}
				broke = true;
				break;
			}
		}
		hasNext = broke;
		return ret;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	
}
