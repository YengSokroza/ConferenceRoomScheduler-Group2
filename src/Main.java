import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    static Scanner input = new Scanner(System.in);
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final  String PURPLE = "\u001B[35m";
    public static final String RESET = "\u001B[0m";
    private static Map<String, Map<String, Map<String, String>>> roomSchedules;
    public static void start(){
        userType();
    }
    public static void userType(){
        Table(true,"User-Type","1.) User","2.) Admin");
        int user = Integer.parseInt(validateInput("Identify yourself (1,2): ","[12]"));
        if(user==2){
            String enteredUsername = validateInput("Enter username: ","^[A-Za-z]+$");
            String enteredPassword = validateInput("Enter password: ",".*");
            if (Authenticator.authenticate(enteredUsername, enteredPassword)) {
                System.out.printf("Authentication successful. Welcome, %s!\n",enteredUsername);
                System.out.println(BLUE + "-".repeat(55) + RESET);
                //admin menu
                Table(false,"Menu","1.) All User's Booking","2.) Edit Schedule","3.) Reboot");
            } else {
                System.out.println("Authentication failed. Please check your username and password.");
                System.out.println(BLUE + "-".repeat(55) + RESET);
                start();
            }
        }else{
            String enteredUsername = validateInput("Enter username: ","^[A-Za-z]+$");
            System.out.println(BLUE + "-".repeat(50) + RESET);
            System.out.printf(BLUE + " # Welcome %s, How can I assist you today?\n",enteredUsername + RESET);
            //userMenu
            userMenu();
        }
    }
    //feature
    public static void userMenu(){
        boolean isContinue = true;
        do{
            Table(false,"Menu","A.) Booking","B.) Rooms","C.) Display Schedule","D.) Display User's Booking");
            String userInput = validateInput("-> Please select menu no: ","^[A-Za-z]+$").trim().toLowerCase();

            switch (userInput){
                case "a" -> {
                    while (true){
                        // User selects a room type
                        System.out.print("\nEnter the room type (A/B/C): ");
                        String chosenRoomType = input.nextLine();

                        // User chooses a shift for the selected room type
                        System.out.print("Enter the day (e.g., Monday): ");
                        String chosenDay = input.nextLine();
                        System.out.print("Enter the shift to choose (Morning/Afternoon/Evening): ");
                        String chosenShift = input.nextLine();

                        chooseShift(chosenRoomType, chosenDay, chosenShift);

                        // Display the updated room schedules
                        System.out.println("\nUpdated Room Schedules:");
                        displayRoomSchedules(true);
                    }
                }
                case "b" -> {

                }
                case "c" -> {
                    displayRoomSchedules(true);
                }
                case "d" -> {

                }
                default -> System.out.println(RED + "[Invalid Input] Please select available choices :  A -> D !" + RESET);
            }
        }while (isContinue);
    }
    public static void InitializeSchedule() {
        // Initialize an empty schedule for each room type with all shifts available
        roomSchedules = new HashMap<>();
        for (String roomType : new String[]{"A", "B", "C"}) {
            Map<String, Map<String, String>> schedule = new HashMap<>();
            for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
                Map<String, String> shifts = new HashMap<>();
                for (String shift : new String[]{"Morning", "Afternoon", "Evening"}) {
                    shifts.put(shift, "Available");
                }
                schedule.put(day, shifts);
            }
            roomSchedules.put(roomType, schedule);
        }
    }

    public static void displayRoomSchedules(boolean allSchedule) {
        // Display the current schedules for each room type
        if(allSchedule){
            for (String roomType : new String[]{"A", "B", "C"}) {
                System.out.println( BLUE+ "-".repeat(90) + RESET);
                System.out.println(BLUE + " ".repeat(35) + "Room Type " + roomType + " Schedule" + RESET);
                System.out.println(BLUE + "-".repeat(90) + RESET);
                displaySchedule(roomType);
                System.out.println();
            }
        }

    }



    public static void displaySchedule(String roomType) {
        // Display the schedule for a specific room type
        System.out.printf("%-15s", ""); // Empty space for alignment
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
            System.out.print(BLUE);
            System.out.printf("%-15s", day);
            System.out.print(RESET);
        }
        System.out.println();

        for (String shift : new String[]{"Morning", "Afternoon", "Evening"}) {
            System.out.print(BLUE);
            System.out.printf("%-15s", shift);
            System.out.print(RESET);
            for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
                String availability = getAvailability(roomType, day, shift);

                // Add ANSI escape codes for red if status is "Unavailable"
                if ("Unavailable".equals(availability)) {
                    System.out.print(RED);
                }

                System.out.printf("%-15s", availability);

                // Reset color if it was changed
                if ("Unavailable".equals(availability)) {
                    System.out.print(RESET);
                }
            }
            System.out.println();
        }
    }

    public static String getAvailability(String roomType, String day, String shift) {
        Map<String, Map<String, String>> schedule = roomSchedules.get(roomType);
        Map<String, String> shifts = schedule.get(day);
        return shifts.get(shift);
    }

    public static void chooseShift(String roomType, String day, String chosenShift) {
        Map<String, Map<String, String>> schedule = roomSchedules.get(roomType);
        Map<String, String> shifts = schedule.get(day);
        // Check if the chosen shift is available for the selected day
        if (shifts.containsKey(chosenShift) && "Available".equals(shifts.get(chosenShift))) {
            // Change the status of the chosen shift to "Unavailable"
            shifts.put(chosenShift,"Unavailable");

            System.out.println("Shift '" + chosenShift + "' on " + day + " for Room Type " + roomType +
                    " has been chosen, and the status is marked as unavailable.");
        } else {
            System.out.println("Shift '" + chosenShift + "' on " + day + " for Room Type " + roomType +
                    " is not available or has already been chosen.");
        }
    }

    //Table
    public static void Table(boolean green,String header,String... lists){
        if(green){
            Table showT = new Table(2, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
            showT.setColumnWidth(0,25,30);
            showT.setColumnWidth(1,25,30);
            for (String m : lists){
                showT.addCell(" " + m);
            }
            System.out.println(GREEN + showT.render() + RESET);

        }else{
            Table showT = new Table(1, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
            showT.addCell(" " + header);
            showT.setColumnWidth(0,50,60);
            for (String m : lists){
                showT.addCell(" " + m);
            }
            System.out.println(BLUE + showT.render() + RESET);
        }

    }

    //validation
    private static String validateInput(String message, String regex){
        while (true){
            System.out.print(message);
            String userInput = input.nextLine();

            Pattern pattern = Pattern.compile(regex);
            if(pattern.matcher(userInput).matches()){
                return userInput;
            }else {
                System.out.println(RED + "Invalid Format!" + RESET);
            }
        }
    }
    //utilities

    public static void main(String[] args) {
        InitializeSchedule();
        start();
    }
}
