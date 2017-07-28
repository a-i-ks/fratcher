package de.iske.fratcher.util;

/**
 * Helper functions to retrieve server URL for return correct entity resource paths.
 *
 * Since we possibly are behind load balancers and want to be as deployment agnostic as possible we simply configure
 * our host URL.
 * Adopted from: https://github.com/micromata/webengineering-2017/blob/master/src/main/java/com/micromata/webengineering/demo/util/AddressService.java
 * @author Michael Lesniak
 */
public interface AddressService {
    /**
     * Return server URL.
     *
     * @return server URL.
     */
    String getServerURL();
}