import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class observador implements HOR, Runnable {
	public Thread thrd;
	private horariosQuadra horQua1, horQua2, horQua3;
	private String hors = "None";
	private int sala = 1;
	private Random rand;
	private ArrayList<Operacao> operacoes;
	private int tranca = -1;

	public observador(String nome, horariosQuadra horQua1, horariosQuadra horQua2, horariosQuadra horQua3,ArrayList<Operacao> operacoes, int tranca) {
		thrd = new Thread(this, nome);
		thrd.start();
		this.horQua1 = horQua1;
		this.horQua2 = horQua2;
		this.horQua3 = horQua3;
		this.rand = new Random();
		this.operacoes = operacoes;
		this.hors = nome;
		this.tranca = tranca;

	}

	public void run() {

		System.out.println(thrd.getName() + ": READY");
		// Tenta produzir 10 vezes
		for (int i = 0; i < 10; i++) {
			// System.out.println("Mais uma");
			// dorme por um intervalo aleatorio
			dorme();
			// Fazer a leitura de cada horário
			String reservaQ1 = "";
			String reservaQ2 = "";
			String reservaQ3 = "";

			reservaQ1 += horQua1._2PL(operacoes, thrd, null, 0) + " - ";
			System.out.println("OB 1: " + reservaQ1);

			reservaQ2 += horQua2._2PL(operacoes, thrd, null, 1) + " - ";
			System.out.println("OB 2: " + reservaQ2);

			reservaQ3 += horQua3._2PL(operacoes, thrd, null, 2) + " - ";
			System.out.println(" OB 3: " + reservaQ3);

		}
	}

	public void dorme() {
		try {
			Thread.sleep((int) (Math.random() * 10));
		} catch (InterruptedException e) {
			System.err.println(e.toString());
		}
	}

	@Override
	public String getHOR() throws RemoteException {
		return hors;
	}

	@Override
	public void setHOR(String valor) throws RemoteException {
		hors = valor;
	}

}
