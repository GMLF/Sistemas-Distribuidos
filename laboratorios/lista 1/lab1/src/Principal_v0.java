
/**
 * Lab0: Leitura de Base de Dados N�o-Distribuida
 * 
 * Autor: Lucio A. Rocha
 * Ultima atualizacao: 20/02/2023
 * 
 * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * 
 */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Random;


public class Principal_v0 {

	public final static Path path = Paths.get("c:\\Users\\guigo\\OneDrive\\Documentos\\GitHub\\Sistemas Distribuidos\\laboratorios\\lab1\\src\\fortune-br.txt");
	private int NUM_FORTUNES = 0;

	public class FileReader {

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

				} // fim while

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
						// System.out.print(lineCount + ".");
					}

					hm.put(lineCount, fortune.toString());
					//System.out.println(fortune.toString());

					//System.out.println(lineCount);
				} // fim while

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

		public void read(HashMap<Integer, String> hm)throws FileNotFoundException {
			 // Verifica se o HashMap não está vazio
			 if (hm.isEmpty()) {
				System.out.println("Nenhuma fortuna foi encontrada.");
			} else {
				// Sorteia uma chave aleatória do HashMap
				Random random = new Random();
				int randomKey = random.nextInt(hm.size()); // Gera um número aleatório dentro do intervalo do HashMap
				
				// Recupera e exibe a fortuna correspondente à chave sorteada
				System.out.println("-----");
				System.out.println("Fortuna sorteada (" + randomKey + "):");
				System.out.println(hm.get(randomKey));  // Exibe a fortuna associada à chave sorteada
				System.out.println("-----");
			}
		}

		public void write(HashMap<Integer, String> hm) throws FileNotFoundException {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Digite uma string para ser adicionada nas fortunas:");
			String userInput = scanner.nextLine();
			String novaFortuna = userInput + "\n%";

			try (FileWriter writer = new FileWriter(path.toString(), true)) {
				writer.write(novaFortuna + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
			scanner.close();
		}
	}

	public void iniciar() {

		FileReader fr = new FileReader();
		try {
			//NUM_FORTUNES = fr.countFortunes();
			HashMap<Integer, String> hm = new HashMap<>();
			fr.parser(hm);
			fr.read(hm);
			fr.write(hm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Principal_v0().iniciar();
	}

}