INSERT INTO "user"(id, hedgedoc_id) VALUES(1, 'f3a41653-626b-4623-9710-ed7ec21a7ac5');

INSERT INTO folder(id, title, user_id) VALUES(1, 'folder1', 1);
INSERT INTO folder(id, title, user_id, parent_folder_id) VALUES(2, 'folder1-1', 1, 1);
INSERT INTO folder(id, title, user_id, parent_folder_id) VALUES(3, 'folder1-2', 1, 1);
INSERT INTO folder(id, title, user_id, parent_folder_id) VALUES(4, 'folder2-1', 1, 2);

INSERT INTO note(id, hedgedoc_id, parent_folder_id) VALUES(1, 'WWdz23vsQciQ2ZKbiYtj8Q', 1);