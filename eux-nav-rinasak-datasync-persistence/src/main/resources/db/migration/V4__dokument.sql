CREATE TABLE dokument
(
    dokument_uuid    uuid primary key,
    sed_id           varchar(100),
    sed_versjon      int,
    sed_type         varchar(100),
    nav_rinasak_uuid uuid references nav_rinasak (nav_rinasak_uuid),
    dokument_info_id varchar(100),
    opprettet_bruker varchar(100),
    opprettet_dato   timestamp
);
