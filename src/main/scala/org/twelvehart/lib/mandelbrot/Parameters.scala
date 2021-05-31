package org.twelvehart.lib.mandelbrot

import spire.math.Complex

case class Parameters(w: Double, h: Double, upper: Complex[Double], delta: Double, iter: Int, width: Int, height: Int) {
  def map(x: Int, y: Int) = Complex(upper.real + x * w / width, upper.imag - y * h / height)

  def zoom(pt: Complex[Double]): Parameters = {
    val _w     = w * 0.6
    val _h     = _w * height / width
    val _x0    = pt.real - _w / 2
    val _y0    = pt.imag + _h / 2
    val _delta = delta * 0.6
    val _iter  = math.floor(iter * 1.4).toInt
    Parameters(_w, _h, Complex(_x0, _y0), _delta, _iter, width, height)
  }
}

object Parameters {
  def empty: Parameters = Parameters(0, 0, Complex(0, 0), 0, 0, 0, 0)
}
