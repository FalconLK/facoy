package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.*;


public class CopyDirectoryTree {
	// copy AND isDirectory AND list
	public static void copyDirectory1(Path src, Path dest) throws IOException {
		Files.copy(src, dest);
		if(Files.isDirectory(src)) {
			for(String filename : src.toFile().list()) {
				Path srcFile = src.resolve(filename);
				Path destFile = dest.resolve(filename);
				copyDirectory1(srcFile, destFile);
			}
		}
	}
	
	// preVisitDirectory AND visitFile AND walkFileTree AND copy
	public static void copyDirectory2(final Path src, final Path dest) throws IOException {
		Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Files.copy(dir, dest.resolve(src.relativize(dir)));
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.copy(file, dest.resolve(src.relativize(file)));
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
}

