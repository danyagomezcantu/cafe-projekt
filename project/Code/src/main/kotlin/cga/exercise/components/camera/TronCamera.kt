package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Math
import org.joml.Matrix4f

class TronCamera(
    parent : Transformable? = Transformable(),
    val fov : Float = Math.toRadians(90.0f),
    val ratio : Float = (16f/9f),
    val near : Float = 0.1f,
    val far : Float = 100.0f) : ICamera, Transformable(parent = parent) {

    override fun getCalculateViewMatrix(): Matrix4f =
        Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()),getWorldYAxis())

    override fun getCalculateProjectionMatrix(): Matrix4f = Matrix4f().perspective(fov, ratio, near, far)

    override fun bind(shader: ShaderProgram) {
        val viewMatrix = getCalculateViewMatrix()
        val projectionMatrix = getCalculateProjectionMatrix()
        shader.setUniform("view_matrix", viewMatrix, false)
        shader.setUniform("projection_matrix", projectionMatrix, false)
    }
}