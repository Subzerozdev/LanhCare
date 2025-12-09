package com.lanhcare.repository;

import com.lanhcare.entity.ICD11Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IcdChapterRepository extends JpaRepository<ICD11Chapter, String> {
}
