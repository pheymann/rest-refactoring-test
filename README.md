[![Build Status](https://travis-ci.org/pheymann/rest-refactoring-test.svg?branch=develop)](https://travis-ci.org/pheymann/rest-refactoring-test)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-core_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-core_2.11)

# rest-refactoring-test (rrt)
Often you have to refactore or fix REST services which are not covered by extensive automated test and hard
to test at all. But you want to make sure that these services haven't changed their behaviour after 
your changes. 

This library gives you the tool to do so fast and easy. You only describe the data you need
and how the request is built and let the library do the work. See this small example:

```Scala
import com.github.pheymann.rrt._
import com.github.pheymann.rrt.TestAction._

// GET /rest/hello/:name?age: Int
val config = newConfig("my-test", "refactored-rest.com", 8080, "old-rest.com", 8081)
              .withRepetitions(100)

val testCase = for {
  userNames <- StaticData(List("Luke", "Anakine", "Yoda")).lift
  ages      <- IntData(900).lift
  result    <- GetEndpoint {
    // selects randomly one name out of the static list
    val uri = s"/rest/hello/${userNames()}"
    // generates a random `Int` between 0 and 900
    val params = Map("age" -> ages().toString)
    
    uri |+| params
  }
} yield result

assert(checkAndLog(testCase.runCase(config)))
```

Here, we create a test for a *GET* endpoint `/rest/hello/:name` which is currently provided by the 
refactored REST service on `refactored-rest.com` and the old version on `old-rest.com`. The library
will create a request with random `name` and `age` and send it to both services. It will
then compare the responses and log possible differences. This step is repeated 100 times as configured.

Besides the random generation or selection of values you are also able to load data from a database,
e.g. if you need existing user ids.

## Get The Library
You can get library by adding the following dependency:

```SBT
libraryDependencies += "com.github.pheymann" %% "rrt-core" % "0.1.0-RC" % Test
```

## Dependecies
This library is build with:
 - Free Mondas provided by [Cats](https://github.com/typelevel/cats) to build the library api
 - the http client by [akka-http](http://doc.akka.io/docs/akka-http/current/scala.html) for the REST calls
 - the db api from [scalike-jdbc](http://scalikejdbc.org/) for the database interactions
 
## Examples
In this early phase you can only find some running examples in the [intregration tests](https://github.com/pheymann/rest-refactoring-test/tree/develop/core/src/it/scala/com/github/pheymann/rrtt).
