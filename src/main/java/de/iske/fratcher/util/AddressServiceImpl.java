package de.iske.fratcher.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Default implementation of AddressService.
 * Adopted from: https://github.com/micromata/webengineering-2017/blob/master/src/main/java/com/micromata/webengineering/demo/util/AddressServiceImpl.java
 * @see de.iske.fratcher.util.AddressService
 * @author Michael Lesniak
 */
@Service
@Profile("default")
public class AddressServiceImpl implements AddressService {
    @Value("${addressService.address}")
    private String serverAddress;

    @Override
    public String getServerURL() {
        return serverAddress;
    }
}
