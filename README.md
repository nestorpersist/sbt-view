# Scaladoc/JavaDoc Viewer SBT Plugin

Opens a browser window with Javadoc/Scaladoc
for the current project or its dependencies.


1. In project/plugins.sbt add

     `addSbtPlugin("com.persist" % "sbt-view" % "1.0.1")`

2. In build.sbt add

     viewSettings

3. Make sure that your SBT dependencies include the javadoc jar.
   Here is an example

    `"com.foo" %% "foopackage" % "1.2.3" withJavadoc()`

4. View Scaladoc for current project. Calls doc if none there.

    `view`
5. Finds an item in the classpath where all words are matched.
           Unpack the javadoc jar (if not already done) in the ivy2
           cache and then view it.

    `view WORD1 WORD2 ...`

The plugin is based on a plugin developed by Whitepages [https://github.com/whitepages/WP_Sbt_Plugins](https://github.com/whitepages/WP_Sbt_Plugins)

Development of this plugin was supported by [47 Degrees](http://www.47deg.com/).


