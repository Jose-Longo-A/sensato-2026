-- Usuários do sistema (validados pelo POST /login)
INSERT INTO usuarios (username, senha) VALUES ('joao', 'joao123');
INSERT INTO usuarios (username, senha) VALUES ('maria', 'maria123');
INSERT INTO usuarios (username, senha) VALUES ('pedro', 'pedro123');

-- Operadores (JdbcUserDetailsManager) - senhas em BCrypt
-- admin / admin123           -> ROLE_ADMIN       (PUT /unlock/{userid})
-- manutencao / manut123      -> ROLE_MANUTENCAO  (GET / e POST /)
INSERT INTO users (username, password, enabled)
VALUES ('admin', '{bcrypt}$2b$10$DBt/otgCmGRgsr4c2HOyjeT/OM6cBQnpYUxI5xtdmnu58qbVECYfW', true);
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');

INSERT INTO users (username, password, enabled)
VALUES ('manutencao', '{bcrypt}$2b$10$QAuOk9oTev0nArPNZWKa/uSfZOA1HiGHq/7VMBWP0tUl9u.okwtAy', true);
INSERT INTO authorities (username, authority) VALUES ('manutencao', 'ROLE_MANUTENCAO');
