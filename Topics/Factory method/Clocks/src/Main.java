import java.util.Scanner;

/* Product - Clock */
interface Clock {
    void tick();
}

/* Concrete Product - Sand Clock */
class SandClock implements Clock {

    @Override
    public void tick() {
        System.out.println("...sand noise...");
    }
}

/* Concrete Product - Digital Clock */
class DigitalClock implements Clock {

    @Override
    public void tick() {
        System.out.println("...pim...");
    }
}

/* Concrete Product - Mechanical Clock */
class MechanicalClock implements Clock {

    @Override
    public void tick() {
        System.out.println("...clang mechanism...");
    }
}

abstract class ClockFactory {

    abstract Clock createClock(String clockName);

    /* It produces concrete clocks corresponding their types : Digital, Sand or Mechanical */
    public Clock produce(String clockName) {
        Clock clock = createClock(clockName);
        if (clock == null) {
            System.out.println("Sorry, clock is not available\n");
            return null;
        }
        return clock;
    }
}

class ClockStore extends ClockFactory {

    @Override
    Clock createClock(String clockName) {
        if (clockName.equalsIgnoreCase("digital")) {
            return new DigitalClock();
        } else if (clockName.equalsIgnoreCase("sand")) {
            return new SandClock();
        } else if (clockName.equalsIgnoreCase("mechanical")) {
            return new MechanicalClock();
        }
        return null; // Return null if the clock type is not recognized
    }

}

public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final String type = scanner.next();
        final ClockStore clockStore = new ClockStore();
        final Clock clock = clockStore.produce(type);
        clock.tick();
        scanner.close();
    }
}