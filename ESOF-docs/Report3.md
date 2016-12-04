#Report 3: Software Design

##Index
1. [Software Design, 4+1 Architectual View Model and Architectual Patterns](#Intro)
2. [Logic View](#LV)
3. [Development View](#DvV)
4. [Deployment View](#DpV)
5. [Process View](#PV)

##Software Design, 4+1 Architectual View Model and Architectual Patterns <a name="Intro"></a>
In this report, we will approach the software architecture of La4j and its view model and patterns. 

Software Architecture is the organization of a system and the designing of the general structure. It is the critical link between design and requirements engineering, since it involves all the major decisions about how the software will be organized and the relationship between all the different components.
There are diverse levels of design, such as, High-level, which is the partition of the system into components, Detailed design, which divides the components into classes and the design of algorithms and data structures.

> Software Architecture is important because it affects the performance, robustness, distributability and maintainability of a system.
> (Bosch, 2000)

Since all the complexity of the current software, it’s impossible to represent all the information in a single model. For that reason, the view models use a 4 + 1 Architectural View Model, designed by Philippe Kruchten, that decomposes the system into modules that show, among others, how the run-time processes interact, how a system is decomposed into models, etc.
The follow diagram represents the 4 + 1 view model of software architecture. 

![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/4plus1model.png)

In this report we will adress the following view models:
* **Logic View** - Demonstrates the main abstractions in the system as objects or object classes 
* **Implementation View** - Represents the decomposition of the software for development
* **Deployment view** - Show the system hardware and how the components are distributed in the system
* **Process View** - Demonstrates the run-time and how the system is composed of interacting processes

On the other hand, architectural patterns represent the idea of sharing and reusing knowledge and solutions. They are stylized, abstract descriptions that present good architectural design practices. We believe this project doesn't follow any known pattern but that it would benefit to follow Abstract Factory. With the Abstract Factory pattern, all the main classes like Vector and Matrix would be the concrete classes and all the operations would be represented by abstract classes.

##Logic View <a name="LV"></a>
The first view model being developed is the logic view. This model shows the key abstractions in the system and should enable the relation between the system requirements to entities in this view. The logic view can also be thought of a UML package diagram.

La4j has two main packages contained in the source file of project: main and test. The main package contains all the source code of the project, on the other hand, the test package all the tests implemented. The next diagram represents the logic view model of the project, related to the main package (source code).

![Logic View Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/logicview.png)

##Development View <a name="DvV"></a>
Also known as Implementation View, the Development View shows how the software is decomposed for development on a programmer's perspective. The component diagram is used to describe this decomposition by using system components.

![Development View Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/development_diagram.png)

Since this project is based on an algebra library, the key component for this diagram is the Operation. It's in this component where all the matrix operations will be applied to the input given by the user.
As for the components Matrix Operations and Result, the first one's objective is to allow the user to choose one of the many available operations and the latter is responsible for the representation of the result matrix.

##Deployment  View <a name="DpV"></a>
The Deployment View details the hardware that the system needs to run as intended. This view includes processing nodes (such as PCs and servers), network connections and storage facilities. For each process node is included the software components and the dependencies between them present.

![Deployment View Diagram](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/deployment_view_diagram.png)

In the case of la4j, as it is a library that enables linear algebra in java, it doesn't need multiple processing nodes besides the machine where the program that is using the library is running. In the user's computer is present all the source code files with the linear algebra solvers, manifested in the Matrix Operation component.

##Process View <a name="PV"></a>
To provide a basis for understanding the process organization of the system, an architectural view called the process view is used in the Analysis & Design discipline. There is only one process view of the system, which illustrates the process decomposition of the system, including the mapping of classes and subsystems on to processes and threads.

DEFINITION - The process view deals with the dynamic aspects of the system, explains the system
processes and how they communicate, and focuses on the runtime behavior of the system.
The process view addresses concurrency, distribution, integrators, performance,and 
scalability, etc. UML diagrams to represent process view include the activity diagram.

Activity diagram is another important diagram in UML to describe dynamic aspects of the system. Activity diagram is basically a flow chart to represent the flow form one activity to another activity. The activity can be described as an operation of the system. So the control flow is drawn from one operation to another. This flow can be sequential, branched or concurrent. Activity diagrams deals with all type of flow control by using different elements like fork, join etc.



![alt](https://github.com/nunomiguel1995/ESOF-la4j/blob/master/ESOF-docs/res/process_view.png)

Since the flow of path in the activity diagram is from beginning to end and if the user wants to use the pre-defined algebraic function it is possible to for him to use them. Also if it doesnot calls a pre-defined algebraic function there is another part of code which will execute the requirements. Hence in all parallel processing is not required, so it would not benefit much to this project.



##Contributors
* [Sara Santos](https://github.com/sarasantos96)
* [Nuno Castro](https://github.com/nunomiguel1995)
* [Daniel Garrido](https://github.com/dalugoga)
* [Yuvak Patel](https://github.com/scorpio9847)
