package org.twelvehart.apps

import org.twelvehart.apps.ColorExperiments._
import org.twelvehart.common.Color.ColorOps._
import org.twelvehart.common.Color._
import org.twelvehart.common.Constants._
import org.twelvehart.common.{EasingFunctions, ProcessingApp}
import processing.core.{PApplet, PConstants, PImage}

import scala.util.Try

class ColorExperiments(exp: Experiment) extends ProcessingApp {
  implicit val self = this

  import processing.core.PApplet._

  private[this] var img: PImage                          = _
  private[this] var sortMode: Option[Ordering[ColorOps]] = None
  private[this] var saveFrame: Boolean                   = false

  override def settings(): Unit = {
    size(16000, 16000)
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
        rectMode(PConstants.CORNER)
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

    // Get Level of Pixelation based on mouse position, and width of canvas
    val tileFactor = floor(img.width / max(1, mouseX))
    val pixelSize  = Try(img.width / tileFactor).getOrElse(1)
    val tileRange  = 0 until tileFactor

    img.loadPixels()

    val gridLocations = tileRange
      .flatMap(y =>
        tileRange
          .map(x => (y, x))
      )

    // get a list of colors by selecting each pixel
    val colors =
      gridLocations
        .map { case (y, x) => img.get(x * pixelSize, y * pixelSize) }
        .map(ColorOps.apply)

    // sort the colors based on the selected mode
    val sorted = sortMode.fold(colors)(colors.sorted(_))

    // redraw the sorted pixels
    val pixelFactor = width / img.width
    gridLocations.zipWithIndex
      .foreach {
        case ((y, x), i) =>
          fill(sorted(i).c)
          drawGrid(pixelFactor, y, x, pixelSize)
      }

    if (saveFrame) {
      saveFrame = false
      saveFrame(s"stills/$sketchName/frame-###.png")
    }
  }

  private[this] def drawGrid(pixelFactor: Int, y: Int, x: Int, pixelSize: Int): Unit = {
    val newPixelSize = pixelSize * pixelFactor
    (1 to pixelFactor).foreach { factor =>
      val incr = newPixelSize * (factor / pixelFactor)
      rect(x * newPixelSize, y * newPixelSize, incr, incr)
      rect(x * newPixelSize + incr, y * newPixelSize, incr, incr)
      rect(x * newPixelSize, y * newPixelSize + incr, incr, incr)
      rect(x * newPixelSize + incr, y * newPixelSize + incr, incr, incr)
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
