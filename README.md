# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Phase 2: Sequence Diagram

[![Sequence Diagram](mckayshields.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcuj3ZfF5vD6L9sgwr5iWw63O+nxPF+SwfgC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatJvqMJpEuGFocjA3K8gagrCjAoriq60pJpe8HlEa2iOs6yYwSWVDAMgWiZFU+ivEsJECtoswQe82KAf+RScTA5JoJQ3pBrO6DSVAIY4Rq+EsuUsTQCgsYzo2unTiGrHmpG1HRjAlnxnKuH8UCJZoTy2a5pgSmdsWabARWYFTtp1nzv8f6dtkPalq+I6jB+NaRXOUFmJwq7eH4gReCg6B7gevjMMe6SZJg8UXipJTlBU0gAKK7o19SNc0LQPqoT7dNOOloLFnmplcfVRdBXnKThiElb6Abpeg2HuUZFEETAkLDBANCWf1c11lF5FMm6VHlNIKDcJkLlBm58Gdkp5RoaVfkIHmAkgnBdWlgcwVxeeYBXEu2WeLlG6Qrau7QjAADio6suVp5Vb9bLBfVkOtR19ijr180Dd9Q3AiN2Pjam73gjAyCxNDoyqLtVlzpiMC8YmBLGaSMD1DU0g1KGK1sYUlo0TytqU2o3OHSy7FTVDMPg7EjMeQBr3lMLqgyy9E1BVeIVTBjVPjJU-Q6ygACS0h6wAjL2ADMAAsTwnpkBphX0Xw6AgoANo7oHO08hsAHKjhMfR7DAjSDac1V-aW2sw3rFQG6OJvm1bttTPb+qkfc3tTK77ueylgc+6O-ujIHweh8uOXroE2A+FA2DcPAuoXcLKQVWeOTMO9161A06OY8E2Mvn7XttrjCsTQTe1zrMw+jETsEcU68owJ6erCzT-Uz0Xo6LTdeE86zPIdGA6+G3ZLMRnzTk2lLowyDxhnsWcE+3ygcBNygK6BU-yOJYbiflAtjbL6msfodyuH0f+ptAHJwBiuIGVcAiWDOkhZIMAABSEAeSv0CDnEADYEYdyRpreqVRKR3haIbLGU90BDnrsAZBUA4AQCQlALeowTYgOoB2PGQFRrTxgPQxhzDWHsONtIeePDF6kwAFZYLQOvfh6A5Ju2ESw6AYiTa7yXrhZmB93RqQpKfBO0gDpmkvuyJWFIcEOnshGEmy9hbXR0ctMWrM-CiRQMLKoqjKCwigSohhlAREaKYhnMxlFHLlEpNgTxNiH5LSfndV+mCeSfwCq9DW3CQpcOOE-CO-0srwLXHlAIXgGFdi9LAYA2B66EHiIkVu8MI7EOyfVJqLU2odWMINcew1PoZPVg4hCK9uB4EZHoAwsJtGiAKHY8oHAzoUgssqA01MZlM3vvohZSzMgKGVMraZctXHmJ2edFZCBtpRXWaLcxBR+anXOS5ZU6YQBLA0I-XhaZEBVOYcMeuyyv6ZJ-iQgZuNw6-UKRXIAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
