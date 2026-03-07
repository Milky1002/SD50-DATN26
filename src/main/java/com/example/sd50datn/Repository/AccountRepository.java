package com.example.sd50datn.Repository;

import com.example.sd50datn.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Integer> {
}
