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
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CopyDir extends SimpleFileVisitor<Path> {

    Path source;
    Path target;
    boolean replace;
    CopyOption[] opt;
    long skipCounter = 0;
    long replCounter = 0;
    static long totalFiles = 0;
    private final LinkOption linkOption = LinkOption.NOFOLLOW_LINKS;
    private final CopyOption[] opt2 = new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING};

    public CopyDir(Path source, Path target, boolean replace) {
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

    private void increaseSkipped() {
        skipCounter = skipCounter + 1;
    }

    private void increaseTotal() {
        totalFiles = totalFiles + 1;
    }

    private void increaseRepl() {
        replCounter = replCounter + 1;
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
        if (!replace) {
            if (!Files.exists(target.resolve(source.relativize(file)), linkOption)) {

                try {
                    System.out.print("Copying: " + file.toAbsolutePath());
                    Files.copy(file, target.resolve(source.relativize(file)), opt);
                    System.out.println(" ▫ Done!");
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            } else {
                increaseSkipped();
                System.out.println("Skipped: " + file.toAbsolutePath());
            }
        } else {
            try {
                if (Files.exists(target.resolve(source.relativize(file)), linkOption)) {
                    System.out.print("Copying: " + file.toAbsolutePath());
                    increaseRepl();
                }
                Files.copy(file, target.resolve(source.relativize(file)), opt2);
                System.out.println(" ▫ Done!");
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return FileVisitResult.CONTINUE;
    }

}
