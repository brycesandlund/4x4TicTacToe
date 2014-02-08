import java.util.Arrays;


public class Main2 {

	public static void main(String[] args) {
		SubsetIterator sI = new SubsetIterator(9, 5);
		while (sI.hasNext())
		{
			int[] arr = sI.next();
			byte[] cur = addOne(intArrToByteArr(arr));
			byte[] curOpp = addOne(intArrToByteArr(SubsetIterator.complement(arr, 9)));
			Backtrack3by3 test = new Backtrack3by3(cur, curOpp);
			System.out.println(Arrays.toString(cur) + ", " + Arrays.toString(curOpp) + " " + test.backtrack(true, 0));
		}
	}
	
	public static byte[] addOne(byte[] arr)
	{
		byte[] arr2 = new byte[arr.length];
		for (int i = 0; i < arr.length; ++i)
		{
			arr2[i] = (byte) (arr[i] + 1);
		}
		return arr2;
	}
	
	public static byte[] intArrToByteArr(int[] arr)
	{
		byte[] arr2 = new byte[arr.length];
		for (int i = 0; i < arr.length; ++i)
		{
			arr2[i] = (byte) arr[i];
		}
		return arr2;
	}
}
