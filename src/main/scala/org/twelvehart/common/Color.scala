package org.twelvehart.common

import Color._
import processing.core.PApplet

sealed abstract class Color(var r: Float, var g: Float, var b: Float, var a: Float = 255) {
  def use(color: FromRGBA): Unit = color(r, g, b, a)

  def withAlpha(a: Float): Color = {
    this.a = a
    this
  }

  def addRed(r: Int): Color = {
    this.r = this.r + r
    this
  }

  def addGreen(g: Int): Color = {
    this.g = this.g + g
    this
  }

  def addBlue(b: Int): Color = {
    this.b = this.b + b
    this
  }
}

object Color {
  type FromRGB  = (Float, Float, Float) => Unit
  type FromRGBA = (Float, Float, Float, Float) => Unit

  sealed trait Colour {
    def apply(method: FromRGB): Unit
  }

  final case object Black extends Colour {
    def apply(method: FromRGB): Unit = method(0, 0, 0)
  }

  final case object White extends Colour {
    def apply(method: FromRGB): Unit = method(255, 255, 255)
  }

  /** Using the colour linear interpolator there is no need to define this */
  final case class Gray(levelD: Double) extends Colour {
    private val level                = levelD.toFloat
    def apply(method: FromRGB): Unit = method(level, level, level)
  }

  case object Cerulean extends Color(42, 82, 190)

  case class ColorOps(c: Int) {

    def hue(implicit app: PApplet): Float =
      app.hue(c)

    def saturation(implicit app: PApplet): Float =
      app.saturation(c)

    def brightness(implicit app: PApplet): Float =
      app.brightness(c)
  }

  object ColorOps {
    def orderingByDefault(implicit app: PApplet): Ordering[ColorOps]    = Ordering.by(_.c)
    def orderingByHue(implicit app: PApplet): Ordering[ColorOps]        = Ordering.by(_.hue)
    def orderingBySaturation(implicit app: PApplet): Ordering[ColorOps] = Ordering.by(_.saturation)
    def orderingByBrightness(implicit app: PApplet): Ordering[ColorOps] = Ordering.by(_.brightness)
  }

}
