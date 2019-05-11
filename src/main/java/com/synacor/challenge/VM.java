package com.synacor.challenge;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static java.nio.file.Files.readAllBytes;

public class VM {
    static final int MODULO = 32768;
    private static Deque<Integer> stack = new LinkedList<>();
    private static int[] registers = new int[8];
    private static int address = 0;
    private static final int NEWLINE = 10;
    private static final String FILENAME = "challenge.bin";
    private static final String PATH = "src/main/resources/";
    static final String[] MAZE = (". . . . . .\n" +
            ". * 8 - 1 .\n" +
            ". 4 * 11 * .\n" +
            ". + 4 - 18 .\n" +
            ". 22 - 9 * .\n" +
            ". . . . . .").split("\\s");

    public static void main(String[] args) throws IOException {
        execute(loadProgram(PATH + FILENAME));
    }

    private static byte[] loadProgram(final String file) throws IOException {
        final byte[] program = readAllBytes(new File(file).toPath());
        System.out.println("Program size is: " + program.length / 2);
        return program;
    }

    private static void loadAddressAndRegisters(final String file) throws IOException {
        final List<String> lines = Files.readAllLines(new File(file).toPath());
        address = Integer.parseInt(lines.get(0));
        for (int i = 0; i < 8; i++) {
            registers[i] = Integer.parseInt(lines.get(i + 1));
        }
    }

