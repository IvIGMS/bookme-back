-- Crea un usuario inicial en la tabla bookme.users

SET search_path TO bookme;

INSERT INTO users (
    email,
    password,
    firstname,
    lastname,
    phone_number,
    is_active,
    role,
    created_at,
    updated_at
) VALUES (
    'friasgilivan@gmail.com',
    '$2a$10$fygZBxo/xx9G/RfhqZNLze8v2NXawaZ7tXkP7xZ4s6ptTGGm7WteC',
    'Ivan',
    'Frias',
    '662594955',
    true,
    'USER',
    NOW(),
    NOW()
);
