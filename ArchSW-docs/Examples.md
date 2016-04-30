# Examples

### Create a matrix and print it
```java
public static double[] a(double... values) {
    return values;
}

public static Matrix m(double[]... values) {
    return Matrix.from2DArray(values);
}

public static void main (String[] args) {
	Matrix a = m(a(1.0, 2.0, 3.0),
            a(4.0, 5.0, 6.0),
            a(7.0, 8.0, 9.0));
    System.out.print(a.toString());
}
```

### Create two matrixes, sum them and print the sum
```java
public static double[] a(double... values) {
    return values;
}

public static Matrix m(double[]... values) {
    return Matrix.from2DArray(values);
}

public static void main (String[] args) {
	Matrix a = m(a(1.0, 2.0, 3.0),
            a(4.0, 5.0, 6.0),
            a(7.0, 8.0, 9.0));
 	Matrix b = m(a(1.0, 2.0, 3.0),
            a(4.0, 5.0, 6.0),
            a(7.0, 8.0, 9.0));
	Matrix c = a.add(b);
    System.out.print(c.toString());
}
```

### Create a vector
```java
public static double[] a(double... values) {
    return values;
}
	
public static void main (String[] args) {
	Vector a = Vector.fromArray(a(1.0, 2.0, 3.0));
}
```

### Add two vectors and print the norm of the sum
```java
public static double[] a(double... values) {
	    return values;
}

public static void main (String[] args) {
	Vector a = Vector.fromArray(a(1.0, 2.0, 3.0));
	Vector b = Vector.fromArray(a(4.0, 5.0, 6.0));
	Vector c = a.add(b);
	System.out.print(c.norm());
}
```

### Solving an Ax=B system with the Gaussian method
```java

/* A:
 * 100
 * 010
 * 001
 *
 * B:
 * 1
 * 3
 * 9
*/

public static double[] a(double... values) {
    return values;
}

public static void main (String[] args) {
	Matrix a = Matrix.identity(3);
	Vector b = Vector.fromArray(a(1.0, 3.0, 9.0));
	GaussianSolver solver = new GaussianSolver(a);
	System.out.print(solver.solve(b));
}
```

### Inverting a matrix with the Gauss-Jordan method
```java
public static double[] a(double... values) {
    return values;
}
	
public static Matrix m(double[]... values) {
    return Matrix.from2DArray(values);
}

public static void main (String[] args) {
	Matrix a = m(a(1.0, 2.0, 3.0),
            a(4.0, 5.0, 6.0),
            a(7.0, 8.0, 8.0));
	GaussJordanInverter inverter = new GaussJordanInverter(a);
	Matrix a_inverted = inverter.inverse();
	System.out.print(a_inverted.toString());
}
```