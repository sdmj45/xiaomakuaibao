package com.mj.xiaomakuaibao

trait Module {
  val lastUpdatedFile: String

  def run: Unit
}
