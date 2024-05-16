INSERT INTO "user"(hedgedoc_id) VALUES('f3a41653-626b-4623-9710-ed7ec21a7ac5');

INSERT INTO folder(title, user_id) VALUES('folder1', 1);
INSERT INTO folder(title, user_id, parent_folder_id) VALUES('folder1-1', 1, 1);
INSERT INTO folder(title, user_id, parent_folder_id) VALUES('folder1-2', 1, 1);
INSERT INTO folder(title, user_id, parent_folder_id) VALUES('folder2-1', 1, 2);

INSERT INTO note(hedgedoc_id) VALUES('WWdz23vsQciQ2ZKbiYtj8Q');

INSERT INTO folder_note(folder_id, note_id) VALUES (1, 1);

INSERT INTO rule(title, regular_expression, user_id, folder_id) VALUES ('rule1', 'a.*', 1, 1);