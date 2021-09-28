package CRC;

import CRC.Useful.*;
import static CRC.Useful.UsefulFuncs.*;
import java.util.Arrays;
import java.util.Random;

public class CRC
{
    //region Fields
    Polynomial gx;
    private final int r;
    private final int n;
    private final int k;
    private final int bigN;

    private String message;
    private final String key;
    private String encodedMessage;
    private static int[] errorLocation;
    //endregion

    //region Constructors
    public CRC(String message, Polynomial gx)
    {
        this.gx = gx;
        this.r = gx.degree();
        this.n = 2*r +1;
        this.k = n - r;
        this.bigN = (int) Math.pow(2.0, k);
        this.key = intArrToString(this.gx.getCoef());
        this.message = message;
        errorLocation = new int[(this.message.length()+this.gx.getCoef().length)-1];
    }

    public CRC(Polynomial gx)
    {
        this.gx = gx;
        this.r = gx.degree();
        this.n = 2*r +1;
        this.k = n - r;
        this.bigN = (int) Math.pow(2.0, k);
        this.key = intArrToString(this.gx.getCoef());
    }
    //endregion

    //region Getters
    public Polynomial getGx()
    {
        return gx;
    }

    public int getN()
    {
        return n;
    }

    public int getBigN()
    {
        return bigN;
    }

    public int getK()
    {
        return k;
    }

    public int getR()
    {
        return r;
    }

