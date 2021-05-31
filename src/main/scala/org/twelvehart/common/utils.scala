package org.twelvehart.common

object utils {
  import java.util.Calendar

  def timestamp: String = {
    val now = Calendar.getInstance
    String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", now)
  }
}
