package com.example.IoCContainerAndDI.bean;

public class ConsolePrinter implements Printer {
	public void print(String message) {
		System.out.println(message);
	}
}
