package com.Bank.BPDZ.Entity;



import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpdztrans")
public class BPDZTrans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "statutmouvement", length = 2, nullable = false)
    private String statutMouvement = "99";

    @Column(name = "typemessage", length = 10, nullable = false)
    private String typeMessage;

    @Column(name = "ibansender", length = 100)
    private String ibanSender;

    @Column(name = "ibanreciver", length = 100)
    private String ibanReciver;

    @Column(name = "amount",precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banque_emetteur", referencedColumnName = "id", nullable = true)
    private BPDZDir banqueEmetteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banque_recepteur", referencedColumnName = "id", nullable = true)
    private BPDZDir banqueRecepteur;

    @Column(name = "datecreation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "datetraitement")
    private LocalDateTime dateTraitement;

    
    public BPDZTrans(Integer id, String statutMouvement, String typeMessage, String ibanSender, String ibanReciver,
			BigDecimal amount, BPDZDir banqueEmetteur, BPDZDir banqueRecepteur, LocalDateTime dateCreation,
			LocalDateTime dateTraitement) {
		super();
		this.id = id;
		this.statutMouvement = statutMouvement;
		this.typeMessage = typeMessage;
		this.ibanSender = ibanSender;
		this.ibanReciver = ibanReciver;
		this.amount = amount;
		this.banqueEmetteur = banqueEmetteur;
		this.banqueRecepteur = banqueRecepteur;
		this.dateCreation = dateCreation;
		this.dateTraitement = dateTraitement;
	}
    

	public BPDZTrans() {
		super();
	}


	// Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatutMouvement() {
        return statutMouvement;
    }

    public void setStatutMouvement(String statutMouvement) {
        this.statutMouvement = statutMouvement;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getIbanSender() {
        return ibanSender;
    }

    public void setIbanSender(String ibanSender) {
        this.ibanSender = ibanSender;
    }

    public String getIbanReciver() {
        return ibanReciver;
    }

    public void setIbanReciver(String ibanReciver) {
        this.ibanReciver = ibanReciver;
    }

   

    public BPDZDir getBanqueEmetteur() {
        return banqueEmetteur;
    }

    public void setBanqueEmetteur(BPDZDir banqueEmetteur) {
        this.banqueEmetteur = banqueEmetteur;
    }

    public BPDZDir getBanqueRecepteur() {
        return banqueRecepteur;
    }

    public void setBanqueRecepteur(BPDZDir banqueRecepteur) {
        this.banqueRecepteur = banqueRecepteur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
    
}
