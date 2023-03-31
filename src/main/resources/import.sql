INSERT INTO roles (rol) VALUES ('ROL_ADMINISTRADOR');
INSERT INTO roles (rol) VALUES ('ROL_TRABAJADOR');
INSERT INTO roles (rol) VALUES ('ROL_CLIENTE');

INSERT INTO usuarios (email,contrasena,estado,rol_id,create_at,update_at,delete_at) VALUES ('rodrigo@gmail.com','$2a$10$t5z9vYOlRBVAc8GS5nFMMuLktYmYtd1fNsI3g7qwEnwjmhyoxfsQG',1,1,NOW(),null,null);