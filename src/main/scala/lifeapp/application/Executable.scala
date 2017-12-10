package lifeapp.application

import lifeapp.life.{Cell, Generation, LifeEngine}
import lifeapp.ui.MainController


/**
  *
  * @author Dmitry Openkov
  */
object Executable extends App {
  private val generation = Generation(Cell.cells((10, 10), (11, 10), (14, 10), (15, 10), (16, 10), (13, 11), (11, 12)))
  new MainController(new LifeEngine(generation))
}
