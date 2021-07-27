import java.io.*;
import java.util.ArrayList;

class FileManager {
    static void write(String string) {
        if (!new File(Main.FILE).exists()) {
            if (!createFile()) return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Main.FILE, false))) {
            bw.write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> read(String filePath) {
        ArrayList<String> strings = new ArrayList<>();

        if (!new File(filePath).exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String tmp;

            while ((tmp = br.readLine()) != null) {
                strings.add(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return strings;
    }

    private static boolean createFile() {
        String[] tmp = Main.FILE.split("\\\\");
        String fileName = tmp[tmp.length - 1];
        String dirName = Main.FILE.substring(0, Main.FILE.length() - fileName.length() - 1);

        new File(dirName).mkdirs();
        try {
            if (!new File(Main.FILE).createNewFile()) return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
