package lifeapp.life

import org.scalatest.{FlatSpec, Matchers}

/**
  *
  * @author Dmitry Openkov
  */
class LifeEngineTest extends FlatSpec with Matchers {

  "Life Engine" should "provide next generation" in {
    val g1 = LifeEngine.step(Generation(Cell.cells((0, 0), (0, 1), (0, 2))))
    g1.n should be (1)
    g1.cells.size should be (3)
    g1.cells should contain (Cell(-1, 1))
    g1.cells should contain (Cell(0, 1))
    g1.cells should contain (Cell(1, 1))
  }

  "Life Engine" should "provide the same cells for stable patterns" in {
    def test(cells: Set[Cell]): Any = {
      val g0 = Generation(cells)
      val g1 = LifeEngine.step(g0)
      g1.n should be(g0.n + 1)
      g1.cells.size should be(g0.cells.size)
      g1.cells should be(g0.cells)
    }

    test(Cell.cells((0, 0), (0, 1), (1, 0), (1, 1)))
    test(Cell.cells((0, 0), (0, 1), (1, 1), (1, -1), (2, 0)))
  }

}
