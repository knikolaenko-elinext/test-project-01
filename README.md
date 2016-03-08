# DEMO APP 

## Task description

Please, write simple project in plain java. Don't use extra libraries, only test libraries are allowed for unit testing. The small application should do the following (this solution shouldn't take more than 1-2 hours of your time):

1. Application should get users from the storage (as a storage you can use external file).
2. Send emails to those, which are marked as activated on the storage (It shouldn't be the actual sent, just write message in console).
3. Please, think about performance and write in README how have you achieved this.
4. IMPORTANT: Write unit tests.
Thank you and good luck!

## Solution description

So, I see following challenges in this task:

1. Read text file in a way which is fast enough.
2. Execute potentially-slow mail sending logic simultaneously by multiple threads
3. Minimize memory consumption since we don't know how big users file is (avoid out of memory error)

##### File reading 

I use `BufferedReader` object for reading file line-by-line. Should be fast enough because of consuming part will do network operations.

##### Multi-threading

Actual line parsing, analyzing and email sending is doing by `ExecutorService`. Classic approach. Main thread submits `Callable`s objects into `ExecutorService`. Underlying threads get them from the queue and execute.

##### Memory consumption

For preventing internal `ThreadPoolExecutor`'s queue growing I use `BlockingQueue<String>` as intermediate string storage between file reader and user lines analyzer. Capacity of `BlockingQueue` is equal to doubled ThreadPool size. In case if email sending will be too slow, file reading thread (main thread in realization) would be blocked until releasing some free cells in a queue. This will minimize heap memory consumption.

### Key Application files

##### `src\main\java\com\elinext\demo\Main.java`

Start point of application. `notifyActivatedUsers()` method must be executed in order to start file processing.

##### `src\main\java\com\elinext\demo\SendEmailTasksConsumer.java`

Gets single line from the queue that was readed from the file, parse it, checks activation status and delegate message sending work to `MailTransportService` if it is needed.

##### `src\main\java\com\elinext\demo\transport\MailTransportService.java`

Abstraction for mail sending logic. Single `notifyUser(UserModel user)` must be thread-safe, since multiple threads will access it. I didn't understand what exactly do you mean by 'Send emails, but it shouldn't be the actual sent, just write message in console'. And that is why I prepared two implementation:

##### `src\main\java\com\elinext\demo\transport\MailTransportServiceImpl.java`

Sends mails using standard javax.mail package

##### `src\test\java\com\elinext\demo\transport\MailTransportServiceMock.java`

Write message in a console with random delay instead of actual sending

##### `data\MOCK_DATA.csv`

Mock data containing 1000 user records

### Building and Running

* `./gradlew clean build` - build application 
* `./gradlew test` - run unit-tests
