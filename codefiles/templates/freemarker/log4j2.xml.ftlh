<?xml version="1.0" encoding="UTF-8"?>
<Configuration name="${name}" status="warn" strict="false">
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{ISO8601} %-5p %c{2} - %m%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="${groupId}" level="trace" />
        <Root level="warn">
            <AppenderRef ref="console"/>
        </Root>
    </Loggers>
</Configuration>
