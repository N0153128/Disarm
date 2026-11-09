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

/**
 * Custom {@link ClassLoader} that loads a single plugin jar discovered at
 * runtime under the {@code plugins/} directory, and the static machinery
 * used to discover plugin directories and register each plugin's jar
 * against a {@link PluginRegistry}. Each instance is scoped to exactly one
 * plugin jar, identified by its {@code Disarm-Plugin-Class} manifest attribute.
 * @since 0.2
 */
public class PluginLoader extends ClassLoader {
    private static final Logger logger = LogManager.getLogger(PluginLoader.class);
    private final JarFile jar;
    private final Path jarPath;

    /**
     * Opens the given plugin jar for class/resource loading.
     * @param jarPath path to the plugin jar
     * @throws IOException if the jar cannot be opened
     * @since 0.2
     */
    private PluginLoader(Path jarPath) throws IOException {
        super(PluginLoader.class.getClassLoader());
        this.jarPath = jarPath;
        this.jar = new JarFile(jarPath.toFile());
    }

    /**
     * Locates and defines a class by name from within this loader's jar.
     * @param name fully qualified class name
     * @return the defined class
     * @throws ClassNotFoundException if the class entry is missing from the jar, or its bytes cannot be read
     * @since 0.2
     */
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

    /**
     * Resolves a resource URL for an entry within this loader's jar.
     * @param name resource path within the jar
     * @return a {@code jar:} URL for the resource, or null if it isn't present or the URL is malformed
     * @since 0.2
     */
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

    /**
     * Closes the underlying jar file.
     * @throws IOException if closing the jar fails
     * @since 0.2
     */
    public void close() throws IOException {
        jar.close();
    }

    /**
     * Scans the {@code plugins/} directory for subdirectories whose name
     * contains "Plugin" — each such directory is expected to hold one
     * plugin jar.
     * @return the discovered plugin directories, or an empty list if {@code plugins/} doesn't exist
     * @throws IOException if listing the plugins directory fails
     * @since 0.2
     */
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

    /**
     * Loads and registers one plugin per given directory: finds the first
     * {@code .jar} file in the directory, loads its {@code Disarm-Plugin-Class}
     * manifest entry via a new {@link PluginLoader}, instantiates it, supplies
     * the shared global config, and registers it against the given registry.
     * A directory that fails to load or register is logged and skipped;
     * it does not abort registration of the remaining directories.
     * @param targetDirs plugin directories to load, as returned by {@link #discoverPluginDirs()}
     * @param registry the registry to register each discovered plugin against
     * @param config the shared global config to supply to each plugin
     * @since 0.2
     */
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
