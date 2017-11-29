package lifeapp.application

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage


/**
  *
  * @author Dmitry Openkov
  */
class Executable extends Application {

  override def start(primaryStage: Stage): Unit = {
    val scene: Scene = FXMLLoader.load(getClass.getResource("/lifeapp/ui/MainScene.fxml"))
    primaryStage.setScene(scene)
    primaryStage.centerOnScreen()
    primaryStage.show()
  }
}

object Executable {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Executable], args:_*)
  }

}
