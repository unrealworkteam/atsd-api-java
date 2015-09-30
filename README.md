# Axibase Time-Series Database Client for Java

The **ATSD Client for Java** enables Java developers to easily read and write statistics and metadata from the
[Axibase Time-Series Database][atsd]. With minimal effort, you can build reporting, analytic, and alerting solutions.
By using **Maven** or downloading the zip file from [GitHub][atsd-zip], get started with this Java API.

```xml
        <dependency>
            <groupId>com.axibase</groupId>
            <artifactId>atsd-api-java</artifactId>
            <version>0.3.32</version>
        </dependency>
```

* [Documentation][atsd-api]
* [Issues][atsd-issues]

## Implemented Methods

The **ATSD Client for Java** provides an easy-to-use client for interfacing with **ATSD** metadata and data REST API services.
It has the ability to read and write time-series values, statistics, properties, alerts, and messages.

- Data API
    - Series
        - QUERY
        - INSERT
        - CSV INSERT
    - Properties
        - QUERY
        - INSERT
    - Alerts
        - QUERY
    - Alerts History
        - QUERY

- Metadata API
    - Metrics
        - Get Metrics
        - Get Metric
        - Create/Update Metric
        - Delete Metric  
        - Get Entities and Series Tags for Metric
    - Entities
        - Get Entities
        - Get Entity
        - Create/Update Entity
        - Delete Entity
        - Get Metrics for Entity
    - Entity Groups
        - Get Entity Groups
        - Get Entity Group
        - Create/Update Entity Group
        - Delete Entity Group
        - Entities for Entity Group
        - Add Entities to Entity Group
        - Set (Replace) Entities in Entity Group
        - Delete Entities from Entity Group


## Getting Started
Before you begin installing the **ATSD Client for Java**, you need to install a copy of the [Axibase Time-Series Database][atsd].
Download the latest version of ATSD that is available for your Linux distribution.

Minimum requirements for running the ATSD Client: Java 1.6+.

We recommend installing the ATSD Client for Java by using Maven. Build the ATSD Client with
Maven after checking out the code from GitHub.

```
git clone https://github.com/axibase/atsd-api-java.git
cd atsd-api-java
mvn clean dependency:copy-dependencies compile jar:jar
cd target
java -cp "atsd-api-java-0.3.32.jar:dependency/*" -Daxibase.tsd.api.client.properties=./client.properties com.axibase.tsd.example.AtsdClientWriteExample
```

## Examples

See:
* [AtsdClientReadExample][atsd-read-example]
* [AtsdClientWriteExample][atsd-write-example]

### Client Configuration

#### Option 1
Use `-Daxibase.tsd.api.client.properties=./client.properties`

```java
        ClientConfiguration clientConfiguration = ClientConfigurationFactory
            .createInstance()
            .createClientConfiguration();
        HttpClientManager httpClientManager = new HttpClientManager(clientConfiguration);
        DataService dataService = new DataService(httpClientManager);
        MetaDataService metaDataService = new MetaDataService(httpClientManager);
```

**client.properties** example:
```
        axibase.tsd.api.server.name=atsd_server
        axibase.tsd.api.server.port=8080
        #axibase.tsd.api.server.port=8443
        #axibase.tsd.api.protocol=https
        #axibase.tsd.api.ssl.errors.ignore=true
        axibase.tsd.api.username=username
        axibase.tsd.api.password=pwd
```

Usage
```java
        AtsdClientWriteExample atsdClientWriteExample = new AtsdClientWriteExample();
        atsdClientWriteExample.configure();
        atsdClientWriteExample.writeData();
        atsdClientWriteExample.printData();
```

#### Option 2
Use pure Java.
```java
        ClientConfigurationFactory configurationFactory = new ClientConfigurationFactory(
                "http", "atsd_server", 8080, // serverPort
                "/api/v1", "/api/v1",
                "username", "pwd",
                3000, // connectTimeoutMillis
                3000, // readTimeoutMillis
                600000, // pingTimeout
                false, // ignoreSSLErrors
                false // skipStreamingControl
        );
        ClientConfiguration clientConfiguration = configurationFactory
            .createClientConfiguration();
        System.out.println("Connecting to ATSD: " + clientConfiguration.getMetadataUrl());
        HttpClientManager httpClientManager = new HttpClientManager(clientConfiguration);

        GenericObjectPoolConfig objectPoolConfig = new GenericObjectPoolConfig();
        objectPoolConfig.setMaxTotal(5);
        objectPoolConfig.setMaxIdle(5);

        httpClientManager.setObjectPoolConfig(objectPoolConfig);
        httpClientManager.setBorrowMaxWaitMillis(1000);

        DataService dataService = new DataService(httpClientManager);
        MetaDataService metaDataService = new MetaDataService(httpClientManager);
```