    public String getKey() {
        return this.key;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getEncodedMessage(){
        return this.encodedMessage;
    }

    public int[] getErrorLocations(){
        return errorLocation;
    }
    //endregion

    /**
     * A simple algorithm that does modulo 2 division of two binary inputs
     * @param data is the numerator/dividend
     * @param concat is a checker bool that determines if 0's should be concatenated with the numerator
     * @return the remainder of the modulo 2 division
     */
    private String mod2Div(String data, boolean concat)
    {
        if (Integer.parseInt(this.key,2) == 0)
            throw new ArithmeticException("Divide by 0!");
        if (concat)
            data = data + "0".repeat(Math.max(0, this.key.length() - 1));

        int i = key.length();

        String remainder = data.substring(0, i);

        while (i < data.length())
        {
            if (Character.toString(remainder.charAt(0)).equals("1"))
                remainder = XOR(key, remainder) + Character.toString(data.charAt(i));
            else
                remainder = XOR(remainder, intArrToString(new int[i])) + Character.toString(data.charAt(i));
            i++;
        }

        if (Character.toString(remainder.charAt(0)).equals("1"))
            remainder = XOR(key, remainder);
        else
            remainder = XOR(intArrToString(new int[i]), remainder);

        return remainder;
    }

    /**
     * Generates a random message, without the 0's or the encoding bits concatenated
     *
     * @return the message as a string
     */
    public String generateRandomMessage()
    {
        StringBuilder toReturn = new StringBuilder("1");
        for (int i = 1; i < this.getN(); i++)
        {
            toReturn.append((Math.random() < 0.5) ? 1 : 0);
        }
        this.message = toReturn.toString();
        errorLocation = new int[(this.message.length()+this.gx.getCoef().length)-1];
        return toReturn.toString();
    }

    /**
     * Generates a random message without the 0's or the encoding bits concatenated
     *
     * @param n is the length of the message
     * @param gx is the generator polynomial
     * @return the message
     */
    public static String generateRandomMessage(int n, Polynomial gx)
    {
        StringBuilder toReturn = new StringBuilder("1");
        for (int i = 1; i < n; i++)
        {
            toReturn.append((Math.random() < 0.5) ? 1 : 0);
        }
        errorLocation = new int[(n+gx.getCoef().length)-1];
        return toReturn.toString();
    }

    /**
     * This method concatenates the remainder of the modulo 2 division to the message
     *
     * @return the encoded message
     */
    public String encodeMessage()
    {
        String toReturn;
        toReturn = this.message + mod2Div(this.message, true);
        this.encodedMessage = toReturn;
        return toReturn;
    }

    /**
     * A method to validate the encoded message
     *
     * @return true if the the remainder is zero for modulo 2 division of the encoded message
     */
    public boolean checkMessage()
    {
        return Integer.parseInt(mod2Div(this.encodedMessage, false))==0;
    }

    /**
     * A method checking if an error was detected
     * @param concat is the parameter used to concat the 0's to the message
     * @return true if message was valid and vice versa
     */
    public boolean checkMessage(boolean concat)
    {
        return Integer.parseInt(mod2Div(this.message, concat))==0;
    }

    /**
     * A method that acts as the intermediary error generator from sender to receiver
     *
     * @param numOfErrors is the amount of errors to be generated
     * @param redundancy_bits_affected is the bool controlling whether the redundancy bits should be affected
     */
    public void generateErrors(int numOfErrors, boolean redundancy_bits_affected, int probability_of_error)
    {
        double tempD = (double) probability_of_error * 0.01;
        if (Math.random() > tempD)
            return;
        int[] temp = stringToIntArr(this.encodedMessage);
        String[] indexAltered = new String[this.encodedMessage.length()];
        Arrays.fill(indexAltered, "0");
        int amount = numOfErrors;
        Random rnd = new Random();

        if (!redundancy_bits_affected)
        {
            if (numOfErrors <= 0 || numOfErrors > temp.length - (key.length() - 1))
                throw new IllegalArgumentException("Boundaries of the message violated, 0 <= numOfErrors < " + (temp.length - key.length() + 1));
            while (amount > 0)
            {
                int rndInt = rnd.nextInt(temp.length - (key.length() - 1));
                if (!indexAltered[rndInt].equals("x"))
                {
                    indexAltered[rndInt] = "x";
                    temp[rndInt] = temp[rndInt] == 0 ? 1 : 0;
                    errorLocation[rndInt] = 1;
                    amount--;
                }
            }
        }
        else
        {
            if (numOfErrors <= 0 || numOfErrors > temp.length)
                throw new IllegalArgumentException("Boundaries of the message violated, 0 <= numOfErrors < " + temp.length);
            while (amount > 0)
            {
                int rndInt = rnd.nextInt(temp.length);
                if (!indexAltered[rndInt].equals("x"))
                {
                    indexAltered[rndInt] = "x";
                    temp[rndInt] = temp[rndInt] == 0 ? 1 : 0;
                    errorLocation[rndInt] = 1;
                    amount--;
                }
            }
        }
        this.encodedMessage = intArrToString(temp);
    }

    public static void main(String[] args)
    {
        Polynomial gx;

        Polynomial gx1 = new Polynomial(1,3);
        Polynomial gx2 = new Polynomial(1,1);
        Polynomial gx3 = new Polynomial(1,0);

        gx = gx1.plus(gx2).plus(gx3);

        CRC crc = new CRC(gx);
        String message = crc.generateRandomMessage();

        System.out.println("Message:                        " + message);
        System.out.println("Key:                            " + crc.getKey());
        System.out.println("Encoded message:                " + crc.encodeMessage());
        System.out.println("---------------------------------------------------------");
        crc.generateErrors(2,false, 100);
        System.out.println("sending message...");
        System.out.println("---------------------------------------------------------");
        System.out.println("Message received:               " + crc.getEncodedMessage());
    }

    static class TestSimulation
    {
        public static void main(String[] args)
        {
            Polynomial gx;

            Polynomial gx1 = new Polynomial(1,4); //1*x^4
            Polynomial gx2 = new Polynomial(1,3); //1*x^3
            Polynomial gx3 = new Polynomial(1,2); //1*x^2
            Polynomial gx4 = new Polynomial(0,1); //0*x^1
            Polynomial gx5 = new Polynomial(1,0); //1*x^0

            gx = gx1.plus(gx2).plus(gx3).plus(gx4).plus(gx5); //x^4 + x^3 + x^2 + 1



            int simSum = 0;
            int simulations = 1000000;

            for (int i = 0; i < simulations; i++)
                if (!new CRC(generateRandomMessage(gx.degree()*2+1, gx), gx).checkMessage(true))
                    simSum++;

            double simulatedP = (100.0)*((double)simSum/simulations);
            System.out.println(simulatedP+"% of random messages were detected with an error");
            double analyticalP = (1 - 1/(Math.pow(2.0, (double) gx.degree())))*100.0;
            System.out.println("From an analytic standpoint this probability should lie close to 1 - 1/2^k = 1 - 1/N = " + analyticalP + "%");
            System.out.println("Difference: " + Math.abs(analyticalP - simulatedP) + "%");

        }
    }
}