package io.mth.db

import java.sql._
import Connector._
import DbValue._

sealed trait Connector[A] {
  val connect: Connection => DbValue[A]

  def map[B](f: A => B): Connector[B] =
    connector(connect(_) map f)

  def flatMap[B](f: A => Connector[B]): Connector[B] =
    connector(c =>
      connect(c).flatMap(v => f(v) connect c)
    )
}

object Connector {
  def connector[A](f: Connection => DbValue[A]): Connector[A] = new Connector[A] {
    val connect = f
  }

  def safe[A](f: Connection => DbValue[A]): Connector[A] = wrap(connector(f))

  def wrap[A](delegate: Connector[A]): Connector[A] = connector(c =>
    try {
      delegate.connect(c)
    } catch {
      case e: SQLException => err(e)
    }
  )

  def executeUpdate(ddl: Ddl): Connector[Int] =
    safe(c => {
      val stmt = c.prepareStatement(ddl.sql)
      ddl.vars.zipWithIndex.foreach({
        case (b, i) => b.fold(
          stmt.setString(i + 1, _),
          stmt.setInt(i + 1, _)
        )
      })
      val count = stmt.executeUpdate
      value(count)
    })

  def executeQuery[A](query: Query[A]): Connector[A] =
    safe(c => {
      val stmt = c.prepareStatement(query.sql)
      query.vars.zipWithIndex.foreach({
        case (b, i) => b.fold(
          stmt.setString(i + 1, _),
          stmt.setInt(i + 1, _)
        )
      })
      val rs = stmt.executeQuery
      query.convert(rs)
    })

  def sequenceConnector[A](values: List[Connector[A]]): Connector[List[A]] = values match {
    case Nil => connector(_ => value(Nil))
    case h :: t => for {
      d <- h
      ds <- sequenceConnector(t)
    } yield d :: ds
  }
}
