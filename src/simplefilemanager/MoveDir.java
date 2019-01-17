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
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MoveDir extends SimpleFileVisitor<Path> {

    public Path source;
    public Path target;
    public boolean replace;
    public CopyOption[] opt;
    public int skipCounter = 0;
    public static long totalFiles = 0;
    public long replCounter = 0;

    private final LinkOption linkOption = LinkOption.NOFOLLOW_LINKS;

    public MoveDir(Path source, Path target, boolean replace) {
        this.source = source;
        this.target = target;
        this.replace = replace;
        skipCounter = 0;
        totalFiles = 0;
        if (replace) {
            opt = new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING};
        } else {
            opt = new CopyOption[]{};
        }
    }

    public void increaseSkipped() {
        skipCounter = skipCounter + 1;
    }

    public void increaseTotal() {
        totalFiles = totalFiles + 1;
    }

    public long getSkipped() {
        return skipCounter;
    }

    public long getTotal() {
        return totalFiles;
    }

    public long getReplaced() {
        return replCounter;
    }

    public void increaseRepl() {
        replCounter = replCounter + 1;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path directory,
            BasicFileAttributes attributes) {
        try {
            Path newDir = target.resolve(source.relativize(directory));
            Files.createDirectory(newDir);
        } catch (FileAlreadyExistsException fAEE) {
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
        increaseTotal();
        try {
            Path targetFile = target.resolve(source.relativize(file));

            if (!replace && Files.exists(targetFile, linkOption)) {
                System.out.println("Skipped: " + file);
                increaseSkipped();
            } else {
                if (replace && Files.exists(targetFile, linkOption)) {increaseRepl();
                }
                System.out.print("Moving: " + file);
                Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                
                System.out.println(" ▫ Done!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory,
            IOException exception) throws IOException {

        if (directory.toFile().listFiles().length == 0) {
            Files.delete(directory);
        }

        return FileVisitResult.CONTINUE;
    }

}
