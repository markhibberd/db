package io.mth.db

import java.sql.SQLException
import io.mth.db.DbValue._

trait DbValue[A] {
  def fold[X](
    err: SQLException => X,
    nul: => X,
    value: A => X
  ): X

  def map[B](f: A => B): DbValue[B] =
    fold(err(_), nul, v => value(f(v)))

  def flatMap[B](f: A => DbValue[B]): DbValue[B] =
    fold(err(_), nul, f(_))

  def orElse(a: => A) = fold(_ => a, a, v => v)

  def orDie = fold(throw _, error("database null encountered"), v => v)
}

object DbValue {
  def err[A](e: SQLException): DbValue[A] = new DbValue[A] {
    def fold[X](
      err: SQLException => X,
      nul: => X,
      value: A => X
    ) = err(e)
  }

  def nul[A]: DbValue[A] = new DbValue[A] {
    def fold[X](
      err: SQLException => X,
      nul: => X,
      value: A => X
    ) = nul
  }

  def value[A](v: A): DbValue[A] = new DbValue[A] {
    def fold[X](
      err: SQLException => X,
      nul: => X,
      value: A => X
    ) = value(v)
  }

  def sequenceValue[A](values: List[DbValue[A]]): DbValue[List[A]] = values match {
    case Nil => value(Nil)
    case h :: t => for {
      d <- h
      ds <- sequenceValue(t)
    } yield d :: ds
  }
}