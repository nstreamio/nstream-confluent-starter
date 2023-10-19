# nstream-confluent-starter

A baseline Nstream application that processes data hosted in Confluent Cloud.

We highly recommend following our [walkthrough](https://www.nstream.io/docs/backend/tutorial/) as you explore this codebase.
Although the walkthrough uses plain Kafka, the diff here is a simple matter of configuration as follows:



## Component Overview

There are two backend components to this repository:

- An Nstream toolkit-empowered Swim server that consumes from Confluent Cloud topics and processes responses in Web Agents with minimal boilerplate (package [`nstream.starter`](/src/main/java/nstream/starter) in the Java code)
- A means to populate the former with reasonably frequent messages (package [`nstream.starter.sim`](/src/main/java/nstream/starter/sim) in the Java code)

There is also a minimal, general-purpose frontend component under [`src/main/resources/index.html`](/src/main/resources/index.html) that is available in a browser window under `localhost:9001` while (at minimum) the first backened component runs.

## Prerequisites

- Java Development Kit (JDK) 11+
   - See [`build.gradle`](/build.gradle) for application-specific Java dependencies

## Run Instructions

1. Run the Nstream server (working directory: this one)

   **\*nix Environment:**
   ```
   ./gradlew run
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat run 
   ```
2. Run the broker populator (working directory: this one)

   **\*nix Environment:**
   ```
   ./gradlew runSim
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat runSim
   ```
   
### Variation: Avro Schema Registry

