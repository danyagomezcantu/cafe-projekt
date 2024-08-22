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
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import kotlin.math.sin


class Scene(private val window: GameWindow) {

    //neuer Tron-Shader
    private val staticShader: ShaderProgram =
        ShaderProgram(
            "assets/shaders/tron_vert.glsl",
            "assets/shaders/tron_frag.glsl"
        )

    private val floorRotation: Float = (90.0f * Math.PI / 180.0f).toFloat()
    private val floorScale: Float = 0.03f
    private val sphereScale: Float = 0.5f

    lateinit var groundRenderable: Renderable
    lateinit var sphereRenderable: Renderable
    private val lightCycle: Renderable?
    lateinit var camera: TronCamera

    lateinit var spotLight : SpotLight

    lateinit var  PointLight0 : PointLight
    lateinit var  PointLight1 : PointLight
    lateinit var  PointLight2 : PointLight
    lateinit var  PointLight3 : PointLight

    lateinit var pointLights: Array<PointLight>

    init {

        // Load ground object
        val resGround = OBJLoader.loadOBJ("assets/models/ground.obj")
        val objGround = resGround.objects[0].meshes[0]

        // Load sphere object
        val resSphere = OBJLoader.loadOBJ("assets/models/sphere.obj")
        val objSphere = resSphere.objects[0].meshes[0]


        var textureDiff = Texture2D("assets/textures/ground_diff.png",true) //TEXTUREi(i=0)
        textureDiff.setTexParams(GL_REPEAT,GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)
        var textureEmit = Texture2D("assets/textures/ground_emit.png",true) //TEXTUREi(i=1)
        textureEmit.setTexParams(GL_REPEAT,GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_NEAREST)
        var textureSpec = Texture2D("assets/textures/ground_Spec.png",true) //TEXTUREi(i=2)
        textureSpec.setTexParams(GL_REPEAT,GL_REPEAT, GL_LINEAR_MIPMAP_NEAREST, GL_NEAREST)

        var groundMaterial = Material(textureDiff, textureEmit, textureSpec, 60.0f, Vector2f(64.0f), Vector3f(0f,250f,0f));

        // Define vertex attributes for the sphere and ground
        val attrPos = VertexAttribute(3, GL_FLOAT, 32, 0)
        val attrTC = VertexAttribute(2, GL_FLOAT, 32, 12)
        val attrNorm = VertexAttribute(3, GL_FLOAT, 32, 20)
        val objAttributes = arrayOf(attrPos, attrTC, attrNorm)

        lightCycle = ModelLoader.loadModel(
            "assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj",
            org.joml.Math.toRadians(-90.0f),
            org.joml.Math.toRadians(90.0f),
            0.0f
        )


        lightCycle?.scaleLocal(Vector3f(0.8f))

        //Define meshes
        val groundMesh = Mesh(objGround.vertexData, objGround.indexData, objAttributes, groundMaterial)
        val sphereMesh = Mesh(objSphere.vertexData, objSphere.indexData, objAttributes)

        groundRenderable = Renderable(mutableListOf(groundMesh))
        sphereRenderable = Renderable(mutableListOf(sphereMesh))

        //Eckpunktlichter
        PointLight0 = PointLight(Vector3f(-15.0f, 1.0f, 15.0f), Vector3f(0f, 255f,0f), parent = null)
        PointLight1=  PointLight(Vector3f(-15.0f, 1.0f, -15.0f), Vector3f(255f, 255f,0f), parent = null)
        PointLight2 = PointLight(Vector3f(15.0f, 1.0f, -15.0f), Vector3f(0f, 255f,255f), parent = null)
        PointLight3 = PointLight(Vector3f(15.0f, 1.0f, 15.0f), Vector3f(255f, 0f,0f), parent = null)

        //Scheinwerfer
        spotLight = SpotLight(Vector3f(0F,2F,-2F), Vector3f(100F,100F,100F),innerCone = 10.0f,
            outerCone = 50.0f,parent = lightCycle)


        // Camera
        camera = TronCamera(parent = lightCycle)

        // Kamera umpositionieren
        camera.rotateLocal(org.joml.Math.toRadians(-35.0f),0.0f, 0.0f)
        camera.translateLocal(Vector3f(0.0f, 0.0f,4.0f))

        enableFaceCulling(GL_CCW, GL_BACK)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        enableDepthTest(GL_LEQUAL)

    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()

        // Update camera matrices
        camera.bind(staticShader)

        spotLight.bind(staticShader, "spotLight", camera.getCalculateViewMatrix())

        PointLight0.bind(staticShader, 0)
        PointLight1.bind(staticShader, 1)
        PointLight2.bind(staticShader, 2)
        PointLight3.bind(staticShader, 3)

        // Render objects
        groundRenderable.render(staticShader)
        lightCycle?.render(staticShader)
    }

    fun update(dt: Float, t: Float) {

        if(window.getKeyState(GLFW_KEY_W)) {
            lightCycle?.translateLocal(Vector3f(0f, 0f, -5f * dt))
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle?.rotateLocal(0f, 5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle?.rotateLocal(0f, -5f * dt, 0f)
        }
        if(window.getKeyState(GLFW_KEY_S)) {
            lightCycle?.translateLocal(Vector3f(0f, 0f, 1.5f * dt))
            if(window.getKeyState(GLFW_KEY_A))
                lightCycle?.rotateLocal(0f, -1.5f * dt, 0f)
            if(window.getKeyState(GLFW_KEY_D))
                lightCycle?.rotateLocal(0f, 1.5f * dt, 0f)
        }


        val redPart = ((sin(t) + 1.0f) / 2 * 255)
        val greenPart = (((sin((t * 2)) + 1f) / 2) * 255)
        val bluePart = ((sin((t * 3)) + 1f) / 2f * 255)

        lightCycle?.meshes?.get(2)?.material?.emitColor = Vector3f(redPart, greenPart, bluePart)
        spotLight.lightColor = Vector3f (redPart, greenPart, bluePart)



    }
    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    var lastMousePosX : Double = 0.0
    var lastMousePosY : Double = 0.0
    fun onMouseMove(xpos: Double, ypos: Double) {
        val yaw = (lastMousePosX - xpos).toFloat() * 0.0002f
        val pitch = 0.0f

        camera.rotateAroundPoint(pitch, yaw, 0.0f, Vector3f(0.0f))
        lastMousePosX = xpos
        lastMousePosY = ypos
    }


    fun cleanup() {}

    /**
     * enables culling of specified faces
     * orientation: ordering of the vertices to define the front face
     * faceToCull: specifies the face that will be culled (back, front)
     * Please read the docs for accepted parameters
     */
    fun enableFaceCulling(orientation: Int, faceToCull: Int) {
        glEnable(GL_CULL_FACE)
        glFrontFace(orientation)
        glCullFace(faceToCull)
    }

    /**
     * enables depth test
     * comparisonSpecs: specifies the comparison that takes place during the depth buffer test
     * Please read the docs for accepted parameters
     */
    fun enableDepthTest(comparisonSpecs: Int) {
        glEnable(GL_DEPTH_TEST)
        glDepthFunc(comparisonSpecs)
    }
}
