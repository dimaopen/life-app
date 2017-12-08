package lifeapp.drawing

import java.awt.Dimension
import java.beans.{PropertyChangeEvent, PropertyChangeListener}

import lifeapp.life.{Generation, LifeEngine}
import org.piccolo2d.activities.PActivity
import org.piccolo2d.extras.handles.PBoundsHandle
import org.piccolo2d.{PCamera, PCanvas, PLayer, PNode}

/**
  *
  * @author Dmitry Openkov
  */
class Drawing(var generation: Generation) {
  private val INITIAL_NUMBER_CELLS_X = 80
  private val INITIAL_NUMBER_CELLS_Y = 60
  private val BLOCK_SIZE = 10
  val initialSize = new Dimension(INITIAL_NUMBER_CELLS_X * BLOCK_SIZE, INITIAL_NUMBER_CELLS_Y * BLOCK_SIZE)
  private val magnifier = new Magnifier()
  val canvas: PCanvas = createCanvas
  private val blockNode = new BlockNode(canvas.getLayer, BLOCK_SIZE, INITIAL_NUMBER_CELLS_Y)
  private val maxZoom = canvas.getCamera.getViewScale
  private val minZoom = .125
  blockNode.drawCells(generation)

  private val mainCycle = new PActivity(-1L, 200, System.currentTimeMillis()) {
    override protected def activityStep(elapsedTime: Long): Unit = {
      super.activityStep(elapsedTime)
      generation = LifeEngine.step(generation)
      blockNode.drawCells(generation)
    }
  }

  private def createCanvas = {
    val canvas = new PCanvas
    val camera = canvas.getCamera
    val gridLayer = new GridLayer(BLOCK_SIZE)
    camera.addLayer(gridLayer)

    val listener = new PropertyChangeListener() {
      override def propertyChange(evt: PropertyChangeEvent): Unit = {
        gridLayer.setBounds(camera.getViewBounds)
      }
    }
    camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, listener)
    camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, listener)

    magnifier.setBounds(10, 310, 330, 330)
    magnifier.addLayer(0, canvas.getLayer)
    magnifier.addLayer(1, gridLayer)
    val magnifierLayer = new PLayer()
    magnifierLayer.addChild(magnifier)
    magnifierLayer.setVisible(false)
    camera.addLayer(magnifierLayer)
    PBoundsHandle.addBoundsHandlesTo(magnifier)

    canvas
  }

  def zoomIn(): Boolean = {
    val currentScale = canvas.getCamera.getViewScale
    val scale = if (currentScale * 2 > maxZoom) maxZoom else currentScale * 2
    val viewCenter = getViewCenter
    canvas.getCamera.scaleViewAboutPoint(scale / currentScale, viewCenter.getX, viewCenter.getY)
    scale < maxZoom
  }

  def zoomOut(): Boolean = {
    val currentScale = canvas.getCamera.getViewScale
    val scale = if (currentScale / 2 < minZoom) minZoom else currentScale / 2
    val viewCenter = getViewCenter
    canvas.getCamera.scaleViewAboutPoint(scale / currentScale, viewCenter.getX, viewCenter.getY)
    scale > minZoom
  }

  private def getViewCenter = {
    canvas.getCamera.getBounds.getCenter2D
  }

  def isRunning: Boolean = mainCycle.isStepping

  def stop(): Unit = {
    if (isRunning) {
      mainCycle.terminate(PActivity.TERMINATE_AND_FINISH_IF_STEPPING)
    }
  }

  def play(): Unit = {
    if (!isRunning) {
      canvas.getRoot.addActivity(mainCycle)
    }
  }
}
