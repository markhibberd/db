package io.mth.db

import DbValue._
import Ddl._
import Query._
import Variable._
import Connector._
import ResultBuilder._

object Demo {
  case class Album(name: String, year: Int)

  def AlbumBuilder = build(rs =>
    for {
      name <- getString(rs, "name")
      year <- getInt(rs, "year")
    } yield Album(name, year))

  val table = ddl("CREATE TABLE ALBUM(name VARCHAR(100), year INT)", List())

  val data = List(
    ("The Piper at the Gates of Dawn", 1967),
    ("A Saucerful of Secrets", 1968),
    ("Ummagumma", 1969),
    ("Atom Heart Mother", 1970),
    ("Meddle", 1971),
    ("The Dark Side of the Moon", 1973),
    ("Wish You Were Here", 1975),
    ("Animals", 1977),
    ("The Wall", 1979),
    ("The Final Cut", 1983),
    ("A Momentary Lapse of Reason", 1987),
    ("The Division Bell", 1994)
  ) map ({
    case (name, year) =>
      ddl("INSERT INTO ALBUM(name, year) VALUES (?, ?)", List(string(name), int(year)))
  })

  val before1980 = query("SELECT * FROM ALBUM WHERE YEAR < ?", List(int(1980)), eachRow(_, AlbumBuilder))

  val demo = for {
    _ <- executeUpdate(table)
    _ <- sequenceConnector(data map (executeUpdate(_)))
    albums <- executeQuery(before1980)
  } yield albums

  def main(args: Array[String]): Unit = {
    val connection = Connect.hsqltest
    val result = demo.connect(connection)
    val albums = result.orDie
    println(albums.mkString(",\n"))
  }
}