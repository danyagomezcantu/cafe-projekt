package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3i

class Material(
    var diff: Texture2D,
    var emit: Texture2D,
    var specular: Texture2D,
    var shininess: Float = 20.0f,
    var tcMultiplier: Vector2f = Vector2f(1.0f),
    var emitColor: Vector3f = Vector3f (255f)
){

    fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
        diff.bind(0)
        emit.bind(1)
        specular.bind(2)
        shaderProgram.setUniform("diffTex", 0)
        shaderProgram.setUniform("emitTex", 1)
        shaderProgram.setUniform("specTex", 2)
        shaderProgram.setUniform("shininess", shininess)
        shaderProgram.setUniform("emitColor", Vector3f(emitColor).mul(1f/255f))
        }

    fun unbind() {
        emit.unbind()
    }
}

