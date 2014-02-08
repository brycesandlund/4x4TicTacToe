import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//1  4  3  0 
//0  0 14  0 
//0  0  0  0 
//0  0  0  0 

public class Test {

	public static void main(String[] args) {
		List<Byte> list = new ArrayList<Byte>();
		byte[] nums = {1, 4, 3, 0, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (int i = 0; i < nums.length; ++i)
		{
			list.add(nums[i]);
		}
		
		BacktrackPlayerTwo backtrack = new BacktrackPlayerTwo(list);
		System.out.println(backtrack.backtrack(true, 4));
	}
}
