package org.twelvehart.common

import processing.core.PApplet

trait ProcessingApp extends PApplet {
  def sketchName: String

  override def settings(): Unit = super.settings()

  override def setup(): Unit = super.setup()

  override def draw(): Unit = super.draw()
}

object ProcessingApp {

  def main(args: Array[String]): Unit = {
    val sketch: ProcessingApp = ???
    PApplet.runSketch(Array("sketch"), sketch)
  }
}
