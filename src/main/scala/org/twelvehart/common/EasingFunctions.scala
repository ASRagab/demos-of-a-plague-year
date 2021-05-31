package org.twelvehart.common

import processing.core.PApplet._

object EasingFunctions {
  type EasingFunction = Float => Float
  // no easing no acceleration
  val linear: EasingFunction = t => t
  // accelerating from zero velocity
  val easeInQuad: EasingFunction = t => t * t
  // decelerating to zero velocity
  val easeOutQuad: EasingFunction = t => t * (2 - t)
  // acceleration until halfway then deceleration
  val easeInOutQuad: EasingFunction = t => if (t < .5) 2 * t * t else -1 + (4 - 2 * t) * t
  // accelerating from zero velocity
  val easeInCubic: EasingFunction = t => t * t * t

  // decelerating to zero velocity
  val easeOutCubic: EasingFunction = t => {
    val h = t - 1
    Math.pow(h, 3).toFloat + 1
  }
  // acceleration until halfway then deceleration
  val easeInOutCubic: EasingFunction = t => if (t < .5) 4 * t * t * t else (t - 1) * (2 * t - 2) * (2 * t - 2) + 1
  // accelerating from zero velocity
  val easeInQuart: EasingFunction = t => t * t * t * t

  // decelerating to zero velocity
  val easeOutQuart: EasingFunction = t => {
    val h = t - 1
    1 - Math.pow(h, 4).toFloat
  }

  // acceleration until halfway then deceleration
  val easeInOutQuart: EasingFunction = t =>
    if (t < .5)
      8 * t * t * t * t
    else {
      val h = t - 1
      1 - (8 * Math.pow(h, 4)).toFloat
    }
  // accelerating from zero velocity
  val easeInQuint: EasingFunction = t => t * t * t * t * t

  // decelerating to zero velocity
  val easeOutQuint: EasingFunction = t => {
    val h = t - 1
    1 + Math.pow(h, 5).toFloat
  }

  // acceleration until halfway then deceleration
  val easeInOutQuint: EasingFunction = t =>
    if (t < .5)
      16 * t * t * t * t * t
    else {
      val h = t - 1
      1 + 16 * Math.pow(h, 5).toFloat
    }

  def easingLerp(start: Float, stop: Float, progress: Float, total: Float, easingFunction: EasingFunction): Float =
    lerp(start, stop, easingFunction(progress / total))

}
