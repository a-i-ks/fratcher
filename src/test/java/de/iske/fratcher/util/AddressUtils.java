package de.iske.fratcher.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class AddressUtils {

    /**
     * Use the AddressService to get the current server address and fix the port to the correct value, that is used
     * in the test environment
     *
     * @param pathSuffix - the location path after the server address
     * @param newPort    - the test environment port
     * @return the correct server url
     */
    public static String getURL(String serverURL, String pathSuffix, int newPort) throws MalformedURLException {
        URL originalUrl = new URL(serverURL);
        URL correctUrl = new URL(originalUrl.getProtocol(), originalUrl.getHost(), newPort, originalUrl.getFile());
        StringBuilder url = new StringBuilder(correctUrl.toString());
        if (!url.toString().endsWith("/")) {
            url.append("/");
        }
        url.append(pathSuffix);
        url.append('/');
        return url.toString();
    }

    /**
     * Use the AddressService to get the current server address and fix the port to the correct value, that is used
     * in the test environment
     *
     * @param pathSuffix - the location path after the server address
     * @param newPort    - the test environment port
     * @return the correct server url as URI
     */
    public static URI getURLasURI(String serverURL, String pathSuffix, int newPort) throws MalformedURLException {
        return URI.create(getURL(serverURL, pathSuffix, newPort));
    }

}
