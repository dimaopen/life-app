package lifeapp.drawing

import java.awt.Color
import java.awt.geom.Rectangle2D
import java.beans.PropertyChangeEvent

import org.piccolo2d.event.PDragEventHandler
import org.piccolo2d.nodes.PPath
import org.piccolo2d.{PCamera, PLayer, PNode}

/**
  *
  * @author Dmitry Openkov
  */
class Magnifier() extends PNode {
  val scaleFactor = 5
  // Drag bar gets resized to fit the available space, so any rectangle
  // will do here
  private val dragBarRect = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 1.0f)
  val dragBar = new PPath.Float(dragBarRect)
  dragBar.setPaint(Color.DARK_GRAY)
  // This forces drag events to percolate up to PLens object
  dragBar.setPickable(false)
  addChild(dragBar)
  val camera = new PCamera
  camera.setPaint(Color.WHITE)
  addChild(camera)
  // create an event handler to drag the lens around. Note that this event
  // handler consumes events in case another conflicting event handler has
  // been installed higher up in the heirarchy.
  val lensDragger = new PDragEventHandler
  lensDragger.getEventFilter.setMarksAcceptedEventsAsHandled(true)
  addInputEventListener(lensDragger)
  // When this PLens is dragged around adjust the cameras view transform.
  addPropertyChangeListener(PNode.PROPERTY_TRANSFORM, (evt: PropertyChangeEvent) => {
    scaleCameraView()
  })

  private def scaleCameraView(): Unit = {
    val transform = getInverseTransform
    val center = localToParent(getBounds.getCenter2D)
    transform.scaleAboutPoint(scaleFactor, center.getX, center.getY)
    camera.setViewTransform(transform)
  }

  /**
    * Creates the default PLens and attaches the given layer to it.
    *
    * @param layer layer to attach to this PLens
    */
  def this(layer: PLayer) {
    this
    addLayer(0, layer)
  }

  /**
    * Adds the layer to the camera.
    *
    * @param index index at which to add the layer to the camera
    * @param layer layer to add to the camera
    */
  def addLayer(index: Int, layer: PLayer): Unit = {
    camera.addLayer(index, layer)
  }

  /**
    * Removes the provided layer from the camera.
    *
    * @param layer layer to be removed
    */
  def removeLayer(layer: PLayer): Unit = {
    camera.removeLayer(layer)
  }

  /**
    * When the lens is resized this method gives us a chance to layout the
    * lenses camera child appropriately.
    */
  override protected def layoutChildren(): Unit = {
    val dragBarHeight = 20
    dragBar.reset()
    dragBarRect.setRect(getX.toFloat, getY.toFloat, getWidth.toFloat, dragBarHeight)
    dragBar.append(dragBarRect, false)
    dragBar.closePath()
    camera.setBounds(getX, getY + dragBarHeight, getWidth, getHeight - dragBarHeight)
    scaleCameraView()
  }
}
