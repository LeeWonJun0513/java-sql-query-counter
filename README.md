# java-sql-query-counter

## What's all this then?

The snappily named `java-sql-query-counter` shows SQL query
information for your web applications.

It uses the JVM's instrumentation API to hook in at the JDBC level,
recording the number of statements executed against the database.  It
also hooks in at the servlet container level to aggregate and report
the queries executed for each HTTP request.

Currently only MySQL is supported, although adding support for other
databases should be fairly straightforward.  On the servlet container
side, both Tomcat and Jetty should work.


## What's it look like?

We log each query performed, prefixed by `[SQL]`, along with a
`[STACK]` entry showing a fairly short stack trace of where in the
code the query was performed.  Finally, each request lists the total
number of queries performed.  Putting it all together:

    Thread[http-bio-7575-exec-7,5,main] [SQL]> com.mysql.jdbc.PreparedStatement.executeQuery: com.mysql.jdbc.JDBC4PreparedStatement@fc7086c: select GROUP_ID, NAME, VALUE from SAKAI_SITE_GROUP_PROPERTY where ( SITE_ID = x'2167617465776179' )                                                                                                   
    [STACK]> impl.BasicSqlService.dbRead:553 impl.BasicSqlService.dbRead:471 impl.DbSiteService$DbStorage.readSiteGroupProperties:2082 impl.DbSiteService$DbStorage.readAllSiteProperties:1971 impl.BaseSite.loadAll:1159 impl.BaseSiteService.getDefinedSite:665 impl.BaseSiteService.getSite:740 tool.SkinnableLogin.getPasswordResetUrl:543 tool.SkinnableLogin.startPageContext:409 tool.SkinnableLogin.doGet:241                                                                                                      
    Thread[http-bio-7575-exec-7,5,main] [TOTAL]> /portal/login - SQL queries executed: 24



## Running it

Build it by running `make` (yep!) and then run your servlet container
with an extra parameter in your `JAVA_OPTS`:

    -javaagent:/path/to/the/build/of/java-sql-query-counter.jar

That's it!  It may not be production-ready or well-tested (it isn't),
but it's at least easy to install!

