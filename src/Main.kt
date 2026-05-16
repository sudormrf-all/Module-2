// Represents one incident in the tracker.
data class Incident(
    val id: Int,
    var title: String,
    var severity: Severity,
    var status: Status,
    var assignedAnalyst: String,
    val evidenceNotes: MutableList<String>
)

// Severity levels for incidents.
enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

// Status values for incidents.
enum class Status {
    NEW, IN_PROGRESS, RESOLVED
}

// Handles all incident-related operations.
class IncidentTracker {
    private val incidents = mutableListOf<Incident>()
    private var nextId = 1

    // Adds a new incident to the collection.
    fun addIncident(title: String, severity: Severity, assignedAnalyst: String) {
        val incident = Incident(
            id = nextId,
            title = title,
            severity = severity,
            status = Status.NEW,
            assignedAnalyst = assignedAnalyst,
            evidenceNotes = mutableListOf()
        )
        incidents.add(incident)
        nextId++
        println("Incident added successfully.")
    }

    // Displays all incidents in the system.
    fun listIncidents() {
        if (incidents.isEmpty()) {
            println("No incidents found.")
            return
        }

        println("\nIncident List")
        println("-------------")
        for (incident in incidents) {
            println(
                "ID: ${incident.id} | Title: ${incident.title} | " +
                        "Severity: ${incident.severity} | Status: ${incident.status} | " +
                        "Analyst: ${incident.assignedAnalyst}"
            )
        }
    }

    // Adds an evidence note to a specific incident.
    fun addEvidence(id: Int, note: String) {
        val incident = findIncidentById(id)
        if (incident != null) {
            incident.evidenceNotes.add(note)
            println("Evidence note added.")
        } else {
            println("Incident not found.")
        }
    }

    // Updates the status of a specific incident.
    fun updateStatus(id: Int, status: Status) {
        val incident = findIncidentById(id)
        if (incident != null) {
            incident.status = status
            println("Incident status updated.")
        } else {
            println("Incident not found.")
        }
    }

    // Changes the analyst assigned to an incident.
    fun reassignIncident(id: Int, newAnalyst: String) {
        val incident = findIncidentById(id)
        if (incident != null) {
            incident.assignedAnalyst = newAnalyst
            println("Incident reassigned.")
        } else {
            println("Incident not found.")
        }
    }

    // Shows only incidents matching a chosen severity.
    fun filterBySeverity(severity: Severity) {
        val filtered = incidents.filter { it.severity == severity }

        if (filtered.isEmpty()) {
            println("No incidents found with severity $severity.")
            return
        }

        println("\nFiltered Incidents: $severity")
        println("-----------------------------")
        for (incident in filtered) {
            println("ID: ${incident.id} | Title: ${incident.title} | Status: ${incident.status}")
        }
    }

    // Prints summary counts for the dashboard view.
    fun showDashboard() {
        val total = incidents.size
        val newCount = incidents.count { it.status == Status.NEW }
        val inProgressCount = incidents.count { it.status == Status.IN_PROGRESS }
        val resolvedCount = incidents.count { it.status == Status.RESOLVED }

        println("\nDashboard")
        println("---------")
        println("Total incidents: $total")
        println("New: $newCount")
        println("In Progress: $inProgressCount")
        println("Resolved: $resolvedCount")
    }

    // Shows full details for one incident.
    fun showIncidentDetails(id: Int) {
        val incident = findIncidentById(id)

        if (incident == null) {
            println("Incident not found.")
            return
        }

        println("\nIncident Details")
        println("----------------")
        println("ID: ${incident.id}")
        println("Title: ${incident.title}")
        println("Severity: ${incident.severity}")
        println("Status: ${incident.status}")
        println("Assigned Analyst: ${incident.assignedAnalyst}")
        println("Evidence Notes:")

        if (incident.evidenceNotes.isEmpty()) {
            println("- No evidence notes yet.")
        } else {
            for (note in incident.evidenceNotes) {
                println("- $note")
            }
        }
    }

