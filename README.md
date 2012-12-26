la4j (Linear Algebra for Java)  [![Build Status](https://travis-ci.org/vkostyukov/la4j.png?branch=master)](https://travis-ci.org/vkostyukov/la4j)
=============================

[la4j](http://la4j.org) - is open source and 100% Java library 
that provides Linear Algebra primitives and algorithms. There are the 
key features of la4j listed bellow:

- no dependencies and tiny size (~100kb jar)
- minimalistic and full object-oriented API
- sparse (CRS, CCS) and dense (1D/2D arrays) matrices
- linear systems solving (Gaussian, Jacobi, Zeidel, Square Root, Sweep and other)
- matrices decomposition (Eigenvalues, SVD, QR, LU, Cholesky and other)
- functors support: predicates, functions and procedures
- MatrixMarket/CSV IO formats support


Changelog
---------
 
la4j-0.3.0 <code>Dec 2012</code>
 * la4j got new API and new package naming (starting with "org.la4j.*")
 * la4j has been moved from Google Code to GitHub;
 * la4j uses Travis-CI;
 * la4j available through Maven now;
 * support of new matrices types (Basic1DMatrix, CCSMatrix);
 * new I/O API and format - Symbol Separated Stream (CSV, TSV, etc.);
 * new la4j entities: matrix/vector sources, matrix/vector functors;
 * la4j uses default unchecked exceptions from java.lang.* now;
 * the code has been formated according to JavaCodeConv;
 * support of unsafe accessors and arithmetic methods;
 * support Eigenvalues decomposition for non-symmetric matrix;
 * new fast matrix-to-matrix multiply algorithm with blocks;
 * new algorithm for runtime-based machine epsilon initialization;
 * fixed several major/critical bugs;
  
la4j-0.2.0 <code>Nov 2011</code>
 * new package la4j.io for reading/writing matrices in MatrixMarket format;
 * matrices decomposition (LU, QR, Cholesky, SVD, Eigenvalues) support;
 * matrix inversion support;
 * sparse (Compressed Row Storage), dense matrices and vectors support;
 * matrices and vectors can be serialized;
 * la4j uses Maven and jUnit now;
 
la4j-0.1.0 <code>Jan 2011</code>
 * eigenvalues decomposition;
 * fast matrix multiply algorithm implementation;
 
la4j-0.0.7 <code>Mar 2010</code>
 * solving linear systems;
 * matrices transposing;
 * finding of inverted matrix;
 
la4j 0.0.0 <code>Jan 2010</code>
 * support real matrices and vectors;
 
 
Download
--------
 
 Details of the latest version of la4j can be found on the la4j
 project web site <http://la4j.org> or GitHub page 
 <https://github.com/vkostykov/la4j>.
 
 
Licensing
---------
 
 This software is licensed under the terms you may find in the file 
 named "LICENSE" in this directory.
 
 
Contributors
------------

La4j wouldn't be the library it is today without the source contributions 
made by the authors:
- Wajdy Essam
- Evgenia Krivova
- Julia Kostyukova

----
by [Vladimir Kostyukov](http://vkostyukov.ru), 2012
