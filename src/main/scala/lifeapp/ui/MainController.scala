package lifeapp.ui

import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing._

import lifeapp.drawing.Drawing
import lifeapp.life.Generation

/**
  *
  * @author Dmitry Openkov
  */
class MainController(generation: Generation) {
  private val drawing: Drawing = new Drawing(generation)
  SwingUtilities.invokeLater(() => createAndShowGUI(createPanel()))

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

  import javax.swing.JFrame

  private def createAndShowGUI(value: JComponent): Unit = {
    val frame = new JFrame("Game of Life")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.add(value)
    frame.pack()
    frame.setVisible(true)
  }
}