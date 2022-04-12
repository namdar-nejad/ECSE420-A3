package ca.mcgill.ecse420.a3;

public class SequentialMultiplier {
    /**
     * Returns the result of a sequential matrix-vector multiplication
     * The matrix and vector are randomly generated
     *
     * @param M is the matrix
     * @param v is the vector
     * @return the result of the multiplication
     */
    public static double[] multiply(double[][] M, double[] v) {
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

}
