import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GrinDecoder {

	public void decode(String infile, String outfile) throws IOException {
		BitInputStream in = new BitInputStream(infile);
		BitOutputStream out = new BitOutputStream(outfile, true);
		Map<Short, Integer> fmap = new HashMap<Short, Integer>();

		int magicNumber = 0;
		
		for(int i = 0; i < 8; i ++) {
			magicNumber += in.readBits(32);
		}
		System.out.println(magicNumber);
		
		if(magicNumber != 1846) {
			throw new IllegalArgumentException("Not appropriate magic number");
		} else {
			int leafCount = in.readBits(32);

			for(int i = 0; i < leafCount; i++) {
				fmap.put((short)in.readBits(16), in.readBits(32));
			}

			HuffmanTree tree = new HuffmanTree(fmap);
			tree.decode(in, out);
		}
		in.close();
		out.close();
	}
}
