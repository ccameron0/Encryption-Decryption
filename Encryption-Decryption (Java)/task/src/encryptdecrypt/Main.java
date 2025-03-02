/**
 * Encryption-Decryption System
 * ---------------------------
 * This program implements a flexible text encryption/decryption system that supports
 * two different cipher algorithms and various input/output methods.
 *
 * Features:
 * - Two encryption algorithms:
 *   1. Caesar Cipher (shift): Only shifts A-Z and a-z characters, preserving case
 *   2. Unicode Cipher: Shifts all characters in the Unicode space
 * - File input/output support
 * - Command-line interface with multiple options
 * - Factory pattern for algorithm selection
 *
 * Command Line Arguments:
 * ----------------------
 * -mode    : "enc" for encryption, "dec" for decryption (default: "enc")
 * -key     : Shift value for encryption/decryption (default: 0)
 * -data    : Text to process (default: empty string)
 * -in      : Input file path (optional)
 * -out     : Output file path (optional)
 * -alg     : Algorithm choice - "shift" or "unicode" (default: "shift")
 *
 * Usage Examples:
 * --------------
 * Basic encryption:
 * java Main -mode enc -key 5 -data "Hello, World!"
 *
 * File-based decryption:
 * java Main -mode dec -key 5 -in encrypted.txt -out decrypted.txt
 *
 * Unicode encryption:
 * java Main -mode enc -key 10 -data "Hello!" -alg unicode
 *
 * Program Structure:
 * ----------------
 * - CipherAlgorithm (interface): Defines the contract for encryption algorithms
 * - CaesarCipher (class): Implements the Caesar cipher algorithm
 * - UnicodeCipher (class): Implements the Unicode shift algorithm
 * - CipherFactory (class): Creates appropriate cipher objects
 * - Main (class): Handles program flow and I/O operations
 *
 * Note: When both -data and -in are provided, -data takes precedence.
 * File operations use try-with-resources for proper resource management.
 */
package encryptdecrypt;

import java.io.*;

// This interface defines what methods any encryption algorithm must implement
// Think of it as a contract that all cipher classes must follow
interface CipherAlgorithm {
    String encrypt(String data, int shift);  // Method to encrypt text
    String decrypt(String data, int shift);  // Method to decrypt text
}

// This class implements the Caesar Cipher algorithm
// It only shifts letters (A-Z, a-z), leaving other characters unchanged
class CaesarCipher implements CipherAlgorithm {
    @Override
    public String encrypt(String data, int shift) {
        StringBuilder encodedData = new StringBuilder();  // Used to build the encrypted string
        shift = shift % 26;  // Ensure shift is within 0-25 (length of alphabet)

        // Process each character in the input string
        for (char c : data.toCharArray()) {
            if (Character.isLetter(c)) {  // Only encrypt letters
                // Determine if we're working with uppercase or lowercase letters
                // This sets our reference point ('A' = 65 or 'a' = 97 in ASCII)
                char base = Character.isUpperCase(c) ? 'A' : 'a';

                // Complex shift calculation broken down:
                // 1. c - base: Convert letter to 0-25 range
                // 2. + shift: Apply the shift
                // 3. + 26: Handle negative numbers
                // 4. % 26: Wrap around alphabet
                // 5. + base: Convert back to ASCII
                char shiftedChar = (char) (base + (c - base + shift + 26) % 26);
                encodedData.append(shiftedChar);
            } else {
                encodedData.append(c);  // Keep non-letters unchanged
            }
        }
        return encodedData.toString();
    }

    @Override
    public String decrypt(String data, int shift) {
        // Decryption is just encryption with negative shift
        return encrypt(data, -shift);
    }
}

// This class implements the Unicode Cipher algorithm
// It shifts ALL characters by the specified amount in the Unicode table
class UnicodeCipher implements CipherAlgorithm {
    @Override
    public String encrypt(String data, int shift) {
        StringBuilder encodedData = new StringBuilder();
        // Simply add the shift value to each character's Unicode value
        for (char c : data.toCharArray()) {
            encodedData.append((char) (c + shift));
        }
        return encodedData.toString();
    }

