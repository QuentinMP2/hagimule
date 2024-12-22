# Hagimule

## Description
**Hagimule** is a school project that aims to deploy severals client and an diary in order to be able to download a file by downloading fragment of this file from others clients that have it. 

## How to use :
- make build 
- `cd src`
- `java Diary.annuaireImpl <ip for the diary>` the default port for the diary is 4000 => example for localhost : `java Diary.annuaireImpl "//localhost"`
on an other terminal go to src
- `java Client.client <ip of the client> <ip of the diary>` the port is random so you can launch serveral on localhost => example for localhost : `java Client.Client "localhost" "//localhost"` 
the client currently parse a directory Input placed in src so place file you want to download here.


After use :
- make clean

## Tools used for the project
- Java JDK 21
- Gradle
- Docker
- IntelliJ Idea / VS Code software
