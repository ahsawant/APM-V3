package com.escala.agent.compiler;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomClassloaderJavaFileManager implements JavaFileManager {

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(CustomClassloaderJavaFileManager.class.getName());

	private final ClassLoader classLoader;
	private final StandardJavaFileManager standardFileManager;
	private final PackageInternalsFinder finder;

	private Map<String, ClassJavaFileObject> newClasses = new HashMap<String, ClassJavaFileObject>();

	public CustomClassloaderJavaFileManager(ClassLoader classLoader,
			StandardJavaFileManager standardFileManager) {
		this.classLoader = classLoader;
		this.standardFileManager = standardFileManager;
		finder = new PackageInternalsFinder(classLoader);
	}

	@Override
	public ClassLoader getClassLoader(Location location) {

		// Do NOT return the classLoader instance from this class here. This
		// causes a nasty bug that after the compile task is executed, the
		// system classloader (because that's the one I passed into this class)
		// fails to load any new class and throws a NoClassDefFoundException; it
		// appears that CompilationTask.call() does something to classloaders
		// that is not part of the public contract...(BUG From what I can tell)
		return null;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof ClassJavaFileObject) {
			return ((ClassJavaFileObject) file).binaryName();
		} else { // if it's not CustomJavaFileObject, then it's coming from
					// standard file manager - let it handle the file
			return standardFileManager.inferBinaryName(location, file);
		}
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasLocation(Location location) {
		// we don't care about source and other location types - not needed for
		// compilation
		return location == StandardLocation.CLASS_PATH
				|| location == StandardLocation.PLATFORM_CLASS_PATH;

	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location,
			String className, JavaFileObject.Kind kind) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, JavaFileObject.Kind kind, FileObject sibling)
			throws IOException {
		logger.info(new StringBuffer("Location: ").append(location)
				.append("; ClassName: ").append(className).append("; Kind: ")
				.append(kind).append("; Sibbling: ").append(sibling.getName()));
		ClassJavaFileObject newClass = new ClassJavaFileObject(className,
				URI.create("class:///" + className + Kind.CLASS.extension));
		newClasses.put(className, newClass);
		return (newClass);
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName,
			String relativeName) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName,
			String relativeName, FileObject sibling) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws IOException {
		// do nothing
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
			Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		if (location == StandardLocation.PLATFORM_CLASS_PATH) { // let standard
																// manager
																// hanfle
			return standardFileManager.list(location, packageName, kinds,
					recurse);
		} else if (location == StandardLocation.CLASS_PATH
				&& kinds.contains(JavaFileObject.Kind.CLASS)) {
			if (packageName.startsWith("java.")) { // a hack to let standard
													// manager handle locations
													// like "java.lang" or
													// "java.util". Prob would
													// make sense to join
													// results of standard
													// manager with those of my
													// finder here
				return standardFileManager.list(location, packageName, kinds,
						recurse);
			} else { // app specific classes are here
				return finder.find(packageName);
			}
		}
		return Collections.emptyList();

	}

	@Override
	public int isSupportedOption(String option) {
		return -1;
	}

	public Map<String, ClassJavaFileObject> generatedClasses() {
		return (newClasses);
	}

}