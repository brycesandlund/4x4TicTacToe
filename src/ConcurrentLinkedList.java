
/**
 * Class that allows for concurrent modification and iteration on a LinkedList. To make it function
 * properly, the same removed Node references need to be added back into the list.
 * @author Bryce Sandlund
 *
 * @param <T>
 */
public class ConcurrentLinkedList<T>{
	
	/**
	 * Number of elements in LinkedList
	 */
	private int size;
	
	/**
	 * Null-data head.
	 */
	private Node head;
	
	public ConcurrentLinkedList()
	{
		head = new Node(null, null, null);
		head.next = head;
		head.prev = head;
	}
	
	/**
	 * Add item into List
	 * @param item
	 * @return
	 */
	public boolean add(T item)
	{
		++size;
		head.prev.next = new Node(item, head, head.prev);
		head.prev = head.prev.next;
		return true;
	}
	
	/**
	 * prints list in readable format
	 */
	public String toString()
	{
		SpecialIterator iter = specialIterator();
		StringBuilder strB = new StringBuilder();
		strB.append("[");
		if (iter.hasNext())
		{
			strB.append(iter.next());
		}
		while (iter.hasNext())
		{
			strB.append(", " + iter.next());
		}
		strB.append("]");
		return strB.toString();
	}
	
	/**
	 * Get size
	 * @return
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * Returns an iterator with special functions that will allow optimization.
	 * @return
	 */
	public SpecialIterator specialIterator()
	{
		return new SpecialIterator();
	}
	
	/**
	 * Iterator object with a remove and add that use Node object references.
	 * @author Bryce Sandlund
	 *
	 */
	public class SpecialIterator
	{
		/**
		 * cursor variable
		 */
		private Node cur = head.next;
		
		/**
		 * Only allow adds with Node references (that were previously removed)
		 * @param node
		 */
		public void add(Node node)
		{
			++size;
			cur.prev.next = node;
			cur.prev = cur.prev.next;
		}
		
		/**
		 * True if there is a next item
		 * @return
		 */
		public boolean hasNext()
		{
			return cur != head;
		}
		
		/**
		 * Removes the last returned item and returns that Node object reference
		 * @return
		 */
		public Node remove()
		{
			--size;
			//reset cur
			Node removal = cur.prev;
			cur.prev.prev.next = cur;
			cur.prev = cur.prev.prev;
			return removal;
		}
		
		/**
		 * Gets the next element in the list
		 * @return
		 */
		public T next()
		{
			if (cur.next.prev != cur)
			{
				//this condition should never happen	
				System.out.println("something is messed up");
			}
			cur = cur.next;
			return cur.prev.data;
		}
	}
	
	/**
	 * Basic Node class
	 * @author Bryce Sandlund
	 *
	 */
	public class Node
	{
		/**
		 * Data
		 */
		private T data;
		
		/**
		 * Next pointer
		 */
		private Node next;
		
		/**
		 * Previous pointer
		 */
		private Node prev;
		
		public Node(T data, Node next, Node prev)
		{
			this.data = data;
			this.next = next;
			this.prev = prev;
		}
		
	}
}
