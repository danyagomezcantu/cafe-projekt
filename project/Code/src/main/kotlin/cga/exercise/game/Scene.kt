package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import java.io.File

class Scene(private val window: GameWindow) {

    // Shader program
    private val staticShader: ShaderProgram = ShaderProgram(
        "assets/shaders/tron_vert.glsl",
        "assets/shaders/tron_frag.glsl"
    )

    // Renderable for Brioche
    private lateinit var brioche: Renderable
    private lateinit var camera: TronCamera

    init {
        // Initialize Brioche and camera
        initializeBrioche()
        initializeCamera()

        glClearColor(0.0f, 1.0f, 0.0f, 1.0f)  // Bright green background for visibility
        enableFaceCulling(GL_CCW, GL_BACK)
        enableDepthTest(GL_LEQUAL)
    }

    private fun initializeBrioche() {
        // Load Brioche's model
        brioche = loadModel("assets/models/humans/OBJs/brioche_male_barista/brioche_with_empty_hand_smiling.obj")

        // Reset scale and position to defaults
        brioche.scaleLocal(Vector3f(1.0f, 1.0f, 1.0f))  // Default scale
        brioche.translateLocal(Vector3f(0.0f, 0.0f, 0.0f))  // Default position
    }

    private fun initializeCamera() {
        camera = TronCamera()
        camera.translateLocal(Vector3f(0.0f, 1.0f, 3.0f))  // Bring the camera closer for inspection
    }

    private fun loadModel(path: String): Renderable {
        val res = OBJLoader.loadOBJ(path)
        val obj = res.objects[0].meshes[0]

        // Use a simple material (solid color) to ensure visibility
        val solidColor = Texture2D("assets/textures/texture.png", true)  // Replace with any default texture
        val material = Material(solidColor, solidColor, solidColor, 60.0f, Vector2f(64.0f), Vector3f(1.0f, 0.0f, 0.0f))  // Red color for visibility

        val attrPos = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attrTC = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attrNorm = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttributes = arrayOf(attrPos, attrTC, attrNorm)

        val mesh = Mesh(obj.vertexData, obj.indexData, objAttributes, material)
        return Renderable(mutableListOf(mesh))
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()

        // Bind the camera to the shader
        camera.bind(staticShader)

        // Render Brioche
        brioche.render(staticShader)
    }

    fun update(dt: Float, t: Float) {
        // Simple controls for moving Brioche around for testing
        if (window.getKeyState(GLFW_KEY_W)) {
            brioche.translateLocal(Vector3f(0f, 0f, -5f * dt))
            if (window.getKeyState(GLFW_KEY_A)) brioche.rotateLocal(0f, 5f * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) brioche.rotateLocal(0f, -5f * dt, 0f)
        }
        if (window.getKeyState(GLFW_KEY_S)) {
            brioche.translateLocal(Vector3f(0f, 0f, 1.5f * dt))
            if (window.getKeyState(GLFW_KEY_A)) brioche.rotateLocal(0f, -1.5f * dt, 0f)
            if (window.getKeyState(GLFW_KEY_D)) brioche.rotateLocal(0f, 1.5f * dt, 0f)
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}

    fun enableFaceCulling(orientation: Int, faceToCull: Int) {
        glEnable(GL_CULL_FACE)
        glFrontFace(orientation)
        glCullFace(faceToCull)
    }

    fun enableDepthTest(comparisonSpecs: Int) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(comparisonSpecs)
    }
}
