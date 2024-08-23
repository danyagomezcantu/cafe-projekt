package cga.exercise.game
import org.joml.Vector3f

class Customer (val id: Int, var position: Vector3f, var order: String) {
    var atTable = false

    fun moveToTable(tablePosition: Vector3f) {
        position = tablePosition
        atTable = true
        println("Kunde $id hat sich zu Tisch an Position $position gesetzt.")
    }

    fun moveForwardToCounter(targetPosition: Vector3f) {
        position = targetPosition
        println("Kunde $id bewegt sich zum Tresen an Position $position.")
    }

    fun generateOrder(availableOrders: List<String>) {
        order = availableOrders.random()
        println("Kunde $id hat Bestellung aufgegeben: $order")
    }
}