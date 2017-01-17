[![Build Status](https://travis-ci.org/pheymann/rest-refactoring-test.svg?branch=develop)](https://travis-ci.org/pheymann/rest-refactoring-test)

**rrt-core**:
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-core_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-core_2.11)

**rrt-play**:
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-play_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-play_2.11)

**rrt-plugin**:
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.pheymann/rrt-plugin)

# rest-refactoring-test (rrt)
Often you have to refactor or fix REST services which are not covered by extensive automated test and are hard
to test at all. But you want to make sure that these services haven't changed their behaviour after 
you have done your changes. 

This library gives you the tools to test your services fast and simple. You only describe the data you need
and how the request is built and let the library do the work. See this small example:

```Scala
import com.github.pheymann.rrt._

// GET /rest/hello/:name?age: Int
val config = newConfig("my-test", ServiceConfig("refactored-rest.com", 8080), ServiceConfig("old-rest.com", 8081))
              .withRepetitions(100)

val testCase = for {
  userNames <- genStaticData("Luke", "Anakin", "Yoda")
  ages      <- genInts(900)
  result    <- testGet { _ =>
    // selects randomly one name out of the static list
    val uri = s"/rest/hello/${userNames()}"
    // generates a random `Int` between 0 and 900
    val params = Map("age" -> ages().toString)
    
    uri |+| params
  }
} yield result

assert(checkAndLog(testCase.runCase(config)))
```

Output:

```
[GET] start my-test

[####################] 100%

test case my-test succeeded
  succeeded tries: 100
  failed trie:     0
```

Here, we create a test for a *GET* endpoint `/rest/hello/:name` which is currently provided by the 
refactored REST service on `refactored-rest.com` and the old version on `old-rest.com`. The library
will create a request with random `name` and `age` and send it to both services. It will
then compare the responses and log possible differences. This step is repeated 100 times as configured.

Besides the random generation or selection of values you are also able to load data from a database,
e.g. if you need existing user ids.

## Get The Libraries
You can get the core library by adding the following dependency:

```SBT
libraryDependencies += "com.github.pheymann" %% "rrt-core" % "0.4.x-RC" % Test
```

Furthermore you can add a [Play](https://www.playframework.com/) dependency which adds the ability to 
read Play database configs.

```SBT
libraryDependencies += "com.github.pheymann" %% "rrt-play" % "0.4.x-RC" % Test
```

Both libs are built for Scala 2.11. And there is a SBT plugin to add a `rrt` task to execute rest refactoring 
tests which are implemented within some Unit test framework as Specs or Scalatest.

```SBT
addSbtPlugin("com.github.pheymann" % "rrt-plugin" % "0.4.x-RC")
```

## Dependecies
This library is build with:
 - Free Mondas provided by [cats](https://github.com/typelevel/cats) to build the library api
 - the http client by [akka-http](http://doc.akka.io/docs/akka-http/current/scala.html) for the REST calls
 - the db api from [scalike-jdbc](http://scalikejdbc.org/) for the database interactions
 
## Examples
In this early phase you can only find some running examples in the [integration tests](https://github.com/pheymann/rest-refactoring-test/tree/develop/core/src/it/scala/com/github/pheymann/rrt).
