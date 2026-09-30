package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.XpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XpTransactionRepository extends JpaRepository<XpTransaction, Long> {
    List<XpTransaction> findByUserOrderByIdDesc(AppUser user);
}
