package cga.exercise.components.geometry

import org.lwjgl.opengl.GL30.*
import cga.exercise.components.shader.ShaderProgram

/**
 * Creates a Mesh object from vertex data, index data, and a given set of vertex attributes.
 *
 * @param vertexData  Plain float array of vertex data.
 * @param indexData   Index data.
 * @param attributes  Vertex attributes contained in vertex data.
 * @throws Exception  If the creation of the required OpenGL objects fails, an exception is thrown.
 *
 * Created 29.03.2023.
 */
class Mesh(vertexData: FloatArray, indexData: IntArray, attributes: Array<VertexAttribute>, val material:Material? = null) {

    // Private data
    private var vaoId = 0
    private var vboId = 0
    private var iboId = 0
    private var indexCount = indexData.size


    init {


        // Create and bind a VAO (Vertex Array Object)
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create and bind a VBO (Vertex Buffer Object) for vertex data
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        // Create and bind an IBO (Index Buffer Object) for index data
        iboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW)

        // Define the vertex attributes

        for (i in attributes.indices) {
            glVertexAttribPointer(
                i, attributes[i].n, attributes[i].type, false, attributes[i].stride, attributes[i].offset.toLong()
            )
            glEnableVertexAttribArray(i)
        }

        // Unbind the VAO and buffers
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    /**
     * Renders the mesh.
     */
    fun render() {
        // Bind the VAO and draw the elements
        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0) // Unbind the VAO to prevent unintended changes
    }

    // 3.2
    //render()-Methode, welche als Parameter ein ShaderProgram entgegennimmt
    //Material wir gebunden (falls vorhanden)
    //und rendern im Anschluss wie gewohnt

    fun render(shaderProgram : ShaderProgram) {
        // bind Methode aus 3.2 wird aufgerufen
        material?.bind(shaderProgram)
        render()
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh.
     */
    fun cleanup() {
        if (iboId != 0) glDeleteBuffers(iboId)
        if (vboId != 0) glDeleteBuffers(vboId)
        if (vaoId != 0) glDeleteVertexArrays(vaoId)
    }
}

/*
package cga.exercise.components.geometry

import org.lwjgl.opengl.GL30.*

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexData plain float array of vertex data
 * @param indexData  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created 29.03.2023.
 */

class Mesh(vertexData: FloatArray, indexData: IntArray, attributes: Array<VertexAttribute>) {


    //private data
    private var vaoId = 0
    private var vboId = 0
    private var iboId = 0
    private var indexcount = indexData.size // //Anzahl Eckpunkte der Dreiecke


    //  1.2.2 Mesh-Klasse
    init {

// 1. Erstellen und Binden eines VAO.

        //int glBindVertexArray(int vaoID);

        vaoId = glGenVertexArrays() // erstellen
        glBindVertexArray(vaoId)    //binden

// 2. Binden und aktivieren von VBO und IBO

        //int glGenBuffers();
        //int glBindBuffer(int target, int bufferID);
        //target besteht aus:
        //GL_ARRAY_BUFFER - array wo nur Daten drinnen stehen z.b position für die vertices
        //GL_ELEMENT_ARRAY_BUFFER - enthält indices in einen anderen VBO

        // The vertex buffer is bound to GL_ARRAY_BUFFER, and the index buffer is bound to GL_ELEMENT_ARRAY_BUFFER

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        iboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId)

//3. Füllen von VBO und IBO mit Daten

        //glBufferData(int target, {int, float, ...} [] data, int usage);
        //target: GL_ARRAY_BUFFER (element(vertices))/ GL_ELEMENT_ARRAY_BUFFER(index buffer)
        //data: Pointer zum ersten Datenelement, dass gespeichert werden soll
        //usage: wie werden diese daten benutzt, wie oft greift man darauf zu, häufige Veränderungen etc
        //GL_STATIC-DRAW - gut, wenn Daten ein Mal initialisiert werden und dann nicht mehr verändert werden
        // und oft rendert z.b statische Objekte in einer Szene
        //GL-DYNAMIC-DRAW - gut, wenn Daten häufig verändert und gerendert werden
        //GL-STREAM-DRAW - gut, wenn manchmal Daten geändert und nur paar Mal gerendert werden


        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW)

// 4. Aktivieren und definieren Sie die entsprechenden Vertices attributes

        //definiert die Vertex-Attribute, indem es das Attribute-Array durchläuft und glVertexAttribPointer() aufruft,
        // um anzugeben, wie die Daten im Vertex-Puffer interpretiert werden sollen.
        // Es aktiviert auch jedes Attribut mit glEnableVertexAttribArray()

        //glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset);

        for (i in attributes.indices) {  //schleife der indexe des Attributs   //attributes: Array<VertexAttribute>
            glVertexAttribPointer( // jeder Attributpointer, gibt an wie Attributdaten im VBO formatiert werden
                i, attributes[i].n, attributes[i].type, false, attributes[i].stride, attributes[i].offset.toLong()
                //übergebende Parameter:
                //i: Der Index des zu definierenden Attributs. Dies entspricht der Position des Attributs im Shader-Programm.
                // n: Die Anzahl der Komponenten in den Attributdaten.
                //type: Der Datentyp des Attributs. GL_FLOAT, GL_INT, GL_UNSIGNED_BYTE ...
                //false: Ein Flag, das angibt, ob die Attributdaten normalisiert werden sollten, bevor sie verwendet werden.
                // stride: Der Byte-Offset zwischen aufeinanderfolgenden Attributen im Vertexpuffer.
                // offset.toLong(): Der Byte-Offset der ersten Komponente des Attributs im Vertexpuffer.
            )
            glEnableVertexAttribArray(i)
            // aktiviert der Code jedes Attribut mithilfe der Funktion glEnableVertexAttribArray.
            // Diese Funktion gibt an, welche Attribute aktiv sind und beim Rendern des Modells verwendet werden sollen.


// 5. Danach sollte alles gelöst werden (unbind), um versehentliche Änderungen an den Buffern und VAO zu vermeiden.
            //glBindVertexArray(0) // unbind VAO
            //glBindBuffer(GL_ARRAY_BUFFER, 0)
            //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

        }
    }

    /**
     * Renders the mesh
     */
    fun render() {


        //Diese Funktion rendert das Mesh, indem sie das VAO mit glBindVertexArray() bindet,
        // die Elemente mit glDrawElements() zeichnet und das VAO mit glBindVertexArray(0) entbindet.

        // 6) Binden Sie das VAO.
        glBindVertexArray(vaoId)

        // 7) Zeichnen Sie die Elemente.
        glDrawElements(GL_TRIANGLES, indexcount, GL_UNSIGNED_INT, 0)
        //glDrawElements(int mode, int count, int type, java.nio.{Byte,Float,…}Buffer indices);
        // type der Geometerie, Anzahl de rzu rendernden Objekte, Datentyp des Indizes-Arrays, Offset des Indexarrays

        // 8) Lösen der Bindung, um versehentliche Änderungen am VAO zu vermeiden, leeres Vertex Array, verhindert ungewollte Seiteneffekte
        glBindVertexArray(0)
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (iboId != 0) glDeleteBuffers(iboId)
        if (vboId != 0) glDeleteBuffers(vboId)
        if (vaoId != 0) glDeleteVertexArrays(vaoId)
    }
}

 */