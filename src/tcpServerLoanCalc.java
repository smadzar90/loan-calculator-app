import java.net.*;
import java.io.*;

public class tcpServerLoanCalc {

    private static ServerSocket ss = null;
    private static Socket socket = null;
    private static DataOutputStream output = null;
    private static DataInputStream input = null;

    private static boolean validateInput(double loanAmount, double interestRate, int years) {
        return (loanAmount > 0 && interestRate > 0 && years > 0);
    }

    private static void calculateLoan(DataInputStream in, DataOutputStream out) {

        try {
            out.writeUTF("Please input the number of years (e.g. 5): ");
            System.out.println("\nserver-sent: Please input the number of years (e.g. 5): \n");
            int years = Integer.valueOf(in.readUTF());
            System.out.println("server-received: " + years + "\n");

            out.writeUTF("Input the Loan amount (e.g. 12000.00): ");
            System.out.println("server-sent: Input the Loan amount (e.g. 12000.00): \n");
            double loanAmount = Double.valueOf(in.readUTF());
            System.out.println("server-received: " + loanAmount + "\n");

            out.writeUTF("Input the monthly Interest Rate(e.g. 0.018): ");
            System.out.println("server-sent: Input the monthly Interest Rate(e.g. 0.018): \n");
            double interestRate = Double.valueOf(in.readUTF());
            System.out.println("server-received: " + interestRate + "\n");

            //Calculate monthly payment
            // P ( r ( 1 + r ) ^ n ) / ( ( 1 + r ) ^ n - 1 )
            if(validateInput(loanAmount, interestRate, years)) {
                double result = loanAmount * (interestRate * Math.pow((1 + interestRate), (years * 12)))
                / (Math.pow((1 + interestRate), ((years * 12))) - 1);
                String monthlyPayment = String.format("%.2f", result);
                out.writeUTF("The monthly payment is: $" + monthlyPayment);
                System.out.println("server-sent: The monthly payment is: $" + monthlyPayment + "\n\n");
            }
            else {
                out.writeUTF("The monthly payment cannot be calculated. All parameters must be greater than 0!");
                System.out.println("server-sent:The monthly payment cannot be calculated. All parameters must be greater than 0!\n\n");
            }

            out.writeUTF("Do you want to try again: (Y)es or (N)o? ");

            if(in.readUTF().equalsIgnoreCase("y")) {
                calculateLoan(in, out);
            }
        }
        catch(IOException e) {
            System.err.println("\nAn I/O error occurred. Please try again!\n");
        }
    
    }

    public static void main(String[] args) throws Exception {
        
        try {
            ss = new ServerSocket(1222);
            System.out.println("\nserver-print: Server started!\n");
            System.out.println("server-print: Waiting for client to connect...\n");
            socket = ss.accept();
            System.out.println("server-print: Client accepted!\n");
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            calculateLoan(input, output);
        }

        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            
            try {
                if(socket != null) {
                    socket.close();
                    System.out.println("server-print: Client connection closed!\n");
                }

                if(ss != null) {
                    ss.close();
                    System.out.println("server-print: Server closed!\n");
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
