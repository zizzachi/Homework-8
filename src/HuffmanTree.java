import java.io.File;
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
				return 1; // was 1
			} else if (n1.frequency < n2.frequency) {
				return -1; // was -1
			} else {
				return 0; // was 0
			}
		}
	}

	private Node root;
	private String[] arr;

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
		System.out.println("In encode.");
		int c = 0;
		c = in.readBits(8);

		while (c != -1) {
			System.out.println("In while loop of encode.");
			String temp = this.arr[c];
			for (int i = 0; i < temp.length(); i++) {
				if (temp.charAt(i) == '0') {
					System.out.println("In the '0' part.");
					out.writeBit(0);
				} else if (temp.charAt(i) == '1') {
					System.out.println("In the '1' part.");
					out.writeBit(1);
				} else {
					throw new IllegalArgumentException("Not '0' or '1'");
				}

			}
			c = in.readBits(8);
		}
	}

	/** Decodes a stream of huffman codes from a file given as a stream of bits
	 * into their uncompressed form, saving the results to the given output
	 * stream
	 * @param in the BitInputStream being read
	 * @param out the BitOutputStream being written to
	 */
	public void decode(BitInputStream in, BitOutputStream out) {

	}

	public void buildPath(Node cur, String s) {
		if (cur.left != null && cur.right != null) {
			buildPath(cur.left, s + "0");
			buildPath(cur.right, s + "1");
		} else {
			this.arr[cur.character] = s;
		}
	}

	public void printArr() {
		for (int i = 0; i < this.arr.length; i++) { // CHECK THIS -- DOESN'T
			// MATCH HOMEWORK CHART
			if (this.arr[i] != null) {
				System.out.println("Character value: " + i);
				System.out.println("Character path:  " + this.arr[i] + "\n");
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Map<Short, Integer> m = new HashMap<Short, Integer>();

		// ask about frequencies
		m.put((short) 'z', 1);
		m.put((short) 'a', 3);
		m.put((short) 'b', 2);
		m.put((short) ' ', 2);

		HuffmanTree t = new HuffmanTree(m);

		t.buildPath(t.root, "");
		t.printArr(); // added

		System.out.println("Tree frequency: " + t.root.frequency);

		BitInputStream input = new BitInputStream("TestInput.txt");
		BitOutputStream output = new BitOutputStream("TestOutput.txt", true);

		System.out.println(input.readBits(8));
		t.encode(input, output);
		// output.writeBit(1);

		System.out.println(input.hasBits());

		input.close();
		output.close();

	}
}
