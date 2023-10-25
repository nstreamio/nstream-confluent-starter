# nstream-confluent-starter

A baseline Nstream application that processes data hosted in Confluent Cloud.

We highly recommend following our [Kafka starter walkthrough](https://www.nstream.io/docs/backend/tutorial/) as you explore this codebase.
Although the walkthrough uses plain Kafka, the only difference is that we use a [`ConfluentIngestingPatch`](https://www.nstream.io/docs/backend/confluent-ingress/) instead of `KafkaIngestingPatch`.

## Component Overview

There are two backend components to this repository:

- An Nstream toolkit-empowered Swim server that consumes from Confluent Cloud topics and processes responses in Web Agents with minimal boilerplate (package [`nstream.starter`](/src/main/java/nstream/starter) in the Java code)
- A means to populate the former with reasonably frequent messages (package [`nstream.starter.sim`](/src/main/java/nstream/starter/sim) in the Java code)

There is also a minimal, general-purpose frontend component under [`index.html`](/index.html) that is available in a browser window under `localhost:9001` while (at minimum) the first backened component runs.

## Prerequisites

- Java Development Kit (JDK) 11+
   - See [`build.gradle`](/build.gradle) for application-specific Java dependencies
- A Confluent Cloud account that contains all the following:
   - A _schemaless_ Kafka topic hosted within a network-reachable cluster
   - A topic-corresponding API key
   - An API secret corresponding to the API key

## Run Instructions

0. Fix the configuration files
   - Rename [`consumer-fixme.properties`](/src/main/resources/consumer-fixme.properties) to `consumer.properties` and fix any FIXMEs
   - Rename [`producer-fixme.properties`](/src/main/resources/producer-fixme.properties) to `producer.properties` and fix any FIXMEs

1. Run the Nstream server

   **\*nix Environment:**
   ```
   ./gradlew run
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat run 
   ```
2. Run the broker populator

   **\*nix Environment:**
   ```
   ./gradlew runSim
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat runSim
   ```
   
## Variation: Using Schema Registry

In addition to achieving full parity with the aforementioned Kafka starter application, this codebase also exercises Confluent Cloud's Schema Registry.
Working with this requires a few additional changes.

### Additional Prerequisites

- A Confluent Cloud account that contains all the following:
   - A Kafka topic hosted within a network-reachable cluster configured with the following Avro schema for its values (and no schema on its keys):
      ```
      {
        "fields": [
          {"name": "id", "type": "int"},
          {"name": "routeId", "type": "int"},
          {
            "name": "dir",
            "type": {
              "name": "Dir", "symbols": ["INBOUND","OUTBOUND"], "type": "enum"
            }
          },
          {"name": "latitude", "type": "float"},
          {"name": "longitude", "type": "float"},
          {"name": "speed", "type": "int"},
          {
            "name": "bearing",
            "type": {
              "name": "Bearing", "symbols": ["N","NE","E","SE","S","SW","W","NW"], "type": "enum"
            }
          },
          {"name": "routeName", "type": "string"},
          {"name": "timestamp", "type": "long"}
        ],
        "name": "vehicle",
        "type": "record"
      }
      ```
   - An API key corresponding to the Schema Registry
   - An API secret corresponding to the API key

0. Fix different configuration files
   - Rename [`schema/consumer-fixme.properties`](/src/main/resources/schema/consumer-fixme.properties) to `consumer.properties` and fix any FIXMEs
   - Rename [`schema/producer-fixme.properties`](/src/main/resources/schema/producer-fixme.properties) to `producer.properties` and fix any FIXMEs
1. Run the Nstream server with different arguments

   **\*nix Environment:**
   ```
   ./gradlew runSchema
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat runSchema
   ```
2. Run the broker populator

   **\*nix Environment:**
   ```
   ./gradlew runSchemaSim
   ```
   **Windows Environment:**
   ```
   .\gradlew.bat runSchemaSim
   ```
