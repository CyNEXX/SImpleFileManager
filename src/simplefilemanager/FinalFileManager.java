/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplefilemanager;

/**
 * @author Rusu Stefanita-Cezar
 */
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;
import java.util.List;

public class FinalFileManager {

    public static File defaultPath = new File("");
    public static Path defPath = Paths.get("").toAbsolutePath();
    public static boolean uncommanded = true;
    public static boolean replace;

    public static void main(String[] args) throws IOException {
        explore(defPath);
        command();
    }

    public static void deleteFiles() throws IOException {

        System.out.println("════════ DELETE FILES ════════");
        System.out.println("● Enter file/folder name to delete");
        System.out.print("► ");
        Scanner scnOrg = new Scanner(System.in);
        String orgFileString = scnOrg.nextLine().trim();
        Path fromPath = defPath.resolve(orgFileString); //numele fisierului sursa
        File orgFile = new File(fromPath.toString());

        if (!orgFile.exists() || !relativeInput(orgFileString)) {
            System.out.println("No such file exists!");
        } else {
            System.out.println("Are you sure you want to delete: " + orgFileString);
            System.out.println("Y/Yes to DELETE or any other to CANCEL");
            System.out.print("► ");
            Scanner scnConf = new Scanner(System.in);
            String confString = (scnConf.nextLine().trim().toLowerCase());

            switch (confString) {
                case "yes":
                case "y":
                    try {
                        Files.walkFileTree(fromPath, new DeleteDir());
                    } catch (IOException ex) {
                    }
                    break;
                default:
                    System.out.println("Cancelled.");
                    break;
            }
        }

    }

    //Display contents of input folder relative to current defaultPath-----------------------------------------------------
    public static void listF() throws IOException {
        System.out.println("════════ LIST DIRECTORY ════════");
        System.out.println("● Select folder to list its contents.");
        System.out.print("► ");
        Scanner folScn = new Scanner(System.in);
        String newLocString = folScn.nextLine().trim(); // new folder to explore
        File orgFile = new File(defPath.toString() + File.separator + newLocString);

        if (!orgFile.exists() || !orgFile.isDirectory()) {
            System.out.println("No such folder exists. Cancelled.");
        } else {
            defaultPath = orgFile;
            defPath = orgFile.toPath();
            explore(defPath);
        }
    }

    //Display available commands method -----------------------------------------------------
    public static void command() throws IOException {

        uncommanded = true;

        OUTER:
        while (uncommanded) {
            System.out.print("► ");
            Scanner scn = new Scanner(System.in);
            String command = scn.nextLine();
            command = (command.toLowerCase()).trim();

            switch (command) {
                case "copy":
                case "c":
                    copyFiles();
                    break;
                case "move":
                case "m":
                    moveFile();
                    break;
                case "list":
                case "l":
                    listF();
                    break;
                case "rename":
                case "r":
                    renameFile();
                    break;
                case "create_dir":
                case "cd":
                    createDir();
                    break;
                case "delete":
                case "d":
                    deleteFiles();
                    break;
                case "exit":
                case "e":
                    System.out.println("Closed.");
                    System.exit(0);
                case "x":
                case "change_drive":
                    changeDrive();
                    break;
                case "up":
                case "u":
                    up();
                    break;
                case "info":
                case "i":
                    info();
                    break;
                case "q":
                case "refresh":
                    explore(defPath);
                    break;
                default:
                    System.out.println("You must enter a command");
                    break;
            }
        }

    }

    //Goes up one level relative to current defaultPath --------------------------------------------------
    public static void up() throws IOException {

        try {
            File orgFile = new File(defPath.toAbsolutePath().getParent().toString());
            if (!orgFile.exists() || !orgFile.isDirectory()) {
                System.out.println("Reached top. Displaying roots");
                changeDrive();
            } else {
                defaultPath = defPath.toFile();
                defPath = orgFile.toPath();
                explore(defPath);
            }
        } catch (NullPointerException e) {
            System.out.println("Reached top");
            changeDrive();
        }
    }