    // Searches the list for an incident by its ID.
    private fun findIncidentById(id: Int): Incident? {
        return incidents.find { it.id == id }
    }
}

// Converts text input into a Severity value.
fun readSeverity(input: String): Severity? {
    return when (input.uppercase()) {
        "LOW" -> Severity.LOW
        "MEDIUM" -> Severity.MEDIUM
        "HIGH" -> Severity.HIGH
        "CRITICAL" -> Severity.CRITICAL
        else -> null
    }
}

// Converts text input into a Status value.
fun readStatus(input: String): Status? {
    return when (input.uppercase()) {
        "NEW" -> Status.NEW
        "IN_PROGRESS" -> Status.IN_PROGRESS
        "RESOLVED" -> Status.RESOLVED
        else -> null
    }
}

// Displays the program menu.
fun printMenu() {
    println(
        """
        === Incident Response Tracker ===
        1. Add incident
        2. List incidents
        3. Add evidence note
        4. Update incident status
        5. Reassign incident
        6. Filter by severity
        7. Show dashboard
        8. View incident details
        9. Exit
        """.trimIndent()
    )
}

// Program entry point.
fun main() {
    val tracker = IncidentTracker()
    var running = true

    // Main loop keeps the program running until the user exits.
    while (running) {
        printMenu()
        print("Choose an option: ")
        val choice = readlnOrNull()?.toIntOrNull()

        when (choice) {
            1 -> {
                print("Enter incident title: ")
                val title = readlnOrNull().orEmpty()

                print("Enter severity (LOW, MEDIUM, HIGH, CRITICAL): ")
                val severityInput = readlnOrNull().orEmpty()
                val severity = readSeverity(severityInput)

                print("Enter assigned analyst: ")
                val analyst = readlnOrNull().orEmpty()

                // Validate input before adding the incident.
                if (title.isBlank() || analyst.isBlank() || severity == null) {
                    println("Invalid input. Incident was not added.")
                } else {
                    tracker.addIncident(title, severity, analyst)
                }
            }

            2 -> tracker.listIncidents()

            3 -> {
                print("Enter incident ID: ")
                val id = readlnOrNull()?.toIntOrNull()

                print("Enter evidence note: ")
                val note = readlnOrNull().orEmpty()

                // Ensure both the ID and note are valid.
                if (id == null || note.isBlank()) {
                    println("Invalid input.")
                } else {
                    tracker.addEvidence(id, note)
                }
            }

            4 -> {
                print("Enter incident ID: ")
                val id = readlnOrNull()?.toIntOrNull()

                print("Enter new status (NEW, IN_PROGRESS, RESOLVED): ")
                val statusInput = readlnOrNull().orEmpty()
                val status = readStatus(statusInput)

                // Validate ID and status before updating.
                if (id == null || status == null) {
                    println("Invalid input.")
                } else {
                    tracker.updateStatus(id, status)
                }
            }

            5 -> {
                print("Enter incident ID: ")
                val id = readlnOrNull()?.toIntOrNull()

                print("Enter new analyst name: ")
                val analyst = readlnOrNull().orEmpty()

                // Reassign only if the ID and new analyst are valid.
                if (id == null || analyst.isBlank()) {
                    println("Invalid input.")
                } else {
                    tracker.reassignIncident(id, analyst)
                }
            }

            6 -> {
                print("Enter severity to filter (LOW, MEDIUM, HIGH, CRITICAL): ")
                val severityInput = readlnOrNull().orEmpty()
                val severity = readSeverity(severityInput)

                if (severity == null) {
                    println("Invalid severity.")
                } else {
                    tracker.filterBySeverity(severity)
                }
            }

            7 -> tracker.showDashboard()

            8 -> {
                print("Enter incident ID: ")
                val id = readlnOrNull()?.toIntOrNull()

                if (id == null) {
                    println("Invalid ID.")
                } else {
                    tracker.showIncidentDetails(id)
                }
            }

            9 -> {
                running = false
                println("Exiting program.")
            }

            else -> println("Invalid menu choice.")
        }

        println()
    }
}