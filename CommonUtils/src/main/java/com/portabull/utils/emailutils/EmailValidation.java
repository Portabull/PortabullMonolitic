package com.portabull.utils.emailutils;

import com.portabull.utils.datautils.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class EmailValidation {

    private EmailValidation() {
    }


    public static boolean isAddressValid(String emailAddress) {
        try {

            int position = emailAddress.indexOf('@');

            if (position == -1)
                return false;

            ArrayList<String> mxList = evaluateHostName(emailAddress.substring(++position));

            if (CollectionUtils.isEmpty(mxList))
                return false;


            for (String mx : mxList) {

                try (Socket socket = new Socket(mx, 25)) {

                    try (BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                        try (BufferedWriter bufferedReaderOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                            return invokeInputOutput(bufferedReaderInput, bufferedReaderOutput, emailAddress);

                        }

                    }

                }

            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static boolean invokeInputOutput(BufferedReader bufferedReaderInput, BufferedWriter bufferedReaderOutput, String emailAddress) throws IOException {
        int response = invokeInput(bufferedReaderInput);

        if (response != 220) {
            //Invalid header
            return false;
        }

        invokeOutput(bufferedReaderOutput, "EHLO orbaker.com");

        response = invokeInput(bufferedReaderInput);

        if (response != 250) {
            //Not ESMTP
            return false;
        }

        // validate the sender address
        invokeOutput(bufferedReaderOutput, "MAIL FROM: <tim@orbaker.com>");
        response = invokeInput(bufferedReaderInput);
        if (response != 250) {
            //Sender rejected
            return false;
        }
        invokeOutput(bufferedReaderOutput, StringUtils.append("RCPT TO: <", emailAddress, ">"));
        response = invokeInput(bufferedReaderInput);

        invokeOutput(bufferedReaderOutput, "RSET");
        invokeInput(bufferedReaderInput);
        invokeOutput(bufferedReaderOutput, "QUIT");
        invokeInput(bufferedReaderInput);
        return response == 250;
    }

    private static int invokeInput(BufferedReader bufferedReader) throws IOException {
        String line;
        int response = 0;
        while ((line = bufferedReader.readLine()) != null) {
            try {
                response = Integer.parseInt(line.substring(0, 3));
            } catch (Exception ex) {
                response = -1;
            }
            if (line.charAt(3) != '-')
                break;
        }
        return response;
    }

    private static void invokeOutput(BufferedWriter bufferedWriter, String text) throws IOException {
        bufferedWriter.write(StringUtils.append(text, "\r\n"));
        bufferedWriter.flush();
    }

    private static ArrayList<String> evaluateHostName(String hostName) throws NamingException {

        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext dirContext = new InitialDirContext(env);
        Attributes attributes = dirContext.getAttributes
                (hostName, new String[]{"MX"});
        Attribute attribute = attributes.get("MX");

        if ((attribute == null) || (attribute.size() == 0)) {
            attributes = dirContext.getAttributes(hostName, new String[]{"A"});
            attribute = attributes.get("A");
            if (attribute == null)
                throw new NamingException
                        (StringUtils.append("No match for name '", hostName, "'"));
        }

        ArrayList<String> response = new ArrayList<>();
        NamingEnumeration<String> namingEnumeration = (NamingEnumeration<String>) attribute.getAll();
        while (namingEnumeration.hasMore()) {
            String[] enumerations = namingEnumeration.next().split(" ");
            if (enumerations[1].endsWith("."))
                enumerations[1] = enumerations[1].substring(0, (enumerations[1].length() - 1));
            response.add(enumerations[1]);
        }
        return response;
    }


}