    //Changes the drives to be explored -----------------------------------------------------
    public static void changeDrive() throws IOException {
        System.out.println("════════ CHANGE ROOT ════════");
        boolean repeat = true;
        System.out.println("● Enter drive number");
        while (repeat) {
            File[] roots = File.listRoots();
            int i;
            for (i = 0; i < roots.length; i++) {
                System.out.println(i + 1 + "|  " + roots[i]);
            }
            System.out.println("● Enter a number [from 1 to " + roots.length + "] corresponding to your choice. Or 0 to exit program.");
            System.out.print("► ");
            Scanner scn = new Scanner(System.in);
            if (scn.hasNextInt()) {
                int option = scn.nextInt() - 1;
                if (option == -1) {
                    System.out.println("Closed.");
                    System.exit(0);
                }
                if (((option < roots.length)) && ((option >= 0))) {
                    Path dir = Paths.get(roots[option].getAbsolutePath());
                    List<Select> fileArr = new ArrayList<>();
                    showHeader(dir);

                    try (DirectoryStream<Path> streamDiretories = Files.newDirectoryStream(dir);
                            DirectoryStream<Path> streamFiles = Files.newDirectoryStream(dir)) {
                        repeat = false;
                        for (Path pathToDirectory : streamDiretories) {
                            if (pathToDirectory.toFile().isDirectory()) {
                                fileArr.add(new Select(pathToDirectory.toString()));
                            }
                        }
                        for (Path pathToFile : streamFiles) {
                            if (pathToFile.toFile().isFile()) {
                                fileArr.add(new Select(pathToFile.toString()));
                            }
                        }
                    } catch (IOException | DirectoryIteratorException x) {
                        System.err.println(x);
                        continue;
                    }
                    for (int k = 0; k < fileArr.size(); k++) {
                        fileArr.get(k).listareSimpla(k);
                    }
                    defPath = dir;
                    showMenu();
                }

            }
        }
    }

