import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class reqHorarios implements HOR, Runnable {
	public Thread thrd;
	private horariosQuadra horQua;
	private String hors = "None";
	private int sala = 1;
	private Random rand;
	private ArrayList<Operacao> operacoes;
	private int tranca = -1;

	public void init_registry(int tranca) {
		horQua.escrever(this, thrd, tranca);
	}

	public reqHorarios(String nome, horariosQuadra horQua, ArrayList<Operacao> operacoes, int tranca) {
		thrd = new Thread(this, nome);
		this.horQua = horQua;
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
			// Escrever na memória
			System.out.println(thrd.getName() + ": Write[" + sala + "]");
			// horQua.escrever(this);
			horQua._2PL(operacoes, thrd, this, tranca);

		}
	}

	public void dorme() {
		try {
			Thread.sleep((int) (Math.random() * 100));
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
