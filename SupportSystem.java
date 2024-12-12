import java.util.*;
class Ticket {
    private static int idCounter = 1;
    private final int id;
    private final String description;
    private int priority;
    private String status;
    private String assignedAgent;

    public Ticket(String description, int priority) {
        this.id = idCounter++;
        this.description = description;
        this.priority = priority;
        this.status = "Open";
        this.assignedAgent = "Unassigned";
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void assignAgent(String agentName) {
        this.assignedAgent = agentName;
        this.status = "In Progress";
    }

    public void resolve() {
        this.status = "Resolved";
    }

    @Override
    public String toString() {
        return String.format("Ticket ID: %d | Description: %s | Priority: %d | Status: %s | Assigned Agent: %s",
                id, description, priority, status, assignedAgent);
    }
}

public class SupportSystem {
    private final Queue<Ticket> regularTickets = new LinkedList<>();
    private final PriorityQueue<Ticket> priorityTickets = new PriorityQueue<>(new Comparator<Ticket>() {
        @Override
        public int compare(Ticket t1, Ticket t2) {
            // Compare by priority (lower value = higher priority)
            if (t1.getPriority() != t2.getPriority()) {
                return Integer.compare(t1.getPriority(), t2.getPriority());
            }
            // If priorities are the same, compare by ID (ascending order)
            return Integer.compare(t1.getId(), t2.getId());
        }
    });
    private final HashMap<Integer, Ticket> ticketDetails = new HashMap<>();
    private final List<String> availableAgents = new ArrayList<>();

    public SupportSystem() {
        availableAgents.add("Agent 1");
        availableAgents.add("Agent 2");
    }

    public void addRegularTicket(String description) {
        Ticket ticket = new Ticket(description, Integer.MAX_VALUE);
        regularTickets.add(ticket);
        ticketDetails.put(ticket.getId(), ticket);
        System.out.println("\n✅ Regular Ticket Added:\n" + ticket);
    }

    public void addPriorityTicket(String description, int priority) {
        Ticket ticket = new Ticket(description, priority);
        priorityTickets.add(ticket);
        ticketDetails.put(ticket.getId(), ticket);
        System.out.println("\n🚨 Priority Ticket Added:\n" + ticket);
    }

    public void reprioritizeTicket(int ticketId, int newPriority) {
        Ticket ticket = ticketDetails.get(ticketId);
        if (ticket != null) {
            if (priorityTickets.remove(ticket)) {
                ticket.setPriority(newPriority);
                priorityTickets.add(ticket);
                System.out.println("\n🔄 Ticket Reprioritized:\n" + ticket);
            } else {
                System.out.println("\n⚠️ Ticket not found in the priority queue!");
            }
        } else {
            System.out.println("\n⚠️ Ticket ID not found!");
        }
    }

    public void assignTickets() {
        Queue<String> busyAgents = new LinkedList<>();

        // Process all priority tickets first
        while ((!availableAgents.isEmpty() || !busyAgents.isEmpty()) && !priorityTickets.isEmpty()) {
            String agent = !availableAgents.isEmpty() ? availableAgents.remove(0) : busyAgents.poll();

            if (agent != null) {
                Ticket ticket = priorityTickets.poll();
                if (ticket != null) {
                    ticket.assignAgent(agent);
                    System.out.println("\n👔 Assigned Ticket:\n" + ticket);

                    // Simulate ticket resolution immediately
                    ticket.resolve();
                    System.out.println("✅ Ticket Resolved Automatically:\n" + ticket);

                    // Return the agent to the busy queue for reuse
                    busyAgents.add(agent);
                }
            }
        }
        // After priority tickets, process regular tickets
        while ((!availableAgents.isEmpty() || !busyAgents.isEmpty()) && !regularTickets.isEmpty()) {
            String agent = !availableAgents.isEmpty() ? availableAgents.remove(0) : busyAgents.poll();

            if (agent != null) {
                Ticket ticket = regularTickets.poll();
                if (ticket != null) {
                    ticket.assignAgent(agent);
                    System.out.println("\n👔 Assigned Ticket:\n" + ticket);

                    // Simulate ticket resolution immediately
                    ticket.resolve();
                    System.out.println("✅ Ticket Resolved Automatically:\n" + ticket);

                    // Return the agent to the busy queue for reuse
                    busyAgents.add(agent);
                }
            }
        }

        // Move any remaining busy agents back to the available pool
        while (!busyAgents.isEmpty()) {
            availableAgents.add(busyAgents.poll());
        }
    }

    public void resolveTicket(int ticketId) {
        Ticket ticket = ticketDetails.get(ticketId);
        if (ticket != null && "In Progress".equals(ticket.getStatus())) {
            ticket.resolve();
            System.out.println("\n✅ Ticket Resolved:\n" + ticket);
        } else {
            System.out.println("\n⚠️ Ticket not found or not in progress!");
        }
    }

    public void listTicketsByStatus(String status) {
        System.out.println("\n📄 Tickets with status: " + status);
        for (Ticket ticket : ticketDetails.values()) {
            if (ticket.getStatus().equalsIgnoreCase(status)) {
                System.out.println(ticket);
            }
        }
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Customer Support System ===");
            System.out.println("1. Add Regular Ticket");
            System.out.println("2. Add Priority Ticket");
            System.out.println("3. Reprioritize Ticket");
            System.out.println("4. Assign Tickets to Agents");
            System.out.println("5. Resolve Ticket");
            System.out.println("6. View Tickets by Status");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter ticket description: ");
                    String description = scanner.nextLine();
                    addRegularTicket(description);
                }
                case 2 -> {
                    System.out.print("Enter ticket description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter ticket priority (lower value = higher priority): ");
                    int priority = scanner.nextInt();
                    addPriorityTicket(description, priority);
                }
                case 3 -> {
                    System.out.print("Enter ticket ID to reprioritize: ");
                    int ticketId = scanner.nextInt();
                    System.out.print("Enter new priority: ");
                    int newPriority = scanner.nextInt();
                    reprioritizeTicket(ticketId, newPriority);
                }
                case 4 -> assignTickets();
                case 5 -> {
                    System.out.print("Enter ticket ID to resolve: ");
                    int ticketId = scanner.nextInt();
                    resolveTicket(ticketId);
                }
                case 6 -> {
                    System.out.print("Enter status to filter (Open/In Progress/Resolved): ");
                    String status = scanner.nextLine();
                    listTicketsByStatus(status);
                }
                case 7 -> exit = true;
                default -> System.out.println("\n⚠️ Invalid choice! Please try again.");
            }
        }
        scanner.close();
        System.out.println("\n👋 Exiting Support System. Goodbye!");
    }
    public static void main(String[] args) {
        new SupportSystem().mainMenu();
    }
}