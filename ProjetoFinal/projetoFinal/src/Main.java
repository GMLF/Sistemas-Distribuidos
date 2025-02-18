import java.util.ArrayList;



public class Main {
    public static ArrayList<Tranca> trancas1; 
    public static ArrayList<Tranca> trancas2; 
    public static ArrayList<Tranca> trancas3; 
	public static ArrayList<Operacao> fila;
    public static ArrayList<ArrayList<Tranca>> trancas_list;
    public static horariosQuadra horQua;
    public static horariosQuadra[] horQuaArray;
	
	public static boolean[] liberouTrancas={false, false, false};
    
    public static void main(String[] args) {
        // Iniciar as entidades
        trancas1 = new ArrayList<>();
		trancas1.add(new Tranca(new Operacao("","0")));
        trancas2 = new ArrayList<>();
		trancas2.add(new Tranca(new Operacao("","0")));
        trancas3 = new ArrayList<>();
		trancas3.add(new Tranca(new Operacao("","0")));

        trancas_list = new ArrayList<ArrayList<Tranca>>();

        trancas_list.add(trancas1);
        trancas_list.add(trancas2);
        trancas_list.add(trancas3);

		fila = new ArrayList<>();
		fila.add(new Operacao("","0"));	

        horQuaArray = new horariosQuadra[3];
        horariosQuadra horQua1 = new horariosQuadra("08:00-09:00");
        horariosQuadra horQua2 = new horariosQuadra("09:00-10:00");
        horariosQuadra horQua3 = new horariosQuadra("10:00-11:00");


        
        ArrayList<Operacao> op_init1 = new ArrayList<>();
        ArrayList<Operacao> op_init2 = new ArrayList<>();
        ArrayList<Operacao> op_init3 = new ArrayList<>();
        
        ArrayList<Operacao> op_reqHorarios1 = new ArrayList<>();
        ArrayList<Operacao> op_reqHorarios2 = new ArrayList<>();
        ArrayList<Operacao> op_reqHorarios3 = new ArrayList<>();
        ArrayList<Operacao> op_reqHorarios4 = new ArrayList<>();
        ArrayList<Operacao> op_reqHorarios5 = new ArrayList<>();
        ArrayList<Operacao> op_reqHorarios6 = new ArrayList<>();
        ArrayList<Operacao> op_monitor = new ArrayList<>();

        op_init1.add(new Operacao("w", "10"));
        op_init2.add(new Operacao("w", "11"));
        op_init3.add(new Operacao("w", "12"));
        op_reqHorarios1.add(new Operacao("w","1"));
        op_reqHorarios2.add(new Operacao("w","2"));
        op_reqHorarios3.add(new Operacao("w","3"));
        op_reqHorarios4.add(new Operacao("w","4"));
        op_reqHorarios5.add(new Operacao("w","5"));
        op_reqHorarios6.add(new Operacao("w","6"));
        op_monitor.add(new Operacao("r","7"));

        // iniciar threads que iniciarão os registros
        reqHorarios init1 = new reqHorarios("Registro 1 - Operando", horQua1, op_init1, 0);
        reqHorarios init2 = new reqHorarios("Registro 2 - Operando", horQua2, op_init2, 1);
        reqHorarios init3 = new reqHorarios("Registro 3 - Opernado", horQua3, op_init3, 2);

        init1.init_registry(0);
        init2.init_registry(1);
        init3.init_registry(2);

        
        reqHorarios req1 = new reqHorarios("Req: 1", horQua1,op_reqHorarios1, 0);
        req1.thrd.start();
        reqHorarios req2 = new reqHorarios("Req: 2", horQua1,op_reqHorarios2, 0);
        req2.thrd.start();
        reqHorarios req3 = new reqHorarios("Req: 3", horQua2,op_reqHorarios3, 1);
        req3.thrd.start();
        reqHorarios req4 = new reqHorarios("Req: 4", horQua2,op_reqHorarios4, 1);
        req4.thrd.start();
        reqHorarios req5 = new reqHorarios("Req: 5", horQua3,op_reqHorarios5,2);
        req5.thrd.start();
        reqHorarios req6 = new reqHorarios("Req: 6", horQua3,op_reqHorarios6,2);
        req6.thrd.start();
        observador obs = new observador("Observador", horQua1, horQua2, horQua3, op_monitor, 0);

        
        try{
           req1.thrd.join();
           req2.thrd.join();
           req3.thrd.join();
           req4.thrd.join();
           req5.thrd.join();
           req6.thrd.join();
           obs.thrd.join();

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}