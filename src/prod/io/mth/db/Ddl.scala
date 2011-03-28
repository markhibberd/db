package io.mth.db

trait Ddl {
  def fold[X](
    d: (String, List[Variable]) => X
  ): X

  def sql = fold((s, _) => s)

  def vars = fold((_, vars) => vars)
}

object Ddl {
  def ddl(statement: String, data: List[Variable]): Ddl = new Ddl {
    def fold[X](
      d: (String, List[Variable]) => X
    ) = d(statement, data)
  }
}