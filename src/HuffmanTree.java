import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
	
	/** Node Class
	 * @author chiarazizza
	 *
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
		public Node() {}
	}
	
	/** Explains how to compare two Nodes
	 * @author chiarazizza
	 *
	 */
	private class NodeComparator implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			if(n1.frequency > n2.frequency) {
				return 1;
			} else if(n1.frequency < n2.frequency) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	private Node root;
	
	/** Constructs a HuffmanTree from the given frequency map of 9-bit values
	 * @param m a Map.
	 */
	public HuffmanTree(Map<Short, Integer> m) {
		this.root = new Node();
		
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
		if(m.isEmpty()) {                                     ////recursive or while loop (three lines)
			throw new IllegalArgumentException("Map must not be empty.");
		} else {
			Iterator<Short> keySet = m.keySet().iterator();
			while(keySet.hasNext()) {
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
		if(pq.isEmpty()) {
			throw new IllegalArgumentException("PriorityQueue must not be empty.");
		} else {
			while(pq.size() > 1) {
				Node temp1 = pq.poll();
				Node temp2 = pq.poll();
				pq.add(new Node(temp1.frequency + temp2.frequency, temp1, temp2));
			}
			
			/* Assign root to new tree */
			this.root = pq.poll();
		}
	}
	
	/** Encodes the file given as a stream of bits into a compressed format
	 * using this Huffman tree. The encoded values are written, bit-by-bit to
	 * the given BitOuputStream
	 * @param in the BitInputStream being read
	 * @param out the BitOutputStream being written to
	 */
	public void encode(BitInputStream in, BitOutputStream out) {
		
	}
	
	/** Decodes a stream of huffman codes from a file given as a stream of bits
	 * into their uncompressed form, saving the results to the given output stream
	 * @param in the BitInputStream being read
	 * @param out the BitOutputStream being written to
	 */
	public void decode(BitInputStream in, BitOutputStream out) {
		
	}
	
    public void buildPath(String[] arr, Node cur, String s) {
        if (cur.left != null && cur.right != null) {
            buildPath(arr, cur.left,  s + "0");
            buildPath(arr, cur.right, s + "1");
        } else {
           arr[cur.character] = s;
        }
    }
	
	public static void main(String[] args) {
		Map<Short, Integer> m = new HashMap<Short, Integer>();
		m.put((short) 'z', 1);
		m.put((short) 'a', 3);
		m.put((short) 'b', 2);
		m.put((short) ' ', 2);
		
		HuffmanTree t = new HuffmanTree(m);
		String[] arr = new String[257];
		
		t.buildPath(arr, t.root, "");
		
		for(int i = 0; i < arr.length; i++) { //CHECK THIS -- DOESN'T MATCH HOMEWORK CHART
			if(arr[i] != null) {
				System.out.println("Character value: " + i);
				System.out.println("Character path:  " + arr[i] + "\n");
			}
		}
		
		System.out.println("Tree frequency: " + t.root.frequency);
	}
}
