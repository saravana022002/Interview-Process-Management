CREATE DATABASE IF NOT EXISTS employee;
USE employee;

CREATE TABLE IF NOT EXISTS admins (
    id INT NOT NULL AUTO_INCREMENT,
    aname VARCHAR(50) DEFAULT NULL,
    passwd VARCHAR(50) DEFAULT NULL,
    arole VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) DEFAULT NULL,
    date VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    city VARCHAR(50) DEFAULT NULL,
    status_value VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS session_store (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    sessid VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_session_store_sessid (sessid)
);

CREATE TABLE IF NOT EXISTS tests (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    scheduled_date INT NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 1800,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tests_scheduled_date (scheduled_date)
);

CREATE TABLE IF NOT EXISTS test_questions (
    id INT NOT NULL AUTO_INCREMENT,
    test_id INT NOT NULL,
    question_text TEXT NOT NULL,
    opt1 VARCHAR(200) NOT NULL,
    opt2 VARCHAR(200) NOT NULL,
    answer VARCHAR(200) NOT NULL,
    marks INT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_test_questions_test_id (test_id),
    CONSTRAINT fk_test_questions_test FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS candidate_test (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    test_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'blocked',
    started_at TIMESTAMP NULL,
    ends_at TIMESTAMP NULL,
    submitted_at TIMESTAMP NULL,
    score INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL DEFAULT 0,
    extra_seconds INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_candidate_test_user_test (user_id, test_id),
    KEY idx_candidate_test_status (status),
    CONSTRAINT fk_candidate_test_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate_test_test FOREIGN KEY (test_id) REFERENCES tests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS candidate_answers (
    id INT NOT NULL AUTO_INCREMENT,
    candidate_test_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option VARCHAR(200) DEFAULT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_candidate_answers (candidate_test_id, question_id),
    CONSTRAINT fk_candidate_answers_attempt FOREIGN KEY (candidate_test_id) REFERENCES candidate_test(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidate_answers_question FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE
);

INSERT INTO admins (aname, passwd, arole)
SELECT 'admin', 'admin123', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE aname = 'admin');