    private static void decode(final byte[] program, final boolean strings, final String file) throws IOException {
        int a;
        int b;
        int c;
        final String fileName;
        final int myAddress = address;
        address = 0;
        if (strings) {
            fileName = file + "_strings.txt";
        } else {
            fileName = file + ".txt";
        }
        try (final BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName))) {
            while (address * 2 < program.length) {
                if (!strings) {
                    writer.write(address + ": ");
                }
                final int op = nextNumber(program);
                if (strings) {
                    if ('a' <= op && op <= 'z' || 'A' <= op && op <= 'Z' || op == NEWLINE || op == ' ') {
                        writer.write(op);
                    }
                    continue;
                }
                switch (op) {
                    case 0:
                        //halt: 0
                        //stop execution and terminate the program
                        writer.append("halt");
                        writer.newLine();
                        break;
                    case 1:
                        //set: 1 a b
                        //set register <a> to the value of <b>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("set ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 2:
                        //push: 2 a
                        //push<a> onto the stack
                        a = nextNumber(program);
                        writer.append("push ").append(printValue(a));
                        writer.newLine();
                        break;
                    case 3:
                        //pop: 3 a
                        //remove the top element from the stack and write it into <a>; empty stack = error
                        a = nextNumber(program);
                        writer.append("pop ").append(printValue(a));
                        writer.newLine();
                        break;
                    case 4:
                        //eq: 4 a b c
                        //set <a> to 1 if <b> is equal to <c>; set it to 0 otherwise
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("eq ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 5:
                        //gt: 5 a b c
                        //set <a> to 1 if <b> is greater than <c>; set it to 0 otherwise
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("gt ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 6:
                        //jmp: 6 a
                        //jump to <a>
                        a = nextNumber(program);
                        writer.append("jmp ").append(printValue(a));
                        writer.newLine();
                        break;
                    case 7:
                        //jt: 7 a b
                        //if <a> is nonzero, jump to <b>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("jt ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 8:
                        //jf: 8 a b
                        //if <a> is zero, jump to <b>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("jf ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 9:
                        //add: 9 a b c
                        //assign into <a> the sum of <b> and <c> (modulo 32768)
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("add ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 10:
                        //mult: 10 a b c
                        //store into <a> the product of <b> and <c> (modulo 32768)
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("mult ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 11:
                        //mod: 11 a b c
                        //store into <a> the remainder of <b> divided by <c>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("mod ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 12:
                        //and: 12 a b c
                        //stores into <a> the bitwise and of <b> and <c>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("and ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 13:
                        //or: 13 a b c
                        //stores into <a> the bitwise or of <b> and <c>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        c = nextNumber(program);
                        writer.append("or ").append(printValue(a)).append(" ").append(printValue(b)).append(" ")
                                .append(printValue(c));
                        writer.newLine();
                        break;
                    case 14:
                        //not: 14 a b
                        //stores 15-bit bitwise inverse of <b> in <a>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("not ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 15:
                        //rmem: 15 a b
                        //read memory at address <b> and write it to <a>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("rmem ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 16:
                        //wmem: 16 a b
                        //write the value from <b> into memory at address <a>
                        a = nextNumber(program);
                        b = nextNumber(program);
                        writer.append("wmem ").append(printValue(a)).append(" ").append(printValue(b));
                        writer.newLine();
                        break;
                    case 17:
                        //call: 17 a
                        //write the address of the next instruction to the stack and jump to <a>
                        a = nextNumber(program);
                        writer.append("call ").append(printValue(a));
                        writer.newLine();
                        break;
                    case 18:
                        //ret: 18
                        //remove the top element from the stack and jump to it; empty stack = halt*/
                        writer.append("ret");
                        writer.newLine();
                        break;
                    case 19:
                        //out: 19 a
                        //write the character represented by ascii code <a> to the terminal
                        a = nextNumber(program);
                        writer.append("out ").append(a < MODULO ? (char) a + "" : printValue(a));
                        writer.newLine();
                        break;
                    case 20:
                        //in: 20 a
                        //read a character from the terminal and write its ascii code to <a>; it can be assumed that once input starts, it will continue until a newline is encountered; this means that you can safely read whole lines from the keyboard and trust that they will be fully read
                        a = nextNumber(program);
                        writer.append("in ").append(printValue(a));
                        writer.newLine();
                        break;
                    case 21:
                        //no op
                        writer.append("no op");
                        writer.newLine();
                        break;
                    default:
                        writer.write((char) op + "");
                        writer.newLine();
                }
            }
        }
        address = myAddress;
    }

    private static void execute(final byte[] memory) throws IOException {
        final Scanner scanner = new Scanner(System.in);
        Queue<String> directions = new LinkedList<>();
        String str = null;
        int i = 0;
        int a;
        int b;
        int c;
        while (address * 2 < memory.length) {
            final int op = nextNumber(memory);
            switch (op) {
                case 0:
                    //halt: 0
                    //stop execution and terminate the program
                    System.exit(0);
                    break;
                case 1:
                    //set: 1 a b
                    //set register <a> to the value of <b>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    registers[a] = b;
                    break;
                case 2:
                    //push: 2 a
                    //push<a> onto the stack
                    a = toValue(nextNumber(memory));
                    stack.push(a);
                    break;
                case 3:
                    //pop: 3 a
                    //remove the top element from the stack and write it into <a>; empty stack = error
                    a = toRegisterAddress(nextNumber(memory));
                    registers[a] = stack.pop();
                    break;
                case 4:
                    //eq: 4 a b c
                    //set <a> to 1 if <b> is equal to <c>; set it to 0 otherwise
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = (b == c) ? 1 : 0;
                    break;
                case 5:
                    //gt: 5 a b c
                    //set <a> to 1 if <b> is greater than <c>; set it to 0 otherwise
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = (b > c) ? 1 : 0;
                    break;
                case 6:
                    //jmp: 6 a
                    //jump to <a>
                    a = toValue(nextNumber(memory));
                    address = a;
                    break;
                case 7:
                    //jt: 7 a b
                    //if <a> is nonzero, jump to <b>
                    a = toValue(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    if (a != 0) {
                        address = b;
                    }
                    break;
                case 8:
                    //jf: 8 a b
                    //if <a> is zero, jump to <b>
                    a = toValue(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    if (a == 0 && address != 5454 /* always activate teleporter */) {
                        address = b;
                    }
                    break;
                case 9:
                    //add: 9 a b c
                    //assign into <a> the sum of <b> and <c> (modulo 32768)
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = (b + c) % MODULO;
                    break;
                case 10:
                    //mult: 10 a b c
                    //store into <a> the product of <b> and <c> (modulo 32768)
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = (b * c) % MODULO;
                    break;
                case 11:
                    //mod: 11 a b c
                    //store into <a> the remainder of <b> divided by <c>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = b % c;
                    break;
                case 12:
                    //and: 12 a b c
                    //stores into <a> the bitwise and of <b> and <c>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = b & c;
                    break;
                case 13:
                    //or: 13 a b c
                    //stores into <a> the bitwise or of <b> and <c>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    c = toValue(nextNumber(memory));
                    registers[a] = b | c;
                    break;
                case 14:
                    //not: 14 a b
                    //stores 15-bit bitwise inverse of <b> in <a>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    registers[a] = ~b & 0x7fff;
                    break;
                case 15:
                    //rmem: 15 a b
                    //read memory at address <b> and write it to <a>
                    a = toRegisterAddress(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    registers[a] = toValue(readNumber(memory, b));
                    break;
                case 16:
                    //wmem: 16 a b
                    //write the value from <b> into memory at address <a>
                    a = toValue(nextNumber(memory));
                    b = toValue(nextNumber(memory));
                    memory[a * 2] = (byte) b;
                    memory[a * 2 + 1] = (byte) (b >> 8);
                    break;
                case 17:
                    //call: 17 a
                    if (address == 5490) {
                        //optimize teleporter computation
                        registers[0] = 6;
                        registers[7] = new Teleporter(6).teleport();
                    } else {
                        //write the address of the next instruction to the stack and jump to <a>
                        a = toValue(nextNumber(memory));
                        stack.push(address);
                        address = a;
                    }
                    break;
                case 18:
                    //ret: 18
                    //remove the top element from the stack and jump to it; empty stack = halt
                    address = stack.pop();
                    break;
                case 19:
                    //out: 19 a
                    //write the character represented by ascii code <a> to the terminal
                    a = toValue(nextNumber(memory));
                    System.out.print((char) a);
                    break;
                case 20:
                    //in: 20 a
                    //read a character from the terminal and write its ascii code to <a>; it can be assumed that once input starts, it will continue until a newline is encountered; this means that you can safely read whole lines from the keyboard and trust that they will be fully read
                    if (str == null) {
                        str = scanner.nextLine();
                        if (str.startsWith("decode")) {
                            final String fileName = PATH + str.split(" ")[1];
                            decode(memory, true, fileName);
                            decode(memory, false, fileName);
                        } else if (str.startsWith("save")) {
                            final String fileName = PATH + str.split(" ")[1];
                            try (final OutputStream writer = new FileOutputStream(fileName)) {
                                writer.write(memory);
                            }
                            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".addr"))) {
                                writer.write((address - 1) + "");
                                writer.newLine();
                                for (int z = 0; z < 8; z++) {
                                    writer.write(registers[z] + "");
                                    writer.newLine();
                                }
                            }
                        } else if (str.startsWith("load")) {
                            final String fileName = PATH + str.split(" ")[1];
                            loadAddressAndRegisters(fileName + ".addr");
                            execute(loadProgram(fileName));
                        } else if (str.equals("solve orb")) {
                            directions = new Orb().solveOrb(MAZE);
                            str = directions.remove();
                        }
                    }
                    if (i < str.length()) {
                        b = str.charAt(i++);
                    } else {
                        b = NEWLINE; //represents a new line
                        str = directions.poll();
                        i = 0;
                    }
                    a = toRegisterAddress(nextNumber(memory));
                    registers[a] = (char) b;
                    break;
                case 21:
                    //no op
                    break;
                default:
            }
        }
    }

    private static int nextNumber(final byte[] arr) {
        final int i = readNumber(arr);
        address++;
        return i;
    }

    private static int readNumber(final byte[] arr) {
        return readNumber(arr, address);
    }

    private static int readNumber(final byte[] arr, final int i) {
        return (arr[i * 2] & 0xFF) | ((arr[i * 2 + 1] & 0xFF) << 8);
    }

    private static int toRegisterAddress(final int i) {
        return i - MODULO;
    }

    private static int toValue(final int i) {
        if (i < MODULO) {
            return i;
        } else {
            return registers[toRegisterAddress(i)];
        }
    }

    private static String printValue(final int i) {
        if (i < MODULO) {
            return String.valueOf(i);
        } else {
            return "r[" + toRegisterAddress(i) + "]";
        }
    }
}