    //Lists information about file or folder -----------------------------------------------------
    public static void info() throws IOException {
        System.out.println("════════ FILE OR FOLDER INFORMATION ════════");
        System.out.println("● Select file or folder to view information about.");
        System.out.print("► ");

        //numele fisierului sursa
        Scanner scnOrg = new Scanner(System.in);
        String orgFileString = scnOrg.nextLine().trim();
        Path fromPath = defPath.resolve(orgFileString); //numele fisierului sursa
        File orgFile = new File(fromPath.toString());

        if (!orgFile.exists()) {
            System.out.println("● No such file exists. Cancelled.");
        } else {
            System.out.print("▒▒▒▒▒▒ ");
            System.out.println(orgFileString);
            System.out.print("Full path: ");
            System.out.println(orgFile.getAbsolutePath());
            if (orgFile.isDirectory()) {
                System.out.println("Type: folder");
            }

            if (orgFile.isFile()) {
                System.out.println("Type: file");
            }
            try {
                Path source = fromPath;
                Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                        Integer.MAX_VALUE, new DirSize(source));
                System.out.print("Approximate size: " + Select.makeSize(DirSize.longSize) + " ");
                System.out.println(" Actual size: " + DirSize.longSize + " b.");
                System.out.print(Select.displayInfo(orgFile.toPath()));
            } catch (IOException e) {
                System.out.println(e);
                System.out.println("Cannot read file. Cancelled.");
            }
        }
        showMenu();
    }

    //Copy Method -----------------------------------------------------
    public static void copyFiles() throws IOException {
        System.out.println("════════ COPY FILES ════════");
        System.out.println("● Replace any existing files? Enter R to REPLACE ALL. Enter C to Cancel.");
        System.out.println("Or enter any other key to continue copying files WIHTOUT overwriting.");
        System.out.print("► ");
        Scanner confirmation = new Scanner(System.in);
        String conf = confirmation.nextLine().trim(); //confirmation for move
        switch (conf) {
            case "C":
            case "c":
                System.out.println("Cancelled");
                break;
            case "R":
            case "r": {
                replace = true;
                System.out.println("▒▒▒ Copying and REPLACING...");
            }
            break;
            default: {
                replace = false;
                System.out.println("▒▒▒ Copying WITHOUT REPLACING any existing files...");
            }
            break;
        }
        if (conf.equals("c")) {
            return;
        }
        System.out.println("● Enter the name of the file or directory to be copied.");
        System.out.print("► ");
        //numele fisierului sursa
        Scanner scnOrg = new Scanner(System.in);
        String orgFileString = scnOrg.nextLine().trim();
        Path fromPath = defPath.resolve(orgFileString); //numele fisierului sursa
        File orgFile = new File(fromPath.toString());
        if (!orgFile.exists() || !relativeInput(orgFileString)) {
            System.out.println("No such file exists. Cancelled");

        } else {
            System.out.println("Copying: " + orgFileString);
            System.out.println("● Enter full destination path.");
            System.out.print("► ");
            Scanner scnDest = new Scanner(System.in);
            String folderDestString = scnDest.nextLine().trim();
            File fileFolderDest = new File(folderDestString);
            if (!fileFolderDest.exists() || !fileFolderDest.isDirectory()) {
                System.out.println("No such location. Cancelled.");

            } else {
                File freshCopy = new File(folderDestString + File.separator + orgFileString);
                //-------------COPIEREA DIRECTORULUI
                if (fileFolderDest.exists() && orgFile.isDirectory()) {
                    System.out.println("Copying directory...");
                    try {
                        Path source = fromPath;
                        Path target = Paths.get(freshCopy.getAbsolutePath());
                        CopyDir copyObj = new CopyDir(source, target, replace);
                        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                                Integer.MAX_VALUE, copyObj);

                        if (copyObj.getSkipped() == 0) {
                            System.out.println("▒▒▒ File " + orgFile.getName() + " succesfully copied to: " + fileFolderDest);
                        } else {
                            System.out.println("▒▒▒ " + (copyObj.getTotal() - copyObj.getSkipped()) + " files succesfully copied from a total of: " + copyObj.getTotal());
                        }
                        System.out.println("▒▒▒ Replaced " + copyObj.getReplaced() + " files.");
                    } catch (IOException e) {
                        System.out.println(e);
                        System.out.println("Error copying folder");
                    }
                } //-------------COPIEREA FISIERULUI
                else if (fileFolderDest.exists() && orgFile.isFile()) {
                    System.out.println("Copying file...");
                    if (replace) {
                        try {
                            Path source = fromPath;
                            Path target = Paths.get(freshCopy.getAbsolutePath());
                            final StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
                            Files.copy(source, target, copyOption);

                            System.out.println("File " + orgFile.getName() + " succesfully copied to: " + fileFolderDest);
                        } catch (IOException ex) {
                            System.out.println(ex);
                            System.out.println("Error copying file");
                        }
                    } else if (!replace && freshCopy.exists()) {
                        System.out.println("File already exists. Cancelled.");
                    }
                }
            }
        }
    }

    //Move Method -----------------------------------------------------
    public static void moveFile() throws IOException {
        System.out.println("════════ MOVE FILES ════════");
        System.out.println("● Replace any existing files? Enter R to REPLACE ALL. Enter C to Cancel.");
        System.out.println("Or enter any other key to continue moving files WIHTOUT overwriting.");
        System.out.print("► ");
        Scanner confirmation = new Scanner(System.in);
        String conf = confirmation.nextLine().trim(); //confirmation for move
        switch (conf) {
            case "C":
            case "c":
                System.out.println("Cancelled");
                break;
            case "R":
            case "r": {
                replace = true;
                System.out.println("▒▒▒ Moving and replacing...");
            }
            break;
            default: {
                replace = false;
                System.out.println("▒▒▒ Moving WITHOUT replacing any existing files...");
            }
        }
        if (conf.equals("c")) {
            return;
        }
        System.out.println("Enter the name of the file or directory to be moved.");
        System.out.print("► ");
        //numele fisierului sursa
        Scanner scnOrg = new Scanner(System.in);
        String orgFileString = scnOrg.nextLine().trim();
        Path fromPath = defPath.resolve(orgFileString); //numele fisierului sursa v3
        File orgFile = new File(fromPath.toString());
        if (!orgFile.exists() || !relativeInput(orgFileString)) {
            System.out.println("No such file exists!");
        } else {
            System.out.println("▒▒▒ Moving: " + orgFileString);
            System.out.println("● Enter full destination path.");
            System.out.print("► ");
            Scanner scnDest = new Scanner(System.in);
            String folderDestString = scnDest.nextLine().trim();
            File fileFolderDest = new File(folderDestString);
            if (!fileFolderDest.exists() || !fileFolderDest.isDirectory()) {
                System.out.println("No such location. Cancelled.");

            } else {
                File freshCopy = new File(folderDestString + File.separator + orgFileString);
                //-------------MUTAREA DIRECTORULUI
                if (fileFolderDest.exists() && orgFile.isDirectory()) {
                    System.out.println("════════════ Moving directory...");
                    try {
                        Path source = fromPath;
                        Path target = Paths.get(freshCopy.getAbsolutePath());
                        MoveDir moveObj = new MoveDir(source, target, replace);
                        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                                Integer.MAX_VALUE, moveObj);
                        if (moveObj.getSkipped() == 0) {
                            System.out.println("▒▒▒ File " + orgFile.getName() + " succesfully moved to: " + fileFolderDest);
                        } else {
                            System.out.println("▒▒▒ " + (moveObj.getTotal() - moveObj.getSkipped()) + " succesfully copied from a total of: " + moveObj.getTotal());
                        }
                        System.out.println("▒▒▒ Replaced " + moveObj.getReplaced() + " files.");
                    } catch (IOException e) {
                        System.out.println(e);
                        System.out.println("Error moving folder");
                    }
                } //-------------COPIEREA FISIERULUI
                else if (fileFolderDest.exists() && orgFile.isFile()) {
                    if (replace) {
                        System.out.println("════════════ Moving file...");
                        try {
                            Path source = fromPath;
                            Path target = Paths.get(freshCopy.getAbsolutePath());
                            final StandardCopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
                            Files.move(source, target, copyOption);
                            System.out.println("File " + orgFile.getName() + " succesfully moved to: " + fileFolderDest);
                        } catch (IOException ex) {
                            System.out.println(ex);
                            System.out.println("Error moving file");
                        }
                    } else if (!replace && freshCopy.exists()) {
                        System.out.println("File already exists. Cancelled.");
                    }
                }
            }

        }
    }

    //Rename Method
    public static void renameFile() throws IOException {
        System.out.println("════════ RENAME FILE OR FOLDER ════════");
        System.out.println("● Enter the name of the file or directory to be renamed.");
        System.out.print("► ");
        Scanner scnOrg = new Scanner(System.in);
        String orgFileString = scnOrg.nextLine().trim();
        Path fromPath = defPath.resolve(orgFileString); //numele fisierului sursa
        File orgFile = new File(fromPath.toString());
        if (!orgFile.exists() || !relativeInput(orgFileString)) {
            System.out.println("No such file exists. Cancelled");
        } else {
            System.out.println("▒▒▒ Renaming: " + orgFileString);
            System.out.println("● Enter new name: ");
            System.out.print("► ");
            Scanner scnNewF = new Scanner(System.in);
            String newFileString = scnNewF.nextLine().trim();
            File newNameFile = new File(defPath.resolve(newFileString).toString());
            if (newNameFile.exists() || !relativeInput(orgFileString)) {
                System.out.println("Choose another name. Do not use empty spaces or absolute paths.");
                newNameFile = new File(defPath.resolve(resolveName(newNameFile)).toString());
            }
            boolean success = orgFile.renameTo(newNameFile);
            if (success) {
                System.out.println("File successfully renamed.");
            } else {
                System.out.println("Could not rename file " + orgFileString + " to: " + newNameFile.getName() + ".");
            }
        }
    }

    //show header of menu
    public static void showHeader(Path path) {
        System.out.println("▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄");
        System.out.println("Contents sorted by name.");
        System.out.println("Currently looking in: " + path.toString());
        System.out.println("──────────────────────────────────────────────────────────────────────────");
    }

    //Create directory method
    public static void createDir() {
        System.out.println("════════ CREATE DIRECTORY ════════");
        System.out.println("● Enter the name of the new directory.");
        System.out.print("► ");

        Scanner scnNewDir = new Scanner(System.in);
        String newDirString = scnNewDir.nextLine().trim();
        Path newDirPath = defPath.resolve(newDirString); //numele directorului nou
        File newDir = new File(newDirPath.toString());
        if (newDir.exists() || !relativeInput(newDirString)) {
            System.out.println("Choose another name. Do not use empty spaces or absolute paths.");
            newDirPath = defPath.resolve(resolveName(newDir));
            newDir = new File(newDirPath.toString());
        }
        boolean succes = newDir.mkdir();
        if (succes) {
            System.out.println("Successfully created directory named: " + newDir.getName());
        } else {
            System.out.println("Cancelled creating " + newDirString + " in: " + defPath + ".");
        }

    }

    //ShowMenu method
    public static void showMenu() {
        System.out.println("──────────────────────────────────────────────────────────────────────────");
        System.out.println("                                           Available commands or shortcuts:                                             ");
        System.out.println("  LIST(L), INFO(I), CREATE_DIR(CD), RENAME(R), COPY(C), MOVE(M), DELETE(D), UP(U), CHANGE_DRIVE(X), REFRESH(Q), EXIT(E) ");
        System.out.println("──────────────────────────────────────────────────────────────────────────");
    }

    //loops until you get a good name for a new folder or rename
    public static String resolveName(File newFile) {
        String orgNameString = newFile.getName();
        String newNameString = new String();
        boolean stillUnnamed = true;
        while (stillUnnamed) {

            String type = new String();
            if (newFile.isFile()) {
                type = "file";
            }
            if (newFile.isDirectory()) {
                type = "folder";
            }

            //confirmation for rename
            while (stillUnnamed) {
                System.out.println("● Press R to enter a new different name or any other key to cancel.");
                System.out.print("► ");
                Scanner confirmation = new Scanner(System.in);
                String conf = confirmation.nextLine().trim();
                switch (conf) {
                    case "R":
                    case "r": {
                        System.out.println("● Enter new " + type + " name.");
                        System.out.print("► ");
                        Scanner scnNew = new Scanner(System.in);
                        newNameString = scnNew.nextLine().trim();
                        if (newNameString.equals("") || !relativeInput(newNameString)) {
                            System.out.println("Choose another name. Do not use empty spaces or absolute paths.");
                            continue;
                        }
                        Path fromPath = defPath.resolve(newNameString); //numele fisierului sursa
                        File newNameFile = new File(fromPath.toString());
                        if (!newNameFile.exists()) {
                            stillUnnamed = false;
                        }
                    }
                    break;
                    default: {
                        stillUnnamed = false;
                        newNameString = orgNameString;
                    }
                    break;
                }
            }
        }
        return newNameString;
    }

    //checks if input file/folder name is a relative one
    public static boolean relativeInput(String strCheck) {
        boolean okRelativeName = false;
        Path chkPth = Paths.get(strCheck);
        if (!chkPth.isAbsolute()) {
            okRelativeName = true;
        }
        return okRelativeName;
    }

    //displays the contents of folders
    public static void explore(Path path) throws IOException {
        Path dir = path;
        List<Select> fileArr = new ArrayList<>();
        showHeader(path);
        try (DirectoryStream<Path> streamDirectories = Files.newDirectoryStream(dir);
                DirectoryStream<Path> streamFiles = Files.newDirectoryStream(dir)) {

            for (Path pathToDirectory : streamDirectories) {
                if (pathToDirectory.toFile().isDirectory()) {
                    fileArr.add(new Select(pathToDirectory.toString()));
                }
            }

            for (Path pathToFile : streamFiles) {
                if (pathToFile.toFile().isFile()) {
                    fileArr.add(new Select(pathToFile.toString()));
                }
            }

        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);

        }

        for (int k = 0; k < fileArr.size(); k++) {
            fileArr.get(k).listareSimpla(k);
        }
        defPath = dir;
        showMenu();
    }
}
