import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.*;
import java.util.regex.Pattern;

public class Main {
    static Scanner input = new Scanner(System.in);
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";
    private static Map<String, Map<String, Map<String, String>>> roomSchedules;
    private static List<String> bookingHistory = new ArrayList<>();
    public static void start(){
        boolean isContinue = true;
        do{
            Table(false,"Conference Room Scheduler","1.) Select type of user","2.) Exit");
            int userInput = Integer.parseInt(validateInput("-> Select your option: ","[12]"));
            switch (userInput){
                case 1 -> userType();
                case 2 -> {
                    isContinue = false;
                    System.out.println("Thank you for using our program !");
                    System.out.println("Exiting Program...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid Option.");
            }
        }while(isContinue);



    }
    public static void userType(){
        Table(true,"User-Type","1.) User","2.) Admin","3.) Back");
        int user = Integer.parseInt(validateInput("Identify yourself (1,2): ","[123]"));
        if(user==2){
            String enteredUsername = validateInput("Enter username: ","^[A-Za-z]+$");
            String enteredPassword = validateInput("Enter password: ",".*");
            if (Authenticator.authenticate(enteredUsername, enteredPassword)) {
                System.out.printf("Authentication successful. Welcome, %s!\n",enteredUsername);
                System.out.println(BLUE + "-".repeat(55) + RESET);
                //admin menu
                adminMenu();
            } else {
                System.out.println("Authentication failed. Please check your username and password.");
                System.out.println(BLUE + "-".repeat(55) + RESET);
                start();
            }
        }else if(user==1){
            String enteredUsername = validateInput("Enter username: ","^[A-Za-z]+$");
            System.out.println(BLUE + "-".repeat(50) + RESET);
            System.out.printf(BLUE + " # Welcome %s, How can I assist you today?\n",enteredUsername + RESET);
            //userMenu
            userMenu();
        }else{
            start();
        }
    }
    //feature
    public static void adminMenu(){
        boolean isContinue = true;
        do{
            Table(false,"Menu","A.) Display All User's Booking","B.) Reboot","C.) Back");
            String userInput = validateInput("-> Please select menu no: ","^[ABCabc]+$").trim().toLowerCase();
            switch (userInput){
                case "a" -> {
                    System.out.println(BLUE + "-".repeat(55) + RESET);
                    displayBookedHistory();
                    System.out.println(BLUE + "-".repeat(55) + RESET);
                }
                case "b" -> {
                    System.out.println(BLUE + "-".repeat(55) + RESET);
                    InitializeSchedule();
                    bookingHistory.clear();
                    System.out.println(" ".repeat(15) +"Reboot Successfully");
                    System.out.println(BLUE + "-".repeat(55) + RESET);
                }
                case "c" -> {
                    userType();
                }
            }
        }while (isContinue);
    }
    public static void userMenu(){
        boolean isContinue = true;
        do{
            Table(false,"Menu","A.) Booking","B.) Rooms","C.) Display Schedule","D.) Back");
            String userInput = validateInput("-> Please select menu no: ","^[ABCDabcd]+$").trim().toLowerCase();
            switch (userInput){
                case "a" -> {
                        showRooms();
                        String chosenRoomType = validateInput("-> Enter the room type (A/B/C): ","^[ABCabc]$").toUpperCase();
                        List<String> bookingInputs = new ArrayList<>();
                        instructionTable("Instructor","1 to 5 represent monday to friday","M : Morning shift", "A : Afternoon" , "E : Evening" ,"Single selected : 1-E","Multiple Selected : 1-E,2-A");
                        System.out.print("-> Please enter your booking : ");

                        String inputLine = input.nextLine().trim();

                        if (!inputLine.isEmpty()) {
                            String[] bookings = inputLine.split(",");
                            boolean allFormatsCorrect = true;

                            for (String bookingInput : bookings) {
                                if (bookingInput.matches("^[1-5]-[MAEmae]$")) {
                                    bookingInputs.add(bookingInput.trim());
                                } else {
                                    System.out.println("Invalid format for booking: " + bookingInput);
                                    allFormatsCorrect = false;
                                }
                            }

                            if (allFormatsCorrect) {
                                String confirmation = validateInput(YELLOW + "Are you sure you want to book the selected slots? (y/n): ","^[yYnN]$").trim().toLowerCase();
                                System.out.print(RESET);
                                if (confirmation.equals("y")) {
                                    for (String input : bookingInputs) {
                                        String[] parts = input.split("-");
                                        int dayInput = Integer.parseInt(parts[0]);
                                        String timeInput = parts[1].toUpperCase();

                                        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
                                        String chosenDay = days[dayInput - 1];
                                        String chosenShift = getTimeOfDay(timeInput);

                                        chooseShift(chosenRoomType, chosenDay, chosenShift);
                                        // Add the booking to the history
                                        bookingHistory.add("Room type: " + chosenRoomType + ", Day: " + chosenDay + ", Shift: " + chosenShift);
                                    }
                                } else {
                                    System.out.println(RED + "Booking canceled. Please try again..." + RESET);
                                    // You might choose to handle going back to a previous step or exit the program.
                                }
                            } else {
                                System.out.println(RED + "Some bookings are in an invalid format. Please try again..." + RESET);
                                // You might choose to handle going back to a previous step or exit the program.
                            }
                        } else {
                            System.out.println(RED +"No bookings entered..." + RESET);
                            // You might choose to handle going back to a previous step or exit the program.
                        }

                }
                case "b" -> {
                    showRooms();
                }
                case "c" -> {
                    displayRoomSchedules(true);
                }
                case "d" -> {
                    userType();
                }
                default -> System.out.println(RED + "[Invalid Input] Please select available choices :  A -> D !" + RESET);
            }
        }while (isContinue);
    }
    public static void showRooms(){
        Table(true,"Type of rooms","Type A : White Board included","Type B : LCD included","Type C : White Board + LCD included");
    }
    public static String getTimeOfDay(String code) {
        switch (code) {
            case "M":
                return "Morning";
            case "A":
                return "Afternoon";
            case "E":
                return "Evening";
            default:
                return "Unknown";
        }
    }

    private static void displayBookedHistory() {
        if(bookingHistory.isEmpty()){
            System.out.println("There is no history...");
        }else{
            for (String booking : bookingHistory) {
                System.out.println(booking);
            }
        }
    }
    public static void InitializeSchedule() {
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
        if(allSchedule){
            for (String roomType : new String[]{"A", "B", "C"}) {
                System.out.println(GREEN+ "-".repeat(90) + RESET);
                System.out.println(GREEN+ " ".repeat(35) + "Room Type " + roomType + " Schedule" + RESET);
                System.out.println(GREEN+ "-".repeat(90) + RESET);
                displaySchedule(roomType);
                System.out.println();
            }
        }

    }

    public static void displaySchedule(String roomType) {
        System.out.printf("%-15s", "");
        for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
            System.out.print(GREEN);
            System.out.printf("%-15s", day);
            System.out.print(RESET);
        }
        System.out.println();

        for (String shift : new String[]{"Morning", "Afternoon", "Evening"}) {
            System.out.print(GREEN);
            System.out.printf("%-15s", shift);
            System.out.print(RESET);
            for (String day : new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}) {
                String availability = getAvailability(roomType, day, shift);
                if ("Unavailable".equals(availability)) {
                    System.out.print(RED);
                }
                System.out.printf("%-15s", availability);

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
        if (shifts.containsKey(chosenShift) && "Available".equals(shifts.get(chosenShift))) {
            shifts.put(chosenShift,"Unavailable");

            System.out.println(BLUE + "Shift '" + chosenShift + "' on " + day + " for Room Type " + roomType +
                    " has been chosen, Booking Successfully !" + RESET);
        } else {
            System.out.println(RED + "Shift '" + chosenShift + "' on " + day + " for Room Type " + roomType +
                    " is not available or has already been chosen." + RESET);
        }
    }

    //Table
    public static void Table(boolean green,String header,String... lists){
        if(green){
            Table showT = new Table(1, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
            showT.addCell(" " + header);
            showT.setColumnWidth(0,50,60);
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
    public static void instructionTable(String header,String... lists){
        Table InsTable = new Table(1, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        InsTable.addCell(" " + header);
        InsTable.setColumnWidth(0,50,60);
        for (String m : lists){
            InsTable.addCell(" " + m);
        }
        System.out.println(YELLOW + InsTable.render() + RESET);
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
