package com.Bank.BPDZ.Service;

import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Bank.BPDZ.DTO.Pacs002DTO;
import com.Bank.BPDZ.DTO.Pacs008DTO;
import com.Bank.BPDZ.DTO.Pacs009DTO;
import com.Bank.BPDZ.DTO.PacsXmlGenerator;
import com.Bank.BPDZ.DTO.PacsXmlParser;
import com.Bank.BPDZ.Entity.BPDZCptBcDCA;
import com.Bank.BPDZ.Entity.BPDZDir;
import com.Bank.BPDZ.Entity.BPDZHabi;
import com.Bank.BPDZ.Entity.BPDZPret;
import com.Bank.BPDZ.Entity.BPDZTrans;
import com.Bank.BPDZ.Entity.BPDZmt;
import com.Bank.BPDZ.Repository.RepositoryBpdzCptbcdca;
import com.Bank.BPDZ.Repository.RepositoryBpdzDir;
import com.Bank.BPDZ.Repository.RepositoryBpdzFraude;
import com.Bank.BPDZ.Repository.RepositoryBpdzHbi;
import com.Bank.BPDZ.Repository.RepositoryBpdzMandat;
import com.Bank.BPDZ.Repository.RepositoryBpdzPret;
import com.Bank.BPDZ.Repository.RepositoryBpdzPta;
import com.Bank.BPDZ.Repository.RepositoryBpdzTrans;
import com.Bank.BPDZ.Repository.RepositoryBpdzmt;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.xml.sax.InputSource;


@Service
public class PacsService {

	@Autowired
	private RepositoryBpdzHbi repositoryBpdzHbi;
    @Autowired
    private RepositoryBpdzDir repositoryBpdzDir;
    @Autowired
    private RepositoryBpdzCptbcdca repositoryBpdzCptbcdca;
    @Autowired
    private RepositoryBpdzMandat repositoryBpdzMandat;
    @Autowired
    private RepositoryBpdzFraude repositoryBpdzFraude;
    @Autowired
    private RepositoryBpdzPret repositoryBpdzPret;
    @Autowired
    private RepositoryBpdzPta repositoryBpdzPta;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RepositoryBpdzmt repositoryBpdzmt;
    @Autowired
    private RepositoryBpdzTrans repositoryBpdzTrans;
    @Autowired
    private ServiceHabi serviceHabi ;
    @Autowired
    private ServiceMADU serviceMADU ;
    @Autowired
    private PacsXmlGenerator pacsXmlGenerator;
    @Autowired
    private PacsXmlParser pacsXmlParser;
    private final double taxRate = 0.05;
    private  PacsXmlGenerator xmlGenerator = new PacsXmlGenerator();
    //---------------------------------------------------------------savePacs.008--------------------------------------------------------------------------
    @Transactional
    public BPDZmt savePacs008(Pacs008DTO pacs008DTO) throws Exception  {
        // Find banks by BIC
        Optional<BPDZDir> senderBank = Optional.ofNullable(repositoryBpdzDir.findByBic(pacs008DTO.getSenderBic()));
        Optional<BPDZDir> receiverBank = Optional.ofNullable(repositoryBpdzDir.findByBic(pacs008DTO.getReceiverBic()));
        BPDZmt movement = new BPDZmt();
        if (senderBank.isPresent()) {
           movement.setBanqueEmetteur(senderBank.get());
        }
        if (receiverBank.isPresent()) {
             movement.setBanqueRecepteur(receiverBank.get());}
        // Generate XML
        String xmlContent = xmlGenerator.generatePacs008Xml(pacs008DTO);
      // Create and save movement     
        movement.setStatutMouvement("99"); // Initial status
        movement.setNatureMouvement("PACS.008 ");
        movement.setTypeMessage("Pacs.008");
        movement.setModeTransmission(pacs008DTO.getTransmissionMode());
        movement.setDateCreation(LocalDateTime.now());
        movement.setInformationsMessage(buildPacs008Info(pacs008DTO));
        movement.setXmlFile(xmlContent);       
        return repositoryBpdzmt.save(movement);
    }
    private String buildPacs008Info(Pacs008DTO dto) {
        return String.format(
            "Instruction ID: %s, EndToEnd ID: %s, Amount: %s %s, From: %s (%s), To: %s (%s)",
            dto.getInstructionId(),
            dto.getEndToEndId(),
            dto.getAmount(),
            dto.getCurrency(),
            dto.getDebtorName(),
            dto.getSenderBic(),
            dto.getCreditorName(),
            dto.getReceiverBic()
        );
    }
    //---------------------------------------------------------------save pacs.009------------------------------------------------------------------------
    public BPDZmt savePacs009(Pacs009DTO pacs009DTO) throws Exception {
        // Find banks by BIC
        Optional<BPDZDir> senderBank = Optional.ofNullable(repositoryBpdzDir.findByBic(pacs009DTO.getSenderBic()));
        Optional<BPDZDir> receiverBank = Optional.ofNullable(repositoryBpdzDir.findByBic(pacs009DTO.getReceiverBic()));

        // Create movement first since we need it for bank assignment
        BPDZmt movement = new BPDZmt();
        movement.setStatutMouvement("99"); // Initial status
        movement.setNatureMouvement("PACS.009");
        movement.setTypeMessage("Pacs.009");
        movement.setModeTransmission(pacs009DTO.getTransmissionMode());
        movement.setDateCreation(LocalDateTime.now());
        
        // Set banks if found
        senderBank.ifPresent(movement::setBanqueEmetteur);
        receiverBank.ifPresent(movement::setBanqueRecepteur);

        // Generate XML after banks are set
        String xmlContent;
        try {
            xmlContent = xmlGenerator.generatePacs009Xml(pacs009DTO);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PACS.009 XML", e);
        }

        // Set remaining fields
        movement.setInformationsMessage(buildPacs009Info(pacs009DTO));
        movement.setXmlFile(xmlContent);
        
        return repositoryBpdzmt.save(movement);
    }

