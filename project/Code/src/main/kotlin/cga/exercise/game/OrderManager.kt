package cga.exercise.game
import java.util.*

class OrderManager (private val customerManager: CustomerManager){
    fun takeOrder(customerId: Int) {
        customerManager.generateCustomerOrder(customerId)
        displayOrder(customerId)
    }

    fun confirmOrder(customerId: Int) {
        val order = customerManager.getCustomerOrder(customerId)
        println("Bestellung bestätigt: $order für Kunde $customerId")
        customerManager.moveCustomerToTable(customerId)
        customerManager.moveQueueForward()
    }

    fun displayOrder(customerId: Int) {
        val order = customerManager.getCustomerOrder(customerId)
        println("Bestellung für Kunde $customerId: $order (Anzeige in Sprechblase)")
    }
}