import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;

public class BikeRental {
    private LinkedList<ActiveRental> activeRentals = new LinkedList<>();
    private List<String> registeredUsers = List.of("john.doe@outlook.com", "admin@njit.edu");

    public void simulateApplicationInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("This is a simulation of the bike rental process.");
        System.out.print("State if the user is a registered user (true/false): ");
        boolean isRegistered = scanner.nextBoolean();
        scanner.nextLine();

        if (!isRegistered) {
            System.out.println("Please register first!");
            return;
        }

        System.out.print("Enter the email address of the user: ");
        String userEmail = scanner.nextLine();
        System.out.print("Enter the location of the bike: ");
        String location = scanner.nextLine();

        System.out.println("\nSimulating the analysis of the rental request...");
        if (!registeredUsers.contains(userEmail)) {
            System.out.println("User not registered!");
            return;
        }
        System.out.println("Welcome back, " + userEmail + "!");

        Bike availableBike = null;
        for (Bike bike : BikeDatabase.getBikes()) {
            if (bike.isAvailable() && bike.getLocation().equals(location)) {
                availableBike = bike;
                break;
            }
        }
        if (availableBike == null) {
            System.out.println("No bikes available at this location!");
            return;
        }
        System.out.println("A bike is available at the location you requested.");

        System.out.println("\nSimulating bike reservation...");
        availableBike.setAvailable(false);
        ActiveRental rental = new ActiveRental(availableBike.getBikeID(), userEmail, LocalDateTime.now());
        activeRentals.add(rental);
        System.out.println("Please follow the on-screen direction to locate your bike and start your trip.");

        System.out.println("\nDisplaying the active rentals...");
        System.out.println("Active Rentals:");
        System.out.println("Bikes Currently In Use:");
        for (ActiveRental r : activeRentals) {
            System.out.println(r);
        }

        System.out.println("\nSimulating trip end...");
        Iterator<ActiveRental> iterator = activeRentals.iterator();
        while (iterator.hasNext()) {
            ActiveRental r = iterator.next();
            if (r.getBikeID().equals(availableBike.getBikeID())) {
                iterator.remove();
                for (Bike b : BikeDatabase.getBikes()) {
                    if (b.getBikeID().equals(r.getBikeID())) {
                        b.setAvailable(true);
                        b.setLastUsedTime(LocalDateTime.now());
                    }
                }
            }
        }
        System.out.println("Trip ended. Bike returned successfully!");
    }
}
