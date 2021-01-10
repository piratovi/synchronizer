DROP TABLE IF EXISTS tree_sync;
CREATE TABLE tree_sync
(
    id             int          not null primary key,
    exist_on_pc    boolean      not null,
    exist_on_phone boolean      not null,
    name           varchar(255) not null,
    relative_path  varchar(255) not null,
    parent_id      int,
    created        timestamp
);

DROP TABLE IF EXISTS folder_sync;
CREATE TABLE folder_sync
(
    id             int          not null primary key,
    exist_on_pc    boolean      not null,
    exist_on_phone boolean      not null,
    name           varchar(255) not null,
    relative_path  varchar(255) not null,
    parent_id      int
);

DROP TABLE IF EXISTS file_sync;
CREATE TABLE file_sync
(
    id             int          not null primary key,
    exist_on_pc    boolean      not null,
    exist_on_phone boolean      not null,
    name           varchar(255) not null,
    relative_path  varchar(255) not null,
    parent_id      int
);

DROP TABLE IF EXISTS history_sync;
CREATE TABLE history_sync
(
    id      int not null primary key,
    action  int,
    sync_id int
);

alter table if exists file_sync add constraint file_sync_relative_path unique (relative_path);
alter table if exists folder_sync add constraint folder_sync_relative_path unique (relative_path);
alter table if exists tree_sync add constraint tree_sync_relative_path unique (relative_path);