# Readme

## 1. About
This repository is part of the bachelor's thesis of Julian Waibel at the Technical University of Vienna.
The following repositories also belong to it:

 * [sam-model][sam-model repository]
 * [sam-transformator-architecture][sam-transformator-architecture repository]

## 2. Content
Contains the transformator, which generates valid out-of-the-box runnable code artifacts for [SAM model][sam-model repository] files.

### 2.1. Usage

#### 2.1.1. Preconditions
 * [Java][Java page] >= 1.8
 * [Maven][Maven page]

#### 2.1.2. Run

##### 2.1.2.1. Build project
`mvn clean install`

##### 2.1.2.2. Execute project (= generate code artifacts for a SAM model)
 * `mvn exec:java -P transformator` generates code for [this][SAM model file] SAM model file in the output-directory `code-output/`
 * or specify other SAM model file and output-directory: `mvn exec:java -P transformator -Dexec.args="'<local path or URL (starting with http[s]) to SAM model file>' '<local path to directory, in which the generated code artifacts should be placed>'"`

e.g.

 * `mvn exec:java -P transformator -Dexec.args="'code-input/MySAMmodel.aml' 'code-output/'"`
 * `mvn exec:java -P transformator -Dexec.args="'https://raw.githubusercontent.com/0xCAF3BAB3/sam-model/master/SAMmodel_v4.aml' 'code-output/'"`

##### 2.1.2.3. Execute generated code

###### 2.1.2.3.1. Import code in IDE
 1. Find the generated code in the specified output-directory.
 2. Import a generated service into your favored IDE as a Maven project.

###### 2.1.2.3.2. Implement business logic
Implement the business logic and logic for the communication events of the ports of the components in the generated `Main` class.
Please take a look at the generated comments, which explain how and where to

 * add receiver-handlers
 * add asynchronous-sender-callbacks
 * connect and execute senders

E.g. to specify what a receiver-port should do for a received message, register a receiver-handler:
```java
portsService.setReceiverHandler(
        CommunicationService.Receivers.PORTX.getName(),
        msg -> {
            System.out.println("Message received");
            return Optional.absent();
        }
);
```

###### 2.1.2.3.3. Run code
 1. Build Maven project: `mvn clean install`
 2. Switch to a components directory: `cd <component-name>` and run it: `mvn exec:java -P <component-name>`

### 2.2. More infos on the 'SAM model to code artifacts' transformation
The [transformation details directory][Transformation details directory] documents briefly which code artifacts get generated for which SAM model elements.

### 2.3. To-Dos
 * Currently the passed SAM model file is not validated (= check, if the model elements are used correctly). Implementation should be done in method `validateModel(...)` in `com.jwa.sam.transformator.service.CodegeneratorService.java`.


[Transformation details directory]: https://github.com/0xCAF3BAB3/sam-transformator/tree/master/docs/transformation-details/
[SAM model file]: https://raw.githubusercontent.com/0xCAF3BAB3/sam-model/master/SAMmodel_v4.aml
[sam-model repository]: https://github.com/0xCAF3BAB3/sam-model/
[sam-transformator-architecture repository]: https://github.com/0xCAF3BAB3/sam-transformator-architecture/
[AutomationML page]: https://www.automationml.org/
[Java page]: https://www.java.com/
[Maven page]: https://maven.apache.org/