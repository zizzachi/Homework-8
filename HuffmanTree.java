import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

	/** Node Class
	 * @author chiarazizza
	 */
	private class Node {
		private Integer frequency;
		private short character;
		private Node left;
		private Node right;

		/** Constructor for leaf Nodes 
		 * @param s a short representing the character
		 * @param f an Integer representing the frequency of the character
		 * @param l the left Node
		 * @param r the right Node
		 */
		public Node(short s, Integer f, Node l, Node r) {
			this.character = s;
			this.frequency = f;
			this.left = l;
			this.right = r;
		}

		/** Constructor for interior Nodes
		 * @param f f an Integer representing the sum of the subtree frequencies
		 * @param l the left Node
		 * @param r the right Node
		 */
		public Node(Integer f, Node l, Node r) {
			this.frequency = f;
			this.left = l;
			this.right = r;
		}

		/** Constructor for initializing root
		 */
		public Node() {
		}
	}

	/** Explains how to compare two Nodes
	 * @author chiarazizza
	 *
	 */
	private class NodeComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			if (n1.frequency > n2.frequency) {
				return 1;
			} else if (n1.frequency < n2.frequency) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private Node root;
	private String[] arr;
	private Map<String, Integer> map;

	/** Constructs a HuffmanTree from the given frequency map of 9-bit values
	 * @param m a Map.
	 */
	public HuffmanTree(Map<Short, Integer> m) {
		this.root = new Node();
		this.arr = new String[257];

		/* Create PriorityQueue from Map */
		PriorityQueue<Node> pq = buildQueue(m);

		/* Create HuffmanTree */
		buildTree(pq);
		buildPath(this.root, "");
	}

	/** Constructs a PriorityQueue from a Map
	 * @param m a Map
	 * @return pq a PriorityQueue
	 */
	private PriorityQueue<Node> buildQueue(Map<Short, Integer> m) {
		Comparator<Node> compareNode = new NodeComparator();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(compareNode);
		
		/* Insert EOF into Map */
		m.put((short) 256, 1);

		/* Populate PriorityQueue */
		if (m.isEmpty()) { //// recursive or while loop (three lines)
			throw new IllegalArgumentException("Map must not be empty.");
		} else {
			Iterator<Short> keySet = m.keySet().iterator();
			while (keySet.hasNext()) {
				short ch = (short) keySet.next();
				pq.add(new Node(ch, m.get(ch), null, null));
			}
		}
		return pq;
	}

	/** Constructs a HuffmanTree from a PriorityQueue
	 * @param pq a PriorityQueue
	 */
	private void buildTree(PriorityQueue<Node> pq) {
		if (pq.isEmpty()) {
			throw new IllegalArgumentException("PriorityQueue must not be empty.");
		} else {
			while (pq.size() > 1) {
				Node temp1 = pq.poll();
				Node temp2 = pq.poll();
				pq.add(new Node(temp1.frequency + temp2.frequency, temp1, temp2));
			}

			/* Assign root to new tree */
			this.root = pq.poll();
		}
	}

	/** Encodes the file given as a stream of bits into a compressed format using
	 * this Huffman tree. The encoded values are written, bit-by-bit to the
	 * given BitOuputStream
	 * @param in the BitInputStream being read
	 * @param out the BitOutputStream being written to
	 */
	public void encode(BitInputStream in, BitOutputStream out) {
		//System.out.println("In encode.");
		boolean proceed = true;
		int c = in.readBits(8);

		while (proceed) {
			/* Checks to see if the EOF has been reached */
			if(c == -1) {
				proceed = false;
				c = 256; //array index for EOF character
			}
			
			String temp = this.arr[c];  //temp is the "01" string designated for each character
			//System.out.println(temp);
//			System.out.println(this.arr[c]);
//			System.out.println("Temp is null: " + temp == null);
//			System.out.println(temp);
			
			for (int i = 0; i < temp.length(); i++) {
				//System.out.println(temp.charAt(i));
				if (temp.charAt(i) == '0') {
					out.writeBit(0);
				} else if (temp.charAt(i) == '1') {
					out.writeBit(1);
				} else {
					throw new IllegalArgumentException("Not '0' or '1'");
				}
			}
			//System.out.println();
			c = in.readBits(8);
		}
	}
	
	/** Decodes a stream of huffman codes from a file given as a stream of bits
	 * into their uncompressed form, saving the results to the given output stream
	 * @param in the BitInputStream being read
	 * @param out the BitOutputStream being written to
	 */
	public void decode(BitInputStream in, BitOutputStream out) {
		String temp = "";

		while(true) {
			int c = in.readBits(8);
			if(c == -1) {
				return;
			}
			temp += (char)c; //converts to "1" or "0"
			System.out.println(temp);
			if(this.map.containsKey(temp)) {
				out.writeBits(this.map.get(temp), 8); //writes the binary equivalent of character ASCII values
				temp = "";
			}
		}
	}
	
	
	
	
	/* EXTRA CODES */
	private void buildPath(Node cur, String s) {
		if (cur.left != null && cur.right != null) {
			buildPath(cur.left, s + "0");
			buildPath(cur.right, s + "1");
		} else {
			this.arr[cur.character] = s;
		}
		
		createDecodeMap(); //from this new array, construct a corresponding map to decode it
	}
	
	private void createDecodeMap() {
		this.map = new HashMap<String, Integer>();
		for(int i = 0; i < this.arr.length; i ++) {
			if(this.arr[i] != null) {
				this.map.put(this.arr[i], i);
			}
		}
	}

	
	
	
	
	public void printArr() {
		for (int i = 0; i < this.arr.length; i++) { // CHECK THIS -- DOESN'T MATCH HOMEWORK CHART
			if (this.arr[i] != null) {
				System.out.println("Character value: " + i);
				System.out.println("Character path:  " + this.arr[i] + "\n");
			}
		}
	}
	
	public int getRootFrequency() {
		return this.root.frequency;
	}
	

	
	
	

	public static void main(String[] args) throws IOException {
		Map<Short, Integer> m = new HashMap<Short, Integer>();

		// ask about frequencies -- using array mapping from lab
		m.put((short) 'z', 1);
		m.put((short) 'a', 3);
		m.put((short) 'b', 2);
		m.put((short) ' ', 2);

		HuffmanTree t = new HuffmanTree(m);

		//t.buildPath(t.root, "");
		//t.printArr();

		//System.out.println("Tree frequency: " + t.root.frequency);

		BitInputStream inputEncode = new BitInputStream("TestInput.txt");
		BitOutputStream outputEncode = new BitOutputStream("TestOutput3.txt", true);
		
		
		

		/* Convert from characters to 01s */
		t.encode(inputEncode, outputEncode);
		
		inputEncode.close();
		outputEncode.close();
		// output.writeBit(1);
		
		
		
		
		BitInputStream inputDecode = new BitInputStream("TestOutput3.txt");
		BitOutputStream outputDecode = new BitOutputStream("TestDecode3.txt", true);
		
		/* Convert from 01s to characters */
		t.decode(inputDecode, outputDecode);
		
		
		/* MAP CREATION TEST */
//		for(int i = 0; i < t.map.keySet().toArray().length; i++) {
//			System.out.println(t.map.keySet().toArray()[i]);
//			System.out.println(Character.toChars(t.map.get(t.map.keySet().toArray()[i])));
//			System.out.println();
//		}


		//System.out.println(inputEncode.hasBits());      /////////////SEEMS TO BE BROKEN?
		//System.out.println(inputDecode.hasBits());

		inputDecode.close();
		outputDecode.close();

	}
}