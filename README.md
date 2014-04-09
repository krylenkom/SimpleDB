Was done for https://www.freelancer.com/u/anjalikp.html from Las Cruces, United States. He did not pay me so I published sources.

CS 582, Spring 2014, Project
SimpleDB

Due: Friday, 28 March 2013, 11:45pm
Acknowledgement

This project is based on the assignments in Prof. Samuel Madden's 6.830 class at MIT.

 
Project goal

In this project, you will write a basic database management system called SimpleDB. First, you will implement the core modules required to access stored data on disk. You will then write a set of operators for SimpleDB to implement selections and joins. The end result is a database system that can perform simple queries over multiple tables. You will not be asked to add transactions, locking, and concurrent queries. However, we invite you to think how you can add such functionality into the system.

SimpleDB is written in Java. You have been provided with a set of mostly unimplemented classes and interfaces. You will need to write the code for these classes. Your code will be graded by running a set of system tests written using JUnit. You are also provided with a number of unit tests, which will not be used for grading but that you may find useful in verifying that your code works. Note that the provided unit tests are to help guide your implementation along, but they are not intended to be comprehensive or to establish correctness.

The remainder of this document describes the basic architecture of SimpleDB and gives some suggestions about how to start coding.

We strongly recommend that you start as early as possible on this project. It requires you to write a fair amount of code!

 
0. Find bugs, be patient, earn candy bars

SimpleDB is a relatively complex piece of code. It is very possible you are going to find bugs, inconsistencies, and bad, outdated, or incorrect documentation, etc.

You are asked, therefore, to do this project with an adventurous mindset. Do not get mad if something is not clear, or even wrong; rather, try to figure it out yourself or send us a friendly email. We promise to help out by posting bug fixes, new tarballs, etc., as bugs and issues are reported.

1. Getting started

These instructions are written for any Unix-based platform (e.g., Linux, MacOS, etc.). Because the code is written in Java, it should work under Windows as well, though the directions in this document may not apply.

We have included Section 1.2 on using the project with Eclipse. Using Eclipse is recommended, especially if you are on Windows.

Download the code from here and untar it. In linux:

$ wget http://www.cs.nmsu.edu/~hcao/teaching/cs582/project.tar.gz 
$ tar zxvf project.tar.gz
$ cd project

In Windows, use a program like 7zip (extract project.tar.gz using 7zip to get project.tar, then extract project.tar with 7zip to unpack it into a directory).

SimpleDB uses the Ant build tool to compile the code and run tests. Ant is similar to make, but the build file is written in XML and is somewhat better suited to Java code. Most modern Linux distributions include Ant. The servers in our CS department already have Ant installed.

To help you during development, we have provided a set of unit tests in addition to the end-to-end tests that are used for grading. These are by no means comprehensive, and you should not rely on them exclusively to verify the correctness of your project.

To run the unit tests use the test build target:

$ cd project
$ # run all unit tests
$ ant test
$ # run a specific unit test
$ ant runtest -Dtest=TupleTest

You should see output similar to:

runtest:
    [junit] Running simpledb.TupleTest
    [junit] Testsuite: simpledb.TupleTest
    [junit] Tests run: 3, Failures: 2, Errors: 1, Skipped: 0, Time elapsed: 0.02 sec
    [junit] Tests run: 3, Failures: 2, Errors: 1, Skipped: 0, Time elapsed: 0.02 sec
    [junit] 

The output above indicates that two failures and one error occurred during compilation; this is because the code we have given you doesn't yet work. As you complete parts of the parject, you will work towards passing additional unit tests. If you wish to write new unit tests as you code, they should be added to the test/simpledb directory.

For more details about how to use Ant, see the manual. The Running Ant section provides details about using the ant command. However, the quick reference table below should be sufficient for working on the project.

