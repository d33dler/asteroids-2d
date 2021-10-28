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
mvn exec:java
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

The project structure follows the Model-View-Controller pattern. This means that
the resources were divided in these main packages (Model, View, Control)
supported by a gameobserver, a network and a util package. Each of these
packages addresses a main concern: Model handles the the states of the game and
keeps track of changes; the view handles the UI of the program; the control
handles the interaction between the user and UI the; 

<!-- Write this section yourself -->
Expected length: as much as you need to explain the above. This will likely be the longest section (it is also the most important one).

A good way to split this section would be by packages (e.g. model/view/controller). Then discuss the (functionality of) relevant components in each package and how they interact with each other. Make sure to treat every package/module.

Two questions we would like you to answer somewhere in this section is the following:

- What does the network communication look like between the host and the players?
- How did you deal with the different packet types and made sure it would be easy to add support for new types of packets?


### ExamplePackageA

### ExamplePackageB

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
