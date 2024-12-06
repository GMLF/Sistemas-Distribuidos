package br.jsf;

import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

/**
 *
 * @author guigo
 */
@Named(value = "JSFProdutor")
@RequestScoped
public class JSFProdutor {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java/Fila")
    private Queue fila;

    private String mensagem;
    
    public String getMensagem(){
        return mensagem;
    }
    
    public void setMensagem(String mensagem){
        this.mensagem = mensagem;
    }
    
    
    /**
     * Creates a new instance of JSFProdutor
     */
    public JSFProdutor() {
    }

    public void send() {
        try {
            JMSContext context = connectionFactory.createContext();
            context.createProducer().send(fila, mensagem);
        } catch (Exception e) {
            System.out.println("ERRO");
            System.out.println(e.getMessage());
        }
    }
}
