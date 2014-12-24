Changelog
---------
la4j-0.5.0 `Apr 2014`
 * Bug fix in serialization algorithm of sparse matrices (contributed by Catherine da Graca)`
 * New sparse matrix methods: `foldNonZero()`, `foldNonZeroInRow()`, `foldNonZeroInColumn()`, `foldNonZeroInColumns()`, `foldNonZeroInRows()`
 * New sparse vector method: `foldNonZero()`
 * New entity: `MatrixBuilder`
 * New entity: `VectorBuilder`
 * New matrix/vector method `mkString()` for customized to-string conversion
 * Bug fix in random generation of sparse matrices
 * New sparse methods: `isZeroAt()` and `nonZeroAt()`
 * Method `eachNonZero` is available only for sparse entities
 * New sparse method: `getOrElse()`
 * Bug fix in `resize()` method of sparse vector (reported by Leonid Ilyevsky)
 * New matrix methods: `removeRow()`, `removeColumn()` (contributed by Maxim Samoylov)
 * Tests migrated to latest junit version, removed `MockVector` and `MockMatrix` classes (contributed by Maxim Samoylov)
 * New vector method `equals(vector, precision)`
 * New matrix method `equals(matrix, precision)`
 * New vector method `normalize(vectorAccumulator)`
 * New vector method `normalize()`
 * Added index range checks for `SparseVector`
 * Added index range checks for `Basic1DMatrix`, `CRSMatrix`, and `CCSMatrix`
 * Removed method `Vector.transform(int, VectorFunction)`
 * Removed methods `Matrix.transform(int, int, MatrixFunction)`

la4j-0.4.9 `Jan 2014`
 * Bug fix in `align()` method for big sparse matrices (reported by Michael Kapper)
 * Bug fix in `growup()` method for big sparse matrices (contributed by Phil Messenger)
 * Bug fix in `MatrixMarketStream`
 * New matrix method `select()` (contributed by Anveshi Charuvaka)
 * New vector method `select()`
 * Bug fix in `growup()` method for the case with positive overflow (contributed by Clement Skau)
 * New matrix predicate `Matrices.SQUARE_MATRIX` (contributed by Miron Aseev)
 * New matrix predicate `Matrices.INVERTIBLE_MATRIX` (contributed by Miron Aseev)
 * New vector method `norm(NormFunction)` that implements p-norm support (contributed by Miron Aseev)
 * New matrix predicate `PositiveDefiniteMatrix` (contributed by Miron Aseev)
 * Bug fix in `each`, `eachInRow`, `eachInColumn` methods of sparse matrices (reported by Leonid Ilyevsky)
 * New matrix methods: `foldColumns` and `foldRows` (contributed by Todd Brunhoff)
 * New matrix methods: `assignRow` and `assignColumn`
 * New matrix methods: `updateRow` and `updateColumn`
 * New matrix methods: `transformRow` and `transformColumn`

la4j-0.4.5 `Sep 2013`
 * New vector methods: `innerProduct()`, `outerProduct()` (contributed by Daniel Renshaw)
 * Bug fix in `Vector.subtract()` method (contributed by Ewald Grusk)
 * Bug fix in `Matrix.subtract()` method (contributed by Ewald Grusk)
 * New matrix method `rotate()` (contributed by Jakob Moellers)
 * New matrix method `shuffle()` (contributed by Jakob Moellers)
 * Bug fix in `Vector.density()` and `Matrix.density()` (contributed by Ewald Grusk)
 * Bug fix in `Matrix.determinant()` method (contributed by Yuriy Drozd)
 * Minor improvement of `SymmetricMatrixPredicate` (contributed by Ewald Grusk)
 * Bug fix in `EigenDecompositor` (reported by Ewald Grusk)
 * Bug fix in `CompressedVector.swap()` (reported by Ewald Grusk, contributed by Yuriy Drozd)
 * Typo fix in `IdentityMattixSource` (reported by Ewald Grusk, contributed by Yuriy Drozd)
 * Renamed `Matrix.product()` to `Matrix.diagonalProduct()` (contributed by Julia Kostyukova)
 * New matrix methods: `sum()` and `product()` (contributed by Julia Kostyukova)
 * New vector methods: `sum()` and `product()` (contributed by Julia Kostyukova)
 * Renamed `Matrix.kronecker()` to `Matrix.kroneckerProduct()` (contributed by Julia Kostyukova)
 * New matrix method `hadamardProduct()` (contributed by Julia Kostyukova)
 * Bug fix in `EigenDecompositor` (contributed by Maxim Samoylov)
 * Improved stability of `EigenDecompositor` (contributed by Maxim Samoylov)
 * New vector method `eachNonZero` (contributed by Maxim Samoylov)
 * New matrix method `power` (contributed by Jakob Moellers)
 * New matrix methods `eachInRow`, `eachInColumn`(contributed by Maxim Samoylov)
 * New matrix methods `eachNonZeroInRow`, `eachNonZeroInColumn`, `eachNonZero` (contributed by Maxim Samoylov)
 * New factory method `createBlockMatrix` (contributed by Maxim Samoylov)
 * New fast and stable algorithm for determinant calculation (contributed by Maxim Samoylov)
 * Improved stability of accumulators (contributed by Maxim Samoylov)
 * Bug fix in `Matrix.rank()` method (contributed by Ewald Grusk)
 * Bug fix in `SingularValueDecompositor` class (reported by Jonathan Edwards)
 * Fixed a typo in `MatrixInvertor` -> `MatrixInverter`
 * New function `Mod` (requested by Luc Trudeau)
 * Bug fix in `GaussianSolver`
 * Bug fix in `SquareRootSolver`
 * Bug fix in `JacobiSolver`
 * New matrix and vector methods `max()`, `min()`, `minInRow()`, `maxInColumn()` (contributed by Maxim Samoylov)
 * New linear solver: `ForwardBackSubstitutionSolver` (for square systems)
 * New linear solver: `LeastSquaresSolver` (least squares solver)
 * New all-things-in-one class `LinearAlgebra`
 * New matrix/vector method: `non()`, which is actually `!is()` delegate
 * New matrix to vector converters: `toRowVector()`, `toColumnVector()`
 * New vector to matrix converters: `toRowMatrix()`, `toColumnMatrix()`
 * New API for solving system of linear equations: `withSolver(SolverFactory)`
 * New API for decomposing: `withDecompositor(DecompositorFactory)`
 * New API for inverting: `withInverter(InverterFactory)`

la4j-0.4.0 `Jun 2013`
 * Up to 2x performance improvement of sparse entries (binary search power)
 * Performance improvement for matrix-by-matrix multiply algorithm (3x for dense, 11x for sparse)
 * New matrix method `rank()` (contributed by Evgenia Krivova)
 * New fast implementation of `Matrix.determinant()` method
 * New method `update()` (as compound operator replacement)
 * Matrices are unsafe by default (new corresponding methods `safe()` and `unsafe()`)
 * New method `slice()`
 * New matrix method `kronecker()` (contributed by Stefano Iannello)
 * Support map-reduce approach by method pair `transform()` and `fold()`
 * New matrices and vectors sources that handles IO streams
 * Support of building a constant matrix in factories (via `createConstantMatrix()`)
 * Matrices and vectors are immutable in terms of dimension
 * Sparse entities are self-clearing (no memory leaks)
 * Support vector-by-matrix multiplication
 * Bug fix in `MatrixDecFunction`
 * Bug fix in `MatrixMarketStream` (contributed by Alessio Placitelli)
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
