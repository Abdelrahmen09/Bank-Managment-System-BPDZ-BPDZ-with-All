package com.Bank.BPDZ.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bank.BPDZ.Entity.BPDZDir;
import com.Bank.BPDZ.Entity.BPDZTrans;

@Repository
public interface RepositoryBpdzTrans extends JpaRepository<BPDZTrans, Long> {
	List<BPDZTrans> findByBanqueEmetteurOrBanqueRecepteur(BPDZDir banqueEmetteur, BPDZDir banqueRecepteur);

}
