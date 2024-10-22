package com.example.classtopdf;

public class A {
    static String name;        // имя
    static int age;            // возраст
    String[] mass = new String[5];
    int[] mass2 = new int[5];
    static void displayInfo(){
        System.out.printf("Name: %s \tAge: %d\n", name, age);
    }
}
