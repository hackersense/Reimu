package eternal.walnut.reimu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static boolean save(File file, String content, boolean append) {
        try {
            if (!file.exists() && !file.createNewFile())
                return false;

            try (FileWriter writer = new FileWriter(file, append)) {
                writer.write(content);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String read(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<String> readList(File file)
    {
        List<String> content = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.add(line);
            }
            fileReader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
}
