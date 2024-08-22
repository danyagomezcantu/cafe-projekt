package cga.exercise.game
import org.lwjgl.glfw.GLFW.*


class GameMechanics(private val window: Long) {
    private val baristaController = Controller()
    private val orderManager = OrderManager()
    private val coffeeStation = CoffeeStation()

    fun update() {
        baristaController.updateInput(window)

        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            orderManager.takeOrder()
        }

        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            orderManager.confirmOrder()
        }

        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
            coffeeStation.startPreparation()
        }

        if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) {
            if (coffeeStation.isReady()) {
                baristaController.serveDrink()
            }
        }

        // Simulierte Fortschrittsaktualisierung der Zubereitung
        coffeeStation.updateProgress()
    }
}
