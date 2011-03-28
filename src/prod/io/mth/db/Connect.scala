package io.mth.db

import java.sql._

object Connect {
  def hsqltest = hsqlmem("testdb", "sa", "")

  def hsqlmem(dbname: String, username: String, password: String) =
    mkConnection(
      "org.hsqldb.jdbcDriver",
      "jdbc:hsqldb:mem:" + dbname,
      username, password
    )

  def hsqlfile(dbfile: String, username: String, password: String) =
    mkConnection(
      "org.hsqldb.jdbcDriver",
      "jdbc:hsqldb:file:" + dbfile,
      username, password
    )

  def mkConnection(driver: String, url: String, username: String, password: String): Connection = {
    Class.forName(driver)
    DriverManager.getConnection(url, username, password)
  }
}