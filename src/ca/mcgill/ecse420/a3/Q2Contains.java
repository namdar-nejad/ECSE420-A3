package ca.mcgill.ecse420.a3;

public class Q2Contains {
	public boolean contains(T item){
		int key = item.hashCode();

		ListNode prev;
		ListNode curr;

		if(head.next != Null){
			curr = head.next;
			prev = head;

			head.lock();
			curr.lock();
		}

		try{
			try{
				while(true){
					prev.unlock();
					prev = curr;
					curr = curr.next;
					curr.lock();

					if (curr.key == key){
						return true;
					}
				}
			}
			finally{
				prev.unlock();
				curr.unlock();
			}
		}
		finally{
			return false;
		}
	}		
}