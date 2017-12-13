package lifeapp.life

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.{ReplaySubject, Subject}

/**
  *
  * @author Dmitry Openkov
  */
object LifeEngine {
  def step(generation: Generation): Generation = {
    val cells = generation.cells
    val dyingCells = cells.foldLeft(Set[Cell]())((dying: Set[Cell], cell: Cell) => {
      val numOfNeighbours = cells.intersect(cell.neighbours).size
      if (numOfNeighbours < 2 || numOfNeighbours > 3) {
        dying + cell
      } else
        dying
    })
    val emptyCells = cells.foldLeft(Set[Cell]())((empty: Set[Cell], cell: Cell) => {
      empty.union(cell.neighbours.diff(cells))
    })
    val newCells = emptyCells.foldLeft(Set[Cell]())((newSet: Set[Cell], cell: Cell) => {
      val numOfNeighbours = cells.intersect(cell.neighbours).size
      if (numOfNeighbours == 3) {
        newSet + cell
      } else
        newSet
    })
    Generation(cells.diff(dyingCells).union(newCells), generation.n + 1)
  }
}

class LifeEngine (private val initialGeneration: Generation) {
  private var gen = initialGeneration
  private val stepSubject: Subject[Generation] = ReplaySubject.create[Generation](1)
  stepSubject.onNext(gen)

  def generation: Generation = gen

  def step(): Generation = {
    val newGeneration = LifeEngine.step(gen)
    gen = newGeneration
    stepSubject.onNext(gen)
    gen
  }

  def toggle(cell: Cell): Unit = {
    if (gen.cells.contains(cell)) {
      gen = generation - cell
    } else {
      gen = generation + cell
    }
    stepSubject.onNext(gen)
  }

  def subscribe(consumer: Consumer[Generation]): Disposable = {
    stepSubject.subscribe(consumer)
  }

}
