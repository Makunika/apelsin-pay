package ru.pshiblo.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.service.KeyGeneratorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyGeneratorServiceImpl implements KeyGeneratorService {

    private static final String ALGORITHM = "RSA";
    private static final String PRIVATE_KEY_FILENAME = "private.key";
    private static final String PUBLIC_KEY_FILENAME = "public.key";

    @Value("${auth.secret.path}")
    private String keysPath;

    @Override
    public KeyPair getKey() throws Exception {
        Path privateKeyPath = Paths.get(keysPath, PRIVATE_KEY_FILENAME);
        Path publicKeyPath = Paths.get(keysPath, PUBLIC_KEY_FILENAME);

        try {
            if (Files.notExists(privateKeyPath) || Files.notExists(publicKeyPath)) {
                Path keysPath = Paths.get(this.keysPath);
                if (Files.notExists(keysPath)) {
                    Files.createDirectory(keysPath);
                }
                KeyPair keyPair = generateKey();
                saveKeys(keyPair, this.keysPath);
                return keyPair;
            } else {
                return loadKey(keysPath);
            }
        } catch (Exception e) {
            log.error("Error loading or generating key pair for token signing", e);
            throw e;
        }
    }

    private KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private void saveKeys(KeyPair keys, String path) throws IOException {
        Files.write(Paths.get(path, PUBLIC_KEY_FILENAME), Base64.getEncoder().encode(keys.getPublic().getEncoded()));
        Files.write(Paths.get(path, PRIVATE_KEY_FILENAME), Base64.getEncoder().encode(keys.getPrivate().getEncoded()));
    }

    private KeyPair loadKey(String path) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(path, PRIVATE_KEY_FILENAME)));
        byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(path, PUBLIC_KEY_FILENAME)));
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM); // or "EC" or whatever
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return new KeyPair(publicKey, privateKey);
    }
}
