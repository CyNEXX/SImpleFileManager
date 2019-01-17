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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributeView;
import java.text.SimpleDateFormat;

public class Select {

    private static final SimpleDateFormat SIMPLE_DT_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  //  private String cale;
    private File fila;
    private String fileType;
    private long size;
    private static String abr = "B";
  //  private BasicFileAttributes attr;
  //  private FileTime fileTime;
    private static final String DIRECTORY_ABR = "<DIR>";

    public Select(String cale) throws IOException {
        this.fila = new File(cale);

    }

    static FileTime getCreation(Path path) throws IOException {
        Path p = path;
        BasicFileAttributes attr = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
        FileTime fileTimeCreated = attr.creationTime();
        return fileTimeCreated;
    }

    static String displayInfo(Path path) throws IOException {
        Path p = path;
        BasicFileAttributes attr = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
        StringBuilder sb = new StringBuilder();

        FileTime fileAcc = attr.lastAccessTime();
        FileTime fileMod = attr.lastModifiedTime();
        FileTime created = attr.creationTime();
        if (Files.isHidden(path)) {
            System.out.println("File is HIDDEN");
        } else {
            System.out.println("File is VISIBLE");
        }
        sb.append("Time created: ").append(SIMPLE_DT_FORMAT.format(created.toMillis())).append("\n");
        sb.append("Last time modified: ").append(SIMPLE_DT_FORMAT.format(fileMod.toMillis())).append("\n");
        sb.append("Last time accessed: ").append(SIMPLE_DT_FORMAT.format(fileAcc.toMillis())).append("\n");
        return sb.toString();
    }

    static String makeSize(long size) {
        abr = "B";
        if ((size > 1024)) {
            size = size / 1024;
            abr = "KB";

            if ((size > 1024)) {
                size = size / 1024;
                abr = "MB";
                if ((size > 1024)) {
                    size = size / 1024;
                    abr = "GB";
                    if (size > 1024) {
                        size = size / 1024;
                        abr = "TB";
                    }
                }
            }
        }

        return (size + " " + abr + ";");
    }

    /*String sizeOrFolder() {
        if (fila.exists() && fila.isDirectory()) {
            fileType = "<DIR>";
        }
        if (fila.exists() && fila.isFile()) {
            fileType = "file";
        } else {
            fileType = "<unknown>";
        }
        return fileType;
    }*/

    String makeSize() {

        size = fila.length();
        abr = "B";
        if (size == 0) {
            size = 0;
        }
        if ((size > 1024)) {
            size = size / 1024;
            abr = "KB";

            if ((size > 1024)) {
                size = size / 1024;
                abr = "MB";
                if ((size > 1024)) {
                    size = size / 1024;
                    abr = "GB";
                    if (size > 1024) {
                        size = size / 1024;
                        abr = "TB";
                    }
                }
            }
        }

        return (size + " " + abr);
    }

    void listareSimpla(int orderNr) throws IOException {

        if (fila.exists() && fila.isDirectory()) {
            System.out.printf("%-5s %-50s %-35s Creation date: %-35s%n", orderNr + ".", fila.getName(), DIRECTORY_ABR, (SIMPLE_DT_FORMAT
                    .format(Select.getCreation(fila.toPath()).toMillis())));
        }

        if (fila.exists() && fila.isFile()) {
            System.out.printf("%-5s %-50s %-35s Creation date: %-35s%n", orderNr + ".", fila.getName(), this.makeSize(), (SIMPLE_DT_FORMAT
                    .format(Select.getCreation(fila.toPath()).toMillis())));
        }
    }

}
