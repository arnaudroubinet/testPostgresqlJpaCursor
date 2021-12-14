package com.example.demo;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
public class TestService {


    @Autowired
    EntityManager entityManager;

    @Autowired
    TestRepository testRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void init() {
        ArrayList<Test> tests = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(v -> (v <= 57 || v >= 65) && (v <= 90 || v >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            tests.add(new com.example.demo.Test(i, generatedString));
        }
        testRepository.saveAll(tests);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public long readData() throws SQLException {
        Connection conn = ((SessionImpl) entityManager.getDelegate()).connection();
        Statement st = conn.createStatement();

// Turn use of the cursor on.
        st.setFetchSize(1);
        System.out.println("Before execute query");
        ResultSet rs = st.executeQuery("SELECT * FROM test LIMIT 10");
        int test = 0;
        while (rs.next()) {
            System.out.println("Next was called with fetch size = 1");
            test++;
        }
        System.out.println("With fetch 50 next was called " + test + " times.");
        rs.close();

// Turn the cursor off.
        st.setFetchSize(200);
        System.out.println("Before execute query");
        rs = st.executeQuery("SELECT * FROM test  LIMIT 10");
        test = 0;
        while (rs.next()) {
            test++;
            System.out.println("Next was called with fetch size = 200");
        }
        System.out.println("With fetch 0 next was called " + test + " times.");
        rs.close();

// Close the statement.
        st.close();


        AtomicLong counter = new AtomicLong();
        try (Stream<Test> test2 = testRepository.findAllBy1()) {
            test2.forEach(test1 -> {
                System.out.println("Next was called with fetch size = 1");
                test1.getId();
                this.entityManager.detach(test1);
                counter.getAndIncrement();
            });
        }
        try (Stream<Test> test2 = testRepository.findAllBy50()) {
            test2.forEach(test1 -> {
                System.out.println("Next was called with fetch size = 50");
                test1.getId();
                this.entityManager.detach(test1);
            });
        }
        return counter.get();

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public long countData() {
        return testRepository.count();
    }
}
