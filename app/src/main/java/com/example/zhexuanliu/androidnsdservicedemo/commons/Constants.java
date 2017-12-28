package com.example.zhexuanliu.androidnsdservicedemo.commons;

/**
 * Created by zhexuanliu on 12/23/17.
 */

public class Constants {
    /**
     * Service type for Network Service Discovery
     */
    public static String SERVICE_TYPE = "_http._tcp.";

    /**
     * Normal log tag (default one)
     */
    public static String LOG_TAG = "NSDDemo";

    /**
     * Toast message displayed when service name is empty
     */
    public static String SERVICE_NAME_EMPTY_PROMPT = "Please enter service name first.";

    /**
     * Toast message displayed when service name is already registered
     */
    public static String SERVICE_NAME_REGISTERED_PROMPT = "Service name already registered";

    /**
     * Toast message displayed when a service is successfully registered
     */
    public static String REGISTER_SUCCESSFUL = "Registration succeeds";

    /**
     * Name of the background handler thread
     */
    public static String BACKGROUND_HANDLER_THREAD = "backgroudhandler";

    /**
     * Toast message displayed when a service is not discovered but asked to connect
     */
    public static String SERVICE_NOT_DISCOVERED_PROMPT = "Service is not discovered yet";

    /**
     * Toast message displayed when a service is not connected but user tried to send message
     */
    public static String SERVICE_NOT_CONNECTED_PROMPT = "Service is not connected yet";

    /**
     * Toast message displayed when a service is connected successfully
     */
    public static String SERVICE_CONNECTED_PROMPT = "Service connected";

    /**
     * Message replied by server when a client is connected
     */
    public static String YOU_ARE_CONNECTED = "You are connected with ";

    /**
     * Size of a data buffer used in datapacket
     */
    public static int DATA_BUFFER_SIZE = 4096;

    /**
     * Message for updating registered service
     */
    public static final int MSG_UPDATE_REGISTERED_SERVICE = 0;

    /**
     * Message for discovering a service
     */
    public static final int MSG_DISCOVERED_SERVICE = 1;

    /**
     * Message for connecting a service
     */
    public static final int MSG_CONNECTED_SERVICE = 2;

    /**
     * Message for successfully created a server
     */
    public static final int MSG_SERVER_CREATED = 3;

    /**
     * Message for client receiving message
     */
    public static final int MSG_CLIENT_RECEIVE = 4;

    /**
     * Message for client connected to server
     */
    public static final int MSG_CLIENT_CONNECTED = 5;

    /**
     * Message for sending a message
     */
    public static final int MSG_SEND_MESSAGE = 6;
}
