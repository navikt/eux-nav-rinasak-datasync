CREATE TABLE nav_rinasak
(
    nav_rinasak_uuid       uuid primary key,
    rinasak_id             integer,
    overstyrt_enhetsnummer varchar(31),
    opprettet_bruker       varchar(100),
    opprettet_dato         timestamp
);