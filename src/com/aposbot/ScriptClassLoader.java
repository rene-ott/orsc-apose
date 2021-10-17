package com.aposbot;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptClassLoader extends ClassLoader {

    private final HashMap<String, Class<?>> classes = new HashMap<>();
    private final HashMap<String, String> subDirectoryScriptsWithNestedClasses = new HashMap<>();
    private static final Pattern pattern = Pattern.compile("(\\\\(?<path>[a-zA-Z]+)\\\\)?(?<className>[a-zA-Z_0-9]+)(?<innerClass>\\$[a-zA-Z_0-9]+)?");

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Matcher m = pattern.matcher(name);
        if (m.matches()) {
            String className = m.group("className");

            String innerClass = m.group("innerClass");
            if (innerClass == null) {
                String path = m.group("path");
                subDirectoryScriptsWithNestedClasses.put(className, path);
            } else {
                String path = subDirectoryScriptsWithNestedClasses.get(className);
                name = "\\" + path + "\\" + name;
            }
        }
        
        final File file = new File(ScriptFrame.dir, name + ".class");
        if (!file.exists()) {
            return super.loadClass(name);
        }
        if (classes.containsKey(name)) {
            return classes.get(name);
        }
        final int len = (int) file.length();
        final byte[] b = new byte[len];
        FileInputStream in = null;
        int read = 0;
        try {
            in = new FileInputStream(file);
            do {
                final int i = in.read(b, read, len - read);
                if (i == -1)
                    throw new EOFException();
                read += i;
            } while (read < len);
        } catch (final Throwable t) {
            throw new ClassNotFoundException(t.getMessage(), t);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        }
        name = name.substring(name.lastIndexOf(File.separator) + 1);
        final Class<?> c = defineClass(name, b, 0, len);
        classes.put(name, c);
        return c;
    }
}
