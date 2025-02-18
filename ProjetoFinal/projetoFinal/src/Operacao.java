public class Operacao {
    public String tipo = "-1";
    public String transacao = "-1";

    public Operacao(String tipo, String transacao) {
        this.tipo = tipo;
        this.transacao = transacao;
    }

    public String toString() {
        return tipo + " " + transacao;
    }
}
