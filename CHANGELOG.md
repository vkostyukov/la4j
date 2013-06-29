Changelog
---------
la4j-0.4.5 `Dec 2013`
 * New vector methods: `innerProduct`, `outerProduct` (contributed by Daniel Renshaw)
 * Bug fix in `Vector.subtract` method (contributed by Ewald Grusk)
 * Bug fix in `Matrix.subtruct` method (contributed by Ewald Grusk)
 * New matrix method `rotate` (contributed by Jakob Moellers)
 * New matrix method `shuffle` (contributed by Jakob Moellers)

la4j-0.4.0 `Jun 2013`
 * Up to 2x performance improvement of sparse entries (binary search power)
 * Performance improvement for matrix-by-matrix multiply algorithm (3x for dense, 11x for sparse)
 * New matrix method `rank()` (contributed by Evgenia Krivova)
 * New fast implementation of `determinant()` method
 * New method `update()` (as compound operator replacement)
 * Matrices are unsafe by default (new corresponding methods `safe()` and `unsafe()`)
 * New method `slice()`
 * New method for `kronecker()` product
 * Support map-reduce approach by method pair `transform()` and `fold()`
 * New matrices and vectors sources that handles IO streams
 * Support of building a constant matrix in factories
 * Matrices and vectors are immutable in terms of dimension
 * Sparse entities are self-clearing (no memory leaks)
 * Support vector-by-matrix multiplication
 * Bug fix in `MatrixDecFunction`
 * Bug fix in `MatrixMatketStream` (contributed by Alessio Placitelli)
 * Bug fix in matrix-to-vector multiplication (contributed by Pavel Kalaidin)
 * Bug fix in `align()` method (contributed by Chandler May)
 * Bug fix in `QRDecompositor`
 
la4j-0.3.0 `Dec 2012`
 * New API and new package naming (starting with "org.la4j.*")
 * New source code hosting system - GitHub
 * New matrix types Basic1DMatrix and CCSMatrix
 * New I/O API and format - Symbol Separated Stream (CSV, TSV, etc.);
 * New entries: matrix/vector sources, matrix/vector functors
 * New exception model
 * Support of unsafe accessors and arithmetics methods
 * Support Eigenvalues decomposition for non-symmetric matrix
 * New fast matrix-to-matrix multiply algorithm with blocks
 * New algorithm for runtime-based machine epsilon initialization
 * Bug fixes for several major/critical issues
 
la4j-0.2.0 `Nov 2011`
 * New package la4j.io for reading/writing matrices in MatrixMarket format
 * New matrices decomposition (LU, QR, Cholesky, SVD, Eigenvalues)
 * New sparse (Compressed Row Storage), dense matrices and vectors support
 * Matrices and vectors can be serialized
 
la4j-0.1.0 `Jan 2011`
 * Eigenvalues decomposition
 * Fast matrix multiply algorithm implementation
 
la4j-0.0.7 `Mar 2010`
 * Solving linear systems
 * Matrices transposing
 * Calculation of inverted matrix
 
la4j 0.0.0 `Jan 2010`
 * Support real matrices and vectors
