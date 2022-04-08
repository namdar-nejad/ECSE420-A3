package ca.mcgill.ecse420.a3;

import ca.mcgill.ecse420.a3.utils.Matrix;
import ca.mcgill.ecse420.a3.utils.Vector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelMatrixVectorMultiplication {

    private static ExecutorService executor;

    /**
     * Returns the result of a concurrent matrix-vector multiplication
     * The matrix and vector are randomly generated
     *
     * @param M is the matrix
     * @param v is the vector
     * @return the result of the multiplication
     */
    public static double[] multiply(double[][] M, double[] v) {
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
        executor = Executors.newCachedThreadPool();

        Matrix M_matrix = new Matrix(M);
        Vector v_vector1 = new Vector(v);
        Vector v_vector = new Vector(v);
        Vector c_vector = new Vector(n);

        try {
            Future<?> future = executor.submit(new MultiplyTask(M_matrix, v_vector, c_vector));
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        return c_vector.getData();
    }


    public static class MultiplyTask implements Runnable {

        private Matrix M;
        private Vector v, c, lhs, rhs;

        public MultiplyTask(Matrix M, Vector v, Vector c) {
            this.M = M;
            this.v = v;
            this.c = c;

            this.lhs = new Vector(M.getDim());
            this.rhs = new Vector(M.getDim());
        }

        /**
         * Multiplies the given row and inputs the result in the prod matrix
         */
        public void run() {
            try {
                if (M.getDim() == 1) {
                    c.set(0, M.get(0, 0) * v.get(0));
                } else {
                    Matrix[][] splitM = M.split();
                    Vector[] splitV = v.split();
                    Vector[] splitLHS = lhs.split();
                    Vector[] splitRHS = rhs.split();

                    Future<?>[][] future = (Future<?>[][]) new Future[2][2];
                    for (int i = 0; i < 2; i++) {
                        future[i][0] = executor.submit(new MultiplyTask(splitM[i][0], splitV[0], splitLHS[i]));
                        future[i][1] = executor.submit(new MultiplyTask(splitM[i][1], splitV[1], splitRHS[i]));
                    }

                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            future[i][j].get();
                        }
                    }

                    Future<?> done = executor.submit(new AdditionTask(lhs, rhs, c));
                    done.get();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public static class AdditionTask implements Runnable {

        private Vector a, b, c;

        public AdditionTask(Vector a, Vector b, Vector c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void run() {
            try {
                if (a.getDim() == 1) {
                    c.set(0, a.get(0) + b.get(0));
                } else {
                    Vector[] splitA = a.split();
                    Vector[] splitB = b.split();
                    Vector[] splitC = c.split();

                    Future<?>[] future = (Future<?>[]) new Future[2];

                    for (int i = 0; i < 2; i++) {
                        future[i] = executor.submit(new AdditionTask(splitA[i], splitB[i], splitC[i]));
                    }

                    for (int i = 0; i < 2; i++) {
                        future[i].get();
                    }
                }
            } catch (
                    Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
