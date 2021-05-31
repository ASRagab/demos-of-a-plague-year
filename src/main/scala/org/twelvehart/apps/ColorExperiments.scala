package org.twelvehart.apps

import org.twelvehart.common.{Constants, EasingFunctions, ProcessingApp}
import Constants._
import processing.core.{PApplet, PConstants, PImage}
import ColorExperiments._
import org.twelvehart.common.Color.ColorOps._
import org.twelvehart.common.Color._
import org.twelvehart.common.utils.timestamp

class ColorExperiments(exp: Experiment) extends ProcessingApp {
  implicit val self = this

  import processing.core.PApplet._

  private[this] var img: PImage                          = _
  private[this] var sortMode: Option[Ordering[ColorOps]] = None
  private[this] var saveFrame: Boolean                   = false

  override def settings(): Unit = {
    size(800, 800)
    smooth(10)
  }

  override def setup(): Unit = experimentSetup

  override def draw(): Unit =
    experiment()

  private[this] def experimentSetup(implicit app: PApplet): Unit =
    exp match {
      case CenterBox =>
        rectMode(PConstants.CENTER)
        background(0)

        val font = createFont("IBMPlexMono-Bold", 32, true)
        textFont(font)
        textAlign(PConstants.LEFT, PConstants.LEFT)
        noStroke()
        colorMode(PConstants.HSB, DegreesCircle, 100, 100)
      case Grid =>
        background(0)
        noStroke()
        ellipseMode(PConstants.CENTER)
        colorMode(PConstants.HSB, DegreesCircle, 100, 100)
      case Image =>
        background(255)
        noStroke()
        ellipseMode(PConstants.CORNER)
        colorMode(PConstants.HSB, 360, 100, 100, 100)
        img = loadImage("data/celebrationday.jpg")
      case _ =>
        background(0)
    }

  private[this] val experiment = exp match {
    case CenterBox =>
      () => centerBox()
    case Grid =>
      () => grid()
    case Image =>
      () => imagePixels()
  }

  private[this] def centerBox(): Unit = {
    background(mouseY / 2, 50, 75)

    fill(DegreesCircle - mouseY, 50, 75)
    rect(height / 2, width / 2, mouseX + 1, mouseX + 1)

    fill(0)
    text(s"X | $mouseX\nY | $mouseY", 50, 50)
  }

  private[this] def grid(): Unit = {
    val maxFrames = 1200
    if (frameCount < maxFrames) {
      val stepX = Math
        .max(
          1,
          EasingFunctions
            .easingLerp(0, DegreesCircle, frameCount, maxFrames / 2, EasingFunctions.easeInOutQuad)
        )

      background(0)

      for {
        gridX <- 0 to DegreesCircle by stepX.toInt
      } yield {
        fill(gridX, 75, 100, 180)
        arc(
          height / 2,
          width / 2,
          600 - gridX,
          600 - gridX,
          radians(gridX),
          radians(gridX + stepX),
          PConstants.PIE
        )
      }
      saveFrame(s"stills/$sketchName/still-####.tga")
    } else
      noLoop()

  }

  private def imagePixels(): Unit = {
    background(0)
    val tileFactor = floor(width / max(1, mouseX))
    val pixelSize  = width / tileFactor
    val tileRange  = 0 until tileFactor

    img.loadPixels()

    val colors =
      tileRange
        .flatMap(y =>
          tileRange
            .map(x => (y, x))
        )
        .map { case (y, x) => img.get(x * pixelSize, y * pixelSize) }
        .map(ColorOps.apply)

    val sorted = sortMode.fold(colors)(colors.sorted(_))

    tileRange
      .flatMap(gridX => tileRange.map(gridY => (gridX, gridY)))
      .zipWithIndex
      .foreach {
        case ((gridY, gridX), i) =>
          fill(sorted(i).c)
          ellipse(gridX * pixelSize, gridY * pixelSize, pixelSize, pixelSize)
      }

    if (saveFrame) {
      saveFrame = false
      saveFrame(s"data/$sketchName/frame-###.png")
    }
  }

  override def keyReleased(): Unit =
    exp match {
      case Image =>
        key match {
          case 's' =>
            saveFrame = true
          case '0' =>
            sortMode = None
          case '1' =>
            sortMode = Some(orderingByHue)
          case '2' =>
            sortMode = Some(orderingBySaturation)
          case '3' =>
            sortMode = Some(orderingByBrightness)
          case _ =>
            sortMode = sortMode
        }
      case _ => ()
    }

  override val sketchName: String = "color-experiments"
}

object ColorExperiments {
  sealed trait Experiment
  case object CenterBox extends Experiment
  case object Grid      extends Experiment
  case object Image     extends Experiment

  def main(args: Array[String]): Unit =
    PApplet.runSketch("Color Experiments" +: args, new ColorExperiments(Image))
}
