package com.example.demo.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepo extends JpaRepository<KiteSession, Long> {

}
