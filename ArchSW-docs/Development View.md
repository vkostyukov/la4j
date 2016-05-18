# Development View

![dev](uml/DevelopmentView.png)

The development view is destined to programmers and software managers. In our case, it is represented by a package diagram that illustrates how the library's code is organized and how its packages relate with each other.

The main package - org.la4j - depends on the packages vector and matrix. Both packages have realizations as dense or sparse - matrices and vectors are sparse if most elements are zero, and dense otherwise. The packages which depend on vector and matrix are:

*Functor: a package that implements functions that run in matrices and vectors
*Linear: a package created to solve equation systems
*Iterator: a package that allows you to iterate over the elements of a matrix or a vector
*Operation: a package that allows you to make matrix/matrix, matrix/vector or vector/vector operations

There are also two packages which depend only on the matrix package:

*Inversion: a package that allows you to invert matrices 
*Decomposition: a package that decomposes a matrix with factorization
