package lifeapp.ui

import java.awt.{BorderLayout, Color, Dimension}
import java.awt.event.{ActionEvent, ActionListener}
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import javax.swing._

import lifeapp.drawing.GridLayer
import lifeapp.life.{Cell, Generation, LifeEngine}
import org.piccolo2d.activities.PActivity
import org.piccolo2d.{PCamera, PCanvas, PNode}

/**
  *
  * @author Dmitry Openkov
  */
class MainController(generation: Generation) {
  private val INITIAL_NUMBER_CELLS_X = 80
  private val INITIAL_NUMBER_CELLS_Y = 60
  var gen: Generation = generation
  private val gridLayer = new GridLayer()
  private val canvas: PCanvas = createCanvas
  drawCells(canvas)

  private val mainCycle = new PActivity(-1L, 200, System.currentTimeMillis()) {
    override protected def activityStep(elapsedTime: Long): Unit = {
      super.activityStep(elapsedTime)
      gen = LifeEngine.step(gen)
      drawCells(canvas)
    }
  }

  SwingUtilities.invokeLater(() => createAndShowGUI(createPanel()))

  private def createPanel(): JPanel = {
    // create a toolbar
    val toolBar = new JToolBar
    val start = createStartButton
    val zoomAboutCanvasCenter = new JToggleButton("canvas")
    //    zoomAboutCanvasCenter.addActionListener((e: ActionEvent) => setFullScreenMode(false))
    val zoomAboutViewCenter = new JToggleButton("view")
    //    zoomAboutViewCenter.addActionListener((e: ActionEvent) => setFullScreenMode(true))
    val buttonGroup = new ButtonGroup
    buttonGroup.add(start)
    buttonGroup.add(zoomAboutCanvasCenter)
    buttonGroup.add(zoomAboutViewCenter)
    toolBar.add(start)
    toolBar.add(zoomAboutCanvasCenter)
    toolBar.add(zoomAboutViewCenter)
    toolBar.setFloatable(true)


    val contentPane = new JPanel
    contentPane.setPreferredSize(new Dimension(INITIAL_NUMBER_CELLS_X * gridLayer.gridSpacing,
      INITIAL_NUMBER_CELLS_Y * gridLayer.gridSpacing))
    contentPane.setLayout(new BorderLayout)
    contentPane.add(toolBar, BorderLayout.PAGE_START)
    contentPane.add(canvas, BorderLayout.CENTER)
    contentPane
  }

  private def createIcon(imageName: String): ImageIcon = {
    val url = this.getClass.getResource(s"/toolbarButtonGraphics/media/${imageName}24.gif")
    val icon = new ImageIcon(url)
    icon
  }

  private def createStartButton = {

    val playIcon: ImageIcon = createIcon("Play")
    val pauseIcon: ImageIcon = createIcon("Pause")
    val button = new JButton(playIcon)
    button.setToolTipText("Play")
    button.addActionListener((e: ActionEvent) => {
      if (mainCycle.isStepping) {
        mainCycle.terminate(PActivity.TERMINATE_AND_FINISH_IF_STEPPING)
        button.setIcon(playIcon)
      } else {
        canvas.getRoot.addActivity(mainCycle)
        button.setIcon(pauseIcon)
      }
    })
    button
  }

  private def createCanvas = {
    val canvas = new PCanvas
    val root = canvas.getRoot
    val camera = canvas.getCamera
    root.addChild(gridLayer)
    camera.addLayer(gridLayer)

    val listener = new PropertyChangeListener() {
      override def propertyChange(evt: PropertyChangeEvent): Unit = {
        gridLayer.setBounds(camera.getViewBounds)
      }
    }
    camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, listener)
    camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, listener)
    canvas
  }

  private def drawCells(canvas: PCanvas): Unit = {
    canvas.getLayer.removeAllChildren()
    gen.cells.foreach((cell: Cell) => {
      val c = new PNode
      c.setPaint(Color.BLUE)
      c.setBounds(cell.x * gridLayer.gridSpacing, (INITIAL_NUMBER_CELLS_Y - cell.y) * gridLayer.gridSpacing,
        gridLayer.gridSpacing, gridLayer.gridSpacing)
      canvas.getLayer.addChild(c)
    })
  }

  import javax.swing.JFrame

  private def createAndShowGUI(value: JComponent): Unit = {
    val frame = new JFrame("Game of Life")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(value)
    frame.pack()
    frame.setVisible(true)
  }
}