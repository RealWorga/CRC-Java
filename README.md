# CRC-Java
An assignment for the course Data-communication where we were to implement a simulating CRC code

To run the simulation, please go to the file CRC.java in the source folder and run the static class "TestSimulation"
You're able to change the generator polynomial, messages are generated randomly.

As you also can see from the main class CRC, there is a main function which can generate a random message, encode it with the remainder of the modulo-2 binary division and then generate a simulated error vector which is appended to the encoded message. The amount of errors, if the redundancy bits are affected and the probability of these errors are a parameter which can be changed. We also have a checkMessage function which checks the encoded message with the same modulo-2 binary division to check for errors.

All codes are properly documented using the standard Java-doc notations.

Have fun! :)
