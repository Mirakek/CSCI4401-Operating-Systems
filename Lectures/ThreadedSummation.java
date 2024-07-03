class Sum {
	private int sum;
	
	public int getSum() {
		return sum;	
	}
	
	public void setSum(int sum) {
		this.sum = sum;
	}
}

class Summation implements Runnable {
	private int upper;
	private Sum sumValue;
	
	public Summation(int upper, Sum sumValue) {
		this.upper = upper;
		this.sumValue = sumValue;
	}
	
	//We must define a run() method
	public void run() {
		int sum = 0;
		for (int i = 0; i <= upper; i++)
			sum += i;					
		sumValue.setSum(sum);
	}
}

public class ThreadedSummation {	

	public static void main(String[] args) {
		
		if (args.length > 0) {
			if (Integer.parseInt(args[0]) < 0)
				System.err.println(args[0] + " must be >= 0.");
			else {
				//Let's use a mutable object		
				Sum sumObject = new Sum();

				int upper = Integer.parseInt(args[0]);
				
				Summation task = new Summation(upper, sumObject);
				
				//Create a thread object
				Thread thrd = new Thread(task);

				//Create the actual thread
				thrd.start();
				try {
					thrd.join();
					System.out.println("Sum = " + sumObject.getSum());		
				} catch (InterruptedException ie) { }
			}
		} 
		else
			System.err.println("Usage : ThreadedSummation <integer value>");
	}	
}
	