import java.util.Scanner;

class Time {

    int hour;
    int minute;
    int second;

    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public static Time noon() {
        return new Time(12, 0, 0);
    }

    public static Time midnight() {
        return new Time(0, 0, 0);
    }

    public static Time ofSeconds(long seconds) {
        // Calculate the number of seconds that represent a full day (24 hours)
        long secondsInADay = 24 * 3600;

        // Get the remainder of seconds after skipping full days
        long remainingSeconds = seconds % secondsInADay;

        // Calculate hours, minutes, and seconds from the remaining seconds
        int hours = (int) (remainingSeconds / 3600);
        int minutes = (int) ((remainingSeconds % 3600) / 60);
        int secs = (int) (remainingSeconds % 60);

        // Return a new Time instance with the calculated hours, minutes, and seconds
        return new Time(hours, minutes, secs);
    }

    public static Time of(int hour, int minute, int second) {
        // Validate the hour, minute, and second
        if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59) {
            return new Time(hour, minute, second);
        } else {
            return null; // Return null if any value is out of range
        }
    }
}

/* Do not change code below */
public class Main {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String type = scanner.next();
        Time time = null;

        switch (type) {
            case "noon":
                time = Time.noon();
                break;
            case "midnight":
                time = Time.midnight();
                break;
            case "hms":
                int h = scanner.nextInt();
                int m = scanner.nextInt();
                int s = scanner.nextInt();
                time = Time.of(h, m, s);
                break;
            case "seconds":
                time = Time.ofSeconds(scanner.nextInt());
                break;
            default:
                time = null;
                break;
        }

        if (time == null) {
            System.out.println(time);
        } else {
            System.out.printf("%s %s %s", time.hour, time.minute, time.second);
        }
    }
}