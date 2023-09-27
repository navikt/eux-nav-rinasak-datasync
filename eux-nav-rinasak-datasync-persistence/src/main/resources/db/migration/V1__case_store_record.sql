CREATE TABLE case_store_record
(
    case_store_record_uuid uuid primary key,
    rinasak_id             integer,
    fagsak_id              varchar(100),
    case_store_id          varchar(100),
    fagsak_tema            varchar(100),
    overstyrt_enhetsnummer varchar(31),
    journalpost_id         varchar(31),
    sync_status            varchar(32),
    buc_id                 uuid,
    opprettet_bruker       varchar(100),
    opprettet_dato         timestamp
);
