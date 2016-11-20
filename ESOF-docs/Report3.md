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

On the other hand, architectural patterns represent the idea of sharing and reusing knowledge and solutions. They are stylized, abstract descriptions that present good architectural design practices.

##Logic View <a name="LV"></a>

##Development View <a name="DvV"></a>

##Deployment  View <a name="DpV"></a>

##Process View <a name="PV"></a>
To provide a basis for understanding the process organization of the system, an architectural view called the process view is used in the Analysis & Design discipline. There is only one process view of the system, which illustrates the process decomposition of the system, including the mapping of classes and subsystems on to processes and threads.

DEFINITION - The process view deals with the dynamic aspects of the system, explains the system
processes and how they communicate, and focuses on the runtime behavior of the system.
The process view addresses concurrency, distribution, integrators, performance,and 
scalability, etc. UML diagrams to represent process view include the activity diagram.

Activity diagram is another important diagram in UML to describe dynamic aspects of the system. Activity diagram is basically a flow chart to represent the flow form one activity to another activity. The activity can be described as an operation of the system. So the control flow is drawn from one operation to another. This flow can be sequential, branched or concurrent. Activity diagrams deals with all type of flow control by using different elements like fork, join etc.



##Contributors
* [Sara Santos](https://github.com/sarasantos96)
* [Nuno Castro](https://github.com/nunomiguel1995)
* [Daniel Garrido](https://github.com/dalugoga)
* [Yuvak Patel](https://github.com/scorpio9847)