Command	Description
ant	Build the default target (for simpledb, this is dist).
ant -projecthelp	List all the targets in build.xml with descriptions.
ant dist	Compile the code in src and package it in dist/simpledb.jar.
ant test	Compile and run all the unit tests.
ant runtest -Dtest=testname	Run the unit test named testname.
ant systemtest	Compile and run all the system tests.
ant runsystest -Dtest=testname	Compile and run the system test named testname.

1.1. Running end-to-end tests

We have also provided a set of end-to-end tests that will eventually be used for grading. These tests are structured as JUnit tests that live in the test/simpledb/systemtest directory. To run all the system tests, use the systemtest build target:

 

  
$ ant systemtest

# ... build output ...

systemtest:

[junit] Running simpledb.systemtest.ScanTest
  [junit] Testsuite: simpledb.systemtest.ScanTest
  [junit] Tests run: 3, Failures: 0, Errors: 3, Time elapsed: 0.237 sec
  [junit] Tests run: 3, Failures: 0, Errors: 3, Time elapsed: 0.237 sec
  [junit] 
  [junit] Testcase: testSmall took 0.017 sec
  [junit] 	Caused an ERROR
  [junit] implement this
  [junit] java.lang.UnsupportedOperationException: implement this
  [junit] 	at simpledb.HeapFile.id(HeapFile.java:46)
  [junit] 	at simpledb.systemtest.SystemTestUtil.matchTuples(SystemTestUtil.java:90)
  [junit] 	at simpledb.systemtest.SystemTestUtil.matchTuples(SystemTestUtil.java:83)
  [junit] 	at simpledb.systemtest.ScanTest.validateScan(ScanTest.java:30)
  [junit] 	at simpledb.systemtest.ScanTest.testSmall(ScanTest.java:41)
    
# ... more error messages ...

This indicates that this test failed, showing the stack trace where the error was detected. To debug, start by reading the source code where the error occurred. When the tests pass, you will see something like the following:

$ ant systemtest

# ... build output ...

    [junit] Testsuite: simpledb.systemtest.ScanTest
    [junit] Tests run: 3, Failures: 0, Errors: 0, Time elapsed: 7.278 sec
    [junit] Tests run: 3, Failures: 0, Errors: 0, Time elapsed: 7.278 sec
    [junit] 
    [junit] Testcase: testSmall took 0.937 sec
    [junit] Testcase: testLarge took 5.276 sec
    [junit] Testcase: testRandom took 1.049 sec

BUILD SUCCESSFUL
Total time: 52 seconds

 
1.1.1 Creating dummy tables


It is likely you'll want to create your own tests and your own data tables to test your own implementation of SimpleDB. You can create any .txt file and convert it to a .dat file in SimpleDB's HeapFile format using the command:

$ ant dist

$ java -jar dist/simpledb.jar convert file.txt N

where file.txt is the name of the file and N is the number of columns in the file. Notice that file.txt has to be in the following format:

int1,int2,...,intN
int1,int2,...,intN
int1,int2,...,intN
int1,int2,...,intN

...where each intN is a non-negative integer.

To view the contents of a table, use the print command. Note that this command will not work until later in the project:

$ java -jar dist/simpledb.jar print file.dat N

where file.dat is the name of a table created with the convert command, and N is the number of columns in the file.
(Note: you will see errors if you did not implement the DbFileIterator yet.)

1.2. Working in Eclipse

Eclipse is a graphical software development environment that you might be more comfortable with working in. The instructions we provide were generated by using Eclipse 3.4.0 (Ganymede) for Java Developers (not the enterprise edition) with Java 1.5.0_13 on Ubuntu 7.10. They should also work under Windows or on MacOS.

Setting the Project Up in Eclipse

    Once Eclipse is installed, start it, and note that the first screen asks you to select a location for your workspace (we will refer to this directory as $W).
    On the file system, copy project.tar.gz to $W/project.tar.gz. Un-GZip and un-tar it, which will create a directory $W/project (to do this, you can type tar -pzxvf project.tar.gz).
    With Eclipse running, select File->New->Project->Java->Java Project, and push Next ( You may also be able to do directly: File->New->Java Project).
    Enter "project" as the project name.
    On the same screen that you entered the project name, select "Create project from existing source," and browse to $W/project.
    Click Finish, and you should be able to see "project" as a new project in the Project Explorer tab on the left-hand side of your screen (if you just installed Eclipse, make sure to close the "Welcome" window). Opening this project reveals the directory structure discussed above - implementation code can be found in "src," and unit tests and system tests found in "test."