    private String buildPacs009Info(Pacs009DTO dto) {
        return String.format(
            "Message ID: %s | Instruction ID: %s | Amount: %s %s | Settlement: %s%n" +
            "Debtor: %s (%s) | Creditor: %s (%s)",
            dto.getMessageId(),
            dto.getInstructionId(),
            dto.getAmount(),
            dto.getCurrency(),
            dto.getSettlementMethod(),
            dto.getDebtorName(),
            dto.getSenderBic(),
            dto.getCreditorName(),
            dto.getReceiverBic()
        );
    }
    
    //------------------------------------------------------------processPayment-------------------------------------------------------------------------------
    
    public String jeutContablePacs (BigDecimal cach ,BigDecimal totalDebit,BPDZCptBcDCA courantReciver
    		,BPDZCptBcDCA courantSender,BPDZCptBcDCA reserveSender ) {
          BPDZDir senderBank = courantSender.getBanque();
    	 Optional<BPDZCptBcDCA> compteplatform= repositoryBpdzCptbcdca.findById(1L);
         BigDecimal amount = courantSender.getSolde().subtract(totalDebit);
         BigDecimal amountt = reserveSender.getSolde().subtract(totalDebit);
         if (amount.compareTo(BigDecimal.ZERO) >= 0) {
        	 courantSender.setSolde(amount);
        	 courantReciver.setSolde(courantReciver.getSolde().add(cach));
        	 compteplatform.get().setSolde(compteplatform.get().getSolde().add(totalDebit));
        	 
        	 repositoryBpdzCptbcdca.save(courantSender);
        	 repositoryBpdzCptbcdca.save(courantReciver);
        	 repositoryBpdzCptbcdca.save(compteplatform.get());
        	 System.out.println("return courant");
        	 return "courant";
         }
         else {
        	 if(amountt.compareTo(BigDecimal.ZERO) >= 0) {
        		 reserveSender.setSolde(amountt);
        		 courantReciver.setSolde(courantReciver.getSolde().add(cach));
        		 compteplatform.get().setSolde(compteplatform.get().getSolde().add(totalDebit));
        		 repositoryBpdzCptbcdca.save(reserveSender);
            	 repositoryBpdzCptbcdca.save(courantReciver);
            	 repositoryBpdzCptbcdca.save(compteplatform.get());
            	 return "reserve";}
        	 else {
        		 if (senderBank.getPret()==true) {
        	             // Inject double the amount as loan
        	             BigDecimal loanAmount = cach.multiply(BigDecimal.valueOf(2));
        	             reserveSender.setSolde(reserveSender.getSolde().add(loanAmount));
        	             repositoryBpdzCptbcdca.save(reserveSender);
        	             // Create and save loan record in BPDZPret
        	             BPDZPret loan = new BPDZPret();
        	             loan.setBanque(senderBank);  // Sender bank ID (the one receiving the loan)
        	             loan.setMontantPret(loanAmount);
        	             loan.setDatePret(LocalDate.now());  // Current date as loan date
        	             repositoryBpdzPret.save(loan);
        	             // Save loan record in BPDZPret
        	             reserveSender.setSolde(reserveSender.getSolde().subtract(totalDebit));
        	        	 courantReciver.setSolde(courantReciver.getSolde().add(cach));
        	        	 compteplatform.get().setSolde(compteplatform.get().getSolde().subtract(loanAmount).add(totalDebit));
        	        	 repositoryBpdzCptbcdca.save(reserveSender);
                    	 repositoryBpdzCptbcdca.save(courantReciver);
                    	 repositoryBpdzCptbcdca.save(compteplatform.get());
                    	 System.out.println("return pret");
                    	 return "pret";}
              	 }
              }
          return null;
      }         
    
