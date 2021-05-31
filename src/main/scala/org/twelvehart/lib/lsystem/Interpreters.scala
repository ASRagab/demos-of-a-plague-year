package org.twelvehart.lib.lsystem

import org.twelvehart.lib.lsystem.ActionType._
import processing.core.PApplet

object Interpreters {

  class ProcessingInterpreter(var radians: Float, var distance: Float) {

    def changeDist(factor: Float): Unit =
      this.distance = this.distance * factor

    def changeRads(factor: Float): Unit =
      this.radians = this.radians * factor

    def renderStep(actionType: ActionType, factor: Int, repeats: Int)(implicit PApplet: PApplet): Int = {
      import PApplet._
      var push = 0
      actionType match {
        case Rotate =>
          rotate(radians * factor)
        case Draw =>
          val randRGB = random(100, 255)
          stroke(randRGB, 255, randRGB, 15)
          line(0, 0, 0, distance * factor)
        case Translate =>
          translate(0, distance * factor)
        case SaveState =>
          push = 1
          pushMatrix()
        case RestoreState =>
          push = -1
          popMatrix()
        case Repeat(action) =>
          push = (0 until repeats).map { _ =>
            renderStep(action, factor, repeats)
          }.sum
        case And(action1, action2) =>
          push = renderStep(action1, factor, repeats) + renderStep(action2, factor, repeats)
        case Negate(action) =>
          renderStep(action, -1 * factor, repeats)
        case NoOp => ()

      }

      push
    }

    def render(system: LSystem, steps: Int)(implicit PApplet: PApplet): Unit = {
      import PApplet._

      var pushes  = 0
      var repeats = 1
      system.axiom.take(steps).foreach { ch =>
        pushes += renderStep(system.ruleset.mapper(ch), 1, repeats)

        if (ch.isDigit)
          repeats = ch - 48
        else
          repeats = 1
      }

      while (pushes > 0) {
        popMatrix()
        pushes -= 1
      }
    }
  }
}
