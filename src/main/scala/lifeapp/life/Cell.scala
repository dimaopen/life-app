package lifeapp.life

import java.lang.Math.max
import java.lang.Math.min

/**
  *
  * @author Dmitry Openkov
  */
case class Cell(x: Int, y: Int) {
  lazy val neighbours: Set[Cell] =
    Set(
      Cell(x - 1, y - 1),
      Cell(x - 1, y),
      Cell(x - 1, y + 1),
      Cell(x, y - 1),
      Cell(x, y + 1),
      Cell(x + 1, y - 1),
      Cell(x + 1, y),
      Cell(x + 1, y + 1),
    )
}

object Cell {
  def cells(cells: (Int, Int)*): Set[Cell] = {
    cells.map(c => Cell(c._1, c._2)).toSet
  }
}

case class Generation(cells: Set[Cell], n: Int, xMin: Int, yMin: Int, xMax: Int, yMax: Int) {
  def +(cell: Cell): Generation = {
    if (!cells.contains(cell)) {
      Generation(cells + cell, 0, min(xMin, cell.x), min(yMin, cell.y), max(xMax, cell.x), max(yMax, cell.y))
    } else {
      this
    }
  }

  def -(cell: Cell): Generation = {
    if (cells.contains(cell)) {
      Generation(cells - cell, 0, min(xMin, cell.x), min(yMin, cell.y), max(xMax, cell.x), max(yMax, cell.y))
    } else {
      this
    }
  }
}

object Generation {
  def apply(cells: Set[Cell]): Generation = {
    Generation(cells, 0)
  }

  def apply(cells: Set[Cell], n: Int): Generation = {
    val boundaries = cells.foldLeft((0, 0, 0, 0))((boundries: (Int, Int, Int, Int), cell: Cell) => {
      (min(boundries._1, cell.x), min(boundries._2, cell.y), max(boundries._3, cell.x), max(boundries._4, cell.y))
    })
    new Generation(cells, n, boundaries._1, boundaries._2, boundaries._3, boundaries._4)
  }
}