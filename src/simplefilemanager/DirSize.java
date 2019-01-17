/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplefilemanager;

/**
 *
 * @author Rusu Stefanita-Cezar
 */
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

public final class DirSize extends SimpleFileVisitor<Path> {

    public Path path;
    public static long longSize;

    ;

    public DirSize(Path path) throws IOException {
        this.path = path;
        DirSize.longSize = getSize(path);
    }

    long getSize(Path startPath) throws IOException {
        final AtomicLong size = new AtomicLong(0);

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                // Skip folders that can't be traversed
                System.out.println("Skipped: " + file + "e=" + exc);
                return FileVisitResult.CONTINUE;
            }
        });

        return size.get();
    }
}
