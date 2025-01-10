# Hagimule

## Description
**Hagimule** is a school project that aims to deploy several's client and a diary in order to be able to download a file by downloading fragment of this file from others clients that have it. 

## How to use :
- make build 
- `cd src`
- `java Diary.annuaireImpl <url for the diary>` the default port for the diary is 4000 => example for localhost : `java Diary.annuaireImpl "localhost"`
on another terminal go to src
- `java Client.client <url of the diary>` the port is random so you can launch several on localhost => example for localhost : `java Client.Client "localhost"` 
the client currently parse a directory Input placed in src so place file you want to download here.


After use :
- make clean

## Tools used for the project
- Java JDK 21
- IntelliJ Idea / VS Code software
- AWS Virtual machines
