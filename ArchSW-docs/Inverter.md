## Inverter

The GaussJordanInverter class holds the matrix we want to solve as a variable, and stores it in a constructor. It also implements the function inverse which verifies if the number of rows and columns is the same, and if so, checks if the matrix is invertible applying the Gaussian resolution. The class also possesses a function self which returns the matrix stored in it.

The NoPivotGaussInverter class has the same structure as the GaussJordanInverter class, but the function inverter implements Gaussian elimination without pivoting (Naive Gauss Elimination).

The classes are implemented through MatrixInverter interface which forces the solvers to implement, at least, the functions self and inverse, making sure that any inverting method implementation is coherent with the rest and interchangeable.