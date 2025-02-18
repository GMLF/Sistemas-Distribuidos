import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class horariosQuadra {
	private boolean leitura = true; //// Nao permite a leitura antes do servico ser registrado
	private boolean escrita = true;
	private String temp_aux = "None";
	private String null_return = "None";
	private Queue<String> sala_queue = new LinkedList<>();
	private String horario;

	public horariosQuadra(String horario) {
		this.horario = horario;
	}

	public String getHorario() {
		return this.horario;
	}

	public synchronized String lerHor(int tranca) {
		boolean success = false;
		try {
			// Get registro
			Registry registro = LocateRegistry
					.getRegistry("localhost", 1099);
			// Pegar o stub
			HOR stub = (HOR) registro
					.lookup("horarios" + Integer.toString(tranca));
			// Ler as temperaturas
			temp_aux = stub.getHOR();
			success = true;

		} catch (AccessException e) {
			// Excecao de permissao de acesso ao metodo remoto
			e.printStackTrace();
		} catch (RemoteException e) {
			// Excecao de comunicacao
			e.printStackTrace();
		} catch (NotBoundException e) {
			// Excecao de localizar nome sem binding
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyAll(); // 'notify()' method wakes only one thread that is waiting for a notify
		// Returna o valor de acordo com o sucesso

		if (success) {
			return temp_aux;
		} else {
			return null_return;
		}
	}

	public synchronized void escrever(reqHorarios objServidor, Thread thrd, int tranca) {
		try {
			// Cria um novo registro RMI
			HOR stub = (HOR) UnicastRemoteObject.exportObject(objServidor, 0);
			System.out.println("Nome stub: " + stub.getHOR());
			Registry registro = LocateRegistry.createRegistry(1099);
			// Vincula o stub
			registro.bind("horarios" + Integer.toString(tranca), stub);

		} catch (RemoteException e) { // Excecao de comunicacao
			// Remove a exportacao anterior
			try {
				UnicastRemoteObject.unexportObject(objServidor, true);
				// Encontra o registro
				HOR stub = (HOR) UnicastRemoteObject.exportObject(objServidor, 0);
				System.out.println("Nome stub: " + stub.getHOR() + " Nome da thread: " + thrd.getName());
				Registry registro = LocateRegistry.getRegistry("localhost", 1099);

				// Vincula novamente o valor do registro
				registro.rebind("horarios" + Integer.toString(tranca), stub);
				System.out.println("Horario: " + this.horario + " Alugada por: " + thrd.getName());

			} catch (NoSuchObjectException e2) {
				e2.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyAll(); // 'notify()' method wakes only one thread that is waiting for a notify
	}

	public synchronized void liberarTrancas(ArrayList<Operacao> operacoes, Thread thrd, int tranca) {
		Main.liberouTrancas[tranca] = false;
		// Realiza a liberação das trancas para cada operação realizada
		for (Operacao operacao : operacoes) {
			// System.out.println(thrd.getName() + ": " + operacao.tipo + "u"+
			// operacao.transacao);
			delTranca(operacao, tranca);
		}

		System.out.println(thrd.getName() + ": liberou trancas.");
		notifyAll();
		Main.liberouTrancas[tranca] = true;
		leitura = true; // Permite acesso ah regiao critica (conflito())

	}

	public synchronized void delTranca(Operacao operacao, int tranca) {

		String tipo = operacao.tipo;
		String transacao = operacao.transacao;
		// Deleta as trancas da Main
		if (Main.trancas_list.get(tranca).size() > 0)
			for (int i = 0; i < Main.trancas_list.get(tranca).size(); i++)
				if (Main.trancas_list.get(tranca).get(i).tipo.equals(tipo)
						&& Main.trancas_list.get(tranca).get(i).transacao.equals(transacao))
					Main.trancas_list.get(tranca).remove(i);
		leitura = false;
	}

	public synchronized boolean enfileirar(Operacao operacao, Thread thrd, int tranca) {
		boolean abort = false;
		boolean timeout = false;
		System.out.println("----------------------------");
		System.out.println(thrd.getName() + ": " + operacao.tipo + operacao.transacao +" entrou na: " + " fila");
		// Adiciona a operação na fila
		Main.fila.add(new Operacao(operacao.tipo, operacao.transacao)); // poe
																		// na
																		// fila
		while (Main.liberouTrancas[tranca] == false && !timeout)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Main.liberouTrancas[tranca] = false;
		System.out.println(thrd.getName() + ": " + operacao.tipo
				+ operacao.transacao + " saiu da fila");
		if (timeout) {
			abort = true;
		}
		return abort;
	}

	public String _2PL(ArrayList<Operacao> operacoes, Thread thrd, reqHorarios st, int tranca) {
		boolean abort = false;
		String return_aux = null_return;
		for (int i = 0; i < operacoes.size() && !abort; i++) {
			Operacao operacao = operacoes.get(i);
			abort = false;
			while (conflito(operacao, thrd.getName(), tranca) && !abort) {
				System.out.println("-------------Entrou em conflito----------------");
				abort = enfileirar(operacao, thrd, tranca);
			}

			if (!abort) {
				Main.trancas_list.get(tranca).add(new Tranca(operacao));
				// Verifica tipo de operacao
				if ("r".equals(operacao.tipo)) {
					// Op leitura
					return_aux = this.lerHor(tranca);
				} else {
					// Op escrita
					this.escrever(st, thrd, tranca);
					return_aux = null_return;
				}

				leitura = true;

				dorme();
			} else {
				System.err.println(thrd.getName() + ": " + operacao.tipo
						+ operacao.transacao + " abort");
			}
		} // fim for
		liberarTrancas(operacoes, thrd, tranca);
		return return_aux;
	}

	public synchronized boolean conflito(Operacao operacao, String thread, int tranca) {
		boolean result = false;

		while (!leitura && Main.liberouTrancas[tranca] == false) {
			try {
				System.out.println(thread + ": WAIT");
				wait();

			} catch (InterruptedException e) {
				System.err.println(e.toString());
			}
		}
		leitura = false;

		Iterator it = Main.trancas_list.get(tranca).iterator();

		String trancas = "";
		// Verifica quantas trancas ainda tem
		while (it.hasNext()) {
			Tranca t = (Tranca) it.next();

			trancas += thread + "-[" + t.tipo + "l" + t.transacao + "] ";

			if (!t.transacao.equals(operacao.transacao) && (// transacoes
															// diferentes
			(t.tipo.equals("w") && operacao.tipo.equals("w")) || // operacoes
																	// conflitantes
					(t.tipo.equals("r") && operacao.tipo.equals("w")) || (t.tipo
							.equals("w") && operacao.tipo.equals("r"))))
				result = true;

		} // fim for

		leitura = true;
		notifyAll();

		return result;
	}

	public void dorme() {
		try {
			Thread.sleep((int) (Math.random() * 1000));
			// Thread.sleep(1000);
			// Thread.sleep(0);
		} catch (InterruptedException e) {
			System.err.println(e.toString());
		}
	}

}
