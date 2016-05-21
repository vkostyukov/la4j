# Description

Linear Algebra for Java (la4j) is an open-source Java library that allows its users to do operations using matrices and vectors. Initially, it was just a student project and then it became popular. 
This isn't a project with a wide range of scenarios and applications, as it is only a library, and a very specific one, thus, the stakeholders are all and every programmer who wish to implement this library in their programs in the most convenient way, along with the developer of this library. 
Within the curricular unit of Software Architecture, it is our job to evaluate la4j in terms of software architecture.
After initial analysis, we concluded that *la4j* follows an object-oriented architecture, which is standard with Java projects. All objects, like matrices and vectors, are self-contained, since can describe themselves from information that they contain, and have methods for their manipulation, and they are able to interact with other objects.

# Examples

We chose the most relevant features that are related with the most important parts of Linear Algebra, in order to present a comprehensive list of examples of what this library can do. 

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

This diagram represents potential use cases for this library.

![usecase](uml/UseCaseDiagram.png)