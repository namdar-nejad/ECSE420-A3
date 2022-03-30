
// Q2.1
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


// Q2.2
import java.util.concurrent.*;

public class Q2Test {
    private static final int THREADS = 4;
    private static final int ITEMS = 1000;
    private static final int NUM_TESTS = 30;

    private static Q2Contains<Integer> ourList = new Q2Contains<>();

    public static void main(String[] args) {
    	Random rand = new Random();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
       

        for(int i = 0; i<NUM_TESTS; i++){
        	int testNum = rand.nextInt(ITEMS);
        	ListRunnable runList = new ListRunnable(testNum);
        	executor.execute(runList);
        }
        
        executor.shutdown();
    }

    private static class ListRunnable implements Runnable {
        int checkIndex;
        private ListRunnable(i) {
            this.checkIndex = i;
        }
        public void run() {
            for (int i = 0; i < ITEMS/THREADS; i++) {
            	try{
            		ourList.add(i);
            	}
            }

            if (ourList.contains(checkIndex)) {
                System.out.println("Item" + checkIndex + " found");
            }
            else{
            	System.out.println("Item" + checkIndex + " not found");
            }
        }
    }
}