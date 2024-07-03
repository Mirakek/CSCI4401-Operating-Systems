import java.util.Random;
import java.util.Date;


public class MatrixMultiplication {

	public static void main(String[] args) {	
		
		//Generate the two Matrices to multiply
		int[][] m1 = MatrixGeneratorUtil.generateMatrix(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		int[][] m2 = MatrixGeneratorUtil.generateMatrix(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		
		Date start = new Date();	
		int[][] result = multiply(m1, m2);
		Date end = new Date();

		//Output info
		System.out.println("\nRuntime: " + ((end.getTime() - start.getTime())/1000) + " seconds");
		//MatrixGeneratorUtil.print(m1);
		//MatrixGeneratorUtil.print(m2);
		//MatrixGeneratorUtil.print(result);
	}

	public static int[][] multiply(int[][] matrix1, int[][] matrix2) {
		int resultRows = matrix1.length; //Matrix1 m		
		int resultColumns = matrix2[0].length; //Matrix2 p
		int[][] result = new int[resultRows][resultColumns]; //Result Matrix m x p
		int n_values = matrix2.length; //Matrix1 & Matrix2 n

		for (int i = 0; i < resultRows; i++) { 
			for (int j = 0; j < resultColumns; j++) {
				result[i][j] = 0;
				for (int k = 0; k < n_values; k++) {
					result[i][j] += (matrix1[i][k] * matrix2[k][j]);
				}
			}
		}
		return result;
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