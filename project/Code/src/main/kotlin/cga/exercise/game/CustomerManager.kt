package cga.exercise.game
import org.joml.Vector3f


class CustomerManager{
    private val customers = mutableListOf<Customer>()
    private val availableOrders = listOf("Latte", "Cappuccino", "Espresso", "Mocha")

    private val queuePositions = listOf(
        Vector3f(2f, 0f, 1f), // Position des ersten Kunden vor dem Tresen
        Vector3f(3f, 0f, 1f), // Position des zweiten Kunden in der Schlange
        Vector3f(4f, 0f, 1f)  // Position des dritten Kunden in der Schlange
    )

    private val tablePositions = listOf(
        Vector3f(5f, 0f, 5f),
        Vector3f(6f, 0f, 5f),
        Vector3f(7f, 0f, 5f)
    )

    private val availableTables = mutableListOf<Vector3f>()

    init {
        availableTables.addAll(tablePositions)
        // Initialisiert die Kunden in der Warteschlange
        for (i in queuePositions.indices) {
            customers.add(Customer(i + 1, queuePositions[i], ""))
        }
    }

    fun getFirstCustomer(): Customer {
        return customers.first()
    }

    fun addCustomer(customer: Customer) {
        customers.add(customer)
    }

    fun getCustomerOrder(customerId: Int): String {
        return customers.first { it.id == customerId }.order
    }

    fun moveCustomerToTable(customerId: Int) {
        if (availableTables.isNotEmpty()) {
            val tablePosition = availableTables.removeAt(0)
            customers.first { it.id == customerId }.moveToTable(tablePosition)
        } else {
            println("Keine freien Tische verfügbar")
        }
    }

    fun moveQueueForward() {
        for (i in 1 until customers.size) {
            customers[i].moveForwardToCounter(queuePositions[i - 1])
        }
        // Den ersten Kunden entfernen, nachdem er zum Tisch gegangen ist
        customers.removeAt(0)
    }

    fun freeTable(tablePosition: Vector3f) {
        availableTables.add(tablePosition)
    }

    fun generateCustomerOrder(customerId: Int) {
        customers.first { it.id == customerId }.generateOrder(availableOrders)
    }

    fun getAllCustomers(): List<Customer> {
        return customers
    }
}

