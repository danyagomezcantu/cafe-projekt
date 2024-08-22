package cga.exercise.game
import java.util.*

class OrderManager {
    private val orders = listOf("Latte", "Cappuccino", "Espresso", "Mocha")
    private var currentOrder: String? = null

    fun takeOrder() {
        currentOrder = orders[Random().nextInt(orders.size)]
        println("Bestellung aufgenommen: $currentOrder")
    }

    fun confirmOrder() {
        println("Bestellung bestätigt: $currentOrder")
        currentOrder = null
    }
}