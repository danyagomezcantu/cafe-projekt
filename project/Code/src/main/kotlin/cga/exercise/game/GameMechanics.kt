package cga.exercise.game
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class GameMechanics (private val window: Long){
    private val baristaController = BaristaController()
    private val customerManager = CustomerManager()
    private val orderManager = OrderManager(customerManager)
    private val coffeeStation = CoffeStation()
    private val cameraController = CameraController()

    init {
        // Beispielhafte Kunden-Initialisierung
        customerManager.addCustomer(Customer(1, Vector3f(1f, 0f, 1f), ""))
        customerManager.addCustomer(Customer(2, Vector3f(2f, 0f, 1f), ""))
        customerManager.addCustomer(Customer(3, Vector3f(3f, 0f, 1f), ""))
    }

    fun update() {
        baristaController.updateInput(window)

        // Bestellung für den ersten Kunden aufnehmen und anzeigen
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            val firstCustomer = customerManager.getFirstCustomer()
            orderManager.takeOrder(firstCustomer.id)
        }

        // Bestellung für den ersten Kunden bestätigen und den nächsten Kunden vorziehen
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) {
            val firstCustomer = customerManager.getFirstCustomer()
            orderManager.confirmOrder(firstCustomer.id)
        }

        // Kaffeezubereitung starten
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
            coffeeStation.startPreparation()
        }

        // Getränk servieren, nachdem die Zubereitung abgeschlossen ist
        if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) {
            if (coffeeStation.isReady()) {
                coffeeStation.completeOrder(1)
                baristaController.serveDrink()
            }
        }

        coffeeStation.updateProgress()

        // Kamera aktualisieren
        if (glfwGetKey(window, GLFW_KEY_V) == GLFW_PRESS) {
            cameraController.switchView()
        }
        cameraController.updateCameraPosition(baristaController.getPosition())
    }
}