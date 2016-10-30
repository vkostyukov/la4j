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

Any project’s requirements need to be well thought out, balanced and clearly understood by all involved. In this report, we will address the project’s specific requirements and features, its use cases and the domain model. 

Requirement engineering is a methodology used on the definition of system requirements according to what a user needs. These requirements can be divided into different types and levels, and will be addressed later in this report.

A use case is a “sequence of transactions in a system whose task is to yield a measurable value to an individual actor of the system.” It describes what the system does from the user’s perspective. Finally, a domain model is a representation of real-situation conceptual classes.

 
### Purpose/Scope <a name="PurposeScope"></a>

The purpose of requirement elicitation is to gather as much data as possible about the requirements of project. This is usually accomplished by connecting directly with the stakeholders. Communication with stakeholders is crucial, and is usually done via interviews, questionnaires, meetings, etc. The team then needs to assert with the highest detail possible, their needs and scope.

It's very important to analyse the data collected during requirement engineering thoughtfully. Many errors can be traced back to this phase. If not prevented, these errors require high amounts of resources to be fixed, as there may be the need to backtrack a lot of work already done.

Many problems can arrive from requirement elicitation. Problems of scope occur when the limitations of the system are not defined well. It can also occur when the costumer specifies unnecessary technical and design details, resulting in confusion of the overall scope. Additionally, costumers/users may not be sure of what they actually need or they may not have the necessary knowledge of things such as the system abilities and/or the problem domain. Furthermore, there may be communication obstacles, information deemed as obvious not transmitted, or simply internal user conflicts. These problems fall in the understanding problems category. With time requirements change, creating problems of volatility.


### Description <a name="Description"></a>

There are different levels of requirements such as business, user, system and software. Distinct levels are useful because they communicate information about the system and, if well distinguish, they may prevent some problems during the requirements engineering process.

Business Requirements are the requests of the customer or organization who requires the system. This are high-level objectives. On the other hand, User Requirements represent the services that the system provides to the user and its operational constraints.
Furthermore, System Requirements are descriptions of the software meaning that gathers all the requirements from software and hardware components. Finally, Software Requirements are considered a derivation of the previous level of requirements, since they detail its requirements and allocate them to the software components.

Software requirements can also be divided in to two different types: functional and non-functional requirements. Functional requirements are the services/functions that the software must provide and how it should behave in certain situations and its response to inputs.  On the contrary, non-functional requirements can be view as quality requirements since these are the constraints of the functions and services of the system. They can include timing constraints, per example, a system should execute a certain function under a limited time, constraints on the development process (per example, the software should be developed in a required language). This type of requirements is usually implemented to the system not to individual system features.

Although understanding the different levels and types of requirements is easy, is not so clear as it may seem. Sometimes it’s hard to differentiate the levels because requirements are not independent from one another.

In the case of la4j, there aren’t any special requirements, except it should be a lightweight library, fully developed in Java language, non-parallel and have a good API. The owner of the project repository decides which features should be implemented. Anyone can open a pull request on Github and discuss it with the owner or other contributors by opening a new issue. This Github feature can also be used for users to expose bugs, errors they encounter, discuss new features and improve the code. If the owner approves your request (the code passed all the tests and the feature is useful for this library), the new feature is merged with the main branch.


## Specific Requirements and Features  <a name="RequirementsFeatures"></a>



| FEATURE             | DESCRIPTION                                                            
| ------------------- |:-----------------------------------------------------------------------
| Matrix Solver       | The user can solve matrixes using different solvers                    
| Matrix Iterator     | The user can iterate matrixes or vectors using different iterators     
| Matrix Decompositor | The user can decompose matrixes using different decomposers            
| Matrix Inversor     | The user can invert matrixes using different inverters                 



| REQUIREMENTS        | DESCRIPTION                                                   | TYPE             
| ------------------- |:--------------------------------------------------------------|-------:
| Coded in Java       | The software must be all written using Java language          |     NF    
| Lightweight         | The software has to be lightweight                            |     NF        
| API                 | The software needs to have a good API                         |     NF        
| Non-Parallel        | The software needs to be non-parallel                         |     NF 

F - Functional Requirement

NF - Non-functional Requirement

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



