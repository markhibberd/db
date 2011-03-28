package io.mth.db

trait Variable {
  def fold[X](
    s: String => X,
    i: Int => X
  ): X
}

object Variable {
  def string(value: String): Variable = new Variable {
    def fold[X](
      s: String => X,
      i: Int => X
    ) = s(value)
  }

  def int(value: Int): Variable = new Variable {
    def fold[X](
      s: String => X,
      i: Int => X
    ) = i(value)
  }
}