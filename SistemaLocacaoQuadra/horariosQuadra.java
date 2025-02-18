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
	private String aux = "None";
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

			aux = stub.getHOR();
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
			return aux;
		} else {
			return null_return;
		}
	}

	public synchronized void escrever(reqHorarios objServidor, Thread thrd, int tranca) {
		try {
			// Cria um novo registro RMI
			HOR stub = (HOR) UnicastRemoteObject
					.exportObject(objServidor, 0);
			Registry registro = LocateRegistry
					.createRegistry(1099);

		} catch (RemoteException e) { // Excecao de comunicacao
			// Remove a exportacao anterior
			try {
				UnicastRemoteObject
						.unexportObject(objServidor, true);
				// Encontra o registro
				HOR stub = (HOR) UnicastRemoteObject
						.exportObject(objServidor, 0);
				Registry registro = LocateRegistry
						.getRegistry("localhost", 1099);
				// Vincula novamente o valor do registro
				registro.rebind("horarios" + Integer.toString(tranca), stub);

			} catch (NoSuchObjectException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyAll(); // 'notify()' method wakes only one thread that is waiting for a notify
	}

	public synchronized void liberarTrancas(ArrayList<Operacao> operacoes, Thread thrd, int tranca) {
		Main.liberouTrancas = false;
		// Realiza a liberação das trancas para cada operação realizada
		for (Operacao operacao : operacoes) {
			// System.out.println(thrd.getName() + ": " + operacao.tipo + "u"+
			// operacao.transacao);
			delTranca(operacao, tranca);
		}

		System.out.println(thrd.getName() + ": liberou trancas.");
		notifyAll();
		Main.liberouTrancas = true;
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

	public synchronized boolean enfileirar(Operacao operacao, Thread thrd) {
		boolean abort = false;
		boolean timeout = false;

		System.out.println(thrd.getName() + ": " + operacao.tipo + operacao.transacao + " fila");
		// Adiciona a operação na fila
		Main.fila.add(new Operacao(operacao.tipo, operacao.transacao)); // poe
																		// na
																		// fila
		while (Main.liberouTrancas == false && !timeout)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Main.liberouTrancas = false;
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
				abort = enfileirar(operacao, thrd);
			}

			if (!abort) {
				Main.trancas_list.get(tranca).add(new Tranca(operacao));
				if ("r".equals(operacao.tipo)) {
					return_aux = this.lerHor(tranca);
					System.out.println(
							thrd.getName() + " : " + operacao.tipo + operacao.transacao + " Horário: " + this.horario);
				} else {
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

		while (!leitura && Main.liberouTrancas == false) {
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
		while (it.hasNext()) {

			Tranca t = (Tranca) it.next();

			trancas += thread + "-[" + t.tipo + "l" + t.transacao + "] ";
			try {

			} catch (Exception e) {
				System.out.println("Excecao:[" + e.getMessage() + "]");
			}

			if (!t.transacao.equals(operacao.transacao) && (// transacoes
															// diferentes

			(t.tipo.equals("w") && operacao.tipo.equals("w")) || // operacoes
																	// conflitantes
					(t.tipo.equals("r") && operacao.tipo.equals("w")) || (t.tipo
							.equals("w") && operacao.tipo.equals("r"))))
				result = true;

		}

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
