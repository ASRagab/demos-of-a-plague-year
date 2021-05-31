package org.twelvehart.apps

import org.twelvehart.common.{EasingFunctions, ProcessingApp}
import processing.core.PApplet._
import processing.core.PConstants._
import processing.core._

class AsciiGenerator extends ProcessingApp {

  val chars: Array[Char] = Array('#', '!', '@', '*', '%', '$', '&')
  val loadPath           = "data/image.png"
  val desiredFrameRate   = 24
  val animationLength    = 10

  var pixelate        = 99
  var picture: PImage = _
  var font: PFont     = _

  override def settings(): Unit = {
    size(800, 600)
    smooth(10)
  }

  override def setup(): Unit = {
    picture = loadImage(loadPath)
    font = createFont("InconsolataAwesome", 14)
    textFont(font)
    picture.filter(GRAY)
    frameRate(desiredFrameRate)
  }

  override def draw(): Unit = {
    val totalFrames = desiredFrameRate * animationLength * 2
    if (frameCount <= totalFrames) {
      background(255)
      val half = totalFrames / 2
      val (start, stop, factor) =
        if (frameCount < half)
          (99, 1, EasingFunctions.linear(frameCount.toFloat / half))
        else
          (1, 99, EasingFunctions.linear((frameCount - half).toFloat / half))

      println(s"$start $stop $factor")
      pixelate = lerp(start, stop, factor).toInt

      for (i <- 0 until height by pixelate)
        for (j <- 0 until width by pixelate) {
          val sample = picture.get(j, i)
          fill(sample)
          textAlign(CENTER)
          val bright = brightness(sample)
          val char   = chars(bright.toInt % chars.length)
          text(char, j, i)
        }

      saveFrame("bells/#######.tga")
    } else
      noLoop()
  }

  override def keyPressed(): Unit =
    keyCode match {
      case UP   => pixelate += 1
      case DOWN => pixelate -= 1
      case _    => ()
    }

  override def sketchName: String = "asciigenerator"
}

object AsciiGenerator {

  def main(args: Array[String]): Unit = {
    val sketch = new AsciiGenerator()
    PApplet.runSketch(Array("sketch"), sketch)
  }
}
