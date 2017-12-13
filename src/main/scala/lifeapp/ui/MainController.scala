package lifeapp.ui

import java.awt.event.{ActionListener, ComponentAdapter, ComponentEvent}
import java.awt.{BorderLayout, Dimension}
import javax.swing.border.BevelBorder
import javax.swing.{BoxLayout, JFrame, JLabel, JPanel, SwingConstants, _}

import lifeapp.drawing.{Drawing, DrawingEvent}
import lifeapp.life.{Generation, LifeEngine}

/**
  *
  * @author Dmitry Openkov
  */
class MainController(lifeEngine: LifeEngine) {
  private val drawing: Drawing = new Drawing(lifeEngine)
  val statusLabel = new JLabel()

  private val stepButton = createButton("Step Forward", "media/StepForward", _ => drawing.step())
  private val playButton = createButton("Play", "media/Play", _ =>
    if (drawing.isRunning) {
      drawing.stop()
    } else {
      drawing.play()
    }
  )
  private val fastForwardButton = createButton("Fast Forward", "media/FastForward", _ => drawing.fastForward())
  private val zoomInButton = createButton("Zoom In", "general/ZoomIn", _ => drawing.zoomIn())
  private val zoomOutButton = createButton("Zoom Out", "general/ZoomOut", _ => drawing.zoomOut())
  private val playIcon = playButton.getIcon
  private val pauseIcon: ImageIcon = createIcon("media/Pause")

  SwingUtilities.invokeLater(() => createAndShowGUI(createPanel()))
  lifeEngine.subscribe((g: Generation) => {
    statusLabel.setText(s"# ${g.n}; population ${g.cells.size}")
  })
  drawing.subscribe {
    case DrawingEvent.Play => playButton.setIcon(pauseIcon)
    case DrawingEvent.Stop => playButton.setIcon(playIcon)
    case DrawingEvent.InitialRate => fastForwardButton.setEnabled(true)
    case DrawingEvent.MaxRate => fastForwardButton.setEnabled(false)
    case DrawingEvent.MaxZoom => zoomInButton.setEnabled(false)
    case DrawingEvent.MinZoom => zoomOutButton.setEnabled(false)
    case DrawingEvent.IntermediateZoom => zoomInButton.setEnabled(true); zoomOutButton.setEnabled(true)
  }

  private def createPanel(): JPanel = {
    // create a toolbar
    val toolBar = new JToolBar

    toolBar.add(stepButton)
    toolBar.add(playButton)
    toolBar.add(fastForwardButton)
    toolBar.addSeparator()
    toolBar.add(zoomInButton)
    toolBar.add(zoomOutButton)
    toolBar.setFloatable(true)


    val contentPane = new JPanel
    contentPane.setPreferredSize(drawing.initialSize)
    contentPane.setLayout(new BorderLayout)
    contentPane.add(toolBar, BorderLayout.PAGE_START)
    contentPane.add(drawing.canvas, BorderLayout.CENTER)

    val statusPanel = new JPanel
    statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED))
    contentPane.add(statusPanel, BorderLayout.SOUTH)
    statusPanel.setPreferredSize(new Dimension(contentPane.getWidth, 20))
    statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS))
    statusLabel.setHorizontalAlignment(SwingConstants.LEFT)
    statusPanel.add(statusLabel)

    contentPane
  }

  private def createIcon(imageName: String): ImageIcon = {
    val url = this.getClass.getResource(s"/toolbarButtonGraphics/${imageName}24.gif")
    val icon = new ImageIcon(url)
    icon
  }

  private def createButton(tooltip: String, iconName: String, listener: ActionListener = null) = {
    val button = new JButton(createIcon(iconName))
    button.setToolTipText(tooltip)
    button.setFocusPainted(false)
    if (listener != null) {
      button.addActionListener(listener)
    }
    button
  }

  private def createAndShowGUI(value: JComponent): Unit = {
    val frame = new JFrame("Game of Life")

    frame.addComponentListener(new ComponentAdapter {
      override def componentShown(e: ComponentEvent): Unit = {
        drawing.shown()
      }
    })
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(value)
    frame.pack()
    frame.setVisible(true)
  }
}