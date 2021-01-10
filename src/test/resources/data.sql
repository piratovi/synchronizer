INSERT INTO tree_sync (id, exist_on_pc, exist_on_phone, name, relative_path)
VALUES (1, true, true, '\', '\');

INSERT INTO folder_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (2, true, true, 'Music Folder1', '\\Music Folder1', 1);
INSERT INTO folder_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (3, true, true, 'Music Folder2', '\\Music Folder2', 1);

INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (4, true, true, 'Composition 1', '\\Music Folder1\Composition 1', 2);
INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (5, true, false, 'Composition 2', '\\Music Folder1\Composition 2', 2);
INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (6, true, true, 'Composition 3', '\\Music Folder1\Composition 3', 2);

INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (7, true, true, 'Composition 1', '\\Music Folder2\Composition 1', 3);
INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (8, true, true, 'Composition 2', '\\Music Folder2\Composition 2', 3);
INSERT INTO file_sync (id, exist_on_pc, exist_on_phone, name, relative_path, parent_id)
VALUES (9, true, true, 'Composition 3', '\\Music Folder2\Composition 3', 3);