    public void processPayment(BPDZmt originalMovement ,BPDZHabi habi) throws Exception {
        // --- FRAUD CONTROL PHASE (NEW) ---
    	String xml = originalMovement.getXmlFile();
    	String pacsType = pacsXmlParser.detectPacsType(xml);
    	 BigDecimal amount;
    	if ("pacs009".equals(pacsType)) {
            Pacs009DTO dto = pacsXmlParser.parsePacs009Xml(xml);
            amount = dto.getAmount();  // ✅ assign amount
        } else if ("pacs008".equals(pacsType)) {
            Pacs008DTO dto = pacsXmlParser.parsePacs008Xml(xml);
            amount = dto.getAmount();  // ✅ assign amount
        } else {
            throw new IllegalArgumentException("Unknown PACS type in XML");
        }
        String fraudCheckResult = serviceMADU.fraudCheck(originalMovement);
        Optional<BPDZDir> senderBank = Optional.ofNullable(repositoryBpdzDir.findByBic(originalMovement.getBanqueEmetteur().getBic()));
        Optional<BPDZDir> receiverBank = Optional.ofNullable(repositoryBpdzDir.findByBic(originalMovement.getBanqueRecepteur().getBic()));
        if (fraudCheckResult!=null) {
            generateRejection(originalMovement, "FR01", fraudCheckResult);
            originalMovement.setStatutMouvement("00");
       	    repositoryBpdzmt.save(originalMovement);
           
        }   
        else{
        	if ((senderBank.get().getBic()!=habi.getBanque().getBic()&& habi.getRole()=="user")
        		||(habi.getRole()!="User"&&senderBank.get().getBic()==null)) {
        	 generateRejection(originalMovement, "FF01", "Missing or invalid bank references");
              }
        	//-------------------------------------------------------------
        	 else if( validateAmount(amount)) {
             BigDecimal cach = amount;
             BigDecimal tax = calculateTax(cach);
             BigDecimal totalDebit = cach.add(tax);
             BPDZCptBcDCA  courantReciver = repositoryBpdzCptbcdca.findByBanqueAndNatureCompteIgnoreCase(receiverBank.get(), "Courant");
             BPDZCptBcDCA  reserveSender = repositoryBpdzCptbcdca.findByBanqueAndNatureCompteIgnoreCase(senderBank.get(), "Reserve");
             BPDZCptBcDCA  courantSender = repositoryBpdzCptbcdca.findByBanqueAndNatureCompteIgnoreCase(senderBank.get(), "Courant");
             
             String state=jeutContablePacs(cach,totalDebit,courantReciver,courantSender,reserveSender);
             if ( state!=null && state=="courant") {        
            	 originalMovement.setStatutMouvement("00");
            	 originalMovement.setInformationsMessage("Paid from current account");
            	 repositoryBpdzmt.save(originalMovement);
                 generateAcceptance(originalMovement, "ACCP", "Payment processed from current account");          
             }
             // 2. Try Reserve Account 
             else if (state!=null && state=="reserve"){
            	 originalMovement.setStatutMouvement("00");
            	 originalMovement.setInformationsMessage("Paid from reserve account"); 
            	 repositoryBpdzmt.save(originalMovement);
                 generateAcceptance(originalMovement, "ACCP", "Payment processed from reserve account");            
             }
              // 3. Check Automatic Loan
             else if (state!=null && state=="pret"){  
            	 System.out.println("state pret");
                 generateAcceptance(originalMovement, "PART", "Payment processed with automatic loan");   
             } 
                // 4. Reject Payment
             else { System.out.println("state null");
            	 generateRejection(originalMovement, "AM04", "Insufficient funds in all accounts");} 
        	 }      
        }    
}
    //----------------------------------------------------------------calculate-------------------------------------------------------------------
    private BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(taxRate));
    }
    private boolean validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return true;
    }
