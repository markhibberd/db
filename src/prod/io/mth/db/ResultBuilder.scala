package io.mth.db

import java.sql.ResultSet
import collection.mutable.ListBuffer
import io.mth.db.DbValue._
;

object ResultBuilder {
  def getString(rs: ResultSet, label: String):  DbValue[String] = {
    val r = rs.getString(label)
    if (rs.wasNull)
      nul
    else
      value(r)
  }

  def getInt(rs: ResultSet, label: String):  DbValue[Int] = {
    val r = rs.getInt(label)
    if (rs.wasNull)
      nul
    else
      value(r)
  }

  def build[A](x: ResultSet => DbValue[A]): ResultSet => DbValue[A] = x

  def eachRow[A](rs: ResultSet, consumer: ResultSet => DbValue[A]): DbValue[List[A]] = {
    val buffer = new ListBuffer[DbValue[A]]
    while (rs.next)
      buffer += consumer(rs)
    sequenceValue(buffer.toList)
  }
}