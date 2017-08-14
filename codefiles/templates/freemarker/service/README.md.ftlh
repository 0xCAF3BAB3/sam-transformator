# Readme

## 1. Content
Generated code for service '${serviceName}'.

## 2. Execute generated code

### 2.1. Import code in IDE
Import generated service into your favored IDE as a Maven project.

### 2.2. Implement business logic
Implement the logic for the communication events of the ports of the components in the generated `Main` class.
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

### 2.3. Run code
 1. Build Maven project: `mvn clean install`
 2. Switch to a components directory: `cd <component-name>` and run it: `mvn exec:java -P <component-name>`