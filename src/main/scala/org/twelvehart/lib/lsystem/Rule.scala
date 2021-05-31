package org.twelvehart.lib.lsystem

import ActionType.ActionType

object Rule {
  val drop: Char => String = _ => ""

  val dropF: Char => String = ch =>
    if (ch == 'F')
      ""
    else
      ch.toString

  case class Rule(symbol: Char, transform: String)

  case class RuleSet(rules: List[Rule], mapper: Char => ActionType, defaultRule: Char => String = drop)

}