Usage
```java
        AtsdClientWriteExample atsdClientWriteExample = new AtsdClientWriteExample();
        atsdClientWriteExample.pureJavaConfigure();
        atsdClientWriteExample.writeData();
        atsdClientWriteExample.printData();
```


#### Option 3
Use Spring.
See **example-beans.xml**
```xml
        <bean id="example" class="com.axibase.tsd.example.AtsdClientWriteExample"/>
        <bean id="dataService" class="com.axibase.tsd.client.DataService"/>
        <bean id="metaDataService" class="com.axibase.tsd.client.MetaDataService"/>
        <bean id="httpClientManager" class="com.axibase.tsd.client.HttpClientManager"/>
        <bean id="genericObjectPoolConfig"
            class="org.apache.commons.pool2.impl.GenericObjectPoolConfig">
            <property name="maxTotal" value="3"/>
        </bean>
        <bean id="clientConfiguration"
            class="com.axibase.tsd.model.system.ClientConfiguration">
            <constructor-arg name="url" value="http://atsd_server:8080/api/v1"/>
            <constructor-arg name="username" value="username"/>
            <constructor-arg name="password" value="pwd"/>
        </bean>
```

Usage
```java
            ApplicationContext context =
                new ClassPathXmlApplicationContext("example-beans.xml");
            AtsdClientWriteExample example =
                (AtsdClientWriteExample)context.getBean("example");
            example.writeData();
            example.printData();
```

### Metadata Processing
```java
        String metricExample = "jvm_memory_used_percent";
        Metric metric = metaDataService.retrieveMetric(metricExample);
        if (metric == null) {
            System.out.println("Unknown metric: " + metricExample);
            return;
        }
        List<EntityAndTags> entityAndTagsList = metaDataService
            .retrieveEntityAndTags(metric.getName(), null);
        System.out.println("===Metric MetaData===");
        System.out.println("Metric: " + metric);
        for (EntityAndTags entityAndTags : entityAndTagsList) {
            String entityName = entityAndTags.getEntityName();
            System.out.println("\n===Entity MetaData===");
            System.out.println("Entity: " + entityName);
            Map<String, String> tags = entityAndTags.getTags();
            System.out.println("===Tags===");
            for (Map.Entry<String, String> tagAndValue : tags.entrySet()) {
                System.out.println("\t" + tagAndValue.getKey() + " : " + tagAndValue.getValue());
            }
        }
```

### Data Queries
```java
        GetSeriesQuery command = new GetSeriesQuery(entityName, metric.getName(), tags,
            System.currentTimeMillis() - 3600, System.currentTimeMillis());
        command.setAggregateMatcher(
            new SimpleAggregateMatcher(new Interval(1, IntervalUnit.MINUTE),
            Interpolate.NONE,
            AggregateType.DETAIL));
        List<GetSeriesResult> getSeriesResults =
            dataService.retrieveSeries(command);
        for (GetSeriesResult getSeriesResult : getSeriesResults) {
            System.out.println("Time Series Key: "
                + getSeriesResult.getTimeSeriesKey());
            List<Series> data = getSeriesResult.getData();
            for (Series series : data) {
                long ts = series.getT();
                System.out.println(toISODate(ts) + "\t" + series.getV());
            }
        }
```

# Aggregation Log Filter

For developers of major systems a typical situation is that with the system's growth, new functionality and new system logs are added. Under certain circumstances, there are failures in the systems, which are written into the logs. When applications run on multiple servers, the task of monitoring service status is complicated.

Log statistics collector is used to gather statistics on errors and warnings in log files and selectively send messages from the logs for fast browsing (no need to go to the server and examine the logs in detail), without any modifications to the application's source code.

Using the collected statistics you can monitor the stability of applications on different servers in the long term. You can configure rules to be notified of application errors, which will reduce the time it takes to eliminate the errors.

## Filter

```xml 
       <filter class="com.axibase.collector.logback.Collector">
            <writer class="com.axibase.collector.writer.HttpStreamingAtsdWriter">
                <url>http://localhost:8088/api/v1/command/</url>
                <username>USERNAME</username>
                <password>PASSWORD</password>
            </writer>
            <level>INFO</level>
            <sendSeries>
                <zeroRepeatCount>3</zeroRepeatCount>
                <metric>log_event</metric>
                <periodSeconds>60</periodSeconds>
                <sendThreshold>100</sendThreshold>
                <minPeriodSeconds>1</minPeriodSeconds>
            </sendSeries>
            <sendMessage>
                <every>100</every>
            </sendMessage>
            <sendMessage>
                <level>ERROR</level>
                <every>30</every>
                <stackTraceLines>-1</stackTraceLines>
            </sendMessage>
            <tag>
                <name>CUSTOM_TAG</name>
                <value>TAG_VALUE</value>
            </tag>
        </filter>
```

