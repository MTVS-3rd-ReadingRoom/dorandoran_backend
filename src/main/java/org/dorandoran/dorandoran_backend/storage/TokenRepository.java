package org.dorandoran.dorandoran_backend.storage;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByNo(Long no);

    Optional<Token> findByUserNo(Long userNo);
}