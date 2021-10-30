<br />
<p align="center">
  <h1 align="center">Asteroids</h1>

  <p align="center">
  A re-interpration of the famous Asteroids game in Java for the Advanced Object
  Oriented Programming Course at Rijksuniversiteit Groningen
  </p>
</p>

## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Design Description](#design-description)
  * [ExamplePackageA](#examplepackagea)
  * [ExamplePackageB](#examplepackageb)
* [Evaluation](#evaluation)
* [Teamwork](#teamwork)
* [Extras](#extras)

## About The Project

<!-- Add short description about the project here -->
This project has the all-time famous Asteroids game as its core. It is developed
following OOP concepts using Java. Along with the classic game, it was enriched
with a networking functionality that lets the users enjoy the game with friends.

### Built With

* [Maven](https://maven.apache.org/)

## Getting Started

To get a local copy up and running follow these simple steps:

1. Clone the resources of the game on your computer from Github using:
   * HTTPS: https://github.com/rug-advoop/2021_Team_093.git
   * SSH: git@github.com:rug-advoop/2021_Team_093.git
   * Github CLI: gh repo clone rug-advoop/2021_Team_093
1. Follow the steps in [Installation](#installation) 

### Prerequisites

The latest versions of the following:

* Java
* Maven 

### Installation

1. Navigate to the Asteroids folder
2. Clean and build the project using:
```sh
mvn install
```
3. Run the `Main` method of Asteroids using:
```sh
mvn exec:java -Dexec.mainClass="nl.rug.aoop.asteroids.Asteroids"
```
4. Alternatively you can run the `main` method in `Asteroids.java` using an IDE of your choice (e.g. IntelliJ)

Should you want to run this program standalone, you can create a JAR file with the following maven command:

```sh
mvn clean package
```
The JAR file will appear in the `/target` directory.

## Design Description

<!-- 
Describe your program's structure (classes and packages) in detail, addressing all but the most trivial features, and provide ample reasoning for why you chose a certain structure, or why you implemented something a certain way. What design patterns did you use? Describe how and where they've been applied. And finally, how does your game handle networking? Give a description of the protocol or messages that the clients use to communicate with servers. Including a diagram here can help! 
-->
In-Game Controls: We added Escape button mapping to activate a pause menu during the game.
Rest of the controls are default. 


The project structure follows the Model-View-Controller pattern. This means that
the resources were divided in these main packages (Model, View, Control)
supported by a gameobserver, a network and a util package. Each of these
packages addresses a main concern: Model handles the the states of the game and
keeps track of changes; the view handles the UI of the program; the control
handles the interaction between the user and UI the; the gameobserver simply
implements listener support for the game; the network package contains
everything needed for the game to support multiplayer features; the util package
contains resources that are needed across all the packages.

### View

The view package contains all the GUI classes of the program. Classes are divided
into some sub-packages depending on their characteristics. For this program,
Java Swing was used. This means there is a frame and many panels that are added
to it. Some panels are supported by model classes.


### Controller

Following the MVC design we have a Controller package which is used by the user
to interact with the game. Hence, here we can find subpackages for all the
(clickable) buttons and the menu actions. These actions generally call the
ViewController which is the main class that is concerned with switching all
panels in the frame according to user input. In particular, this class was
implemented using a Command pattern. All the controls are implemented in terms
of a Control abstract class which enforces the programmer to implement a display
function that will be called by the View Controller. Besides making the code
much cleaner and understandable, this allows further controls to be added with
ease and work right away with the View Controller.

Furthermore, in this package we have the keylistener implementation to make the
view responsive to the keyboard and a game updater, tasked with running the main
game loop and update each step of the game. We also added a secondary keylistener
adapted specifying a different set of recorded inputs (for a different activity mode)

### Model

The model package collects the models of the game and the objects in the game.
The game package contains a game class which is concerned with starting, pausing
and stopping the game, while the helper class gameResources manages the game
objects. 

Game objects are defined in the gameobjects package and they define all the
characteristics of the objects such as position, velocity etc. Game objects are
created into a game using a factory pattern design. Its implementation is found
in the obj_factory package and comprises some interfaces and a class which
spawns and updates game objects.

Finally there is the Multiplayer game helper class which uses resources from the
networking package to update the model in multiplayer games.

### Networking 

Networking

Networking :
The classes responsible for networking are:\
User , DeltaProcessor , HostingServer , ClientConnection, IO , GameplayDeltas, ConfigData

User.class - client side device used to handle incoming and outgoing packets by delegating 
tasks to the IOProtocol inteface implementing class that focuses on actually packaging and sending. 
User.class runs on two threads: - the class object itself acts as producer for the host, 
by looping with timed intervals and sending GameplayDeltas; 
- 2nd one is the Consumer inner-class which reads incoming packets using the same technique.
User.class comunicates with the DeltaProcessor.\

DeltaProcessor.class - is used by both client and server side and it’s role is to update the gameplay/ game state 
based on the incoming packet data.  Any transmitted object which wraps essential data
must also implement the DeltasData interface which includes the injectDeltas method required a DeltaDevice interface
inplementing class, and by setting this constraint of contract2contact communication we establish protocol boundaries
of what changes can  DeltasData apply with DeltasDevice on the app (game). This also allows further abstraction for 
expanding the applet eith a dispatcher coordinating which module would be changed and each module having it’s own 
version of a “DeltaDevice” and the injectChanges method here would first call by some reference for 
the apropiate “DeltaDevice” .\

ClientConnection.class - similar in behaviour to User.class,  but adapted to the host's demands;

GamePlayDeltas.class & ConfigData.class both are used to relay different types of states.

### Util

The Util package contains classes to implement randomizer suport, coordinates
computation and reflection (used for menu buttons). Furthermore, It contains a
database package which implements persistence support to keep track of the
scores.

#### Database
The database package is fairly essential. It has a class to represent a "Score"
entity to be stored and a class which implements a manager to makes it easy to
manipulate the database. The Database used for this project is ObjectDB. The
choice was driven mainly by the really basic needs (no relations etc) of the
data representation and the ease of use of the API. 

The manager is implemented using the singleton pattern so that only one instance
is ever created and used. This was a natural solution for such a class since
the game only interacts with a unique database. By implementing synchronized
methods for querying and posting, racing conditions are avoided as well.


-->

## Evaluation

Discuss the stability of your implementation. What works well? Are there any bugs? Is everything tested properly? Are there still features that have not been implemented? Also, if you had the time, what improvements would you make to your implementation? Are there things which you would have done completely differently?
<!-- Write this section yourself -->
Expected length: ~300-500 words

## Teamwork

What did each team member contribute to the assignment? Not just in terms of code, but also more abstractly, such as, "Tom upgraded the game model to support multiple ships.", or "Jerry designed the protocol that clients use for communicating with the server."
<!-- Write this section yourself -->
Expected length: ~150 words.

## Extras

If you implemented any extras, you can list/mention them here.
<!-- Write this section yourself -->



<!-- Below you can find some sections that you would normally put in a README, but we decided to leave out (either because it is not very relevant, or because it is covered by one of the added sections) -->

<!-- ## Usage -->
<!-- Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources. -->

<!-- ## Roadmap -->
<!-- Use this space to show your plans for future additions -->

<!-- ## Contributing -->
<!-- You can use this section to indicate how people can contribute to the project -->

<!-- ## License -->
<!-- You can add here whether the project is distributed under any license -->


<!-- ## Contact -->
<!-- If you want to provide some contact details, this is the place to do it -->

<!-- ## Acknowledgements  -->