| Name | Required | Default | Description |
|---|---|---|---|
| level | no | TRACE | minimum level to process event |
| entity | no | current hostname | entity name for series and messages |
| tag | no | - | custom tag(s) to attach to series and messages, MULTIPLE |
| writer | yes | - | see writer config |
| sendSeries | yes | - | see sendSeries config |
| sendMessage | no | - | see sendMessage config, MULTIPLE |


## writer

### TCP writer

```xml
<writer class="com.axibase.collector.writer.TcpAtsdWriter">
    <host>localhost</host>
    <port>8081</port>
</writer>
```

| Name | Required | Default | Description |
|---|---|---|---| 
| host | yes | - | ATSD host, string |
| port | yes | - | ATSD TCP port, integer |

### UDP writer

```xml
<writer class="com.axibase.collector.writer.UdpAtsdWriter">
    <host>localhost</host>
    <port>8082</port>
</writer>
```

| Name | Required | Default | Description |
|---|---|---|---|
| host | yes | - | ATSD host, string |
| port | yes | - | ATSD UDP port, integer |

### HTTP writer

```xml
<writer class="com.axibase.collector.writer.HttpStreamingAtsdWriter">
    <url>http://localhost:8088/api/v1/command/</url>
    <username>axibase</username>
    <password>*****</password>
</writer>
```

| Name | Required | Default | Description |
|---|---|---|---|
| url | yes | - | ATSD API command URL like 'http://localhost:8088/api/v1/command/', string |
| username | yes | - | user name, string |
| password | yes | - | password, string |


## sendSeries

```xml
<sendSeries>
    <!-- 0+ default:1 -->
    <zeroRepeatCount>5</zeroRepeatCount>
    <!-- default: log_event -->
    <metric>log_event</metric>
    <!-- default: _total -->
    <totalSuffix>_sum</totalSuffix>
    <!-- default: 60 -->
    <periodSeconds>1</periodSeconds>
    <!-- default: 0 -->
    <sendThreshold>10</sendThreshold>
    <!-- default: 5 -->
    <minPeriodSeconds>0</minPeriodSeconds>
</sendSeries>
```

| Name | Required | Default Value | Description |
|---|---|---|---|
| metric | no | log_event  | metric names prefix  |
| rateSuffix | no | _rate  | `rate` metric name suffix  |
| totalSuffix | no | _total  | `total` metric name suffix  |
| counterSuffix | no | _counter  | `counter` metric suffix  |
| zeroRepeatCount | no | 1 | count of zero values after the last significant events |
| periodSeconds | no | 60 | the period of sending collected log statistics (seconds) |
| ratePeriodSeconds | no | 60 | period to calculate rate (seconds)|
| minPeriodSeconds | no | 5 | minimum period between sending of statistics (seconds), in case `sendThreshold` is triggered|
| sendThreshold | no | 0 | initiates sending of statistics before `periodSeconds` is completed, useful to decrease latency |
| messageSkipThreshold | no | 100 | remove oldest message from the internal queue if queue size more than `messageSkipThreshold` |
| cacheFlushThreshold | no | 10000 | initiates sending of statistics before `periodSeconds` completed, `minPeriodSeconds` is ignored  |
| cacheSkipThreshold | no | 100000 | remove oldest log event in the internal queue to collect statistics, necessary to avoid OOM |


## sendMessage

```xml
<sendMessage>
    <every>1000</every>
</sendMessage>
<sendMessage>
    <level>ERROR</level>
    <every>10</every>
    <stackTraceLines>15</stackTraceLines>
</sendMessage>
```

| Name | Required | Default Value | Description |
|---|---|---|---|
| level | no | WARN | minimum level to send message |
| every | yes | - | one of every n log events will be sent to ATSD as message |
| stackTraceLines | no | 0 | count of stack trace line that will be included in the message, -1 -- all lines |



[atsd]:https://axibase.com/products/axibase-time-series-database/
[atsd-api]:https://axibase.com/atsd/api/
[atsd-zip]:https://github.com/axibase/atsd-api-java/releases/download/0.3.32/atsd-api-java-0.3.32-bin.zip
[atsd-issues]:https://www.axibase.com/support.htm
[atsd-read-example]:https://github.com/axibase/atsd-api-java/blob/master/src/main/java/com/axibase/tsd/example/AtsdClientReadExample.java
[atsd-write-example]:https://github.com/axibase/atsd-api-java/blob/master/src/main/java/com/axibase/tsd/example/AtsdClientWriteExample.java