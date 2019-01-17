/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplefilemanager;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteDir extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
            throws IOException {
        System.out.println("Deleting file: " + file.getFileName());
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path directory,
            IOException exception) throws IOException {
        if (exception == null) {
            System.out.println("Deleting folder: " + directory.getFileName());
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        } else {
            throw exception;
        }
    }

}
