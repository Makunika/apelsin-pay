package ru.pshiblo.transaction.utils;

import lombok.experimental.UtilityClass;

/**
 * @author Maxim Pshiblo
 */
@UtilityClass
public class RandomGenerator {

    public String randomNumber(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(java.util.random.RandomGenerator.getDefault().nextInt(0, 10));
        }
        return sb.toString();
    }

}
