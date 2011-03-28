package io.mth.db

import java.sql.ResultSet

trait Query[A] {
  def fold[X](
    q: (String, List[Variable], ResultSet => DbValue[A]) => X
  ): X

  def sql = fold((q, _, _) => q)

  def vars = fold((_, vars, _) => vars)

  def convert = fold((_, _, c) => c)
}

object Query {
  def query[A](query: String, data: List[Variable], action: ResultSet => DbValue[A]): Query[A] = new Query[A] {
    def fold[X](
      q: (String, List[Variable], ResultSet => DbValue[A]) => X
    ) = q(query, data, action)
  }
}