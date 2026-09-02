package ru.pshiblo.auth.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.pshiblo.auth.config.AuthProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA-пара для подписи JWT: читается из {@code <auth.keys.path>/private.key} и {@code public.key}
 * (Base64 DER: PKCS#8 / X.509 — формат прежнего KeyGeneratorService), при отсутствии — генерируется
 * и сохраняется туда же. Каталог задаётся через AUTH_KEYS_PATH (по умолчанию ./.keys) и не должен
 * попадать в git.
 */
@Slf4j
@Component
public class RsaKeyPairStore {

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    static final String PRIVATE_KEY_FILENAME = "private.key";
    static final String PUBLIC_KEY_FILENAME = "public.key";

    private final Path keysDir;

    public RsaKeyPairStore(AuthProperties properties) {
        this.keysDir = Paths.get(properties.getKeys().getPath());
    }

    public RSAKey loadOrGenerate() {
        try {
            KeyPair keyPair;
            Path privateKeyPath = keysDir.resolve(PRIVATE_KEY_FILENAME);
            Path publicKeyPath = keysDir.resolve(PUBLIC_KEY_FILENAME);
            if (Files.exists(privateKeyPath) && Files.exists(publicKeyPath)) {
                keyPair = load(privateKeyPath, publicKeyPath);
                log.info("Loaded RSA signing key pair from {}", keysDir.toAbsolutePath());
            } else {
                Files.createDirectories(keysDir);
                keyPair = generate();
                save(keyPair, privateKeyPath, publicKeyPath);
                log.warn("RSA signing key pair not found, generated a new one in {}", keysDir.toAbsolutePath());
            }
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .build();
            // стабильный kid: отпечаток открытого ключа, одинаковый при каждом старте
            return new RSAKey.Builder(rsaKey)
                    .keyID(rsaKey.computeThumbprint().toString())
                    .build();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            throw new IllegalStateException("Error loading or generating key pair for token signing", e);
        }
    }

    private KeyPair generate() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    private void save(KeyPair keys, Path privateKeyPath, Path publicKeyPath) throws IOException {
        Files.write(publicKeyPath, Base64.getEncoder().encode(keys.getPublic().getEncoded()));
        Files.write(privateKeyPath, Base64.getEncoder().encode(keys.getPrivate().getEncoded()));
    }

    private KeyPair load(Path privateKeyPath, Path publicKeyPath)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = Base64.getMimeDecoder().decode(Files.readAllBytes(privateKeyPath));
        byte[] publicKeyBytes = Base64.getMimeDecoder().decode(Files.readAllBytes(publicKeyPath));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return new KeyPair(publicKey, privateKey);
    }
}
