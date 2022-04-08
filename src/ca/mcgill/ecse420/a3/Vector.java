package ca.mcgill.ecse420.a3;

import java.util.Arrays;

public class Vector {
    int dim;
    double[] data;
    int displace;

    public Vector(int d) {
        dim = d;
        displace = 0;
        data = new double[d];
    }

    public Vector(double[] vector) {
        data = vector;
        displace = 0;
        dim = vector.length;
    }

    private Vector(double[] vector, int x, int d) {
        data = vector;
        displace = x;
        dim = d;
    }

    public double get(int i) {
        return data[i + displace];
    }

    public void set(int i, double value) {
        data[i + displace] = value;
    }

    public int getDim() {
        return dim;
    }

    public Vector[] split() {
        Vector[] result = new Vector[2];
        int newDim = dim / 2;

        result[0] = new Vector(data, displace, newDim);
        result[1] = new Vector(data, displace + newDim, newDim);

        return result;
    }

    public double[] getData() {
        return Arrays.copyOf(data, data.length);
    }
}