//--------------------------------------------------------------------------------------------------------------------------------------
private void generateAcceptance(BPDZmt originalMovement, String status, String message)throws Exception {
	 System.out.println("start generateAcceptance");
    Pacs002DTO response = new Pacs002DTO();
    Pacs008DTO dto = pacsXmlParser.parsePacs008Xml(originalMovement.getXmlFile());
    response.setOriginalMessageId(dto.getInstructionId());
    response.setStatus(status);
    response.setAdditionalInformation(message);
    response.setOriginalAmount(dto.getAmount());
    response.setOriginalCurrency(dto.getCurrency());
    response.setAdditionalInformation("s");
    response.setOriginalEndToEndId(dto.getEndToEndId());
    response.setReasonCode("oui");
    response.setReasonText("Non");
    
    String responseXml = pacsXmlGenerator.generatePacs002(response);
    savePacs002Movement(originalMovement, responseXml, "99", "00");
    System.out.println(" end generateAcceptance");
}
//generate pacs 002 refu
//----------------------------------------------------------------------------------------------------
private void generateRejection(BPDZmt originalMovement, String reasonCode, String reason)throws Exception {
    Pacs002DTO rejection = new Pacs002DTO();
    Pacs008DTO dto = pacsXmlParser.parsePacs008Xml(originalMovement.getXmlFile());
    rejection.setOriginalMessageId(dto.getInstructionId());
    rejection.setStatus("RJCT");
    rejection.setReasonCode(reasonCode);
    rejection.setReasonText(reason);
    rejection.setOriginalAmount(dto.getAmount());
    rejection.setOriginalCurrency(dto.getCurrency());
    
    String rejectionXml = pacsXmlGenerator.generatePacs002(rejection);
    //savePacs002Movement(originalMovement, rejectionXml, "RJCT", "REJECTED");
    savePacs002Movement(originalMovement, rejectionXml, "99", "00");
}
//accept
//----------------------------------------------------------------------------------------------------
private void savePacs002Movement(BPDZmt originalMovement, String xmlContent, String status, String nature)throws Exception {
	  System.out.println("start save ");
    BPDZmt responseMovement = new BPDZmt();
    responseMovement.setTypeMessage("Pacs.002");
    responseMovement.setStatutMouvement(status);
    responseMovement.setNatureMouvement(nature);
    responseMovement.setXmlFile(xmlContent);
    responseMovement.setBanqueEmetteur(originalMovement.getBanqueRecepteur()); // Reversed
    responseMovement.setBanqueRecepteur(originalMovement.getBanqueEmetteur());
    responseMovement.setDateCreation(LocalDateTime.now());
    responseMovement.setDateTraitement(LocalDateTime.now());
    
    repositoryBpdzmt.save(responseMovement);
    System.out.println("end save ");
}}