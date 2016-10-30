#Report 2: Requirements elicitation

##Index
1. [Requirements elicitation](#Requirements)
  1. [Introduction](#Introduction)
  2. [Purpose/Scope](#PurposeScope)
  3. [Description](#Description)
2. [Specific Requirements and Features](#RequirementsFeatures)
3. [Use Cases](#UseCases)
4. [Domain Model](#DomainModel)

##Requirements elicitation <a name="Requirements"></a>

### Introduction <a name="Introduction"></a>
   Any project’s requirements need to be well thought out, balanced and clearly understood by all involved. In this work we will address some issues as requirements engineering, use cases and domain model. Firstly it is necessary to understand the basic needs of users and customers. For this we use reqirements engineering in order to understand the issues of the customer. In the project, these are going to be addressed as functional and nonfunctional requirements of the the product.

   For better understanding of these issues we will use requirement elecitation, which is the most important part of the project and which leads to more development of the project.We will have some requirements as, past invalid response of a defined function as functional requirement and reliability,compability, usability , maintainability ,portability, etc as nonfunctional requirements. Thus having a target in defining and improvising the product with proper development strategy.
 
### Purpose/Scope <a name="PurposeScope"></a>

### Description <a name="Description"></a>

## Specific Requirements and Features  <a name="RequirementsFeatures"></a>

## Use Cases <a name="UseCases"></a>
Due to this project structural complexity, we chose to define the 4 main use cases, these being:

 > 1. Matrix Solver
 
 > 2. Matrix Iterator
 
 > 3. Matrix Inversor
 
 > 4. Matrix Decompositor
 
**Use Case number 1:**

 > > **Name:** Matrix Solver
  
 > > **Actors:** la4j User
  
 > > **Goal Description:** The user should be able to solve matrixes using different types of solvers:
 
 > > > * Sweep Solver
 
 > > > * Jacobi Solver
 
 > > > * Least Square Solver
 
 > > > * Gaussian Solver
 
 > > > * Least Norm Solver
 
 > > > * Forward Back Substitution Solver
 
 > > > * Seidel Solver
 
 > > > * Square Root Solver
 
 > > **Reference to requirements:**
  
 > > **Pre-conditions:** The user should provide the program with a valid matrix.
  
 > > **Description:** The user provides the program with a matrix. He then has to choose the type of solver he wants to use in his matrix. The program will then check if that solver is valid for the given matrix. The program will solve the matrix using the solver. The program returns the solved matrix for the user.
  
 > > **Post-conditions:** The user will have a solved matrix.
  
 > > **Variations:** It does not have any variations.
  
 > > **Exceptions:** The program will return the same matrix if it does not have any applicable solver.
  
 > > **Diagram:** 
 
 ![Matrix Solver Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/MatrixSolver.png)
 
 **Use Case number 2:**

 > > **Name:** Matrix Iterator
  
 > > **Actors:** la4j User
  
 > > **Goal Description:** The user should be able to iterate matrixes and vectors using different types of iterators:
 
 > > > * Column Major Matrix Iterator
 
 > > > * Row Major Matrix Iterator
 
 > > > * Cursor to Column Major Matrix Iterator
 
 > > > * Cursor to Row Major Matrix Iterator
 
 > > > * Cursor to Vector Iterator
 
 > > **Reference to requirements:**
  
 > > **Pre-conditions:** The user should provide the program with a valid matrix or vector.
  
 > > **Description:** The user provides the program with a matrix or vector. He then has to choose the type of iterator he wants to use in his matrix or vector. The program will iterate the matrix or vector using the chosen iterator.
 
 > > **Post-conditions:** It does not have any post-conditions.
  
 > > **Variations:** It does not have any variations.
  
 > > **Exceptions:** It does not have any exceptions.
  
 > > **Diagram:** 
 
 ![Matrix Iterator Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/MatrixIterator.png)

**Use Case number 3:**

 > > **Name:** Matrix Inversor
  
 > > **Actors:** la4j User
  
 > > **Goal Description:** The user should be able to invert the matrix using the following inverters:
 
 > > > * Gauss Jordan Inverter
 
 > > > * No Pivot Gauss Jordan Inverter
 
 > > **Reference to requirements:**
  
 > > **Pre-conditions:** The user should provide the program with a valid matrix.
  
 > > **Description:** The user provides the program with a matrix. He then has to choose between the two available inverters. The program then inverts the matrix. The program returns the inverted matrix. 
  
 > > **Post-conditions:** The user will have an inversed matrix.
  
 > > **Variations:** It does not have any variations.
  
 > > **Exceptions:** It does not have any exceptions
  
 > > **Diagram:** 
 
 ![Matrix Inverter Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/MatrixInverter.png)
 
 **Use Case number 4:**

 > > **Name:** Matrix Decompositor
  
 > > **Actors:** la4j User
  
 > > **Goal Description:** The user should be able to invert the matrix using the following decompositors:
 
 > > > * Singular Value Decompositor
 
 > > > * Eigen Decompositor
 
 > > > * Cholesky Decompositor
 
 > > > * Raw LU Decompositor
 
 > > > * LU Decompositor
 
 > > > * Raw QR Decompositor
 
 > > > * QR Decompositor
 
 > > **Reference to requirements:**
  
 > > **Pre-conditions:** The user should provide the program with a valid matrix.
  
 > > **Description:** The user provides the program with a matrix. He then has to choose between the available decompositors. The program then decomposes the matrix. The program returns the decomposed matrix. 
  
 > > **Post-conditions:** The user will have a decomposed matrix.
  
 > > **Variations:** It does not have any variations.
  
 > > **Exceptions:** It does not have any exceptions
  
 > > **Diagram:** 
 
 ![Matrix Decompositor Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/MatrixDecompositor.png)
 
## Domain Model <a name="DomainModel"></a>
For the sames reasons stated before, we chose to define only the domain model with the 4 main functions of the library.

 ![Matrix Solver Domain Model Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/linear.png)
 
 ![Matrix Inverter Domain Model Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/inversor.png)
 
 ![Matrix Decompositor Domain Model Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/decompositor.png)
 
 ![Matrix Iterator Domain Model Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/iterator.png)



