#Report 4: Verification and Validation

##Index
1. [Introduction](#Intro)
2. [Software Testability and Reviews](#Test)
3. [Test Statistics and Analytics](#Stats)
4. [Project Bugs](#Bug)

##Introduction <a name="Intro"></a>

##Software Testability and Reviews <a name="Test"></a>

##Test Statistics and Analytics <a name="Stats"></a>
To test this project we used various testing tools. Some of these tools were JUnit, Eclemma and Codacy.

Firstly, we used JUnit to run all the 799 tests already present in la4j, all of which passed when we ran the project. We concluded that it has a considerable ammount of tests given its size.
![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_junit.PNG)

Secondly, we used Eclemma for test coverage which and concluded that they cover aproximately 74% of the code. We believe this coverage isn't enough as it should cover around 80% of the code to make it more robust and less error prone.
![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_eclemma.PNG)

Finally, we used Codacy for test statistics and analytics. From it we concluded that this project has a very good performance, secutiry and compatibility aswell as it doesn't have any unused part of the code. These are excellent statistics since it tells us that this project is very robust and not likely to have many bugs. However, 41% of all the code is error prone, which means that that code is most likely to fail or cause errors.

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/test_codacy.PNG)

##Project Bug <a name="Bug"></a>
