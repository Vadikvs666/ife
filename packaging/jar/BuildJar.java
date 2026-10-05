import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

/** Builds a single Java 8 application JAR, preserving service providers and licenses. */
public final class BuildJar {
    private final Map<String, byte[]> entries = new TreeMap<>();
    private final Map<String, LinkedHashSet<String>> services = new TreeMap<>();

    public static void main(String[] args) throws Exception {
        if (args.length < 3) throw new IllegalArgumentException("output.jar classes-dir dependency.jar ...");
        BuildJar builder = new BuildJar();
        for (int i = 2; i < args.length; i++) builder.addJar(Paths.get(args[i]));
        builder.addDirectory(Paths.get(args[1]));
        builder.write(Paths.get(args[0]));
        System.out.println("Created " + args[0] + " (" + builder.entries.size() + " entries)");
    }

    private static byte[] read(InputStream input) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int size;
        while ((size = input.read(buffer)) != -1) result.write(buffer, 0, size);
        return result.toByteArray();
    }

    private void addJar(Path path) throws IOException {
        try (JarFile jar = new JarFile(path.toFile())) {
            Enumeration<JarEntry> contents = jar.entries();
            while (contents.hasMoreElements()) {
                JarEntry entry = contents.nextElement();
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                String upper = name.toUpperCase(Locale.ROOT);
                // Signatures and package indexes belong to the original, unmerged JAR.
                if (upper.equals("META-INF/MANIFEST.MF") || upper.equals("META-INF/INDEX.LIST")
                        || upper.matches("META-INF/[^/]+\\.(SF|RSA|DSA|EC)")) continue;
                byte[] bytes;
                try (InputStream input = jar.getInputStream(entry)) { bytes = read(input); }
                if (name.startsWith("META-INF/services/")) {
                    LinkedHashSet<String> providers = services.computeIfAbsent(name, key -> new LinkedHashSet<>());
                    for (String line : new String(bytes, StandardCharsets.UTF_8).split("\\r?\\n")) {
                        String provider = line.split("#", 2)[0].trim();
                        if (!provider.isEmpty()) providers.add(provider);
                    }
                } else if (upper.matches("(META-INF/)?(LICENSE|LICENCE|NOTICE|COPYING|DEPENDENCIES).*")) {
                    put("META-INF/dependency-licenses/" + path.getFileName() + "/" + name, bytes);
                } else {
                    put(name, bytes);
                }
            }
        }
    }

    private void addDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path file : (Iterable<Path>) paths.filter(Files::isRegularFile)::iterator) {
                String name = directory.relativize(file).toString().replace('\\', '/');
                put(name, Files.readAllBytes(file));
            }
        }
    }

    private void put(String name, byte[] bytes) throws IOException {
        byte[] previous = entries.get(name);
        if (previous != null && !Arrays.equals(previous, bytes)) {
            throw new IOException("Conflicting dependency resource: " + name);
        }
        entries.put(name, bytes);
    }

    private void write(Path output) throws IOException {
        for (Map.Entry<String, LinkedHashSet<String>> service : services.entrySet()) {
            entries.put(service.getKey(), (String.join("\n", service.getValue()) + "\n")
                    .getBytes(StandardCharsets.UTF_8));
        }
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Manifest-Version", "1.0");
        attributes.putValue("Main-Class", "vadikvs.ife.MainApp");
        attributes.putValue("Implementation-Title", "IFE");
        attributes.putValue("Implementation-Version", "0.12");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(output), manifest)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                JarEntry target = new JarEntry(entry.getKey());
                target.setTime(0);
                jar.putNextEntry(target);
                jar.write(entry.getValue());
                jar.closeEntry();
            }
        }
    }
}
