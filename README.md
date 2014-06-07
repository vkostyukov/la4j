[la4j (Linear Algebra for Java)](http://la4j.org)  [![Build Status](https://travis-ci.org/vkostyukov/la4j.png?branch=master)](https://travis-ci.org/vkostyukov/la4j)
=============================
<img src="https://raw.github.com/vkostyukov/la4j/master/la4j-logo.png" width="220px" />

The [**la4j**](http://la4j.org) is an open source and **100% Java** library
that provides **Linear Algebra** primitives (matrices and vectors) and algorithms. The **la4j** library was initially
designed to be a lightweight and simple tool for passionate Java developers. It has been started as student project
and turned into one of the most popular Java packages for matrices and vectors.

The key features of the **la4j** are listed bellow:

* Great performance allied with beautiful design
* No dependencies and tiny size (~150kb jar)
* Fluent and object-oriented/functional API
* Sparse (CRS, CCS) and dense (1D/2D arrays) matrices and vectors
* Linear systems solving (Gaussian, Jacobi, Seidel, Square Root, Sweep and other)
* Matrices decomposition (Eigenvalues/Eigenvectors, SVD, QR, LU, Cholesky and other)
* Functors support: predicates, functions, procedures and accumulators
* MatrixMarket/CSV IO formats support


Samples
-------
**Building the matrix**
```java
// We will use a CRS (Compressed Row Storage) matrix builder
// We will use a 2D array matrix builder
Matrix b = Matrices.asBuilder(LinearAlgebra.BASIC2D_FACTORY)
           .shape(25, 25)    // We want a 25x25 matrix
           .source(3.14)     // ... with all the elements equal '3.14'
           .buildDiagonal(); // ... and it should be a diagonal matrix

// We wil use a CCS (Compressed Column Storage) matrix builder
Matrix c = Matrices.asBuilder(LinearAlgebra.CCS_FACTORY)
           .shape(14, 14)         // We want a 14x14 matrix
           .source(new Random())  // ... with random elements
           .buildSymmetric();     // ... and it should be a symmetric matrix
```

**Building the vector**
```java
// We will use a dense vector builder
Vector a = Vectors.asBuilder(LinearAlgebra.DENSE_FACTORY)
           .length(10)            // We want 10 elements vector
           .source(new Random())  // ... with random values
           .build();

Vector a = Vectors.asBuilder(LinearAlgebra.DENSE_FACTORY)
           .length(10)                    // We want 10 elements vector
           .source(new double[] { 1.0 })  // ... with the values from an array
           .build();                      // ... and it should be extended to a requested size
```

**Matrix inversion**
```java
// We want a simple dense matrix that uses 2D array as internal representation
Matrix a = new Basic2DMatrix(new double[][] {
   { 1.0, 2.0, 3.0 },
   { 4.0, 5.0, 6.0 },
   { 7.0, 8.0, 9.0 }
});

// We will use Gauss-Jordan method for inverting
MatrixInverter inverter = a.withInverter(LinearAlgebra.GAUSS_JORDAN);
// The 'b' matrix will be dense
Matrix b = inverter.inverse(LinearAlgebra.DENSE_FACTORY);
```
**System of linear equations**
```java
// The coefficient matrix 'a' is a CRS (Compressed Row Storage) matrix
Matrix a = new CRSMatrix(new double[][] {
   { 1.0, 2.0, 3.0 },
   { 4.0, 5.0, 6.0 },
   { 7.0, 8.0, 9.0 }
});

// A right hand side vector, which is simple dense vector
Vector b = new BasicVector(new double[] {
   1.0, 2.0, 3.0
});

// We will use standard Forward-Back Substitution method,
// which is based on LU decomposition and can be used with square systems
LinearSystemSolver solver = a.withSolver(LinearAlgebra.FORWARD_BACK_SUBSTITUTION);
// The 'x' vector will be sparse
Vector x = solver.solve(b, LinearAlgebra.SPARSE_FACTORY);
```
**Matrix decomposition**
```java
// We want simple dense matrix, which is based on 1D double array
Matrix a = new Basic1DMatrix(new double[][] {
   { 1.0, 2.0, 3.0 },
   { 4.0, 5.0, 6.0 },
   { 7.0, 8.0, 9.0 }
});

// We will use LU decomposer
MatrixDecomposer decomposer = a.withDecomposer(LinearAlgebra.LU);
// The result should be treated as: L = lup[0], U = lup[1], P = lup[2]
Matrix[] lup = decomposer.decompose(LinearAlgebra.DENSE_FACTORY);
```

Changelog
------------

See [CHANGELOG.md](https://github.com/vkostyukov/la4j/blob/master/CHANGELOG.md)


Download
--------
 
Details of the last version of the [**la4j**](http://la4j.org) can be found on the
project web site <http://la4j.org> or its GitHub page <https://github.com/vkostyukov/la4j>.


Licensing
---------
 
This software is licensed under the terms you may find in the file 
named "LICENSE" in this directory.

How To Contribute
-----------------

- Fork it
- Create your feature branch (`git checkout -b my-new-feature`)
- Commit your changes (`git commit -am 'Add some feature'`)
- Push to the branch (`git push origin my-new-feature`)
- Create new Pull Request
 
 
Contributors
------------

See [CONTRIBUTORS.md](https://github.com/vkostyukov/la4j/blob/master/CONTRIBUTORS.md)

----
by [Vladimir Kostyukov](http://vkostyukov.ru), 2011-2014


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/vkostyukov/la4j/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

