import java.util.HashMap;


public class MemoryTest {

	public static void main(String[] args) {
		HashMap<Long, Boolean> test = new HashMap<Long, Boolean>();
		long i = 0;
		try
		{
			for (i = 0; i < 100000000; ++i)
			{
				test.put(i, false);
			}
		}
		catch (Exception e)
		{
			System.out.println(i);
		}
	}
}
