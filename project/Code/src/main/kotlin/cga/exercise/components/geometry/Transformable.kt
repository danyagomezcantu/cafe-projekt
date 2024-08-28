package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f
//speichert/verwaltet Modelmatritzen
open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    fun getLocalModelMatrix(): Matrix4f = Matrix4f(modelMatrix)

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix -> alle transformations, auch der parent-objekte
     */
    fun getWorldModelMatrix(): Matrix4f = parent?.getWorldModelMatrix()?.mul(modelMatrix) ?: getLocalModelMatrix()

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        modelMatrix.rotateXYZ(pitch,yaw,roll)
    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val transposedModelMatrix4f =
            Matrix4f().translate(altMidpoint).rotateXYZ(pitch,yaw,roll).translate(altMidpoint.negate())
        modelMatrix = transposedModelMatrix4f.mul(modelMatrix)
    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translateLocal(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun translateGlobal(deltaPos: Vector3f) {
        val translation = Matrix4f().setTranslation(deltaPos)
        modelMatrix = translation.mul(modelMatrix)
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scaleLocal(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f = modelMatrix.getTranslation(Vector3f())

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f = getWorldModelMatrix().getTranslation(Vector3f())

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        val localModelMatrix = getLocalModelMatrix()
        return Vector3f(localModelMatrix.m00(), localModelMatrix.m01(), localModelMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        val localModelMatrix = getLocalModelMatrix()
        return Vector3f(localModelMatrix.m10(), localModelMatrix.m11(), localModelMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        val localModelMatrix = getLocalModelMatrix()
        return Vector3f(localModelMatrix.m20(), localModelMatrix.m21(), localModelMatrix.m22()).normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        var worldModelMatrix = getWorldModelMatrix()
        return Vector3f(worldModelMatrix.m00(), worldModelMatrix.m01(), worldModelMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        var worldModelMatrix = getWorldModelMatrix()
        return Vector3f(worldModelMatrix.m10(), worldModelMatrix.m11(), worldModelMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        var worldModelMatrix = getWorldModelMatrix()
        return Vector3f(worldModelMatrix.m20(), worldModelMatrix.m21(), worldModelMatrix.m22()).normalize()
    }

    /**
     * Returns the yaw (rotation around Y-axis) from the rotation matrix.
     */
    fun getYaw(): Float {
        return Math.atan2(modelMatrix.m20().toDouble(), modelMatrix.m00().toDouble()).toFloat()
    }

    /**
     * Returns the pitch (rotation around X-axis) from the rotation matrix.
     */
    fun getPitch(): Float {
        return Math.asin((-modelMatrix.m21()).toDouble()).toFloat()
    }

    /**
     * Returns the roll (rotation around Z-axis) from the rotation matrix.
     */
    fun getRoll(): Float {
        return Math.atan2(modelMatrix.m12().toDouble(), modelMatrix.m11().toDouble()).toFloat()
    }
}


