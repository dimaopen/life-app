package lifeapp.drawing

import java.awt.Dimension
import java.beans.{PropertyChangeEvent, PropertyChangeListener}

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.ReplaySubject
import lifeapp.drawing.DrawingEvent.DrawingEvent
import lifeapp.life.LifeEngine
import org.piccolo2d.activities.PActivity
import org.piccolo2d.extras.handles.PBoundsHandle
import org.piccolo2d.{PCamera, PCanvas, PLayer, PNode}

/**
  *
  * @author Dmitry Openkov
  */
class Drawing(var lifeEngine: LifeEngine) {
  private val subject = ReplaySubject.create[DrawingEvent]()
  private val INITIAL_NUMBER_CELLS_X = 80
  private val INITIAL_NUMBER_CELLS_Y = 60
  private val BLOCK_SIZE = 10
  val initialSize = new Dimension(INITIAL_NUMBER_CELLS_X * BLOCK_SIZE, INITIAL_NUMBER_CELLS_Y * BLOCK_SIZE)
  private val magnifier = new Magnifier()
  val canvas: PCanvas = createCanvas
  private val blockNode = new BlockNode(canvas.getLayer, BLOCK_SIZE, INITIAL_NUMBER_CELLS_Y)
  private val maxZoom = canvas.getCamera.getViewScale
  private val minZoom = .125
  private val INITIAL_STEP_RATE = 500
  blockNode.drawCells(lifeEngine.generation)
  subject.onNext(DrawingEvent.InitialRate)
  subject.onNext(DrawingEvent.MaxZoom)

  private val mainCycle = new PActivity(-1L, INITIAL_STEP_RATE, System.currentTimeMillis()) {
    override protected def activityStep(elapsedTime: Long): Unit = {
      super.activityStep(elapsedTime)
      doStep()
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

  def zoomIn(): Unit = {
    val currentScale = canvas.getCamera.getViewScale
    val scale = if (currentScale * 2 > maxZoom) maxZoom else currentScale * 2
    val viewCenter = getViewCenter
    canvas.getCamera.scaleViewAboutPoint(scale / currentScale, viewCenter.getX, viewCenter.getY)
    produceZoomEvents()
  }

  def zoomOut(): Unit = {
    val currentScale = canvas.getCamera.getViewScale
    val scale = if (currentScale / 2 < minZoom) minZoom else currentScale / 2
    val viewCenter = getViewCenter
    canvas.getCamera.scaleViewAboutPoint(scale / currentScale, viewCenter.getX, viewCenter.getY)
    produceZoomEvents()
  }

  private def produceZoomEvents(): Unit = {
    val scale = canvas.getCamera.getViewScale
    if (scale >= maxZoom) {
      subject.onNext(DrawingEvent.MaxZoom)
    } else if (scale <= minZoom) {
      subject.onNext(DrawingEvent.MinZoom)
    } else {
      subject.onNext(DrawingEvent.IntermediateZoom)
    }
  }

  private def getViewCenter = {
    canvas.getCamera.getBounds.getCenter2D
  }

  private def doStep(): Unit = {
    blockNode.drawCells(lifeEngine.step())
  }

  def isRunning: Boolean = mainCycle.isStepping

  def step(): Unit = {
    stop()
    doStep()
  }

  def stop(): Unit = {
    if (isRunning) {
      mainCycle.terminate(PActivity.TERMINATE_AND_FINISH_IF_STEPPING)
      subject.onNext(DrawingEvent.Stop)
    }
  }

  def play(): Unit = {
    if (!isRunning) {
      mainCycle.setStepRate(INITIAL_STEP_RATE)
      subject.onNext(DrawingEvent.InitialRate)
      canvas.getRoot.addActivity(mainCycle)
      subject.onNext(DrawingEvent.Play)
    }
  }

  def fastForward(): Unit = {
    play()
    if (mainCycle.getStepRate == 0) {
      mainCycle.setStepRate(INITIAL_STEP_RATE)
      subject.onNext(DrawingEvent.InitialRate)
    } else {
      mainCycle.setStepRate(mainCycle.getStepRate / 2)
      if (mainCycle.getStepRate == 0) {
        subject.onNext(DrawingEvent.MaxRate)
      }
    }
  }

  def subscribe(consumer: Consumer[DrawingEvent]): Disposable = {
    subject.subscribe(consumer)
  }
}

object DrawingEvent extends Enumeration {
  type DrawingEvent = Value
  val Play, Stop, MaxRate, InitialRate, MaxZoom, MinZoom, IntermediateZoom = Value
}
