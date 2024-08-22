package cga.exercise.components.light


import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f


open class PointLight(lightPos : Vector3f, open val lightColor : Vector3f,parent : Transformable?) : Transformable(Matrix4f(), parent), IPointLight {

    val constAttenuation : Float
        get() = if (this is SpotLight) 0.5f else 1.0f

    val linAttenuation : Float
        get() = if (this is SpotLight) 0.05f else 0.5f

    val quadAttenuation : Float
        get() = if (this is SpotLight) 0.01f else 0.1f


    init{
        translateGlobal(lightPos)
    }

    override fun bind(shaderProgram: ShaderProgram) {
       shaderProgram.setUniform("lightPos",getWorldPosition())
        shaderProgram.setUniform("lightColor",lightColor)

    }

    fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.use()

        shaderProgram.setUniform("${name}Position", getWorldPosition())
        shaderProgram.setUniform("${name}Color", Vector3f(lightColor).mul(1f / 255f))
        shaderProgram.setUniform("${name}Attenuation", Vector3f(constAttenuation, linAttenuation, quadAttenuation))
    }

    fun bind(shaderProgram: ShaderProgram, index: Int) {
        shaderProgram.use()

        shaderProgram.setUniform("pointLights[${index}].position", getWorldPosition())
        shaderProgram.setUniform("pointLights[${index}].color", Vector3f(lightColor).mul(1f/255f))
        shaderProgram.setUniform("pointLights[${index}].attenuation", Vector3f(constAttenuation, linAttenuation, quadAttenuation))
    }
}