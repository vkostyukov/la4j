#Report 4: Verification and Validation

##Index
1. [Introduction](#Intro)
2. [Software Testability and Reviews](#Test)
3. [Test Statistics and Analytics](#Stats)
4. [Project Bugs](#Bug)

##Introduction <a name="Intro"></a>
In this report, we will analyse the La4j state of verification and validation. These are the processes related to checking if the software meet it's specification and delivers the intended functionalities. Basically we will look at La4j with concern to software testing.

In the first section of the report we will discuss the  factors like controllability, observability, isolateability, separation of concern, understandability and heterogeneity that can affect the software testability and also their dominating degree on the software system. Nextly we have mentioned some test statistics of this project by some number of tests, and other aspects. And at the end we have talked about the project bug.

Verification and validation are not hte same thing. Validation aims that the software meets its specified functional and non-functional requirements. On the other hand, the verification purpose is to guarantee that the software meets the customer’s expectations. 
Validation: Are we building the right product?
Verification: Are we building the product right?


##Software Testability and Reviews <a name="Test"></a>
Software Testability is the probability of exposing a software fault through testing, which means that in a software with high testability it is easier to find errors/bugs by running tests. In other to review the La4j testability we are going to analyse the further points.

**Controllability** is the ability to affect the software behaviour, replicating that behaviour. It’s the degree to which is possible to control the state of the component being tested. On the other hand, **observability** is the capability of observing software behaviour, such as intermediate and final test results. These two points are important, because low controllability and/or low observability imply a low software testability. 
We concluded that la4j has a good controllability, since it is easy and possible to control by instantiating any type of component and applying an operation to recreate a certain behaviour. On the other hand, its observability is not so good, for it is only possible to observe the final result, not any intermediate state

Furthermore, **isolateability** is the capacity of a component under test can be tested in isolation.La4j has a good isolateability, since it’s possible to test every functionality independently, per example, for testing matrix operation we can just instantiate new matrix and apply any operation implemented. Every algebraic operation implemented is independent from others, which makes testing a component in isolation possible and very simple. 

**Separation of concerns** is a design principle that aims for separating a computer program into distinct sections, so that a component under test has a unique and well defined responsibility. Each section addresses a different concern. This is a value principle for software engineering, since a good separation of concerns simplifies the development and maintenance of the software, and allows the individual sections to be reused, as well as developed or updated independently, without having to know the details and make corresponding changes of other sections. In terms of this point, the project has a good design. All its functionalities are divided according to their use and type, so that each section addresses a different concern. Matrix and vector operations are separated and, inside each sort, each type of operation it’s also divided and implemented mostly independently, so it makes it easy to change, add or correct any section, without affecting others. Also, we notice by checking the issues section of the GitHub, that a good design and a good separation is important for the project owner. 

**Understandability** is how well each component is documented or self-explaining, this is how difficult it is to understand the code, how complex are the control structures used and if the variables have meaningful names according to their use. This will affect other levels of software quality, such as maintainability, since a good organized and easy-to-read code software will be easier to maintain and to understand by a team. 
We admit that this project lacks in understandability, even though that a good API is a requirement. Analysing the code, we notice that it is difficult to understand the heritage between classes and how you can apply certain algebraic operations. Per example, it was a little complicated just for us to understand how it is possible two create a new matrix and invert it. 

**Heterogeneity** is the extent in which diverse test methods and tools are used in parallel. Heterogeneous computing refers to systems that use more than one processor or cores, in other to gain performance or to specialize the processing to handle certain tasks. La4j doesn’t have any case of heterogeneity, since all its development is done in a non-parallel way, the testing follow the same principle.


##Test Statistics and Analytics <a name="Stats"></a>
To test this project we used various testing tools. Some of these tools were JUnit, Eclemma and Codacy.

Firstly, we used JUnit to run all the 799 tests already present in la4j, all of which passed when we ran the project. We concluded that it has a considerable ammount of tests given its size and that every component from the project is being tested. However, some of these tests depend on functions created solely for test purposes and because of that makes them harder to understand.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_junit.PNG)

Secondly, we used Eclemma for test coverage and concluded that they cover aproximately 74% of the code. We believe this coverage isn't enough as it should cover around 80% of the code to make it more robust and less error prone. The solution for this would be to create more tests to cover every possibility in the if clauses, for example.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_eclemma.PNG)

Finally, we used Codacy for test statistics and analytics. From it we concluded that this project has a very good performance, security and compatibility aswell as it doesn't have any unused part of the code. These are excellent statistics since it tells us that this project is very robust and not likely to have many bugs. However, 41% of all the code is error prone, which means that that code is most likely to fail or cause errors.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_codacy.PNG)

##Project Bug <a name="Bug"></a>
To accomplish this part of the report, we first searched the issues page of the project on Github, where we found two identified bugs. One of the bugs was already under work by the la4j team. The other, as said by the la4j team in the comments, was going to need a rework from scratch to fix. Finding new unregistered bugs would be very hard and time consuming as we would have to run every function of the library and compare results. In the end, we turned to Codacy to find some error prone code that could generate bugs in the future and we found seven. After modifying the code were those errors appeared, we ran the Junits tests and all was as before. After checking Codacy again, we noticed a 5% increase in the error prone meter to 52%, as seen below.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/afterfix_codacy_new.PNG)
