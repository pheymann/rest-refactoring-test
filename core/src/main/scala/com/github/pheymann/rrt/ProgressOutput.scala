package com.github.pheymann.rrt

object ProgressOutput {

  private final val `0%` = "[                    ] 0%"
  private final val `5%` = "[#                   ] 5%"
  private final val `10%` = "[##                  ] 10%"
  private final val `15%` = "[###                 ] 15%"
  private final val `20%` = "[####                ] 20%"
  private final val `25%` = "[#####               ] 25%"
  private final val `30%` = "[######              ] 30%"
  private final val `35%` = "[#######             ] 35%"
  private final val `40%` = "[########            ] 40%"
  private final val `45%` = "[#########           ] 45%"
  private final val `50%` = "[##########          ] 50%"
  private final val `55%` = "[###########         ] 55%"
  private final val `60%` = "[############        ] 60%"
  private final val `65%` = "[#############       ] 65%"
  private final val `70%` = "[##############      ] 70%"
  private final val `75%` = "[###############     ] 75%"
  private final val `80%` = "[################    ] 80%"
  private final val `85%` = "[#################   ] 85%"
  private final val `90%` = "[##################  ] 90%"
  private final val `95%` = "[################### ] 95%"
  private final val `100%` = "[####################] 100%"

  private def roundToPercentage(round: Int, repetitions: Int, printedPercentage: Int): Option[(Int, String)] = {
    val percentage = ((round * 100) / repetitions) / 5

    if (percentage != printedPercentage) {
      Some(percentage -> (percentage match {
        case 0 => `0%`
        case 1 => `5%`
        case 2 => `10%`
        case 3 => `15%`
        case 4 => `20%`
        case 5 => `25%`
        case 6 => `30%`
        case 7 => `35%`
        case 8 => `40%`
        case 9 => `45%`
        case 10 => `50%`
        case 11 => `55%`
        case 12 => `60%`
        case 13 => `65%`
        case 14 => `70%`
        case 15 => `75%`
        case 16 => `80%`
        case 17 => `85%`
        case 18 => `90%`
        case 19 => `95%`
        case 20 => `100%`
      }))
    } else
      None

  }

  def printProgress(round: Int, repetitions: Int, printedPercentage: Int): Int = {
    roundToPercentage(round, repetitions, printedPercentage).fold(printedPercentage) { case (percentage, line) =>
      println(line)
      percentage
    }
  }

}
