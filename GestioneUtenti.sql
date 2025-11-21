

DROP DATABASE IF EXISTS userdb;
CREATE DATABASE userdb;
USE userdb;

CREATE TABLE utente (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(100)      NOT NULL,
    cognome          VARCHAR(100)      NOT NULL,
    codice_fiscale   VARCHAR(16)       NOT NULL UNIQUE,
    data_di_nascita  DATE              NOT NULL
);


INSERT INTO utente (nome, cognome, codice_fiscale, data_di_nascita) VALUES
('Mario',   'Rossi',    'MRARSS80A01H501U', '1980-01-01'),
('Luigi',   'Verdi',    'LGIVRD85B15H501X', '1985-02-15'),
('Anna',    'Bianchi',  'NNABNC90C20H501Y', '1990-03-20'),
('Simone',  'Mangano',  'SMNMGN00A01H501X', '2000-01-01'),
('Sara',    'Neri',     'SRANER95D10H501Z', '1995-04-10'),
('Francesca',  'Esposito',  'FRCESP88E12H501P', '1988-05-12'),
('Marco',      'Romano',    'MRCRMN92A22H501L', '1992-01-22'),
('Giulia',     'Ferrari',   'GLLFRR99C11H501T', '1999-03-11'),
('Paolo',      'Greco',     'PLLGRF77B05H501R', '1977-02-05'),
('Elisa',      'Costa',     'LSACST85D20H501W', '1985-04-20'),
('Andrea',     'Gallo',     'NDRGLL93E18H501F', '1993-05-18'),
('Chiara',     'Fontana',   'CHRFTN91H30H501A', '1991-08-30'),
('Davide',     'Sartori',   'DVDSTR82F08H501Q', '1982-06-08'),
('Luca',       'Marchetti', 'LCAMRC89L25H501H', '1989-12-25'),
('Valentina',  'Moretti',   'VLNMRR94S14H501C', '1994-11-14');

