
/*Data is the data packet that will be sent from Sender to Receiver.*/
class Data { //This is our monitor
	private String packet; //data transferred over the network.

	// True if receiver should wait.
	// False if sender should wait.
	private boolean transfer = true; //used to sync Sender & Receiver


	// The sender will use send() to send data to receiver.
	public synchronized void send(String packet) {
		while(!transfer) { // we need to wait
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread interrupted: " + e.toString());
			}
		}
		transfer = false; // toggle status
		this.packet = packet; // set the message
		notifyAll(); // wake up all other threads
	}

	// The receiver will use receive() to get data.	
	public synchronized String receive() {
		while (transfer) { //Wait unless the receiver set transfer to False.
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread interrupted: " + e.toString());
			}
		}
		transfer = true; //toggle status
		notifyAll(); // wake up all other threads

		return packet; // return the data
	}
}

class Sender implements Runnable {
	private Data data;
	private String[] packets;

	public Sender(Data data, String[] packets) {
		this.data = data;
		this.packets = packets;
	}

	public void run() {
		for (String packet: packets) {
			data.send(packet);
		}
	}
}

class Receiver implements Runnable {
	private Data load; 

	public Receiver(Data data) {
		this.load = data;
	}

	public void run() {
		String receivedMessage = load.receive();
		while (!("Stop").equals(receivedMessage)) {
			System.out.println(receivedMessage);
			receivedMessage = load.receive();
		}
	}
}

public class ThreadSynchronization {
	public static void main(String[] args) {
		Data data = new Data();
		String[] packets = {"1st Packet", "2nd Packet", "3rd Packet", "4th Packet", "5th Packet", "6th Packet", "7th Packet", "8th Packet", "9th Packet", "10th Packet", "Stop"};

		for (int i = 0; i < 1; i++) {
			Sender sendTask = new Sender(data, packets);	
			Thread sender = new Thread(sendTask);

			Receiver receiverTask = new Receiver(data);
			Thread receiver = new Thread(receiverTask);
				
			sender.start();
			receiver.start();
		}
	}
}