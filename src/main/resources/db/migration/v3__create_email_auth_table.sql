    create table email_auth
    (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email      VARCHAR(255) NOT NULL,
                          auth_code  VARCHAR(255) NOT NULL
    );