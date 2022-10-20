# ThingsBoard 

## Requirement Software

- IntelliJ IDEA Community 2022 (run backend)
- postgres + pgAdmin (run database)
- VSCode (run frontend)

## Step 1 : Run source code in first time

Follow this instruction via this link.

[Contribution Guide](https://thingsboard.io/docs/user-guide/contribution/how-to-contribute/)

Summary: \
`mvn clean install -DskipTests`

## Step 2 : Custom Source Code

- If customize backend, only run backend and use **Postman** to test API. \
`build in folder`
- If customize frontend, run both and open localhost:4200 to test. \
`yarn start`

---------------------------------

# Upgrade Thingsboard on Windows

- Step 1 : Download thingsboard-windows.zip and extract it.

- Step 2: Stop service `net stop thingsboard`

- Step 3: Copy folder /conf to other place

- Step 4: Copy new file in folder thingsboard-windows to C:/Program Files (x86)/thingsboard; copy folder conf to replace recent

- Step 5: Run `C:\thingsboard>upgrade_dev_db.bat --fromVersion=3.3.4.1`

- Step 6: Start service `net start thingsboard`

# Custom Only Frontend

## Step 1: Download source code 

## Step 2: Install Windows

[Install Thingsboard Windows](https://thingsboard.io/docs/user-guide/install/windows/)

Read via link, this follow instruction below is simple

### Step 2.1: Install Java 11 (OpenJDK), PostgreSQL JDBC

### Step 2.2: Thingsboard Service Installation

Instead of using thingsboard-windows.zip original by thingsboard, contact the builder to send .zip file

### Step 2.3: Config Thingsboard database

### Step 2.4: Run installation script

Command Prompt Administrator

`C:\Program Files (x86)\thingsboard>install.bat` \
`C:\Program Files (x86)\thingsboard>upgrade.bat` \
`C:\Program Files (x86)\thingsboard>install.bat --loadDemo`

### Step 2.5: Start Thingsboard Service

`net start thingsboard`



ThingsBoard is an open-source IoT platform for data collection, processing, visualization, and device management.

<img src="./img/logo.png?raw=true" width="150" height="70">

## Documentation

ThingsBoard documentation is hosted on [thingsboard.io](https://thingsboard.io/docs).

## Getting Started

Collect and Visualize your IoT data in minutes by following this [guide](https://thingsboard.io/docs/getting-started-guides/helloworld/).

## Support

 - [Community chat](https://gitter.im/thingsboard/chat)
 - [Q&A forum](https://groups.google.com/forum/#!forum/thingsboard)
 - [Stackoverflow](http://stackoverflow.com/questions/tagged/thingsboard)

## Licenses

This project is released under [Apache 2.0 License](./LICENSE).
