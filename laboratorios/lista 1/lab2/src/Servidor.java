import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.io.*;
import java.util.Random;

public class Servidor {

    private static Socket socket;
    private static ServerSocket server;

    private static DataInputStream entrada;
    private static DataOutputStream saida;
    public final static Path path = Paths.get(
            "c:\\Users\\guigo\\OneDrive\\Documentos\\GitHub\\Sistemas Distribuidos\\laboratorios\\lab2\\src\\fortune-br.txt");

    private int porta = 1025;

    public void iniciar() {
        System.out.println("Servidor iniciado na porta: " + porta);
        try {

            server = new ServerSocket(porta);
            socket = server.accept();

            // Criar os fluxos de entrada e saida
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());

            String resultado = entrada.readUTF();

            String metodo = "";
            String argumentos = "";
            String frase = "";

            // Patterns para method e args
            java.util.regex.Pattern pattern = java.util.regex.Pattern
                    .compile("\"method\"\\s*:\\s*\"([^\"]+)\"|\"args\"\\s*:\\s*\"([^\"]*)\"");
            java.util.regex.Matcher matcher = pattern.matcher(resultado);

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    metodo = matcher.group(1);
                } else if (matcher.group(2) != null) {
                    argumentos = matcher.group(2);
                }
            }

            if ("read".equals(metodo)) {
                CustomFileReader fr = new CustomFileReader();
                HashMap<Integer, String> hm = new HashMap<>();
                fr.parser(hm);
                frase = fr.read(hm);
            } else if ("write".equals(metodo)) {
                CustomFileReader fr = new CustomFileReader();
                HashMap<Integer, String> hm = new HashMap<>();
                fr.parser(hm);
                fr.write(hm, argumentos);
                frase = "{\"result\":\"true\"}\n";
            } else {
                frase = "{\"result\":\"false\"}\n";
            }

            saida.writeUTF(frase);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CustomFileReader {

        public int countFortunes() throws FileNotFoundException {

            int lineCount = 0;

            InputStream is = new BufferedInputStream(new FileInputStream(
                    path.toString()));
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    is))) {

                String line = "";
                while (!(line == null)) {

                    if (line.equals("%"))
                        lineCount++;

                    line = br.readLine();

                }

                System.out.println(lineCount);
            } catch (IOException e) {
                System.out.println("SHOW: Excecao na leitura do arquivo.");
            }
            return lineCount;
        }

        public void parser(HashMap<Integer, String> hm)
                throws FileNotFoundException {

            InputStream is = new BufferedInputStream(new FileInputStream(
                    path.toString()));
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    is))) {

                int lineCount = 0;

                String line = "";
                while (!(line == null)) {

                    if (line.equals("%"))
                        lineCount++;

                    line = br.readLine();
                    StringBuffer fortune = new StringBuffer();
                    while (!(line == null) && !line.equals("%")) {
                        fortune.append(line + "\n");
                        line = br.readLine();
                    }

                    hm.put(lineCount, fortune.toString());

                }

            } catch (IOException e) {
                System.out.println("SHOW: Excecao na leitura do arquivo.");
            }
        }

        public String read(HashMap<Integer, String> hm) {
            if (hm.isEmpty()) {
                return "Nenhuma fortuna foi encontrada.";
            } else {
                Random random = new Random();
                int randomKey = random.nextInt(hm.size());
                return "-----\nFortuna sorteada (" + randomKey + "):\n" + hm.get(randomKey) + "\n-----";
            }
        }

        public void write(HashMap<Integer, String> hm, String userInput) throws FileNotFoundException {
            String novaFortuna = userInput + "\n%";

            try (FileWriter writer = new FileWriter(path.toString(), true)) {
                writer.write(novaFortuna + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new Servidor().iniciar();

        CustomFileReader fr = new CustomFileReader();
        try {
            HashMap<Integer, String> hm = new HashMap<>();
            fr.parser(hm);
            fr.read(hm);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}