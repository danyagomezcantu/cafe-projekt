package cga.exercise.game
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
class CameraController {
    private var firstPersonView = true

    fun switchView() {
        firstPersonView = !firstPersonView
        println("Kameraperspektive gewechselt zu: ${if (firstPersonView) "First-Person" else "Third-Person"}")
    }

    fun updateCameraPosition(baristaPosition: Vector3f) {
        if (firstPersonView) {
            println("Kamera auf First-Person bei Barista Position $baristaPosition")
        } else {
            println("Kamera auf Third-Person bei Barista Position $baristaPosition")
        }
    }
}