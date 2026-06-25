package com.ifteakar.portfolio_backend.repository;

import com.ifteakar.portfolio_backend.model.CvDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CvRepository extends JpaRepository<CvDocument, Long> {
    Optional<CvDocument> findFirstByOrderByUploadDateDesc();
}