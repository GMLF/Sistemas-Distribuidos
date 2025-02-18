public class Tranca {
    public String tipo = "-1";
    public String transacao = "-1";

    public Tranca(Operacao operacao) {
        this.tipo = operacao.tipo;
        this.transacao = operacao.transacao;
    }

    public String toString() {
        return tipo + " " + transacao;
    }
}
