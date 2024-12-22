# Hagimule

## Description
**Hagimule** is a school project that aims to deploy severals client and an diary in order to be able to download a file by downloading fragment of this file from others clients that have it. 

## How to use :
- `cd src`
- Make an `Input` directory to place file disponible for dl
- Make an `Output` directory for the files downloaded
- `javac */*.java`
- `java Diary.annuaireImpl <ip for the diary>` the default port for the diary is 4000 => example for localhost : `java Diary.annuaireImpl "//localhost"`
- on an other terminal go to src
- `java Client.client <ip of the client> <ip of the diary>` the port is random so you cann launch serveral on localhost => example for localhost : `java Client.Client "localhost" "//localhost"`

## Tools used for the project
- Java JDK 21
- Gradle
- Docker
- IntelliJ Idea / VS Code software
