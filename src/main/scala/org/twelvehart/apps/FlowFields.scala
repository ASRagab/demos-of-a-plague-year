package org.twelvehart.apps

import breeze.stats.distributions._
import org.twelvehart.common.ProcessingApp
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent

import java.util.concurrent.ForkJoinPool
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParSeq

class FlowFields extends ProcessingApp {
  import PApplet._
  implicit val self: PApplet = this

  override def sketchName: String = "flow-fields"

  override def settings(): Unit = {
    size(2000, 2000)
    smooth(10)
  }
  private[this] var saveFrame: Boolean = true

  var resolution: Float                                = _
  var numColumns: Int                                  = _
  var numRows: Int                                     = _
  var initialGrid: IndexedSeq[IndexedSeq[Float]]       = _
  var startingPositions: List[((Double, Double), Int)] = _

  val defaultAngle     = PConstants.TAU * 5
  val resolutionFactor = 0.01f

  override def setup(): Unit = {
    strokeWeight(2.0f)
    strokeCap(PConstants.PROJECT)
    background(255)
    noFill()
    resolution = width * resolutionFactor
    numColumns = (2 * width / resolution).toInt
    numRows = (2 * height / resolution).toInt

    initialGrid = (0 until numColumns).map { i =>
      (0 until numRows).map { j =>
        val xScale     = i * 0.005f
        val yScale     = j * 0.005f
        val noiseScale = noise(xScale, yScale)

        map(noiseScale, 0f, 1.0f, 0f, defaultAngle)
      }
    }

    val distributionX = Uniform(0, width)
    val distributionY = Uniform(0, height)
    startingPositions = List.fill(15000)((distributionX.draw(), distributionY.draw())).zipWithIndex
  }

  override def draw(): Unit = {

    startingPositions.foreach {
      case ((x, y), i) =>
        var xStart     = y.toFloat
        var yStart     = x.toFloat
        var angleStart = defaultAngle

        val numSteps   = 20
        val stepLength = 10
        strokeWeight(Rand.randInt(1, 2).get)

        beginShape()
        (0 to numSteps).foreach { _ =>
          curveVertex(xStart, yStart)
          val column = (xStart / resolution).toInt
          val row    = (yStart / resolution).toInt

          val (angle, checkFailed) =
            if (boundsCheck(column, numColumns) && boundsCheck(row, numRows)) {
              angleStart = initialGrid(column)(row)
              (angleStart, false)
            } else
              (angleStart, true)

          stroke(
            200.0f + 50f * cos(angle),
            130.0f + 120f * sin(angle),
            130.0f + 120f * (1 - cos(angle))
          )

          val xStep = stepLength * cos(angle)
          val yStep = stepLength * sin(angle)

          xStart = if (checkFailed) xStart - xStep else xStart + xStep
          yStart = if (checkFailed) yStart - yStep else yStart + yStep
        }
        endShape()

        print(s"Finished position: $i\r")
    }

    if (saveFrame) {
      saveFrame = false
      saveFrame(s"stills/$sketchName/still-####.png")
    }

    noLoop()
  }

  @inline private[this] def boundsCheck(test: Int, limit: Int) =
    test > -1 && test < limit

  override def keyReleased(event: KeyEvent): Unit =
    key match {
      case 's' | 'S' => saveFrame = true
      case _         => ()
    }
}

object FlowFields {

  def main(args: Array[String]): Unit = {
    val sketch = new FlowFields()

    PApplet.runSketch(sketch.sketchName +: args, sketch)
  }
}
