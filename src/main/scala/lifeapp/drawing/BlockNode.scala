package lifeapp.drawing

import java.awt.Color
import java.awt.geom.Rectangle2D

import lifeapp.life.{Cell, Generation}
import org.piccolo2d.PNode

/**
  *
  * @author Dmitry Openkov
  */
class BlockNode(pNode: PNode, gridLayer: GridLayer) {

  def drawCells(generation: Generation): Unit = {
    pNode.removeAllChildren()
    generation.cells.foreach((cell: Cell) => {
      val c = new PNode
      c.setPaint(Color.BLUE)
      val cellRectangle: Rectangle2D = gridLayer.getCellRectangle(cell.x, cell.y)
      c.setBounds(cellRectangle)
      pNode.addChild(c)
    })
  }
}
