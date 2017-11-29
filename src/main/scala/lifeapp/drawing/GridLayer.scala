package lifeapp.drawing

import java.awt.geom.Line2D
import java.awt.{BasicStroke, Color}

import org.piccolo2d.PLayer
import org.piccolo2d.util.PPaintContext

/**
  *
  * @author Dmitry Openkov
  */
class GridLayer extends PLayer {
  val gridLine = new Line2D.Double
  val gridStroke = new BasicStroke(1)
  val gridPaint: Color = Color.LIGHT_GRAY
  val gridSpacing = 20

  override protected def paint(paintContext: PPaintContext): Unit = {
    // make sure grid gets drawn on snap to grid boundaries. And
    // expand a little to make sure that entire view is filled.
    val bx = getX - getX % gridSpacing - gridSpacing
    val by = getY - getY % gridSpacing - gridSpacing
    val rightBorder = getX + getWidth + gridSpacing
    val bottomBorder = getY + getHeight + gridSpacing
    val g2 = paintContext.getGraphics
    val clip = paintContext.getLocalClip
    g2.setStroke(gridStroke)
    g2.setPaint(gridPaint)
    var x = bx
    while ( {
      x < rightBorder
    }) {
      gridLine.setLine(x, by, x, bottomBorder)
      if (clip.intersectsLine(gridLine)) g2.draw(gridLine)

      x += gridSpacing
    }
    var y = by
    while ( {
      y < bottomBorder
    }) {
      gridLine.setLine(bx, y, rightBorder, y)
      if (clip.intersectsLine(gridLine)) g2.draw(gridLine)

      y += gridSpacing
    }
  }
}
