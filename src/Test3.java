import java.util.HashMap;


public class Test3 {

	public static void main(String[] args) {
//		HashMap<byte[], Boolean> map = new HashMap<byte[], Boolean>();
//		byte[] test = new byte[9];
//		test[0] = 1;
//		map.put(test, true);
//		System.out.println(map.get(test));
//		test[1] = 1;
//		System.out.println(map.get(test));
//		HashMap<byte[], Boolean> map;
		byte[] grid = new byte[9];
		grid[0] = 3;
		grid[8] = 9;
		System.out.println(Long.toBinaryString(Backtrack3by3.canoc(grid)));
	}
	
	
}
