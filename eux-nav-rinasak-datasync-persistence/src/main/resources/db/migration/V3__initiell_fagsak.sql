CREATE TABLE initiell_fagsak
(
    nav_rinasak_uuid uuid primary key references nav_rinasak (nav_rinasak_uuid),
    id               varchar(100),
    tema             varchar(100),
    system           varchar(100),
    nr               varchar(100),
    type             varchar(100),
    arkiv            varchar(100),
    aktoer_id        varchar(100),
    opprettet_bruker varchar(100),
    opprettet_dato   timestamp
);
