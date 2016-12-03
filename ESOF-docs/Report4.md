#Report 4: Verification and Validation

##Index
1. [Introduction](#Intro)
2. [Software Testability and Reviews](#Test)
3. [Test Statistics and Analytics](#Stats)
4. [Project Bugs](#Bug)

##Introduction <a name="Intro"></a>

##Software Testability and Reviews <a name="Test"></a>
Software Testability is the probability of exposing a software fault through testing, which means that in a software with high testability it is easier to find errors/bugs by running tests. In other to review the La4j testability we are going to analyse the further points.

**Controllability** is the ability to affect the software behaviour, replicating that behaviour. It’s the degree to which is possible to control the state of the component being tested. On the other hand, **observability** is the capability of observing software behaviour, such as intermediate and final test results. These two points are important, because low controllability and/or low observability imply a low software testability.
Furthermore, **isolateability** is the capacity of a component under test can be tested in isolation.

**Separation of concerns** is a design principle that aims for separating a computer program into distinct sections, so that a component under test has a unique and well defined responsibility. Each section addresses a different concern. This is a value principle for software engineering, since a good separation of concerns simplifies the development and maintenance of the software, and allows the individual sections to be reused, as well as developed or updated independently, without having to know the details and make corresponding changes of other sections. 

**Understandability** is how well which component is documented or self-explaining, this is how difficult it is to understand the code, how complex are the control structures used and if the variables have meaningful names according to their use. This will affect other levels of software quality, such as maintainability, since a good organized and easy-to-read code software will be easier to maintain and to understand by a team.

**Heterogeneity** is the extent in which diverse test methods and tools are used in parallel. Heterogeneous computing refers to systems that use more than one processor or cores, in other to gain performance or to specialize the processing to handle certain tasks.


##Test Statistics and Analytics <a name="Stats"></a>
To test this project we used various testing tools. Some of these tools were JUnit, Eclemma and Codacy.

Firstly, we used JUnit to run all the 799 tests already present in la4j, all of which passed when we ran the project. We concluded that it has a considerable ammount of tests given its size.
![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_junit.PNG)

Secondly, we used Eclemma for test coverage which and concluded that they cover aproximately 74% of the code. We believe this coverage isn't enough as it should cover around 80% of the code to make it more robust and less error prone.
![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_eclemma.PNG)

Finally, we used Codacy for test statistics and analytics. From it we concluded that this project has a very good performance, secutiry and compatibility aswell as it doesn't have any unused part of the code. These are excellent statistics since it tells us that this project is very robust and not likely to have many bugs. However, 41% of all the code is error prone, which means that that code is most likely to fail or cause errors.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_codacy.PNG)

##Project Bug <a name="Bug"></a>
