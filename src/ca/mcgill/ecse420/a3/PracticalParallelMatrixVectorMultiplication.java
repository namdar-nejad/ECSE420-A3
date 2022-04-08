package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PracticalParallelMatrixVectorMultiplication {

    /**
     * Returns the result of a concurrent matrix-vector multiplication
     * The matrix and vector are randomly generated
     *
     * @param M is the matrix
     * @param v is the vector
     * @return the result of the multiplication
     */
    public static double[] multiply(double[][] M, double[] v, int threadCount) {
        int m = M.length;
        int _n = M[0].length;
        int n = v.length;

        // check if the dimensions are correct
        // the number of columns in M should be equal to the number of elements in v
        if (n != _n || m != n) {
            try {
                throw new Exception("Invalid input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Creating threads
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        /*
        Create a thread for each row of a that is going to be multiplies, each thread will take take of one row multiplication
         */
        double[] prod = new double[m];
        for (int i = 0; i < m; i++) {
            executor.execute(new DotProduct(prod, M, v, i));
        }

        executor.shutdown();

        // waiting for threads to complete their execution
        try {
            executor.awaitTermination(m, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return prod;
    }

    public static class DotProduct implements Runnable {

        private double[] prod;
        private double[][] M;
        private double[] v;
        private final int row;

        /**
         * Constructor for the RowMultiply class
         *
         * @param prod product vector till now
         * @param M    matrix
         * @param v    vector
         * @param row  row number that is being multiplies by the current thread
         */
        public DotProduct(double[] prod, double[][] M, double[] v, int row) {
            this.prod = prod;
            this.M = M;
            this.v = v;
            this.row = row;
        }

        /**
         * Multiplies the given row and inputs the result in the prod matrix
         */
        public void run() {
            prod[row] = 0;
            for (int i = 0; i < v.length; i++) {
                prod[row] += M[row][i] * v[i];
            }
        }
    }
}
