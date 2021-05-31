package org.twelvehart.lib.lsystem

import cats.data.State
import Rule._

case class LSystem(axiom: String, ruleset: RuleSet, generation: Int = 0) {
  private val ruleMap                   = ruleset.rules.map(r => r.symbol -> r).toMap
  private val transform: Char => String = ch => ruleMap.get(ch).fold(ruleset.defaultRule(ch))(_.transform)
}

object LSystem {
  import ActionType._

  val penroseRuleSet: RuleSet = RuleSet(
    List(
      Rule('W', "YF++ZF4-XF[-YF4-WF]++"),
      Rule('X', "+YF--ZF[3-WF--XF]+"),
      Rule('Y', "-WF++XF[+++YF++ZF]-"),
      Rule('Z', "--YF++++WF[+ZF++++XF]--XF")
    ),
    {
      case 'F' => Repeat(And(Negate(Draw), Negate(Translate)))
      case '+' => Repeat(Rotate)
      case '-' => Repeat(Negate(Rotate))
      case '[' => SaveState
      case ']' => RestoreState
      case _   => NoOp
    },
    dropF
  )

  val treeRuleSet: RuleSet = RuleSet(
    List(Rule('F', "FF+[+F-F-F]-[-F+F]")),
    {
      case 'F' => And(Draw, Translate)
      case '+' => Rotate
      case '-' => Negate(Rotate)
      case '[' => SaveState
      case ']' => RestoreState
      case _   => NoOp
    }
  )

  val serpinksiRuleSet: RuleSet = RuleSet(
    List(Rule('F', "FF+[+F-F-F]-[-F+F]"), Rule('G', "GG")),
    {
      case 'F' | 'G' => And(Draw, Translate)
      case '+'       => Rotate
      case '-'       => Negate(Rotate)
      case '['       => SaveState
      case ']'       => RestoreState
      case _         => NoOp
    }
  )

  val tree: LSystem      = LSystem("F", treeRuleSet)
  val penrose: LSystem   = LSystem("[X]++[X]++[X]++[X]++[X]", penroseRuleSet)
  val serpinski: LSystem = LSystem("F--F--F", serpinksiRuleSet)

  def generate: State[LSystem, String] =
    State { sys =>
      val (ax, gen) =
        (sys.axiom.map(sys.transform).mkString(""), sys.generation + 1)

      (sys.copy(axiom = ax, generation = gen), ax)
    }

  def simulate(current: LSystem, n: Int): LSystem =
    (1 to n).foldLeft(current)((state, _) => LSystem.generate.runS(state).value)
}
