package ru.pshiblo.auth.service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public interface KeyGeneratorService {
    KeyPair getKey() throws Exception;
}
