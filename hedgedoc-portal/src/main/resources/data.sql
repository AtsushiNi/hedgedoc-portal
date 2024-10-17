INSERT INTO "user"(hedgedoc_id, created_at) VALUES('f3a41653-626b-4623-9710-ed7ec21a7ac5', '2024-10-17 22:20:18');

INSERT INTO folder(title, user_id, created_at) VALUES('folder1', 1, '2024-10-17 22:20:18');
INSERT INTO folder(title, user_id, parent_folder_id, created_at) VALUES('folder1-1', 1, 1, '2024-10-17 22:20:18');
INSERT INTO folder(title, user_id, parent_folder_id, created_at) VALUES('folder1-2', 1, 1, '2024-10-17 22:20:18');
INSERT INTO folder(title, user_id, parent_folder_id, created_at) VALUES('folder2-1', 1, 2, '2024-10-17 22:20:18');

INSERT INTO note(hedgedoc_id, created_at) VALUES('WWdz23vsQciQ2ZKbiYtj8Q', '2024-10-17 22:20:18');

INSERT INTO folder_note(folder_id, note_id) VALUES (1, 1);

INSERT INTO rule(title, regular_expression, user_id, folder_id) VALUES ('rule1', 'a.*', 1, 1);