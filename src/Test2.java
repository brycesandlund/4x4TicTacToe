import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Test2 {

	public static void main(String[] args) {
		HashMap<List<Byte>, Boolean> map = new HashMap<List<Byte>, Boolean>();
		List<Byte> blah = new ArrayList<Byte>();
		blah.add((byte)3);
		blah.add((byte)4);
		map.put(blah, true);
		blah.add((byte)5);
		
		List<Byte> blah2 = new ArrayList<Byte>();
		blah2.add((byte)3);
		blah2.add((byte)4);
		
		System.out.println(map.get(blah2));
	}
}
