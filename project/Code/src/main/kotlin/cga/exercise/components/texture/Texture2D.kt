package cga.exercise.components.texture

import cga.framework.GLError.checkEx
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer
import java.nio.IntBuffer


/**
 * Created by Fabian on 16.09.2017.
 */
class Texture2D(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean): ITexture{
    private var texID: Int = -1

    init {
        try {
            processTexture(imageData, width, height, genMipMaps)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
    companion object {
        //create texture from file
        //don't support compressed textures for now
        //instead stick to pngs
        operator fun invoke(path: String, genMipMaps: Boolean): Texture2D {
            val x = BufferUtils.createIntBuffer(1)
            val y = BufferUtils.createIntBuffer(1)
            val readChannels = BufferUtils.createIntBuffer(1)
            //flip y coordinate to make OpenGL happy
            STBImage.stbi_set_flip_vertically_on_load(true)
            val imageData = STBImage.stbi_load(path, x, y, readChannels, 4)
                    ?: throw Exception("Image file \"" + path + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())

            try {
                return Texture2D(imageData, x.get(), y.get(), genMipMaps)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                throw ex
            } finally {
                STBImage.stbi_image_free(imageData)
            }
        }
    }

    override fun processTexture(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) {
        // Texture-ID generieren
        texID = GL11.glGenTextures()
        // texture aktivieren
        glBindTexture(GL_TEXTURE_2D,texID)

        // Texturdaten definieren
            // target: 2D/3D/...
            // level (Detaillierungsgrad), internalFormat (Speicherung der kanäle), MipMaplevel
            // internalFormat, breite, höhe
            // target: einlesen, format: speichern
            // data: eigentliche Daten
        glTexImage2D(GL_TEXTURE_2D,0, GL_RGBA8,width,height,0, GL_RGBA, GL_UNSIGNED_BYTE,imageData)

        // generiert Mipmaps für die Textur
        if(genMipMaps)
            GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D)

        unbind()
    }

    override fun setTexParams(wrapS: Int, wrapT: Int, minFilter: Int, magFilter: Int) {
        glBindTexture(GL30.GL_TEXTURE_2D, texID)

        // TEX_WRAP -> wrapping (texture coordinates outside the range [0, 1] should be clamped to the edge of the texture)
        // GL_LINEAR -> Mag-/ Minification (blending, wenn Qualität niedig/ höher ist)
        // -> wrapping mode, für die x- und y-Achse
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS)
        glTexParameteri(GL_TEXTURE_2D,  GL_TEXTURE_WRAP_T, wrapT)
        // texturfiltermodus für verkleinerung/ vergrößerung
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter)

        // 3.5 Schärfeeindruck
        glTexParameterf(
            GL_TEXTURE_2D,
            EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
            16.0f
        )

       unbind()
    }

    override fun bind(textureUnit: Int) {
        // Textureinheit aktivieren
        glActiveTexture(GL_TEXTURE0 + textureUnit)
        // Textur-ID an Textureinheit binden
        glBindTexture(GL_TEXTURE_2D, texID)
    }


    override fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun cleanup() {
        unbind()
        if (texID != 0) {
            GL11.glDeleteTextures(texID)
            texID = 0
        }
    }
}