Running Individual Unit and System Tests

To run a unit test or system test (both are JUnit tests, and can be initialized the same way), go to the Package Explorer tab on the left side of your screen. Under the "project" project, open the "test" directory. Unit tests are found in the "simpledb" package, and system tests are found in the "simpledb.systemtests" package. To run one of these tests, select the test (they are all called *Test.java - don't select TestUtil.java or SystemTestUtil.java), right click on it, select "Run As," and select "JUnit Test." This will bring up a JUnit tab, which will tell you the status of the individual tests within the JUnit test suite, and will show you exceptions and other errors that will help you debug problems.

Running Ant Build Targets

If you want to run commands such as "ant test" or "ant systemtest," right click on build.xml in the Package Explorer. Select "Run As" and then "Ant Build..." (note: select the option with the ellipsis (...), otherwise you won't be presented with a set of build targets to run). Then, in the "Targets" tab of the next screen, check off the targets you want to run (probably "dist" and one of "test" or "systemtest"). This should run the build targets and show you the results in Eclipse's console window.

1.3. Implementation hints

Before beginning to write code, we strongly encourage you to read through this entire document to get a feel for the high-level design of SimpleDB.

You will need to fill in any piece of code that is not implemented. It will be obvious where we think you should write code. You may need to add private methods and/or helper classes. You may change APIs, but make sure our grading tests still run and make sure to mention, explain, and defend your decisions in your writeup.

In addition to the methods that you need to fill out for this project, the class interfaces contain numerous methods that you need not implement in this project. These will either be indicated per class:

// Not necessary for this project
public class Insert implements DbIterator {

or per method:

public boolean deleteTuple(Tuple t) throws DbException {

  // Not necessary for this project
  return false;
}

The code that you submit should compile without having to modify these methods.

We suggest exercises along this document to guide your implementation, but you may find that a different order makes more sense for you. Here's a rough outline of one way you might proceed with your SimpleDB implementation:

    Implement the classes to manage tuples, namely Tuple, TupleDesc. We have already implemented Field, IntField, StringField, and Type for you. Since you only need to support integer and (fixed length) string fields and fixed length tuples, these are straightforward.
    Implement the Catalog (this should be very simple).
    Implement the BufferPool constructor and the getPage() method.
    Implement the access methods, HeapPage and HeapFile and associated ID classes. A good portion of these files has already been written for you.
    Implement the operator SeqScan.
    At this point, you should be able to pass the ScanTest system test.
    Implement the operators Filter and Join and verify that their corresponding tests work. The Javadoc comments for these operators contain details about how they should work. We have given you implementations of Project and OrderBy which may help you understand how other operators work.

At this point you should be able to pass all of the tests in the ant systemtest target, which is the goal of this project. Section 2 below walks you through these implementation steps and the unit tests corresponding to each one in more detail.

 
1.4. Transactions, locking, and recovery

As you look through the interfaces that we have provided you, you will see a number of references to locking, transactions, and recovery. You do not need to support these features. We will not be implementing this part of SimpleDB this semester. It is there because we may use it in subsequent semesters.The test code we have provided you with generates a fake transaction ID that is passed into the operators of the query it runs; you should pass this transaction ID into other operators and the buffer pool.

2. SimpleDB Architecture and Implementation Guide

SimpleDB consists of:

    Classes that represent fields, tuples, and tuple schemas;
    Classes that apply predicates and conditions to tuples;
    One or more access methods (e.g., heap files) that store relations on disk and provide a way to iterate through tuples of those relations;
    A collection of operator classes (e.g., select, join, insert, delete, etc.) that process tuples;
    A buffer pool that caches active tuples and pages in memory and handles concurrency control and transactions (neither of which you need to worry about for this project); and,
    A catalog that stores information about available tables and their schemas.

SimpleDB does not include many things that you may think of as being a part of a "database." In particular, SimpleDB does not have:

    Views.
    Data types except integers and fixed length strings.
    Query optimizer.
    Indices.

In the rest of this Section, we describe each of the main components of SimpleDB that you will need to implement in this project. You should use the exercises in this discussion to guide your implementation. This document is by no means a complete specification for SimpleDB; you will need to make decisions about how to design and implement various parts of the system.

2.1. The Database Class

The Database class provides access to a collection of static objects that are the global state of the database. In particular, this includes methods to access the catalog (the list of all the tables in the database), the buffer pool (the collection of database file pages that are currently resident in memory), and the log file. You will not need to worry about the log file in this project. We have implemented the Database class for you. You should take a look at this file as you will need to access these objects.

2.2. Fields and Tuples

Tuples in SimpleDB are quite basic. They consist of a collection of Field objects, one per field in the Tuple. Field is an interface that different data types (e.g., integer, string) implement. Tuple objects are created by the underlying access methods (e.g., heap files, or B-trees), as described in the next section. Tuples also have a type (or schema), called a tuple descriptor, represented by a TupleDesc object. This object consists of a collection of Type objects, one per field in the tuple, each of which describes the type of the corresponding field.

Exercise 1. Implement the skeleton methods in:

   src/simpledb/TupleDesc.java
   src/simpledb/Tuple.java

At this point, your code should pass the unit tests TupleTest and TupleDescTest.
2.3. Catalog

The catalog (class Catalog in SimpleDB) consists of a list of the tables and schemas of the tables that are currently in the database. You will need to support the ability to add a new table, as well as getting information about a particular table. Associated with each table is a TupleDesc object that allows operators to determine the types and number of fields in a table.

To learn more about catalogs in a DBMS, take a look at Section 12.1 of the R&G book.

The global catalog is a single instance of Catalog that is allocated for the entire SimpleDB process. The global catalog can be retrieved via the method Database.getCatalog(), and the same goes for the global buffer pool (using Database.getBufferPool()).


Exercise 2. Implement the skeleton methods in:

   src/simpledb/Catalog.java

At this point, your code should pass the unit tests in CatalogTest.
2.4. BufferPool

The buffer pool (class BufferPool in SimpleDB) is responsible for caching pages in memory that have been recently read from disk. All operators read and write pages from various files on disk through the buffer pool. It consists of a fixed number of pages, defined by the numPages parameter to the BufferPool constructor. You only need to implement (i) the constructor and (ii) the BufferPool.getPage() method used by the SeqScan operator. The BufferPool should store up to numPages pages. For this project we will not be implementing an eviction policy. If more than numPages requests are made for different pages, then instead of implementing an eviction policy, you may throw a DbException.

The class Database class provides a static method, Database.getBufferPool(), which returns a reference to the single BufferPool instance for the entire SimpleDB process.

Exercise 3. Implement the getPage() method in:

   src/simpledb/BufferPool.java

We have not provided unit tests for BufferPool. The functionality you implemented will be tested in the implementation of HeapFile below. You should use the DbFile.readPage method to access pages of a DbFile.

2.5. HeapFile access method


Access methods provide a way to read or write data from disk that is arranged in a specific way. Common access methods include heap files (unsorted files of tuples) and B-trees; for this project, you will only implement a heap file access method, and some of the code has been provided to you.

A HeapFile object is arranged into a set of pages, each of which consists of a fixed number of bytes for storing tuples, (defined by the constant BufferPool.PAGE_SIZE), plus a header. In SimpleDB, there is one HeapFile object for each table in the database. Each page in a HeapFile is arranged as a set of slots, each of which can hold one tuple (tuples for a given table in SimpleDB are all of the same size). In addition to these slots, each page has a header that consists of a bitmap with one bit per tuple slot. If the bit corresponding to a particular tuple is 1, it indicates that the tuple is valid; if it is 0, the tuple is invalid (e.g., has been deleted or was never initialized). Pages of HeapFile objects are of type HeapPage which implements the Page interface. Pages are stored in the buffer pool but are read and written by the HeapFile class.

SimpleDB stores heap files on disk in more or less the same format they are stored in memory. Each file consists of page data arranged consecutively on disk. Each page consists of one or more 32-bit integers representing the header, followed by the BufferPool.PAGE_SIZE bytes of actual page content. The number of 32-bit integers in the header is defined by the formula:

((BufferPool.PAGE_SIZE / tuple size) / 32 ) +1 )

Where tuple size is the size of a tuple in the page in bytes.

The low (least significant) bits of each integer represent the status of the slots that are earlier in the file. Hence, the lowest bit of the first integer represents whether or not the first slot in the page is in use. Also, note that the high-order bits of the last such integer may not correspond to a slot that is actually in the file, since the number of slots may not be a multiple of 32. Also note that all Java virtual machines are big-endian.

The page content of each page consists of floor(BufferPool.PAGE_SIZE/tuple size) tuple slots, where the 0-indexed ith slot begins i * tuple size bytes into the page.

Exercise 4. Implement the skeleton methods in:

   src/simpledb/HeapPageId.java
   src/simpledb/HeapPage.java

Although you will not use them directly in this project, we ask you to implement getNumEmptySlots() and getSlot() in HeapPage. These require pushing around bits in the page header. You may find it helpful to look at the other methods that have been provided in HeapPage or in src/simpledb/HeapFileEncoder.java to understand the layout of pages.

You will also need to implement an Iterator over the tuples in the page, which may involve an auxiliary class or data structure.

At this point, your code should pass the unit tests in HeapPageIdTest, RecordIDTest, and HeapPageReadTest.

After you have implemented HeapPage, you will write methods for HeapFile in this project to calculate the number of pages in a file and to read a page from the file. You will then be able to fetch tuples from a file stored on disk.

Exercise 5. Implement the skeleton methods in:

   src/simpledb/HeapFile.java

To read a page from disk, you will first need to calculate the correct offset in the file. Hint: you will need random access to the file in order to read and write pages at arbitrary offsets.

You will also need to implement the HeapFile.iterator() method, which should iterate through the tuples of each page in the HeapFile. The iterator must use the BufferPool.getPage() method to access pages in the HeapFile. This method loads the page into the buffer pool and will eventually be used to implement locking-based concurrency control and recovery (which is not required in this project).

At this point, your code should pass the unit tests in HeapFileReadTest.

2.6. Operators

Operators are responsible for the actual execution of the query plan. They implement the operations of the relational algebra. In SimpleDB, operators are iterator based; each operator implements the DbIterator interface.

Operators are connected together into a plan by passing lower-level operators into the constructors of higher-level operators, i.e., by 'chaining them together.' Special access method operators at the leaves of the plan are responsible for reading data from the disk (and hence do not have any operators below them).

At the top of the plan, the program interacting with SimpleDB simply calls getNext on the root operator; this operator then calls getNext on its children, and so on, until these leaf operators are called. They fetch tuples from disk and pass them up the tree (as return arguments to getNext); tuples propagate up the plan in this way until they are output at the root or combined or rejected by another operator in the plan.

Exercise 6. Implement the skeleton methods in:

   src/simpledb/SeqScan.java

This operator sequentially scans all of the tuples from the pages of the table specified by the tableid in the constructor. This operator should access tuples through the DbFile.iterator() method.

At this point, you should be able to complete the ScanTest system test.
2.7. Filter and Join

Recall that SimpleDB DbIterator classes implement the operations of the relational algebra. You will now implement two operators that will enable you to perform queries that are slightly more interesting than a table scan.

    Filter: This operator only returns tuples that satisfy a Predicate that is specified as part of its constructor. Hence, it filters out any tuples that do not match the predicate.
    Join: This operator joins tuples from its two children according to a JoinPredicate that is passed in as part of its constructor. We only require a simple nested loops join, but you may explore more interesting join implementations. Describe your implementation in your project writeup.

Exercise 1. Implement the skeleton methods in:

    src/simpledb/Predicate.java
    src/simpledb/JoinPredicate.java
    src/simpledb/Filter.java
    src/simpledb/Join.java

At this point, your code should pass the unit tests in PredicateTest, JoinPredicateTest, FilterTest, and JoinTest. Furthermore, you should be able to pass the system tests FilterTest and JoinTest.
2.8. Aggregates (bonus)

An additional SimpleDB operator implements basic SQL aggregates with a GROUP BY clause. You should implement the five SQL aggregates (COUNT, SUM, AVG, MIN, MAX) and support grouping. You only need to support aggregates over a single field, and grouping by a single field.

In order to calculate aggregates, we use an Aggregator interface which merges a new tuple into the existing calculation of an aggregate. The Aggregator is told during construction what operation it should use for aggregation. Subsequently, the client code should call Aggregator.merge() for every tuple in the child iterator. After all tuples have been merged, the client can retrieve a DbIterator of aggregation results. Each tuple in the result is a pair of the form (groupValue, aggregateValue), unless the value of the group by field was Aggregator.NO_GROUPING, in which case the result is a single tuple of the form (aggregateValue).

Note that this implementation requires space linear in the number of distinct groups. For the purposes of this project, you do not need to worry about the situation where the number of groups exceeds available memory.

Exercise 2. Implement the skeleton methods in:

    src/simpledb/IntAggregator.java
    src/simpledb/StringAggregator.java
    src/simpledb/Aggregate.java

At this point, your code should pass the unit tests IntAggregatorTest, StringAggregatorTest, and AggregateTest. Furthermore, you should be able to pass the AggregateTest system test.

You have now completed this project. Good work!

The section below shows how to use the system that you just wrote as a DBMS by issuing SQL queries. It is optional, but can serve as additional testing to verify that your system is working.
2.9. Query Parser (optional)
all the materials in this section is optional and is just left for you to have more fun!!

We've provided you with a query parser for SimpleDB that you can use to write and run SQL queries against your database once you have completed the exercises in this project.

The first step is to create some data tables and a catalog. Suppose you have a file data.txt with the following contents:

1,10
2,20
3,30
4,40
5,50
5,50

You can convert this into a SimpleDB table using the convert command (make sure to type ant first!):

java -jar dist/simpledb.jar convert data.txt 2 "int,int"

This creates a file data.dat. In addition to the table's raw data, the two additional parameters specify that each record has two fields and that their types are int and int.

Next, create a catalog file, catalog.txt, with the follow contents:

data (f1 int, f2 int)

This tells SimpleDB that there is one table, data (stored in data.dat) with two integer fields named f1 and f2.

Finally, invoke the parser. You must run java from the command line (ant doesn't work properly with interactive targets.)

java -jar dist/simpledb.jar parser catalog.txt

You should see output like:

Added table : data with schema INT(f1), INT(f2), 
SimpleDB> 

Finally, you can run a query:

SimpleDB> select d.f1, d.f2 from data d;
Started a new transaction tid = 1221852405823
 ADDING TABLE d(data) TO tableMap
     TABLE HAS  tupleDesc INT(d.f1), INT(d.f2), 
1       10
2       20
3       30
4       40
5       50
5       50

 6 rows.
----------------
0.16 seconds

SimpleDB> 

The parser is relatively full featured (including support for SELECTs, INSERTs, DELETEs, and transactions), but does have some problems and does not necessarily report completely informative error messages. Here are some limitations to bear in mind:

    You must preface every field name with its table name, even if the field name is unique (you can use table name aliases, as in the example above, but you cannot use the AS keyword.)
    Nested queries are supported in the WHERE clause, but not the FROM clause.
    No arithmetic expressions are supported (for example, you can't take the sum of two fields.)
    At most one GROUP BY and one aggregate column are allowed.
    Set-oriented operators like IN, UNION, and EXCEPT are not allowed.
    Only AND expressions in the WHERE clause are allowed.
    UPDATE expressions are not supported.
    The string operator LIKE is allowed, but must be written out fully (that is, the Postgres tilde [~] shorthand is not allowed.)

Test your DBMS (Optional)

We have built SimpleDB-encoded version of the DBLP database you used in problem set 1; the needed files are located at: dblp_data.tar.gz

You should unpack the file in your project directory. It will create four files in the dblp_data sub-directory. Use "tar zxvf dblp_data.tar.gz" on linux, or a program like 7zip in Windows (extract dblp_data.tar.gz using 7zip to get dblp_data.tar, then extract dblp_data.tar with 7zip to unpack it into a directory).

You can then run the parser with:

cd dblp_data
java -jar ../dist/simpledb.jar parser papers.schema
  

Now you can test your SimpleDB with those queries:

    SELECT p.title
    FROM papers p
    WHERE p.title LIKE 'Coffee';
      

    SELECT p.title
    FROM papers p, pa, auths a
    WHERE a.name = 'Samuel Madden'
    AND a.aid = pa.authid
    AND pa.paperid = p.pid;
      

    SELECT a2.name, count(p.title) 
    FROM auths a1, auths a2, pa pa1, pa pa2, papers p 
    WHERE a1.name = 'Samuel Madden' 
    AND a1.aid = pa1.authid 
    AND pa1.paperid = pa2.paperid 
    AND pa2.authid = a2.aid 
    AND pa2.paperid = p.pid
    GROUP BY a2.name
    ORDER BY a2.name;
      

Depending on the efficiency of your implementation, each of these queries will take seconds to minutes to run to completion, outputting tuples as they are computed. Certainly don't expect the level of performance of postgres. :)

Turn in instructions

You must submit your code (see below) as well as a short (2 pages, maximum) writeup describing your approach. This writeup should:

    Describe any design decisions you made. For example any class or complex data structure you add to the project. If you used something other than a nested-loops join, describe the tradeoffs of the algorithm you chose.
    Discuss and justify any changes you made to the API.
    Describe any missing or incomplete elements of your code.
    Describe how long you spent on the project, and whether there was anything you found particularly difficult or confusing.

To submit your code, please create a project.tar.gz tarball (such that, untarred, it creates a project/src/simpledb directory with your code) and submit it through Canvas.

You may submit your code multiple times; we will use the latest version you submit that arrives before the deadline.
Please also include your writeup as a PDF or text file in the submission package.
Submitting a bug

Please submit (friendly!) bug reports to the instructor. When you do, please try to include:

    A description of the bug.
    A .java file we can drop in the test/simpledb directory, compile, and run.
    A .txt file with the data that reproduces the bug. We should be able to convert it to a .dat file using HeapFileEncoder.


Grading

70% of your grade will be based on whether or not your code passes the system test suite we will run over it. These tests will be a superset of the tests we have provided. Before handing in your code, you should make sure it produces no errors (passes all of the tests) from both ant test and ant systemtest. (If you did not work on the Aggregates bonus exercise, the tests related to aggregation will fail, which will not cause any point deduction.)

Important: before testing, we will replace your build.xml, HeapFileEncoder.java, and the entire contents of the test directory with our version of these files. This means you cannot change the format of .dat files! You should also be careful changing our APIs. You should test that your code compiles the unmodified tests. In other words, we will untar your tarball, replace the files mentioned above, compile it, and then grade it. It will look roughly like this:

$ tar xvzf project.tar.gz

$ cd ./project
[replace build.xml, HeapFileEncoder.java, and test]

$ ant test
$ ant systemtest

[additional tests]

An additional 30% of your grade will be based on the quality of your writeup and our subjective evaluation of your code.

I hope you will enjoy this project and will learn a lot about how a simple DBMS system can be implemented!
