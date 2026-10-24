package dev.n0153.app.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class PluginLoader extends ClassLoader {
    private static final Logger logger = LogManager.getLogger(PluginLoader.class);
    private final JarFile jar;
    private final Path jarPath;

    private PluginLoader(Path jarPath) throws IOException {
        super(PluginLoader.class.getClassLoader());
        this.jarPath = jarPath;
        this.jar = new JarFile(jarPath.toFile());
    }

    @Override
    protected Class<?> findClass (String name) throws ClassNotFoundException {
        JarEntry entry = jar.getJarEntry(name.replace(".", "/") +".class");
        if (entry == null) throw new ClassNotFoundException(name);

        try (InputStream in = jar.getInputStream(entry)) {
            byte[] bytes = in.readAllBytes();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    @Override
    protected URL findResource(String name) {
        if (jar.getJarEntry(name) == null) return null;
        try {
            return URI.create("jar:" + jarPath.toUri() + "!/" + name).toURL();
        } catch (MalformedURLException e) {
            logger.error(e);
            return null;
        }
    }

    public void close() throws IOException {
        jar.close();
    }

    public static List<Path> discoverPluginDirs() throws IOException {
        Path pluginsDir = Path.of("plugins");
        if (!Files.isDirectory(pluginsDir)) {
            logger.warn("Plugins directory not found: {}", pluginsDir.toAbsolutePath());
            return List.of();
        }
        try (Stream<Path> entries = Files.list(pluginsDir)) {
            return entries
                    .filter(Files:: isDirectory)
                    .filter(path -> path.getFileName().toString().contains("Plugin"))
                    .toList();
        }
    }

    public static void registerPlugins(List<Path> targetDirs, PluginRegistry registry, GlobalConfig config) {
        for (Path dir : targetDirs) {
            try (Stream<Path> files = Files.list(dir)) {
                Path jarPath = files.filter(path -> path.toString().endsWith(".jar")).findFirst().orElseThrow();
                PluginLoader loader = new PluginLoader(jarPath);
                String entry = loader.jar.getManifest().getMainAttributes().getValue("Disarm-Plugin-Class");
                MediaPlugin plugin = (MediaPlugin) loader.loadClass(entry)
                        .getDeclaredConstructor().newInstance();
                // register discovered plugin
                plugin.registerGlobalConfig(config);
                MediaConfig pluginConfig = plugin.getConfig();
                plugin.register(registry, pluginConfig);
            } catch (IOException | LinkageError | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | IllegalAccessException | InstantiationException e) {
                logger.error("Failed to register plugin in {}", dir.getFileName(), e);
            }
        }
    }

}
