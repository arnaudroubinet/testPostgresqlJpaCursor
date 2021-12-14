package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.*;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = HINT_PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query(value = "select * from test",nativeQuery = true)
    Stream<Test> findAllBy1();

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = HINT_PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query(value = "select * from test",nativeQuery = true)
    Stream<Test> findAllBy50();
}
