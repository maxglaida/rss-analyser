### IMPORTANT ###
to run

```./mvnw spring-boot:run```

this project is developed and compiled with java 11
To avoid mismatch errrors when running please make sure you run it with JRE 11

or run it from an environment(IDE for example)

Java versions on a mac:
```
https://medium.com/w-logs/installing-java-11-on-macos-with-homebrew-7f73c1e9fadf
```

#### TODOs ####
More tests <br>
More Error Handling <br>
More Logging <br>
Swagger API documentation <br>
JavaDocs(?)
Performance testing with big amounts of data

### Testing ###
It was important for me here to avoid testing code that is not mine. for example ROME rss library or RAKE NLP manipulations. <br>

Focus is only to test code that belongs to me in this case.

### App weaknesses ###
The embedded Machine learning algorithm is not complete and trained on hot-topics, it is a concept which allows to think differently from string manipulations when it comes to NLP tasks.

With Machine learning, the solution becomes flexible, to solve this weakness, I would extract this to a seperate service and train it on correct data, instead of using a pre-trained model such as here <br>
I decided to go for a machine learning solution to show the concept. In my opinion, today, we have to use ML for such NLP analysis tasks.<br>
It can scale to many languages and the models could be trained to fit the exact requirement, whereas other manipulations are very limited
to programmatic ideas.

https://medium.com/datadriveninvestor/rake-rapid-automatic-keyword-extraction-algorithm-f4ec17b2886c
http://opennlp.sourceforge.net/models-1.5/

Async - using async is not a cloud native solution, it scales based on cores, this is expensive, would be better to implement here non blocking reactivity.

XML feeds and ROME - this should be JSON, this way its easy to write POJOs, transform and preform analysis.
The library is very complex for a very simple task.

Database is JDBC - blocking DB, R2DBC would have been better here.(also for h2 there is one already)

Query - hibernate - JPA optimization, I am sure there are ways to query with higher performance, especially when the amount of data gets bigger. This was out of my scope though..

### my vision ###
The idea was to come from my area as a cloud native cognitive dev, and bring my vision of integrating NLP models here.
The app is supposed to utilize maximum resources, the calls to external APIs happen parrallel in a non blocking manner,
the processing of the data should happen multi-threaded(async) to allow max utilization. When this is all done,
to get the frequency and data from the database I have preformed basic SQL queries which are known to be faster than
any code manipulation(usually with a good db). This way it is kept simple and the functionality can be extended very simply.
### analysis endpoint input ###
```
POST
http://localhost:8080/analyse/new

body:
["https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss", "http://feeds.bbci.co.uk/news/world/rss.xml"]
```

### analysis endpoint output ###
```
50168f7f-cd2b-4369-8223-b8fd9e49bb22
```

### frequency endpoint input ###
```
GET
http://localhost:8080/frequency/50168f7f-cd2b-4369-8223-b8fd9e49bb22
```
### frequency endpoint output ###

```
[
    {
        "topic": "coronavirus",
        "frequency": 7,
        "originalFeedItems": [
            {
                "link": "https://www.bbc.co.uk/news/world-europe-53320350",
                "originalTitle": "Coronavirus: Italian beach nudists fined as police crack down"
            },
            {
                "link": "https://www.bbc.co.uk/news/world-us-canada-53320336",
                "originalTitle": "Coronavirus: Anger over US decision on foreign students' visas"
            },
            ...
        ]
    },
    ...
]
```

### IMPORTANT LINKS ###

Java for Data Science - how to clean data, RAKE, options to extract topics.<br> <br>
https://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html <br><br>
https://stackoverflow.com/questions/54495300/how-to-customize-springwebflux-webclient-xmlrss-jaxb-deserialization <br><br>
https://www.baeldung.com/spring-webclient-simultaneous-calls

### JPA LINKS ###
https://stackoverflow.com/questions/12930935/illegalargumentexception-expecting-idclass-mapping
## JPA Queries ##
```
SELECT count(keyword) as freq, keyword FROM TOPIC where topic.feed_item_uuid =  'd98fb1591e8f47bdbc225d3770b7173d' group by keyword order by freq DESC
```

# Exercise

Implement a hot topic analysis for RSS feeds.

## Specification
Your application should expose two HTTP endpoints:

### API Definition: 

```
/analyse/new
```

### API Input:

This API endpoint should take at least two RSS URLs as a parameter (more are possible) e.g.:

https://news.google.com/news?cf=all&hl=en&pz=1&ned=us&output=rss

### API Response:

For each request executed against the API endpoint you should return an unique identifier, which will be the input for the second API endpoint.

### Workflow:

When the the API is being called, your code should do a HTTP request to fetch the RSS feeds.
Your code should then analyse the entries in this feed and find potential hot topics --> are there any overlaps between the news.

### Example:

RSS Feed one contains following news:
To Democrats, Donald Trump Is No Longer a Laughing Matter
Burundi military sites attacked, 12 insurgents killed
San Bernardino divers return to lake seeking electronic evidence

RSS Feed two contains following news:
Attacks on Military Camps in Burundi Kill Eight
Saudi Women to Vote for First Time
Platini Dealt Further Blow in FIFA Presidency Bid

Your analysis should return that there are news related to Burundi in both feeds.
The analysed data should be stored within a data store and referenced by an unique identifier (see API response).

### API Definition: 

```
/frequency/{id}
```

### API Input:

This API endpoint takes an id as input

### API Output:

Returns the three elements with the most matches, additinally the orignal news header and the link to the whole news text should be displayed.

### Workflow:

When this API is being called, you will read the analysis data stored in the database by using the given id parameter
Return the top three results as a json object ordered by their frequency of occurrence

## Additional Information
You should use following frameworks for your work.

Spring JPA
H2 database running in memory (data will not be persistent across application restarts)
You are free to add / change any libraries which you might need to solve this exercise, the only requirement is that we do not have to setup / install any external software to run this application.

Running the exercise with maven

```mvn spring-boot:run```
