# Readme

## 1. Content
Contains the generator, which generates valid Maven/Java projects for [AutomationML][AutomationML page] models.

### 1.1. Usage

#### 1.1.1. Preconditions
 * [Java][Java page] >= 1.8
 * [Maven][Maven page]

#### 1.1.2. Run

##### 1.1.2.1. Build project
`mvn clean install`

##### 1.1.2.2. Execute project (= generate code for AutomationML file)
`mvn exec:java -P generator` generates code for [this][PushListener file] AutomationML file in the output-directory `code-output/`
or
`mvn exec:java -P generator -Dexec.args="'<local path or URL (starting with http[s]) to AutomationML file>' '<local path to directory, in which the generated code should be placed>'"`
e.g.
 * `mvn exec:java -P generator -Dexec.args="'code-input/PushListener.aml' 'code-output/'"`
 * `mvn exec:java -P generator -Dexec.args="'https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/raw/master/AMLmodel_v4/PushListener.aml' 'code-output/'"`

Attention: currently the AutomationML file is not validated (= if the model elements are used correctly).

##### 1.1.2.3. Execute generated code

###### 1.1.2.3.1. Import code in IDE
 * Find the generated code in the specified output-directory.
 * Import a generated service into your favored IDE as a Maven project.

###### 1.1.2.3.2. Implement business logic
Implement the logic for the communication events of the ports of the components in the generated `Main.java` class.
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

###### 1.1.2.3.3. Run code
 1. Build Maven project: `mvn clean install`
 2. Switch to a components directory: `cd <component-name>` and run it: `mvn exec:java -P <component-name>`

### 1.2. More infos on the 'AutomationML model elements to code-artefacts' transformation
The [transformation document][Transformation document directory] describes, which code-artefacts are generated for which AutomationML model element.

## 2. About
This repository is part of the bachelor thesis of JWa in the year 2017.
The following repositories also belong to it:
 * [pushlistener-amlmodel][pushlistener-amlmodel repository]
 * [pushlistener-code-architecture][pushlistener-code-architecture repository]


[Transformation document directory]: src/master/docs/AmlmodelToCodeTransformation/
[PushListener file]: https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/raw/master/AMLmodel_v4/PushListener.aml
[pushlistener-amlmodel repository]: https://bitbucket.org/0xCAF3BAB3/pushlistener-amlmodel/
[pushlistener-code-architecture repository]: https://bitbucket.org/0xCAF3BAB3/pushlistener-code-architecture/
[AutomationML page]: https://www.automationml.org/
[Java page]: https://www.java.com/
[Maven page]: https://maven.apache.org/