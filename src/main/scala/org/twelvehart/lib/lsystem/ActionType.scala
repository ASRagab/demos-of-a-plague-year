package org.twelvehart.lib.lsystem

object ActionType {

  sealed trait ActionType

  case object Rotate       extends ActionType
  case object Draw         extends ActionType
  case object Translate    extends ActionType
  case object SaveState    extends ActionType
  case object RestoreState extends ActionType
  case object NoOp         extends ActionType

  case class And(action1: ActionType, action2: ActionType) extends ActionType
  case class Negate(actionType: ActionType)                extends ActionType
  case class Repeat(actionType: ActionType)                extends ActionType

}
