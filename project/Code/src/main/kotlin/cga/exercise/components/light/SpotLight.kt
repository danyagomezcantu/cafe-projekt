package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Math.toRadians
import org.joml.Vector3f
import org.joml.Matrix4f
import org.joml.Math.cos

class SpotLight(lightPos : Vector3f,
                override var lightColor : Vector3f,
                parent : Transformable?,
                private val innerCone : Float, private  val outerCone: Float )
   : PointLight(lightPos, lightColor, parent), ISpotLight{


   override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f){
      shaderProgram.setUniform("lightPos",getWorldPosition())
      shaderProgram.setUniform("lightColor",lightColor)
      shaderProgram.setUniform("innerCone",toRadians(innerCone))
      shaderProgram.setUniform("outerCone",toRadians(outerCone))

   }

   fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
      super.bind(shaderProgram, name)
      shaderProgram.setUniform("${name}Direction", viewMatrix.transformDirection(getWorldZAxis().negate())) //World Space Vektor -> View Space Vektor
      shaderProgram.setUniform("${name}InnerCone", cos(toRadians(innerCone)))
      shaderProgram.setUniform("${name}OuterCone", cos(toRadians(outerCone)))
   }
}