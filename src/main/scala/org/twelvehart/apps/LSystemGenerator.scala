package org.twelvehart.apps

import org.twelvehart.common.ProcessingApp
import org.twelvehart.lib.lsystem.Interpreters.ProcessingInterpreter
import org.twelvehart.lib.lsystem.LSystem
import org.twelvehart.lib.lsystem.LSystem.{penrose, simulate}
import processing.core.PApplet._
import processing.core.PConstants._
import processing.core._

class LSystemGenerator extends ProcessingApp {
  implicit val self: PApplet = this

  val generations                        = 6
  var current: LSystem                   = _
  var interpreter: ProcessingInterpreter = _

  private[this] var steps         = 500
  private[this] val stepIncrement = steps

  override def settings(): Unit = {
    size(1600, 900)
    smooth(10)
  }

  override def setup(): Unit = {
    val triangly = TWO_PI / 6.0f
    val penrosy  = TWO_PI / 10.0f
    val treey    = TWO_PI / 40.0f
    background(0)
    noFill()
    current = simulate(penrose, generations)
    interpreter = new ProcessingInterpreter(treey, 4096 / Math.pow(2, generations).toFloat)
  }

  override def draw(): Unit =
    if (frameCount < 100000) {
      translate(0, height)
      interpreter.render(current, steps)
      steps = min(steps + stepIncrement, current.axiom.length)
      saveFrame("system/lsystem-####.tga")
    } else
      noLoop()

  override def sketchName: String = "lsystem"
}

object LSystemGenerator {

  def main(args: Array[String]): Unit = {
    val sketch = new LSystemGenerator()
    PApplet.runSketch(Array("sketch"), sketch)
  }
}
