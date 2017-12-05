package lifeapp.drawing

import java.awt.Color

import lifeapp.life.{Cell, Generation}
import org.piccolo2d.PNode

/**
  *
  * @author Dmitry Openkov
  */
class BlockNode(pNode: PNode, blockSize: Int, viewHeight: Int) {

  def drawCells(generation: Generation): Unit = {
    pNode.removeAllChildren()
    generation.cells.foreach((cell: Cell) => {
      val c = new PNode
      c.setPaint(Color.BLUE)
      c.setBounds(cell.x * blockSize, (viewHeight - cell.y) * blockSize, blockSize, blockSize)
      pNode.addChild(c)
    })
  }
}
