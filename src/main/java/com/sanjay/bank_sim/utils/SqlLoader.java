package com.sanjay.bank_sim.utils;

import java.io.IOException;
import java.io.InputStream;

public class SqlLoader {
    public static String load(String path) {
        try (InputStream is = SqlLoader.class
                .getClassLoader()
                .getResourceAsStream("queries/" + path)) {
            return new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Could not load SQL file: " + path, e);
        }
    }
}
