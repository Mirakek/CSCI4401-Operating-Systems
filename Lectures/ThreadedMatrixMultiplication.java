import java.util.Random;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;


public class ThreadedMatrixMultiplication {
	
	public static void main(String[] args) {		
	
		//Generate the two Matrices to multiply
		int[][] m1 = MatrixGeneratorUtil.generateMatrix(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		int[][] m2 = MatrixGeneratorUtil.generateMatrix(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

		//Generate an empty Matrix to store result.
		int[][] result = new int[m1.length][m2[0].length];

		Date start = new Date();
		ThreadsCreator.multiply(m1, m2, result);
		Date end = new Date();
		//MatrixGeneratorUtil.print(result);
		System.out.println("\nRuntime: " + ((end.getTime() - start.getTime())/1000) + " seconds");
	}
}

class ThreadsCreator {	
	public static void multiply(int[][] matrix1, int[][] matrix2, int[][] result) {
		
		// We want to use a list to limit the number of threads at once.
		//You can get an "Out of Memory Error"
		List<Thread> threads = new ArrayList<Thread>();
		
		int rows1 = matrix1.length; //Matrix1 m
		for (int i = 0; i < rows1; i++) {
			// Create a new task
			RowMultiplyWorker task = new RowMultiplyWorker(result, matrix1, matrix2, i);
			
			// Create a new thread
			Thread thread = new Thread(task);
			
			//Initialize the thread
			thread.start();

			//Add the thread to our list. If we have 10 threads, wait until they finish.
			threads.add(thread);
			if (threads.size() == 10) {
				for (Thread t : threads) {
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				threads.clear();
			}
		}
	}
}

class RowMultiplyWorker implements Runnable {
	private final int[][] result;
	private int[][] matrix1;
	private int[][] matrix2;
	private final int row;

	public RowMultiplyWorker(int[][] result, int[][] matrix1, int[][] matrix2, int row) {
		this.result = result;
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.row = row;
	}

	public void run() {
		for (int j = 0; j < matrix2[0].length; j++) { //Matrix2 p
			result[row][j] = 0;
			for (int k = 0; k < matrix1[row].length; k++) { //Matrix1 & Matrix2 n
				result[row][j] += matrix1[row][k] * matrix2[k][j];
			}
		}
	}
}



//Some useful functions for working with Matrices.
class MatrixGeneratorUtil {

	// A function that generates a Matrix containing random values.
	public static int[][] generateMatrix(int rows, int columns) {
		// output array to store the matrix values
		int[][] result = new int[rows][columns];

		// Generate a random integer.
		Random random = new Random();

		// adding values at each index.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				result[i][j] = random.nextInt(100) * 10;
			}
		}		
		return result;
	}

	// A function to print a Matrix.
	public static void print(int[][] matrix) {
		System.out.println();

		int rows = matrix.length;
		int columns = matrix[0].length;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				System.out.print(matrix[i][j] + "  ");
			}
			System.out.println();
		}
	}
}