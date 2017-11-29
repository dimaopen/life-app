package lifeapp.ui

import java.awt.Color
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.net.URL
import java.util.ResourceBundle
import javafx.embed.swing.SwingNode
import javafx.fxml.{FXML, Initializable}
import javax.swing.SwingUtilities

import lifeapp.drawing.GridLayer
import lifeapp.life.{Cell, Generation, LifeEngine}
import org.piccolo2d.activities.PActivity
import org.piccolo2d.{PCamera, PCanvas, PNode}

import scala.collection.mutable

/**
  *
  * @author Dmitry Openkov
  */
class MainController extends Initializable {
  @FXML
  private var swingNode: SwingNode = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    val canvas = new PCanvas
    val root = canvas.getRoot
    val camera = canvas.getCamera
    val gridLayer = new GridLayer

    //    root.removeChild(camera.getLayer(0))
    //    camera.removeLayer(0)
    root.addChild(gridLayer)
    camera.addLayer(gridLayer)

    val listener = new PropertyChangeListener() {
      override def propertyChange(evt: PropertyChangeEvent): Unit = {
        gridLayer.setBounds(camera.getViewBounds)
      }
    }
    camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, listener)

    camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, listener)

    var generation: Generation = Generation(Cell.cells((10, 10), (11, 10), (14, 10), (15, 10), (16, 10), (13, 11), (11, 12)))
    val drawnCells = mutable.MutableList()

    def drawCells(): Unit = {
      canvas.getLayer.removeAllChildren()
      generation.cells.foreach((cell: Cell) => {
        val c = new PNode
        c.setPaint(Color.BLUE)
        c.setBounds(cell.x * gridLayer.gridSpacing, cell.y * gridLayer.gridSpacing, gridLayer.gridSpacing, gridLayer.gridSpacing)
        canvas.getLayer.addChild(c)
      })
    }

    def lifeStep(): Unit = {
      generation = LifeEngine.step(generation)
      drawCells()
    }

    val mainCycle = new PActivity(-1L, 10, System.currentTimeMillis + 4000L) {
      override protected def activityStep(elapsedTime: Long): Unit = {
        super.activityStep(elapsedTime)
        lifeStep()
      }
    }
    drawCells()

    root.addActivity(mainCycle)

    SwingUtilities.invokeLater(() => swingNode.setContent(canvas))
  }
}
