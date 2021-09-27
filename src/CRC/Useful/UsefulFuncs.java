package CRC.Useful;

public class UsefulFuncs
{
    /**
     * A simple algorithm that divides two numbers in binary represented as string
     *
     * @param dividend is the numerator
     * @param divisor is the denominator
     * @return the fraction represented in binary as a string array
     */
    public static String[] regularDivision(String dividend, String divisor)
    {
        if (Integer.parseInt(divisor,2) == 0)
            throw new ArithmeticException("Can't divide by 0");
        return new String[]
                {
                        Integer.toBinaryString(Integer.parseInt(dividend,2)/Integer.parseInt(divisor,2)),
                        Integer.toBinaryString(Integer.parseInt(dividend,2)%Integer.parseInt(divisor,2))
                };
    }

    /**
     * Simple int array to string function
     *
     * @param arr the int array to convert
     * @return the int array represented as a string
     */
    public static String intArrToString(int[] arr)
    {
        StringBuilder toReturn = new StringBuilder();
        for (int value : arr)
            toReturn.append(String.valueOf(value));
        return toReturn.toString();
    }

    /**
     * Simple string to int array function
     *
     * @param s the string to be converted
     * @return the int array that was converted from string
     */
    public static int[] stringToIntArr(String s)
    {
        String[] stringArr = s.split("");
        int[] toReturn = new int[stringArr.length];

        for (int i = 0; i < stringArr.length; i++)
            toReturn[i] = Integer.parseInt(stringArr[i]);

        return toReturn;
    }

    /**
     * A simple XOR-function
     * @param a first string
     * @param b second string
     * @return a XOR b
     */
    public static String XOR(String a, String b)
    {
        StringBuilder result = new StringBuilder();

        String[] aArr = a.split("");
        String[] bArr = b.split("");
        for (int i = 1; i < Math.min(a.length(),b.length()); i++)
           result.append(String.valueOf(Integer.parseInt(aArr[i]) ^ Integer.parseInt(bArr[i])));

        return result.toString();
    }

    /**
     * A simple hamming distance code for string inputs
     * @param a first string
     * @param b second string
     * @return checks all characters within a and b and increments a counter for each mismatch (could XOR also)
     */
    public static int hammingDistance(String a, String b)
    {
        if (a.length()!=b.length())
            throw new IllegalArgumentException("Lengths don't match!");

        int sum = 0;

        String[] aArr = a.split("");
        String[] bArr = b.split("");

        for (int i = 0; i < Math.min(aArr.length, bArr.length); i++)
            sum += (aArr[i].equals(bArr[i])) ? 1 : 0;
        return sum;
    }
}
