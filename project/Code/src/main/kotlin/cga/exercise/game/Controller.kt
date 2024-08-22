package cga.exercise.game
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class Controller {
    private var position = Vector3f(0f, 0f, 0f)

    fun updateInput(window: Long) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            position.z -= 0.1f
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            position.z += 0.1f
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            position.x -= 0.1f
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            position.x += 0.1f
        }
    }

    fun getPosition(): Vector3f {
        return position
    }

    fun interactWithStation() {
        println("Interagiert mit Kaffeestation")
    }

    fun serveDrink() {
        println("Getränk serviert")
    }
}