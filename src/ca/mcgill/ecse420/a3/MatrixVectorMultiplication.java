package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixVectorMultiplication {

    private static final int NUMBER_THREADS = 4;
    private static final int MATRIX_SIZE = 5;

    public static void main(String[] args) {

        // Generate two random matrices, same size
        double[][] M = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        double[] v = generateRandomVector(MATRIX_SIZE);

//        multiplyTimed(M, v);
    }

    /**
     * Returns the result of a sequential matrix-vector multiplication
     * The matrix and vector are randomly generated
     *
     * @param M is the matrix
     * @param v is the vector
     * @return the result of the multiplication
     */
    public static double[] sequentialMatrixVectorMultiplication(double[][] M, double[] v) {
        // check if the dimensions are correct
        // the number of columns in M should be equal to the number of elements in v
        if (M[0].length != v.length) {
            try {
                throw new Exception("Invalid input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int m = M.length;
        int n = v.length;
        double[] prod = new double[m];

        /*
        multiply each row of matrix a with the vector
         */
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                prod[i] += M[i][j] * v[j];
            }
        }

        return prod;
    }

    /**
     * Returns the result of a concurrent matrix multiplication
     * The two matrices are randomly generated
     *
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {

        // check if the matrix dimensions are correct
        // the number of columns in a should be equal to the number of rows in b
        if (a[0].length != b.length) {
            try {
                throw new Exception("Invalid input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*
        aRows is the number of rows in matrix a
        bRows is the number of rows in matrix b
        aColumns is the number of columns in matrix a which is equal to the number of columns in matrix b
        prod is the product matrix
         */
        int aRows = a.length;
        int bColumns = b[0].length;
        double[][] prod = new double[aRows][bColumns];

        // Creating threads
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        /*
        Create a thread for each row of a that is going to be multiplies, each thread will take take of one row multiplication
         */
        for (int i = 0; i < aRows; i++) {
            executor.execute(new RowMultiply(prod, a, b, i));
        }

        executor.shutdown();

        // waiting for threads to complete their execution
        try {
            executor.awaitTermination(MATRIX_SIZE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return prod;
    }


    public static class RowMultiply implements Runnable {

        private double[][] prod, a, b;
        private final int row;

        /**
         * Constructor for the RowMultiply class
         *
         * @param prod product matrix till now
         * @param a    first matrix
         * @param b    second matrix
         * @param row  row number that is being multiplies by the current thread
         */
        public RowMultiply(double[][] prod, double[][] a, double[][] b, int row) {
            this.prod = prod;
            this.a = a;
            this.b = b;
            this.row = row;
        }

        /**
         * Multiplies the given row and inputs the result in the prod matrix
         */
        public void run() {

            for (int i = 0; i < b[0].length; i++) {
                prod[row][i] = 0;
                for (int j = 0; j < a[row].length; j++) {
                    prod[row][i] += a[row][j] * b[j][i];
                }
            }
        }
    }


//    /**
//     * multiplies the matracies by using the sequentialMultiplyMatrix and parallelMultiplyMatrix method
//     * prints the time it tool to complete the task using each method.
//     *
//     * @param a first matrix to be multiplied
//     * @param b second matrix to be multiplied
//     */
//    public static void multiplyTimed(double[][] a, double[][] b) {
//        Date start, end;
//        double[][] res;
//
//        start = new Date();
//        res = sequentialMultiplyMatrix(a, b);
//        end = new Date();
//        System.out.println("\nsequential multiplication (milli seconds): " + (end.getTime() - start.getTime()));
//
//        start = new Date();
//        res = parallelMultiplyMatrix(a, b);
//        end = new Date();
//        System.out.println("\nparallel multiplication (milli seconds): " + (end.getTime() - start.getTime()));
//    }

    /**
     * Populates a matrix of given size with randomly generated integers between 0-10.
     *
     * @param numRows number of rows
     * @param numCols number of cols
     * @return matrix
     */
    private static double[][] generateRandomMatrix(int numRows, int numCols) {
        double matrix[][] = new double[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }

    /**
     * Populates a vector of given size with randomly generated integers between 0-10.
     *
     * @param num number of elements
     * @return vector
     */
    private static double[] generateRandomVector(int num) {
        double vector[] = new double[num];
        for (int i = 0; i < num; i++) {
            vector[i] = (double) ((int) (Math.random() * 10.0));
        }
        return vector;
    }

}
