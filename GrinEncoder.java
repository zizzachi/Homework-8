import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GrinEncoder {
	
	private Map<Short, Integer> createFrequencyMap(String file) throws IOException {
		BitInputStream in = new BitInputStream(file);
		Map<Short, Integer> m = new HashMap<Short, Integer>();

		short ch = (short) in.readBits(8);
		while(ch != -1) {
			if(m.containsKey(ch)) {
				m.put((short)ch, m.get(ch) + 1);
				
				System.out.println((ch + ", " + m.get(ch)));
				System.out.println();
			} else {
				m.put((short)ch, 1);
				
				System.out.println("Creating a new entry");
				System.out.println((ch + ", " + m.get(ch)));
			}
			ch = (short) in.readBits(8);
		}
		in.close();
		return m;
	}

	public void encode(String infile, String outfile) throws IOException {
		Map<Short, Integer> map = createFrequencyMap(infile);
		BitInputStream in = new BitInputStream(infile);
		BitOutputStream out = new BitOutputStream(outfile, true);
		
		out.writeBits(1846, 32);  //converts 1846 into binary
		
		Iterator<Short> keySet = map.keySet().iterator();
		
		while (keySet.hasNext()) {
			short key = keySet.next();
			out.writeBits(key, 16);
			out.writeBits(map.get(key), 32);
		}
		
		HuffmanTree tree = new HuffmanTree(map);
		System.out.println(tree.getRootFrequency());
		tree.printArr();
		tree.encode(in, out);
		
		in.close();
		out.close();
	}
}
