package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class Renderable(val meshes: MutableList<Mesh>) : Transformable(), IRenderable {



        override fun render(shaderProgram: ShaderProgram) {
            for (mesh in meshes) {
                shaderProgram.setUniform("model_matrix", getLocalModelMatrix(), false)
                mesh.render(shaderProgram)
            }
        }
    }



