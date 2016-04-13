package com.persist

import sbt._
import Keys.doc
import Keys.dependencyClasspath
import Keys.scalaVersion
import Keys.Classpath
import complete.DefaultParsers._
import java.io.File

object View extends Plugin {

  private case class pinfo(path: String, prefix: String, ver: String, pack: String, id: String)

  private val osName = System.getProperty("os.name").toLowerCase()

  private def parsePath(path: String) = {
    val i = path.replace("-SNAPSHOT", "*SNAPSHOT").lastIndexOf("-")
    val j = path.replace("/bundles/", "/jars/").indexOf("/jars/")
    val prefix = path.substring(0, j)
    val parts = prefix.split("/")
    val pack = parts(parts.size - 2).replace(".scala", "").replace(".", "/")
    val ver = path.substring(i + 1).replace(".jar", "")
    val id = s"${parts(parts.size - 1)}-$ver"
    pinfo(path, prefix, ver, pack, id)
  }

  private def open(path: String): Unit = {
    val cmd = osName match {
      case "linux" => "xdg-open"
      case _ => "open"
    }
    s"$cmd $path".!
    println(s"opening browser window for $path")
  }

  private def showProjectDoc(sver:String, genDoc: ()=> Unit): Unit = {
    val path = s"target/scala-$sver/api/index.html"
    genDoc()
    open(path)
  }

  private def viewAct(args: Seq[String], v: Classpath, sver:String, genDoc: ()=> Unit) {
    if (args.size == 0) {
      showProjectDoc(sver, genDoc)
    } else {
      val paths = v.files map (_.getPath)
      val selected = paths filter {
        path => args.forall(path.contains(_))
      } filter (_.endsWith(".jar")) map (parsePath(_))
      if (selected.size == 0) {
        println("Nothing selected")
      } else if (selected.size > 1) {
        println("Too many selected")
        for ((pp, i) <- selected.zipWithIndex) {
          println(s"[${i + 1}] $pp")
        }
      } else {
        val select = selected(0)
        val out = s"${select.prefix}/docs/html-${select.ver}"
        val outf = new File(out)
        val in = s"${select.prefix}/docs/${select.id}-javadoc.jar"
        val inf = new File(in)
        if (!outf.exists) {
          if (inf.exists) {
            outf.mkdir()
            val cmd = osName match {
              case "linux" => s"unzip $in -d $out"
              case _ => s"tar -xf $in -C $out"
            }
            cmd.!
          } else {
            println(s"${in} not present")
          }
        }
        if (outf.exists) {
          val index = s"${out}/index.html"
          open(index)
        }
      }
    }
  }

  val view = InputKey[Unit]("view", "view from doc jar")

  val view1 = view := {
    val args = spaceDelimited("<arg>").parsed
    val cp = (dependencyClasspath in Runtime).value ++ (dependencyClasspath in Test).value ++ (dependencyClasspath in Compile).value
    def genDoc():Unit  = (doc in Compile).value
    val sver = scalaVersion.value
    val sver1 = sver.split("[.]").dropRight(1).mkString(".")
    viewAct(args, cp, sver1, genDoc)
  }

  lazy val viewSettings = Seq(view1)
}

