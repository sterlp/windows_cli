import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FindInFile {

    public static void main(String[] args) throws Exception {
        if (args.length >= 3) {
            List<String> matches = new ArrayList<>();
            for (int i = 2; i < args.length; i++) {
                matches.add(args[i]);
            }
            findInFile(args[0], args[1], matches);
        } else {
            printHelp();
        }
    }

    record FileMatch (boolean matched, String value, String match, int line) {}
    
    private static void findInFile(String dir, String fileName, Collection<String> matches) throws IOException {
        List<Path> files = searchFile(dir, fileName);
        for (Path file : files) {
            FileMatch match = findInFile(file, matches);
            if (match.matched()) {
                System.out.println("Found '" + match.match() + "' in line " + match.line() + " of file " + file);
                System.out.println(match.value());
                System.out.println();
            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage of findInFile");
        System.out.println("===================");
        System.out.println("");
        System.out.println("findInFile <path-to-search> <file-name-to-contain> <list of in file values to search for>");
        System.out.println("");
        System.out.println("Example: search in the current directory for csv files containing '500' or 'hallo'");
        System.out.println("findInFile . .csv 500 hallo");
    }


    private static FileMatch findInFile(Path path, Collection<String> matches) throws IOException {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            List<String> readLines = readLines(path);
            int line = 1;
            for (String value : readLines) {
                for (String match : matches) {
                    if (value.contains(match)) {
                        return new FileMatch(true, value, match, line);
                    }
                }
                line++;
            }
        }
        return new FileMatch(false, null, null, -1);
    }
    
    private static List<Path> searchFile(String path, String name) throws IOException {
        return Files.walk(Paths.get(path)).filter(Files::isRegularFile)
            .filter(f -> f.getFileName().toString().contains(name))
            .toList();
    }
    
    private static List<String> readLines(final Path path) throws IOException {
        final List<String> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            try (final BufferedReader bufReader = new BufferedReader (new InputStreamReader(fis, Charset.defaultCharset()))) {
                String line;
                while ((line = bufReader.readLine()) != null) {
                    list.add(line);
                }
            }
        }
        return list;
    }
}
