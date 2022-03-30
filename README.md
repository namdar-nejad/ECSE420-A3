# ECSE420-A3
Q1 & Q2: https://docs.google.com/document/d/1cS2c4FhNmBTUPNL-YwyA1TGwbrMNRHofLwIOE6XivKw/edit?usp=sharing

Q1.
1.1.
Since the average time per access remains constant in the green graph, we can conclude that we are fetching each element of the array from the memory and are able to store it in the cache without doing extra operations (write, replace, … due to the cache misses). So L’ represents the size of the cache which is 4 words here and t0 is the average access time to the array that is entirely in the memory,

1.2.
If the array size is bigger than the cache size, we will be facing cache misses at some points. So t1 is the average access time to the array when fetching elements that are not in the cache.

1.3.
Part 1: The entire array can fit into the cache and we have an empty cache so the access time is constant and rather small.
Part 2: In this case, we are slowly facing cache misses and the access time is not constant. As the stride increase so does the access time.
Part 3: In this section, we have reached a point where the cache is entirely full and we are only having cache misses. So the access time is constant but quite high.

1.4.
Using the padding technique would increase the cache misses as we would be filling the cache with extra data that is not useful, thus degrading the overall performance of the lock.

Q2.

2.1.
The implementation for the contains() can be found in the Q2Contains class.
The fine-grained agorithm described in chapter 9.5 improves concurrency by locking individual nodes instead of the entire list.  We need a “lock coupling” protocol to do our operations on the list. In the contains() method, we iterate throu the list until we find the key we are looking for, and check at each step compare it with the key of the node.

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



2.2.
The test code can be found in the Q2Test class. In the test method we create a number of threads that add a certain number of items to the list. After completing the list we check if it contain a given element and print the result. 

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


