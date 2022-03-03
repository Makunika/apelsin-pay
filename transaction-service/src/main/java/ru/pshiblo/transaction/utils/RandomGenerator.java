package ru.pshiblo.transaction.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

/**
 * @author Maxim Pshiblo
 */
@UtilityClass
public class RandomGenerator {

    @SneakyThrows
    public String randomNumber(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(SecureRandom.getInstanceStrong().nextInt(10));
        }
        return sb.toString();
    }

}
