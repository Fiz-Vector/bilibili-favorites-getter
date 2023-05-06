package me.soda.bfg;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final Gson GSON = new Gson();

    public static void main(String[] args) throws Exception {
        Scanner configReader = new Scanner(new File("conf.txt"));
        FavGetter favGetter = new FavGetter(Long.parseLong(configReader.nextLine()), configReader.nextLine());
        FavGetter.FavList list = favGetter.getFavFolders();
        List<FavGetter.FavFolder> folders = list.data.list;
        for (int i = 0; i < folders.size(); i++) {
            System.out.println(i + ": " + folders.get(i).title);
        }

        System.out.println("序号（用逗号分隔）: ");
        List<FavGetter.FavFolder> foldersRequired = new ArrayList<>();
        for (String s : new Scanner(System.in).nextLine().split(",")) {
            foldersRequired.add(folders.get(Integer.parseInt(s)));
        }

        if (!Files.isDirectory(Path.of("output"))) Files.createDirectory(Path.of("output"));

        favGetter.getFolderResults(foldersRequired, re -> {
            try {
                Files.writeString(Path.of("output/" + re.data.info.id + ".json"), GSON.toJson(re));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("完成！");
    }
}