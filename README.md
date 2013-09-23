[la4j (Linear Algebra for Java)](http://la4j.org)  [![Build Status](https://travis-ci.org/vkostyukov/la4j.png?branch=master)](https://travis-ci.org/vkostyukov/la4j)
=============================
![la4j logo](https://raw.github.com/vkostyukov/la4j/master/la4j-logo.png) 

The [**la4j**](http://la4j.org) is open source and **100% Java** library 
that provides **Linear Algebra** primitives (matrices and vectors) and algorithms. The **la4j** was initially designed 
to be lightweight and simple tool for passionate Java developers. It has been started as student project and turned into
one of the most popular Java packages for matrices and vectors.

The key features of the **la4j** are listed bellow:

* No dependencies and tiny size (~150kb jar)
* Simple and object-oriented/functional API
* Sparse (CRS, CCS) and dense (1D/2D arrays) matrices and vectors
* Linear systems solving (Gaussian, Jacobi, Zeidel, Square Root, Sweep and other)
* Matrices decomposition (Eigenvalues, SVD, QR, LU, Cholesky and other)
* Functors support: predicates, functions, procedures and accumulators
* MatrixMarket/CSV IO formats support


Brief Example
------------
```java
Matrix a = new Basic2DMatrix(new double[][] {
   { 1.0, 2.0, 3.0 },
   { 4.0, 5.0, 6.0 },
   { 7.0, 8.0. 9.0 }
});

Matrix b = a.invert(Matrices.DEFAULT_INVERTER); // uses Gaussian Elimination 
```


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

The [**la4j**](http://la4j.org) wouldn't be the library it is today without the source contributions 
made by the authors:
- Wajdy Essam
- Evgenia Krivova
- Julia Kostyukova
- Alessio Placitelli
- Pavel Kalaidin
- Chandler May
- Daniel Renshaw
- Ewald Grusk
- Jakob Moellers
- Yuriy Drozd
- Maxim Samoylov

----
by [Vladimir Kostyukov](http://vkostyukov.ru), 2011-2013


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/vkostyukov/la4j/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

