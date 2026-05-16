import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Represents one evidence note with text and a timestamp.
data class EvidenceNote(
    val note: String,
    val timestamp: String
)

// Represents one incident in the tracker.
data class Incident(
    val id: Int,
    var title: String,
    var severity: Severity,
    var status: Status,
    var assignedAnalyst: String,
    val evidenceNotes: MutableList<EvidenceNote>
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

    // Adds an evidence note with a timestamp to a specific incident.
    fun addEvidence(id: Int, note: String) {
        val incident = findIncidentById(id)
        if (incident != null) {
            val timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            incident.evidenceNotes.add(EvidenceNote(note, timestamp))
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
                println("- [${note.timestamp}] ${note.note}")
            }
        }
    }

    // Saves all incidents to a text file.
    fun saveToFile(fileName: String) {
        val file = File(fileName)
        file.printWriter().use { writer ->
            for (incident in incidents) {
                writer.println("INCIDENT|${incident.id}|${incident.title}|${incident.severity}|${incident.status}|${incident.assignedAnalyst}")

                for (note in incident.evidenceNotes) {
                    writer.println("NOTE|${note.timestamp}|${note.note}")
                }

                writer.println("END")
            }
        }
        println("Incidents saved to file.")
    }

    // Loads incidents from a text file.
    fun loadFromFile(fileName: String) {
        val file = File(fileName)

        if (!file.exists()) {
            println("File not found.")
            return
        }

        incidents.clear()
        var currentIncident: Incident? = null
        var highestId = 0

        file.forEachLine { line ->
            val parts = line.split("|")

            when (parts[0]) {
                "INCIDENT" -> {
                    val id = parts[1].toInt()
                    val title = parts[2]
                    val severity = Severity.valueOf(parts[3])
                    val status = Status.valueOf(parts[4])
                    val analyst = parts[5]

                    currentIncident = Incident(
                        id = id,
                        title = title,
                        severity = severity,
                        status = status,
                        assignedAnalyst = analyst,
                        evidenceNotes = mutableListOf()
                    )

                    incidents.add(currentIncident!!)
                    if (id > highestId) {
                        highestId = id
                    }
                }

                "NOTE" -> {
                    if (currentIncident != null) {
                        val timestamp = parts[1]
                        val noteText = parts.drop(2).joinToString("|")
                        currentIncident!!.evidenceNotes.add(EvidenceNote(noteText, timestamp))
                    }
                }

                "END" -> {
                    currentIncident = null
                }
            }
        }

        nextId = highestId + 1
        println("Incidents loaded from file.")
    }

    // Exports incidents to a CSV file.
    fun exportToCsv(fileName: String) {
        val file = File(fileName)
        file.printWriter().use { writer ->
            writer.println("ID,Title,Severity,Status,AssignedAnalyst,EvidenceCount")

            for (incident in incidents) {
                val safeTitle = incident.title.replace(",", ";")
                val safeAnalyst = incident.assignedAnalyst.replace(",", ";")

                writer.println(
                    "${incident.id},$safeTitle,${incident.severity},${incident.status},$safeAnalyst,${incident.evidenceNotes.size}"
                )
            }
        }

        println("CSV report exported.")
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
        9. Save incidents to file
        10. Load incidents from file
        11. Export report to CSV
        12. Exit
        """.trimIndent()
    )
}

// Program entry point.
fun main() {
    val tracker = IncidentTracker()
    val defaultDataFile = "incidents.txt"
    val defaultCsvFile = "incident_report.csv"
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

            9 -> tracker.saveToFile(defaultDataFile)

            10 -> tracker.loadFromFile(defaultDataFile)

            11 -> tracker.exportToCsv(defaultCsvFile)

            12 -> {
                running = false
                println("Exiting program.")
            }

            else -> println("Invalid menu choice.")
        }

        println()
    }
}