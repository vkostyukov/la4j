#Report 1: Software Processes

##Project description

  La4j stands for Linear Algebra for Java and it is an open source Java library that offers linear algebra primitives, such as matrices and vectors, and algorithms. It started as a student project, designed to be a lightweight and simple tool for Java developers and later grew to be one of the most popular Java packages for its purpose.
  In the early development, la4j only contained simple algebra like solving linear systems, simple matrices and vectors operations. Today it expanded to more complex methods, also containing algorithms for instance Cholesky Decomposition, Gauss Jordan inverter and others.

##Development Process

###Description

  A software development process is a structure imposed on the development of a software product. There are several models for such processes, each describing approaches to a variety of tasks or activities that take place during the process.
	In order to understand the processes used in la4j we contacted some of the developers, and along with the information on Github page we concluded that the process being used is Continuous Deployment.
  In continuous deployment the code is release to production as soon as it’s ready. This process is also seen as an extension of continuous integration, where the development work is  merged with a Master/Trunk/Mainline branch constantly so that it can be tested. The goal is to test the code regularly to catch issues early. Continuous deployment differs from continuous integration because all the tests are done prior to merging to the master branch.
  In both processes all the tests are automated so it requires a unit test framework, usually a build server so the developers can continue working while tests are being performed.  La4j uses Travis CI, a distributed continuous integration service hosted at Github, which builds and performs JUnit tests located in /src/test from the repository root directory.


###Opinions, Critics and Alternatives 

  Overall, continuous deployment is a good process to implement to the development of la4j.  Since the different functionalities of the library are mostly independent from each other, this process allows that distinct contributors to work on separated features, leading to a rapid development of the project. 
	Using a build server to perform all the tests can be beneficial, but also has its down sides. Automated testing the code allows the development process to continue while making sure that the code being added to the main branch is robust. On the other side, delegate testing the code can also lead to some “red flags”, like some tests that weren't created by the build server, not being noticed.
	An alternative for this software process would be Waterfall. This model is a sequential design process with several phases: requirements definition, system and software design, implementing and unit testing, integration and system testing and operation and maintenance. This process would fit nicely on this project, since it has its requirement well defined and there aren't great changes on its features. Nonetheless, it wouldn't be as efficient as using the current software process (continuous deployment) because there isn't a well defined initial plan, but different features such as algebric operations that are being added while the project develops.

##Contributors
	* [Sara Santos](https://github.com/sarasantos96)
	* [Nuno Castro](https://github.com/nunomiguel1995)
	* [Daniel Garrido](https://github.com/dalugoga)
	* [Yuvak Patel](https://github.com/scorpio9847)
