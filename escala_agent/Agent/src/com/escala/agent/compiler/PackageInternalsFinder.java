package com.escala.agent.compiler;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import javax.tools.JavaFileObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class PackageInternalsFinder {
	private ClassLoader classLoader;
	private static final String CLASS_FILE_EXTENSION = ".class";

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(PackageInternalsFinder.class.getName());

	public PackageInternalsFinder(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public List<JavaFileObject> find(String packageName) throws IOException {
		String javaPackageName = packageName.replaceAll("\\.", "/");

		List<JavaFileObject> result = new ArrayList<JavaFileObject>();

		Enumeration<URL> urlEnumeration = classLoader
				.getResources(javaPackageName);
		while (urlEnumeration.hasMoreElements()) { // one URL for each jar on
													// the classpath that has
													// the given package
			URL packageFolderURL = urlEnumeration.nextElement();
			result.addAll(listUnder(packageName, packageFolderURL));
		}

		return result;
	}

	private Collection<JavaFileObject> listUnder(String packageName,
			URL packageFolderURL) {
		File directory = new File(packageFolderURL.getFile());
		if (directory.isDirectory()) { // browse local .class files - useful for
										// local execution
			return processDir(packageName, directory);
		} else { // browse a jar file
			return processJar(packageFolderURL);
		} // maybe there can be something else for more involved class loaders
	}

	private List<JavaFileObject> processJar(URL packageFolderURL) {
		List<JavaFileObject> result = new ArrayList<JavaFileObject>();
		try {
			String jarUri = packageFolderURL.toExternalForm().split("!")[0];

			JarURLConnection jarConn = (JarURLConnection) packageFolderURL
					.openConnection();
			String rootEntryName = jarConn.getEntryName();
			int rootEnd = rootEntryName.length() + 1;

			Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
			while (entryEnum.hasMoreElements()) {
				JarEntry jarEntry = entryEnum.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(rootEntryName)
						&& name.indexOf('/', rootEnd) == -1
						&& name.endsWith(CLASS_FILE_EXTENSION)) {
					URI uri = URI.create(jarUri + "!/" + name);
					String binaryName = name.replaceAll("/", ".");
					binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION
							+ "$", "");

					result.add(new ClassJavaFileObject(binaryName, uri));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Wasn't able to open "
					+ packageFolderURL + " as a jar file", e);
		}
		return result;
	}

	private List<JavaFileObject> processDir(String packageName, File directory) {
		List<JavaFileObject> result = new ArrayList<JavaFileObject>();

		File[] childFiles = directory.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isFile()) {
				// We only want the .class files.
				if (childFile.getName().endsWith(CLASS_FILE_EXTENSION)) {
					String binaryName = packageName + "." + childFile.getName();
					binaryName = binaryName.replaceAll(CLASS_FILE_EXTENSION
							+ "$", "");

					result.add(new ClassJavaFileObject(binaryName, childFile
							.toURI()));
				}
			}
		}

		return result;
	}
}