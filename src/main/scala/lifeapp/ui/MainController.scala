package lifeapp.ui

import java.awt.event.ActionEvent
import java.awt.{BorderLayout, Dimension}
import javax.swing.border.BevelBorder
import javax.swing.{BoxLayout, JFrame, JLabel, JPanel, SwingConstants, _}

import io.reactivex.functions.Consumer
import lifeapp.drawing.Drawing
import lifeapp.life.{Generation, LifeEngine}

/**
  *
  * @author Dmitry Openkov
  */
class MainController(lifeEngine: LifeEngine) {
  private val drawing: Drawing = new Drawing(lifeEngine)
  val statusLabel = new JLabel()
  SwingUtilities.invokeLater(() => createAndShowGUI(createPanel()))
  lifeEngine.subscribe((g: Generation) => {
    statusLabel.setText(s"# ${g.n}, number of cells ${g.cells.size}")
  })

  private def createPanel(): JPanel = {
    // create a toolbar
    val toolBar = new JToolBar
    val start = createStartButton

    val zoomIn = new JButton(createIcon("general/ZoomIn"))
    zoomIn.addActionListener((e: ActionEvent) => drawing.zoomIn())
    val zoomOut = new JButton(createIcon("general/ZoomOut"))
    zoomOut.addActionListener((e: ActionEvent) => drawing.zoomOut())
    val buttonGroup = new ButtonGroup
    buttonGroup.add(start)
    buttonGroup.add(zoomIn)
    buttonGroup.add(zoomOut)
    toolBar.add(start)
    toolBar.add(zoomIn)
    toolBar.add(zoomOut)
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

  private def createStartButton = {

    val playIcon: ImageIcon = createIcon("media/Play")
    val pauseIcon: ImageIcon = createIcon("media/Pause")
    val button = new JButton(playIcon)
    button.setToolTipText("Play")
    button.addActionListener((e: ActionEvent) => {
      if (drawing.isRunning) {
        drawing.stop()
        button.setIcon(playIcon)
      } else {
        drawing.play()
        button.setIcon(pauseIcon)
      }
    })
    button
  }

  private def createAndShowGUI(value: JComponent): Unit = {
    val frame = new JFrame("Game of Life")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(value)
    frame.pack()
    frame.setVisible(true)
  }
}