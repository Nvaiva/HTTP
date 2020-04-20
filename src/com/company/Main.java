package com.company;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        HttpConnection getClass = new HttpConnection();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter URL");
        String enteredUrl = scanner.nextLine();
        URL url = new URL(enteredUrl);

        URL newUrl = new URL("http", url.getHost(), url.getPort(), url.getFile());
        if (newUrl.getProtocol() == "https" ||newUrl.getProtocol() == "http"){
            getClass.getRequest(newUrl);
        }
        else {
            System.out.println("Only Http or https protocols!");
        }
    }
}
