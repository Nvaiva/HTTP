package com.company;

import java.io.*;
import java.net.*;

public class HttpConnection {

    private static final int port = 80;
    private static final String filePath = "C:\\Users\\vnost\\Desktop\\";
    private static final String fileName = "failas";
    private String format;
    private String contentType;
    private int headerBegin;

    private void printHeader(InputStream in, String requestName, InputStream inputStreamCopy) throws IOException {
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(in));
        String outStr, response = "";
        while ((outStr = bufRead.readLine()) != null) {
            response += outStr;
            response += "\r\n";
            if (outStr.equals(""))
                break;
        }
        final String statusCode = response.substring(response.indexOf(" ") + 1, response.indexOf(" ") + 4);

        if (response != "" && statusCode.equals("200")) {

            System.out.println(requestName + " method status code:" + statusCode);
            System.out.println(response);
            headerBegin = response.length();
        } else {
            System.out.println(requestName + " method error code:" + statusCode);
        }
        if (requestName == "GET") {
            setFormat(response);
            getBody(inputStreamCopy);
        }
    }
    private void getBody(InputStream in) throws IOException {
        File file = new File(filePath + fileName + format);
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final InputStream inputStream = in;

        boolean headerEnded = false;

        byte[] bytes = new byte[2048];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            if (headerEnded)
                fileOutputStream.write(bytes, 0, length);
            else {
                for (int i = 0; i < 2045; i++) {
                    if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                        headerEnded = true;
                        fileOutputStream.write(bytes, i + 4, 2048 - i - 4);
                        break;
                    }
                }
            }
        }
        inputStream.close();
        fileOutputStream.close();
    }
    private void setFormat(String response) {
        String contentType = response.substring(response.indexOf("Content-Type:"));

        if (contentType.indexOf(";") < contentType.indexOf("\r\n") && contentType.indexOf(";") != -1) {
            contentType = contentType.substring(0, contentType.indexOf(";"));
        } else {
            contentType = contentType.substring(0, contentType.indexOf("\r\n"));
        }
        this.contentType = contentType.substring(contentType.indexOf(":") + 2, contentType.indexOf("/"));
        this.format = contentType.substring(contentType.indexOf("/") + 1);
        this.format = "." + this.format;
    }
    public void getRequest(URL url) throws IOException {

        Socket socket = new Socket(url.getHost(), port);
        OutputStream out = socket.getOutputStream();

        String request = "GET " + url.getPath() + "?" + " HTTP/1.1\r\n"
                + "Host: " + url.getHost() + "\r\n"
                + "Connection: Close\r\n\r\n";

        out.write(request.getBytes());
        out.flush();

        //get InputStream and make two deep copies
        InputStream in = socket.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        in.transferTo(baos);
        InputStream firstClone = new ByteArrayInputStream(baos.toByteArray());
        InputStream secondClone = new ByteArrayInputStream(baos.toByteArray());

        printHeader(firstClone, "GET", secondClone);
        socket.close();
    }
}