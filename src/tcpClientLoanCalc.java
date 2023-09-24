import java.net.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class tcpClientLoanCalc {

    private static Socket socket = null;
    private static DataInputStream input = null;
    private static DataOutputStream output = null;
    private static Scanner scan = new Scanner(System.in);

    
    private static <K> K validateInput(Class<K> type) {

        K value = null;
        boolean invalidInput = true;

        while(invalidInput) {
            if(type == Integer.class) {
                try {
                    value = type.cast(scan.nextInt());
                    invalidInput = false;
                }
                catch(InputMismatchException e) {
                    System.err.print("\nInvalid input format. Please input again: ");
                    scan.nextLine();
                }
            }

            else if(type == Double.class) {
                try {
                    value = type.cast(scan.nextDouble());
                    invalidInput = false;
                }
                catch(InputMismatchException e) {
                    System.err.print("\nInvalid input format. Please input again: ");
                    scan.nextLine();
                }
            }
        }
        return value;
    }

    private static void sendLoanData(DataInputStream in, DataOutputStream out) {
        
        try {
            System.out.print("\n\nclient-received: " + in.readUTF());
            int years = validateInput(Integer.class).intValue();
            out.writeUTF(Integer.toString(years));

            System.out.print("\nclient-received: " + in.readUTF());
            double loanAmount = validateInput(Double.class).doubleValue();
            out.writeUTF(String.format("%.2f", loanAmount));

            System.out.print("\nclient-received: " + in.readUTF());
            double interestRate = validateInput(Double.class).doubleValue();
            out.writeUTF(Double.toString(interestRate));

            System.out.println("\nclient-received: *** " + in.readUTF() + " ***\n");
            System.out.println("\nclient-received: " + in.readUTF());
            
            while(true) {
                String choice = scan.next();
                if(choice.equalsIgnoreCase("n")) {
                    out.writeUTF(choice);
                    break;
                }
                else if(choice.equalsIgnoreCase("Y")) {
                    out.writeUTF(choice);
                    sendLoanData(in, out);
                    break;
                }
            }
        }
        catch(IOException e) {
            System.err.println("\nAn I/O error occurred. Please try again!\n");
        }
    }

    public static void main(String[] args) throws Exception {

        try {
            socket = new Socket("localhost", 1222);
            System.out.println("\nclient-print: Connected!");
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            sendLoanData(input, output);
        }
        catch (UnknownHostException e) {
            System.err.println("\nUnknown host occured. Please set the correct host!\n");
        }
        catch(IOException e) {
            System.err.println("\nAn I/O error occurred. Please try again!\n");
        }
        finally {

            try {
                if(socket != null) {
                    socket.close();
                    System.out.println("\nclient-print: Client closed!\n");
                }

                if(input != null) {
                    input.close();
                }

                if(output != null) {
                    output.close();
                }
            }
            catch(IOException e) {
                System.err.println("\nAn I/O error occurred. Please try again!\n");
            }
        }
    }
}
