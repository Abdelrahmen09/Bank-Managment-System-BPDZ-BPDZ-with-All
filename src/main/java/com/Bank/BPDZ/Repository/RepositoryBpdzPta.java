package com.Bank.BPDZ.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bank.BPDZ.Entity.BPDZPmtA;

@Repository
public interface RepositoryBpdzPta extends JpaRepository<BPDZPmtA, Long> {

}
