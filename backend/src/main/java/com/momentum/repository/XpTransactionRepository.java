package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.XpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface XpTransactionRepository extends JpaRepository<XpTransaction, Long> {
    Page<XpTransaction> findByUserOrderByIdDesc(AppUser user, Pageable pageable);
}
