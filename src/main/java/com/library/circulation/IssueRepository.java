package com.library.circulation;

import org.springframework.data.jpa.repository.*;
import java.time.*;
import java.util.*;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByStatus(String status);

    List<Issue> findByUserIdAndStatus(Long id, String status);

    List<Issue> findByDueDateAndStatus(LocalDate d, String s);

    long countByStatus(String s);
}
