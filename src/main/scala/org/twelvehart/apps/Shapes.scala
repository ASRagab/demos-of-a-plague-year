package org.twelvehart.apps

import org.twelvehart.common.ProcessingApp
import processing.core.PApplet._
import processing.core.PConstants._
import processing.core._

class Shapes extends ProcessingApp {
  private[this] val saveFrames = true

  override def settings() = {
    size(1200, 800)
    smooth(10)
  }

  private[this] var image: Option[PImage] = None

  override def setup(): Unit = {
    background(0, 50)
    rectMode(CENTER)
    strokeWeight(1)
  }

  override def draw(): Unit = {
    if (frameCount % 180 == 0)
      background(0, 50)
    // draw circle and rectangle in original scale// draw circle and rectangle in original scale
    translate(width / 2, height / 2)
    rotate(radians(0.5f * (frameCount % 360) * Math.pow(-1, frameCount % 360).toFloat))
    stroke(246, 173, 113, 50)
    noFill()
    ellipse(0, 0, 355, 355)
    rect(0, 0, 355, 235, 130)
    // draw first scaled rectangle

    scale(2.0f)
    noFill()

    rect(0, 0, 355, 175, 230)
    // draw second scaled rectangle'
    scale(0.5f)
    fill(113, 70, 132, 50)
    stroke(246, 173, 113, 50)
    rect(0, 0, 355, 175, 230)

    if (saveFrames && frameCount <= 360 * 2)
      saveFrame("stills/shapes-####.tga")
    else
      noLoop()
  }

  override def sketchName: String = "shapes"
}

object Shapes {

  def main(args: Array[String]): Unit = {
    val sketch = new Shapes()
    PApplet.runSketch(Array("sketch"), sketch)
  }
}