    @Override
    public String decrypt(String data, int shift) {
        // Decryption is just encryption with negative shift
        return encrypt(data, -shift);
    }
}

// Factory class that creates the appropriate cipher object based on the algorithm name
// This is an example of the Factory Pattern - it helps create objects without exposing creation logic
class CipherFactory {
    public static CipherAlgorithm getCipher(String algorithm) {
        if ("unicode".equalsIgnoreCase(algorithm)) {
            return new UnicodeCipher();
        } else {
            return new CaesarCipher();  // Default to Caesar Cipher
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ArgumentParser parser = new ArgumentParser();
        parser.parse(args);

        runMenuSystem(
                parser.getMode(),
                parser.getShiftByNumber(),
                parser.getData(),
                parser.getInputFile(),
                parser.getOutputFile(),
                parser.getAlgorithm()
        );
    }

    static class ArgumentParser {
        // These are the default values used when arguments aren't provided in the command line
        // They ensure the program can run even with minimal input
        private String mode = "enc";          // Default to encryption mode
        private int shiftByNumber = 0;        // Default to no shift
        private String data = "";             // Default to empty string
        private String inputFile = "noFile";  // Special value indicating no input file
        private String outputFile = "noFile"; // Special value indicating no output file
        private String algorithm = "shift";   // Default to shift (Caesar) cipher

        /**
         * Processes command-line arguments and stores their values.
         * Arguments are expected in pairs: a flag followed by its value
         * Example: -mode enc -key 5 -data "Hello"
         *
         * @param args The command-line arguments array from main()
         */
        public void parse(String[] args) {
            // Loop through arguments in pairs (flag + value)
            // i += 2 because we process two arguments in each iteration
            for (int i = 0; i < args.length; i += 2) {
                // Check if we have both flag and value
                // (i + 1) would be the value position
                if (i + 1 >= args.length) {
                    System.out.println("Error: Missing value for " + args[i]);
                    System.exit(1);  // Exit program if value is missing
                }

                // Get the value that follows the flag
                String value = args[i + 1];

                // Process each flag and its value
                switch (args[i]) {
                    case "-mode" -> mode = value;        // Set encryption/decryption mode
                    case "-key" -> {
                        try {
                            // Convert string to number for the shift value
                            shiftByNumber = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            // If value isn't a valid number
                            System.out.println("Error: Key must be a number");
                            System.exit(1);
                        }
                    }
                    case "-data" -> data = value;        // Set the text to process
                    case "-in" -> inputFile = value;     // Set input file path
                    case "-out" -> outputFile = value;   // Set output file path
                    case "-alg" -> algorithm = value;    // Set encryption algorithm
                }
            }
        }

        // Getter methods to access the parsed values
        // These are package-private (no access modifier) so they can be accessed
        // within the same package but not from outside
        String getMode() { return mode; }                // Get enc/dec mode
        int getShiftByNumber() { return shiftByNumber; } // Get shift value
        String getData() { return data; }                // Get text to process
        String getInputFile() { return inputFile; }      // Get input file path
        String getOutputFile() { return outputFile; }    // Get output file path
        String getAlgorithm() { return algorithm; }      // Get chosen algorithm
    }

    // Main processing method that handles the encryption/decryption workflow
    public static void runMenuSystem(String mode, int shiftByNumber, String data,
                                     String inputFile, String outputFile, String algorithm) {
        // If no direct data provided but input file exists, read from file
        if (!"noFile".equals(inputFile) && data.isEmpty()) {
            data = readFromFile(inputFile);
        }

        // Get the appropriate cipher and process the data
        CipherAlgorithm cipher = CipherFactory.getCipher(algorithm);
        String result = mode.equals("enc") ?
                cipher.encrypt(data, shiftByNumber) :
                cipher.decrypt(data, shiftByNumber);

        // Output result either to console or file
        if ("noFile".equals(outputFile)) {
            System.out.println(result);
        } else {
            writeToFileIfNeeded(outputFile, result);
        }
    }

    // Helper method to read data from a file
    public static String readFromFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.readLine();  // Read first line of file
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        return "";  // Return empty string if file doesn't exist or error occurs
    }

    // Helper method to write data to a file
    public static void writeToFileIfNeeded(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(data);  // Write the data to file
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}