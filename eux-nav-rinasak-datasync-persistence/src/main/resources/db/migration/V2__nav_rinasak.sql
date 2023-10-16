CREATE TABLE nav_rinasak
(
    nav_rinasak_uuid       uuid primary key,
    rinasak_id             integer not null,
    overstyrt_enhetsnummer varchar(31),
    sync_status            varchar(32),
    opprettet_bruker       varchar(100),
    opprettet_dato         timestamp
);
