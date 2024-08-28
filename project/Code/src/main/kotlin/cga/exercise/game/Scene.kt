package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GameWindow
import cga.framework.OBJLoader
import cga.framework.ModelLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import cga.exercise.components.geometry.Material
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight

class Scene(private val window: GameWindow) {

    // Shader program
    private val staticShader: ShaderProgram = ShaderProgram(
        "assets/shaders/tron_vert.glsl",
        "assets/shaders/tron_frag.glsl"
    )

    // Renderables
    private lateinit var skyboxRenderable: Renderable
    private lateinit var coffeeShopRenderable: Renderable
    private lateinit var coffeeCupRenderable: Renderable
    private lateinit var brioche: Renderable
    private lateinit var hans: Renderable
    private lateinit var briocheWithCoffee: Renderable
    private lateinit var hansWithCoffee: Renderable

    // Camera
    private lateinit var camera: TronCamera

    // SpotLight
    private lateinit var spotLight: SpotLight

    init {
        // Initialize models and camera
        initializeModels()
        initializeCamera()

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        enableFaceCulling(GL_CCW, GL_BACK)
        enableDepthTest(GL_LEQUAL)
    }

    private fun initializeModels() {
        // Load models
        skyboxRenderable = loadModel("assets/models/skybox/Skybox.obj")
        coffeeShopRenderable = loadModel("assets/models/coffee_shop/coffee_shop.obj")
        coffeeCupRenderable = loadModel("assets/models/coffee_cup/coffee_cup_obj.obj")
        brioche = loadModel("assets/models/humans/OBJs/brioche_male_barista/brioche_with_empty_hand_or_working.obj")
        hans = loadModel("assets/models/humans/OBJs/hans_client/hans_sitting_waiting.obj")
        briocheWithCoffee = loadModel("assets/models/humans/OBJs/brioche_male_barista/brioche_standing_smiling_with_coffee.obj")
        hansWithCoffee = loadModel("assets/models/humans/OBJs/hans_client/hans_sitting_happy_with_coffee.obj")

        // Add models to the scene
        skyboxRenderable.parent = null
        coffeeShopRenderable.parent = null
        coffeeCupRenderable.parent = null
        brioche.parent = null
        hans.parent = null
    }

    private fun initializeCamera() {
        camera = TronCamera(parent = brioche)
        camera.rotateLocal(-org.joml.Math.toRadians(35.0f), 0.0f, 0.0f)
        camera.translateLocal(Vector3f(0.0f, 0.0f, 4.0f))
    }

    private fun loadModel(path: String): Renderable {
        val res = OBJLoader.loadOBJ(path)
        val obj = res.objects[0].meshes[0]

        val textureDiff = Texture2D("assets/textures/texture.png", true)
        val textureEmit = Texture2D("assets/textures/texture.png", true)
        val textureSpec = Texture2D("assets/textures/texture.png", true)

        textureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        textureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        textureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val material = Material(textureDiff, textureEmit, textureSpec, 60.0f, Vector2f(64.0f), Vector3f(0f, 250f, 0f))

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

        // Render the scene objects
        skyboxRenderable.render(staticShader)
        coffeeShopRenderable.render(staticShader)
        coffeeCupRenderable.render(staticShader)
        brioche.render(staticShader)
        hans.render(staticShader)
    }

    fun update(dt: Float, t: Float) {
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

        if (window.getKeyState(GLFW_KEY_P)) {
            switchBriocheModel()
        }
        if (window.getKeyState(GLFW_KEY_L)) {
            switchHansModel()
        }
    }

    private fun switchBriocheModel() {
        // Save the position and rotation
        val position = brioche.getWorldPosition()
        val yaw = brioche.getYaw()
        val pitch = brioche.getPitch()
        val roll = brioche.getRoll()

        // Switch to the model with the coffee
        brioche = briocheWithCoffee

        // Apply the saved transformation
        brioche.translateLocal(position)
        brioche.rotateLocal(pitch, yaw, roll)
        brioche.parent = null // Detach from the parent
        camera.parent = brioche // Attach camera to the new model
    }

    private fun switchHansModel() {
        // Save the position and rotation
        hansWithCoffee.parent = null
        hansWithCoffee.translateLocal(hans.getWorldPosition())
        hansWithCoffee.rotateLocal(hans.getPitch(), hans.getYaw(), hans.getRoll())

        // Replace the model
        hans = hansWithCoffee
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        // Handle mouse movements here
    }

    fun cleanup() {}

    /**
     * enables culling of specified faces
     * orientation: ordering of the vertices to define the front face
     * faceToCull: specifies the face that will be culled (back, front)
     */
    fun enableFaceCulling(orientation: Int, faceToCull: Int) {
        glEnable(GL_CULL_FACE)
        glFrontFace(orientation)
        glCullFace(faceToCull)
    }

    /**
     * enables depth test
     * comparisonSpecs: specifies the comparison that takes place during the depth buffer test
     */
    fun enableDepthTest(comparisonSpecs: Int) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(comparisonSpecs)
    }
}
