# rest-refactoring-test
Often you have to refactore or fix REST services which are not covered by extensive automated test and hard
to test at all. But you want to make sure that these services haven't changed their behaviour after 
your changes. 

This library gives you the tool to do so fast and easy. You only describe the data you need
and how the request is built and let the library do the work. See this small example:

```Scala
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
    
    (uri, params)
  }
} yield result

assert(checkAndLog(testCase.runCase(config)))
```

Here, we create a test for a *GET* endpoint `/rest/hello/:name` which is currently provided by the 
refactored REST service on `refactored-rest.com` and the old version on `old-rest.com`. The library
will create requests with random `name` and `age` and send the same request to both services. It will
then compare the responses and log possible differences. This step is repeated 100 times as configured.
