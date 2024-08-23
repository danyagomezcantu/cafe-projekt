package cga.exercise.game

class CoffeStation {
    private var progress = 0

    fun startPreparation() {
        progress = 0
        println("Zubereitung gestartet")
    }

    fun updateProgress() {
        if (progress < 100) {
            progress += 10
            println("Zubereitung: $progress% abgeschlossen")
        } else {
            println("Zubereitung abgeschlossen!")
        }
    }

    fun isReady(): Boolean {
        return progress >= 100
    }

    fun completeOrder(customerId: Int) {
        println("Getränk für Kunde $customerId fertiggestellt")
    }
}