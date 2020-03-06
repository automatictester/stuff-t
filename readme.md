Test execution
-

To run tests (if you have Maven installed):

`mvn clean test`

To run tests (if you do not have Maven installed, Unix platform):

`./mvnw clean test`

To run tests (if you do not have Maven installed, Windows platform):

`mvnw.cmd clean test`


To keep things simple
-
- I have implemented only key assertions. In real-world scenario I could consider implementing more extensive checks.
- I am generating payload from JSONObjects rather than serializing Java models.
- I have not implemented any test data cleanup.
- Functionality which is best covered on unit test level with access to the source code was not covered in API tests.
This includes boundary values and data types.


Design considerations
- 

- There is a number of dependencies in these tests. Tests for threads depend on signup, tests for thread messages 
depend on signup and threads. Ideally, each test would create test data it needs, e.g. thread test would create 
a new user etc. The trade-off is that if signup functionality is broken, tests for threads wouldn't run. Moreover,
test execution times would go up. There is no silver bullets. I decided to create static test data which I keep in
`app.properties` and use in my tests. This will work as long as the data is retained in the datastore. This allows me to
ignore the dependencies. In real-world scenario, this could be the right thing to do or not, depending on the context. 
- I have generated JSON schemas from sample responses. In real-world situation I'd prefer to reuse the schemas created
by devs, which I have no access to right now.
- As I do not have access to the datastore, these are strictly API tests. Normally I'd do other things as well, 
related to data persistence in particular - was the newly created user actually saved to the datastore etc.


Open questions
-

- Requirements for private threads are not clear to me. GET method on /threads does not return private threads.
I don't know if this was intented or not. In real-world I'd check the requirements. Here I assumed that
private threads should not be returned by GET /threads.

Issues
-

- POST `/threads` accepts thread names up to 60 chars, while should accept up to 50.
- GET `/threads` doesn't return only `"items": ThreadModel[]` as documented, but also a bunch of other properties
(next, previous, limit, start, itemsFound). This looks like there was a pagination functionality implemented, but
not documented.
- GET `/threads/id/<String:thread_id>/messages` - same as above.
- There is a slight discrepancy in endpoints between `apiDoc` and `api structure`. One indicates
`/threads/<String:thread_id>/messages` as the endpoint for `Get thread messages`, while the other indicates
`/threads/id/<String:thread_id>/messages`. The latter is implemented. I don't think this is in line with REST
standard.
- Same for `Send message in thread`.
