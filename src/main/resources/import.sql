INSERT INTO roles (rol) VALUES ('ROLE_ADMINISTRADOR');
INSERT INTO roles (rol) VALUES ('ROLE_TRABAJADOR');
INSERT INTO roles (rol) VALUES ('ROLE_CLIENTE');

INSERT INTO usuarios (email,contrasena,estado,rol_id,create_at,update_at,delete_at) VALUES ('rodrigo@gmail.com','$2a$10$t5z9vYOlRBVAc8GS5nFMMuLktYmYtd1fNsI3g7qwEnwjmhyoxfsQG',1,1,NOW(),null,null);

INSERT INTO perfiles (run,nombre,apellido_paterno,apellido_materno,contacto,usuario_id,create_at,update_at) VALUES ("18580892-0","Rodrigo","Lazo","Fredes","991676062",1,NOW(),null);

INSERT INTO direcciones (quien_recibe,region_id,comuna_id,poblacion,calle,numero,perfiles_id) VALUES ("Rodrigo Lazo",4,10,"La Aguada Centro","El Litre","433",1);

INSERT INTO perfiles_direcciones (perfil_id,direcciones_id) VALUES (1,1);