package com.portabull.utils;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RemoteTransferUtils {

    private static final String REMOTE_HOST = "192.168.0.49";
    private static final int REMOTE_PORT = 22;
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 10000;
    private static final JSch jSch = new JSch();

    public void transferFilesToRemote(String localFilePath, String destinationFilePath) throws JSchException, SftpException {
        Session session = null;

        try {

            session = openSession();

            Channel channel = session.openChannel("sftp");

            channel.connect(CHANNEL_TIMEOUT);

            ChannelSftp channelSftp = (ChannelSftp) channel;

            channelSftp.put(localFilePath, destinationFilePath);

            channelSftp.exit();
        } catch (JSchException | SftpException ex) {
            throw ex;
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    private static Session openSession() throws JSchException {

        Session session = jSch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);
        session.setPassword(PASSWORD);
        session.connect(SESSION_TIMEOUT);

        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        return session;
    }


}
