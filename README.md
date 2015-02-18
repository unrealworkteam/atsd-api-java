# Axibase Time-Series Database Client for Java

The **ATSD Client for Java** enables Java developers to easily read and write statistics and metadata from
[Axibase Time-Series Database][atsd]. Build reporting, analytics, and alerting solutions with minimal effort.
Get started using this API for Java using **Maven** or by downloading a [zip file][atsd-zip].

* [Documentation][atsd-api]
* [Issues][atsd-issues]

## Implemented Methods

**The ATSD Client for Java** provides an easy-to-use client for interfacing with **ATSD** metadata and data REST API services.
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

- Meta Data API
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
Install ATSD - Before you begin, you need to install your own copy of the [Axibase Time-Series Database][atsd].
Download the latest version of ATSD available for your Linux distribution.

Minimum requirements - Java 1.6+ is needed to run the ATSD Client.

Install the ATSD Client - We recommend installing the ATSD Client for Java by using Maven.

Building from the Source – After checking out the code from GitHub, you can build the ATSD Client using Maven.

```
git clone https://github.com/axibase/atsd-api-java.git
cd atsd-api-java
mvn clean dependency:copy-dependencies compile jar:jar
java -cp "atsd-api-java-1.0-SNAPSHOT.jar:dependency/*" com.axibase.tsd.example.AtsdClientExample
```

## Examples

See [Example][atsd-example]

### Client Configuration
```java
        AtsdClientExample atsdClientExample = new AtsdClientExample();
        atsdClientExample.configure();
```

### Metadata Processing
```java
        Metric metric = metaDataService.retrieveMetric(metricExample);
        if (metric == null) {
            System.out.println("Unknown metric: " + metricExample);
            return;
        }
        List<EntityAndTags> entityAndTagsList = metaDataService.retrieveEntityAndTags(metric.getName(), null);
        System.out.println("===Metric MetaData===");
        System.out.println("Metric: " + metric.getName());
```

### Data Queries
```java
        GetSeriesCommand command = new GetSeriesCommand(entityName, metric.getName(), tags);
        List<GetSeriesResult> getSeriesResults = dataService.retrieveSeries(new Interval(1, IntervalUnit.MINUTE), 10, command);
        for (GetSeriesResult getSeriesResult : getSeriesResults) {
            System.out.println("Time Series Key: " + getSeriesResult.getTimeSeriesKey());
            List<Series> data = getSeriesResult.getData();
            for (Series series : data) {
                long ts = series.getT();
                System.out.println(toISODate(ts) + "\t" + series.getV());
            }
        }
```


[atsd]:https://axibase.com/products/axibase-time-series-database/
[atsd-api]:https://axibase.com/products/axibase-time-series-database/reading-data/java/
[atsd-zip]:https://github.com/axibase/atsd-api-java/releases/download/untagged-0901a806a9372ef24c51/v0.3-alpha.zip
[atsd-issues]:https://www.axibase.com/support.htm
[atsd-example]:https://github.com/axibase/atsd-api-java/blob/master/src/main/java/com/axibase/tsd/example/AtsdClientExample.java