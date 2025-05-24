 package com.Bank.BPDZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Bank.BPDZ.Entity.BPDZCptBcDCA;
import com.Bank.BPDZ.Entity.BPDZDir;
import com.Bank.BPDZ.Entity.BPDZFraude;
import com.Bank.BPDZ.Entity.BPDZHabi;
import com.Bank.BPDZ.Repository.RepositoryBpdzDir;
import com.Bank.BPDZ.Repository.RepositoryBpdzHbi;
import com.Bank.BPDZ.Service.ServiceHabi;
 @EnableJpaRepositories(basePackages = "com.Bank.BPDZ.Repository")
@SpringBootApplication
public class BankManagmentSystemBpdz1Application implements CommandLineRunner{
	

	public static void main(String[] args) {
		SpringApplication.run(BankManagmentSystemBpdz1Application.class, args);
	}
	/*public List<BPDZDir> suggestByName(List<BPDZDir> list, String keyword) {
	    List<BPDZDir> suggestions = new ArrayList<>();
	    String lowerKeyword = keyword.toLowerCase();

	    for (BPDZDir dir : list) {
	        if (dir.getNomBanque()!=null &&dir.getNomBanque().toLowerCase().contains(lowerKeyword)||
	                (dir.getBic() != null && dir.getBic().toLowerCase().contains(lowerKeyword)) ||
	                (dir.getCodeBanque() != null && dir.getCodeBanque().toLowerCase().contains(lowerKeyword))) {
	            suggestions.add(dir);
	        }
	    }

	    return suggestions;
	}*/

    @Autowired
	private RepositoryBpdzDir savecompte;
    @Autowired
   	private RepositoryBpdzHbi takecompt;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    ServiceHabi servicehabi;
    //@Autowired
    
    //serviceFraude
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		/*BPDZDir x=new BPDZDir("BNA","123456","BNALDZAL627","BNA A.P 627 N° 81 Rue Ali REMLI Bouzareah –Alger",true,"O","P1");
		 BPDZDir h=new BPDZDir(" BNP Paribas EL Djazair","1234568","BDLODZALXXX"," 8 Quartier d'Affaires d'Alger Lot 1 N°03.I – Bab Ezzouar – 16024 Alger",true,"O","P1");
		 BPDZDir v=new BPDZDir("BDL","1234567","BNPADZALXXX El Djazair"," 05, Rue GACI Amar, Staouéli, Alger ",true,"O","P1");
		 BPDZDir c=new BPDZDir("BPDZ","0000000","BPDZALALXXX","home",true,"O","P4");
		savecompte.save(h);
		savecompte.save(v);
		savecompte.save(c);*/
		
		
		/*BPDZFraude fraude = new BPDZFraude();
		
	    boolean s ;//serviceFraude.ghdjdjd("kjk",)
		
		 BPDZHabi compte=new BPDZHabi();
		  compte=takecompt.findByid(1L);
		  System.out.println("");
		  
		  
		  
		 String X=compte.getRole();
		 BPDZDir dir=new BPDZDir();
		 BPDZCptBcDCA cp=new BPDZCptBcDCA();
		 dir= servicehabi.getInformationsBank(compte);
		 cp=servicehabi.getOneAccount(compte);
		 System.out.println("the role is :"+cp.getNatureCompte());
		 //System.out.println("this is :"+dir.getBic());
		 System.out.println("this is :"+cp.getNatureCompte());
		 //BPDZDir [] s=savecompte.findAll(null);
		 List<BPDZDir> get=new ArrayList<>();
		 get=servicehabi.getAllDir();
		 for(int i=0;i<get.size();i++) {
			 BPDZDir dirr =get.get(i);
			 System.out.println("The [i] is :"+dirr.getBic());
		 }
		/*Scanner scanner = new Scanner(System.in);
		System.out.print("Enter name to search: ");
		String input = scanner.nextLine();

		List<BPDZDir> dirs = servicehabi.getAllDir();
		List<BPDZDir> suggestions = suggestByName(dirs, input);

		if (!suggestions.isEmpty()) {
		    System.out.println("Suggestions:");
		    for (int i = 0; i < suggestions.size(); i++) {
		        System.out.println((i + 1) + ". " + suggestions.get(i).getNomBanque());
		    }*/
		 
		//s=null;
		//s=servicehabi.getInformationsBank(compte);
		
		 //BPDZHabi compte=new BPDZHabi();
		 //compte=takecompt.findByid(1L);
		 //boolean s =passwordEncoder.matches("12345678", compte.getMotDePasse());
		 //System.out.println("this :"+s);
		 //System.out.println("");
		  // compte .setMotDePasse(passwordEncoder.encode("MotDePasse2"));
		   //System.out.println("this :"+compte.getMotDePasse());
		 //compte.setRole("User");
		 //System.out.println("this :"+compte.getRole());
		//takecompt.save(compte);

		BPDZDir bank = new BPDZDir();
		bank.setNomBanque("Banque Nationale");
		bank.setCodeBanque("BNAT001");
		bank.setBic("BNATFRPPXXX");
		bank.setAdresse("123 Rue de la République, 75001 Paris, France");
		bank.setParticipante(true);
		bank.setIso20022Integration("O"); // Must be "O" or "N"
		bank.setAbonnement("P2");         // Must be P1–P4
		bank.setPret(true);
		bank.setNomCorrespondant("Jean Dupont");
		bank.setMail("contact@banquenationale.fr");
		bank.setTel("+33123456789");
		savecompte.save(bank);
		// Assuming you already have a BPDZDir object named `bank`
		BPDZHabi habi = new BPDZHabi();
		habi.setBanque(bank); // bank is a BPDZDir instance

		habi.setLogin("omar");
		habi.setMotDePasse(passwordEncoder.encode("12345678")); // Ideally this would be hashed
		habi.setAdresseIP("192.168.1.10");
		habi.setPortReception(8080);
		habi.setAdresseipsecours("192.168.1.11");
		habi.setPortReceptionSecours(8081);
		habi.setRole("Admin"); // Example: ADMIN, USER, MANAGER etc.
		habi.setPermissions("READ,WRITE,DELETE"); // Or a JSON string depending on how you store it
		habi.setNom("Dupont");
		habi.setPrenom("Jean");
        takecompt.save(habi);


	}
}
